package xyz.hexagons.client.menu.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import me.wieku.animation.animations.Animation;
import me.wieku.animation.timeline.AnimationSequence;
import me.wieku.animation.timeline.Timeline;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Version;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.api.HColor;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.menu.ActorAccessor;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.menu.widgets.MenuButton;
import xyz.hexagons.client.menu.settings.ConfigEngine;
import xyz.hexagons.client.menu.settings.SettingsTab;
import xyz.hexagons.client.engine.render.BlurEffect;
import xyz.hexagons.client.engine.render.MapRenderer;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.rankserv.AccountManager;
import xyz.hexagons.client.rankserv.MotdApi;
import xyz.hexagons.client.utils.FpsCounter;
import xyz.hexagons.client.utils.GUIHelper;

import java.util.ArrayList;

public class MainMenu implements Screen {

	public static MainMenu instance = new MainMenu();

	private Stage stage;
	private MenuButton button, button2, button3;
	private ArrayList<MenuButton> list = new ArrayList<>();
	private Label version, copyright;
	private int currentIndex = -1;
	private Image beatIHigh;
	private Image beatILow;

	private BlurEffect blurEffect;
	private SkewCamera camera = new SkewCamera();
	private ShapeRenderer shapeRenderer;
	private MapRenderer mapRenderer = new MapRenderer();
	private Table music;
	private Label title;
	private boolean escclick = false;

	private Table motdTable = GUIHelper.getTable(new Color(0,0,0,0.8f));
	private Label motdLabel;
	private Timeline motdAnimation;

	private float[] dfg = new float[60];

	public boolean optionsShowed;

	SettingsTab sTab;

	public MapSelect sl;
	FpsCounter cd = new FpsCounter(60);
	public MainMenu(){
		stage = new Stage(new ExtendViewport(1024, 768));
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		blurEffect = new BlurEffect(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		blurEffect.setPower(5f);
		blurEffect.setDarkness(1.5f);

		shapeRenderer = new ShapeRenderer();

		ConfigEngine.register();

		sTab = SettingsTab.getInstance();

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.UP) {
					int index = (currentIndex == 0 ? list.size() - 1 : currentIndex - 1);
					selectIndex(index);
				}

				if (keycode == Keys.DOWN) {
					int index = (currentIndex == list.size() - 1 ? 0 : currentIndex + 1);
					selectIndex(index);
				}

				if(keycode == Keys.LEFT){
					SoundManager.playSound("click");
					MenuPlaylist.previousSong();
				}

				if(keycode == Keys.RIGHT){
					SoundManager.playSound("click");
					MenuPlaylist.nextSong();
				}

				if(keycode == Keys.ENTER){
					if(currentIndex == 0){
						Instance.game.setScreen((sl!=null ? sl : (sl=new MapSelect(Instance.maps))));
					}

					if(currentIndex == 1) {
						optionsShowed = true;

						if(sTab.isShowed())
							sTab.hide();
						else
							sTab.show();
						/*Main.getInstance().setScreen(options);*/
					}

					if(currentIndex == 2){
						Gdx.app.exit();
					}
				}
				if(keycode == Keys.L)
					Instance.accountManager.loginGoogle();

				if(keycode == Keys.ESCAPE)
					escclick = true;
				return false;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if(keycode == Keys.ESCAPE){
					if(escclick == true) {
						Gdx.app.exit();
					}
					escclick = false;
				}
				return false;
			}
		});

		version = new Label("Build: " + Version.version, GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 8));
		version.pack();
		version.setPosition(5, stage.getHeight() - version.getHeight() - 5);
		stage.addActor(version);

		copyright = new Label("Hexagons! 2016 Written by Wieku and Magik6k", GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 10));
		copyright.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
				Gdx.net.openURI("https://hexagons.xyz/");
				return true;
			}
		});
		copyright.pack();
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		stage.addActor(copyright);

		Texture tex = new Texture(Gdx.files.internal("assets/hexlogobig.png"), true);
		tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		beatIHigh = new Image(tex);
		beatIHigh.setScaling(Scaling.fit);

		beatILow = new Image(tex);
		beatILow.setColor(1, 1, 1, 0.3f);
		beatILow.setScaling(Scaling.fit);

		stage.addActor(beatIHigh);
		stage.addActor(beatILow);


		music = new Table();
		music.setBackground(GUIHelper.getTxRegion(new Color(0.1f, 0.1f, 0.1f, 0.5f)));

		music.add(title = new Label("", GUIHelper.getLabelStyle(new Color(0xa0a0a0ff), 12))).pad(5).row();
		//music.add(bar = new ProgressBar(0f, 100f, 1f, false, GUIHelper.getProgressBarStyle(Color.DARK_GRAY, new Color(0x02eafaff), 10)));
		music.pack();

		stage.addActor(music);

		list.add(button = new MenuButton("Start"));
		list.add(button2 = new MenuButton("Options"));
		list.add(button3 = new MenuButton("Exit"));

		button.setBounds(stage.getWidth() - 328, 252, 512, 100);
		button2.setBounds(stage.getWidth() - 394, 142, 512, 100);
		button3.setBounds(stage.getWidth() - 460, 32, 512, 100);
		stage.addActor(button);
		stage.addActor(button2);
		stage.addActor(button3);
		stage.addActor(sTab);
		selectIndex(0);

		CurrentMap.reset();

		motdLabel = GUIHelper.text(MotdApi.instance.getMotd().text, Color.WHITE, 20);
		motdTable.add(motdLabel).center();
		motdTable.pack();
		motdTable.setWidth(stage.getWidth());
		motdTable.setPosition(0, 1f/3 * 768);
		stage.addActor(motdTable);
	}

	private boolean first = false;


	private Map currentPlaying;
	@Override
	public void show() {
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		Gdx.input.setInputProcessor(stage);
		Instance.setForegroundFps.accept(0);
		if(!first){
			MenuPlaylist.start();
			motdTable.setColor(1, 1, 1, 0f);
			motdAnimation = new Timeline().beginSequence().push(ActorAccessor.createFadeTableTween(motdTable, 2f, 0, 1f))
					.pushPause(5).push(ActorAccessor.createFadeTableTween(motdTable, 2f, 0, 0f)).end();
			motdAnimation.start(Instance.getAnimationManager());
			first = true;
			if(Instance.maps.isEmpty()) {
				CurrentMap.data.colors.add(new HColor(36f/255, 36f/255, 36f/255, 1f).addPulse(20f / 255, 20f / 255, 20f / 255, 0f));
				CurrentMap.data.colors.add(new HColor(20f / 255, 20f / 255, 20f / 255, 1f).addPulse(20f / 255, 20f / 255, 20f / 255, 0f));
			}
		}

		if(!Instance.maps.isEmpty()){
			CurrentMap.reset();
			MenuPlaylist.getCurrent().script.initColors();
			MenuPlaylist.getCurrent().script.onInit();
		}

		MenuPlaylist.setLooping(false);
		currentPlaying = MenuPlaylist.getCurrent();

	}

	private Timeline beatHigh;
	private Timeline beatLow;
	private float delta0 = 0;
	private float delta1 = 0;

	private boolean lo = false;

	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		camera.rotate(CurrentMap.data.rotationSpeed * 360f * delta);
		camera.update(delta);
		if((delta0 += delta)>=1f/60) {
			cd.update(delta);
			CurrentMap.data.walls.update(delta0);
			CurrentMap.data.skew = 1f;
			CurrentMap.setMinSkew(0.9999f);
			CurrentMap.setMaxSkew(1);
			CurrentMap.setSkewTime(1);

			if(currentPlaying != MenuPlaylist.getCurrent()){

				currentPlaying = MenuPlaylist.getCurrent();
				if(!Instance.maps.isEmpty()){
					CurrentMap.reset();
					MenuPlaylist.getCurrent().script.initColors();
					MenuPlaylist.getCurrent().script.onInit();
					camera.reset();
				}
			}

			if(MenuPlaylist.getCurrent() != null) {
				title.setText(MenuPlaylist.getCurrent().info.songAuthor + " - " + MenuPlaylist.getCurrent().info.songName);
				float[] cv = MenuPlaylist.getCurrentPlayer().getFFT();
				for(int i=0;i<40;i++) {
					dfg[i] = Math.max(2, Math.max(Math.min(MathUtils.log2(cv[i] * 2) * 50, dfg[i] + delta0 * 800), dfg[i] - delta0 * 300));
				}
			} else title.setText("No maps available");
			music.pack();
			
			motdTable.setWidth(stage.getWidth());
			motdTable.layout();
			//lel.set(Gdx.graphics.getWidth() - music.getWidth(), Gdx.graphics.getHeight() - music.getHeight());
			//float gx = (stage.getWidth()/1024f) * music.getWidth();
			//float gy = (stage.getHeight()/1024f) * music.getHeight();
			music.setPosition(stage.getWidth() - music.getWidth(), stage.getHeight() - music.getHeight());

			if(MenuPlaylist.getCurrentPlayer() != null) {
				if(!lo && MenuPlaylist.getCurrentPlayer().isOnset()/*beatLow == null || beatLow.isFinished()*/){
					lo=true;
					if(beatLow != null) beatLow.kill();
					beatLow = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 0.1f*(1.025f/beatILow.getScaleX()), 1.025f, 0))
							.push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 0.2f, 1f, 0)).end();
					beatLow.start(Instance.getAnimationManager());

					if(beatHigh != null) beatHigh.kill();

					beatHigh = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 0.1f/2*(0.96f/ beatIHigh.getScaleX()), 0.96f, 0))
							.push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 0.2f, 1f, 0)).end();
					beatHigh.start(Instance.getAnimationManager());

				}

				if(!MenuPlaylist.getCurrentPlayer().isOnset()) lo = false;

			} else {
				if(beatLow == null || beatLow.isFinished()) {
					if(beatLow != null) beatLow.kill();
					beatLow = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 0.1f*(1.025f/beatILow.getScaleX()), 1.025f, 0))
							.push(ActorAccessor.createSineTween(beatILow, ActorAccessor.SIZEC, 1f, 1f, 0)).end();
					beatLow.start(Instance.getAnimationManager());

					if(beatHigh != null) beatHigh.kill();

					beatHigh = new Timeline().beginSequence().push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 0.1f/2*(0.96f/ beatIHigh.getScaleX()), 0.96f, 0))
							.push(ActorAccessor.createSineTween(beatIHigh, ActorAccessor.SIZEC, 1f, 1f, 0)).end();
					beatHigh.start(Instance.getAnimationManager());
				}
			}

			delta0 = 0;
		}

		if((delta1 += delta)>=1f) {
			delta1=0;
		}

		blurEffect.bind();

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.identity();
		shapeRenderer.rotate(1, 0, 0, 90);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		mapRenderer.renderBackground(shapeRenderer, delta, true, 0);
		shapeRenderer.end();

		blurEffect.unbind();

		blurEffect.render(stage.getBatch());

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		shapeRenderer.identity();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		shapeRenderer.setColor(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, 0.1f);
		float g = stage.getHeight()/40f;
		for(int i=0;i<40;i++){
			shapeRenderer.rect(0, i*g, dfg[i], g-1);
		}
		shapeRenderer.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);

		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		version.setPosition(5, stage.getHeight() - version.getHeight() - 5);
		copyright.setPosition(stage.getWidth() - copyright.getWidth() - 5, 5);
		
		beatIHigh.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		beatIHigh.setPosition((401f / 1024) * stage.getWidth() - beatIHigh.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - beatIHigh.getHeight() / 2);

		beatILow.setSize((508f / 1024) * stage.getWidth(), (508f / 768) * stage.getHeight());
		beatILow.setPosition((401f / 1024) * stage.getWidth() - beatIHigh.getWidth() / 2, ((768f - 301f) / 768) * stage.getHeight() - beatIHigh.getHeight() / 2);


		button.setBounds(stage.getWidth() - 328 - (list.indexOf(button) == currentIndex ? 20 : 0), 252, 512, 100);
		button2.setBounds(stage.getWidth() - 394 - (list.indexOf(button2) == currentIndex ? 20 : 0), 142, 512, 100);
		button3.setBounds(stage.getWidth() - 460 - (list.indexOf(button3) == currentIndex ? 20 : 0), 32, 512, 100);

		blurEffect.resize(width, height);
		music.setPosition(stage.getWidth() - music.getWidth(), stage.getHeight() - music.getHeight());
		music.layout();
	}

	private void selectIndex(int index){
		if(currentIndex != -1){
			list.get(currentIndex).select(false);
			float x=(currentIndex==0?stage.getWidth() - 328:currentIndex==1?stage.getWidth() - 394:stage.getWidth() - 460);

			ActorAccessor.startTween(ActorAccessor.createCircleOutTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.5f, x , 0f));
		}
		currentIndex = index;
		SoundManager.playSound("click");
		list.get(currentIndex).select(true);
		float x=(currentIndex==0?stage.getWidth() - 328:currentIndex==1?stage.getWidth() - 394:stage.getWidth() - 460);
		ActorAccessor.startTween(ActorAccessor.createCircleOutTween(list.get(currentIndex), ActorAccessor.SLIDEX, 0.5f, x - 20, 0f));
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}
}
