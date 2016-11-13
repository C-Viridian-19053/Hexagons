package xyz.hexagons.client.menu.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.Version;
import xyz.hexagons.client.api.CurrentMap;
import xyz.hexagons.client.audio.MenuPlaylist;
import xyz.hexagons.client.audio.SoundManager;
import xyz.hexagons.client.engine.Game;
import xyz.hexagons.client.menu.widgets.MenuMap;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.engine.camera.SkewCamera;
import xyz.hexagons.client.engine.render.MapRenderer;
import xyz.hexagons.client.engine.render.ObjRender;
import xyz.hexagons.client.map.Map;
import xyz.hexagons.client.utils.GUIHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Sebastian Krajewski on 04.04.15.
 */
public class MapSelect implements Screen {

	ArrayList<Map> maps;
	Stage stage;

	Table info;
	Label number, name, description, author, music;
	Game game;

	Table table;

	SkewCamera camera = new SkewCamera();
	ObjRender shapeRenderer;

	MapRenderer mapRenderer = new MapRenderer();

	public ArrayList<MenuMap> mapButtons = new ArrayList<>();
	public ScrollPane scrollPane;

	public static int mapIndex = 0;

	public static MapSelect instance;

	public MapSelect(ArrayList<Map> maps){
		this.maps = maps;
		maps.sort((e1, e2)->e1.info.name.compareTo(e2.info.name));
		instance = this;
		shapeRenderer = new ObjRender();

		stage = new Stage(new ExtendViewport(1024, 768));

		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage.addListener(new InputListener(){

			@Override
			public boolean keyDown(InputEvent event, int keycode) {

				if(!maps.isEmpty()){

					if(keycode == Keys.UP){

						mapButtons.get(mapIndex).check(false);

						if(--mapIndex < 0) mapIndex = maps.size()-1;
						selectIndex(mapIndex);
						mapButtons.get(mapIndex).check(true);

						MenuMap ms = mapButtons.get(mapIndex);
						scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==maps.size()-1?-1:1), ms.getWidth(), ms.getHeight());

						mapButtons.forEach(MenuMap::update);
					}

					if(keycode == Keys.DOWN){

						mapButtons.get(mapIndex).check(false);
						if(++mapIndex > maps.size()-1) mapIndex = 0;
						selectIndex(mapIndex);
						mapButtons.get(mapIndex).check(true);

						MenuMap ms = mapButtons.get(mapIndex);
						scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==0?1:-1), ms.getWidth(), ms.getHeight());

						mapButtons.forEach(MenuMap::update);
					}

					//System.out.println(mapButtons.get(mapIndex).getY());

					if(keycode == Keys.DOWN || keycode == Keys.UP) {
						try {
							Method method = scrollPane.getClass().getDeclaredMethod("resetFade");
							method.setAccessible(true);
							method.invoke(scrollPane);
						} catch (Exception e) {}
					}

					if(keycode == Keys.ENTER){
						SoundManager.playSound("beep");
						Gdx.input.setInputProcessor(null);
						//audioPlayer.pause();
						MenuPlaylist.pause();
						Instance.game.setScreen(game = new Game(maps.get(mapIndex)));
					}

				}



				if(keycode == Keys.ESCAPE){
					SoundManager.playSound("beep");
					Instance.game.setScreen(MainMenu.instance);
				}

				return false;
			}
		});

		stage.addListener(new ActorGestureListener(){
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				Vector2 r = scrollPane.screenToLocalCoordinates(new Vector2(x, Gdx.graphics.getHeight()-y));
				for(MenuMap m : mapButtons) {
					if(m.isIn((int)r.x, (int)r.y)) {

						System.out.println(m.map.info.name);
						if(mapButtons.indexOf(m) != mapIndex) {
							if(mapButtons.indexOf(m) > mapIndex){

								mapButtons.get(mapIndex).check(false);

								mapIndex = mapButtons.indexOf(m);
								selectIndex(mapIndex);
								mapButtons.get(mapIndex).check(true);

								MenuMap ms = mapButtons.get(mapIndex);
								scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==maps.size()-1?-1:1), ms.getWidth(), ms.getHeight());

								mapButtons.forEach(MenuMap::update);
							}

							if(mapButtons.indexOf(m) < mapIndex){

								mapButtons.get(mapIndex).check(false);
								mapIndex = mapButtons.indexOf(m);
								selectIndex(mapIndex);
								mapButtons.get(mapIndex).check(true);

								MenuMap ms = mapButtons.get(mapIndex);
								scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==0?1:-1), ms.getWidth(), ms.getHeight());

								mapButtons.forEach(MenuMap::update);
							}
						} else {
							SoundManager.playSound("beep");
							Gdx.input.setInputProcessor(null);
							//audioPlayer.pause();
							MenuPlaylist.pause();
							Instance.game.setScreen(game = new Game(maps.get(mapIndex)));
						}

						if(count == 2) {
							SoundManager.playSound("beep");
							Gdx.input.setInputProcessor(null);
							//audioPlayer.pause();
							MenuPlaylist.pause();
							Instance.game.setScreen(game = new Game(maps.get(mapIndex)));
						}

						return;
					}

				}
			}
		});
		info = new Table();
		info.add(number = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(name = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(description = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(author = new Label("", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();
		info.add(music = new Label("No maps available!", GUIHelper.getLabelStyle(Color.WHITE, 8))).left().row();

		info.pack();

		info.setWidth(Gdx.graphics.getWidth()/3);
		info.setPosition(5, stage.getHeight()-5-info.getHeight());

		stage.addActor(info);
		
		table = new Table();

		for(Map map : maps){
			MenuMap mp = new MenuMap(map);
			mapButtons.add(mp);
			table.add(mp).left().center().fillX().pad(1).row();
		}

		table.pack();
		table.setHeight(Gdx.graphics.getHeight()-200);

		scrollPane = new ScrollPane(table, GUIHelper.getScrollPaneStyle(Color.WHITE));
		scrollPane.setupFadeScrollBars(1f, 1f);
		scrollPane.setSmoothScrolling(true);
		scrollPane.setVelocityY(0.1f);
		scrollPane.setScrollingDisabled(true, false);

		((Table) scrollPane.getChildren().get(0)).center().left();
		scrollPane.setCancelTouchFocus(true);

		stage.addActor(scrollPane);
		
		//if(Settings.instance.graphics.fullscreen)
		//	Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode());
	}

	float delta0 = 0;

	Color tmpC = new Color();

	boolean showed = false;
	@Override
	public void render(float delta) {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		updateSkew(delta);
		camera.rotate(CurrentMap.data.rotationSpeed * 360f * delta);
		camera.update(delta);

		if((delta0 += delta)>=1f/60){
			CurrentMap.data.walls.update(delta0);

			MenuPlaylist.update(delta0);

			CurrentMap.setMinSkew(0.9999f);
			CurrentMap.setMaxSkew(1);
			CurrentMap.setSkewTime(1);

			tmpC.set(CurrentMap.data.walls.r, CurrentMap.data.walls.g, CurrentMap.data.walls.b, CurrentMap.data.walls.a);

			number.getStyle().fontColor = tmpC;
			name.getStyle().fontColor = tmpC;
			description.getStyle().fontColor = tmpC;
			author.getStyle().fontColor = tmpC;
			music.getStyle().fontColor = tmpC;

			delta0 = 0;
		}

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		mapRenderer.renderBackground(shapeRenderer, delta, true, 0);
		shapeRenderer.end();

		scrollPane.setBounds(stage.getWidth()-Math.max(468, stage.getWidth()/3), 100, Math.max(468, stage.getWidth()/3), stage.getHeight()-200);
		scrollPane.layout();

		if(!showed) {
			MenuMap ms = mapButtons.get(mapIndex);

			if(ms.getY()+ms.getHeight()/2<scrollPane.getHeight()/2) {
				scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==0?1:-1), ms.getWidth(), ms.getHeight());
			} else if((table.getHeight()-ms.getY()-ms.getHeight()/2) < scrollPane.getHeight()/2) {
				scrollPane.scrollTo(0, ms.getY()+(scrollPane.getHeight()/2-ms.getHeight()/2)*(mapIndex==maps.size()-1?-1:1), ms.getWidth(), ms.getHeight());
			}
			showed = true;
		}

		stage.act(delta);
		stage.draw();
	}

	int inc;
	float delta2;
	public void updateSkew(float delta) {
		inc = (delta2 == 0 ? 1 : (delta2 == CurrentMap.data.skewTime ? -1 : inc));
		delta2 += delta * inc;
		delta2 = Math.min(CurrentMap.data.skewTime, Math.max(delta2, 0));
		float percent = delta2 / CurrentMap.data.skewTime;
		CurrentMap.data.skew = CurrentMap.data.minSkew + (CurrentMap.data.maxSkew - CurrentMap.data.minSkew) * percent;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		info.setWidth(Gdx.graphics.getWidth()/3);
		scrollPane.setBounds(stage.getWidth()-Math.max(468, stage.getWidth()/3)-15, 0, Math.max(468, stage.getWidth()/3)+15, stage.getHeight());
		scrollPane.layout();
		mapButtons.forEach(e->{e.setX(0);e.update();});

		info.pack();
		info.setPosition(5, stage.getHeight()-5-info.getHeight());
	}

	@Override
	public void show() {

		Instance.setForegroundFps.accept(120);
		Gdx.graphics.setTitle("Hexagons! " + Version.version);
		selectIndex(mapIndex= Instance.maps.indexOf(MenuPlaylist.getCurrent()));

		if(game != null){
			MenuPlaylist.play();
			MenuPlaylist.setPosition(game.exitPosition);
			game = null;
		}
		MenuPlaylist.setLooping(true);
		if(!mapButtons.isEmpty()){
			for(int i=0; i<mapButtons.size();i++)mapButtons.get(i).check(i==mapIndex);
		}
		mapButtons.forEach(MenuMap::update);

		scrollPane.setBounds(Gdx.graphics.getWidth()-Math.max(468, Gdx.graphics.getWidth()/3), 100, Math.max(468, Gdx.graphics.getWidth()/3), Gdx.graphics.getHeight()-200);
		scrollPane.layout();

		table.setHeight(Gdx.graphics.getHeight()-200);
		table.layout();
		showed = false;
		Gdx.input.setInputProcessor(stage);

		//System.out.println("Map selection screen showed up");
	}


	public void selectIndex(int index){
		if(maps.isEmpty()){
			number.setText("");
			name.setText("");
			description.setText("");
			author.setText("");
			music.setText("No maps available!");
			info.pack();
			info.setPosition(5, Gdx.graphics.getHeight()-5-info.getHeight());
		} else {
			SoundManager.playSound("click");
			Map map = maps.get(index);
			CurrentMap.reset();
			maps.get(mapIndex).script.initColors();
			maps.get(mapIndex).script.onInit();
			camera.reset();

			if(MenuPlaylist.getCurrent() == null || !MenuPlaylist.getCurrent().equals(map)){
				MenuPlaylist.replaceCurrent(map);
				MenuPlaylist.skipToPreview();
				MenuPlaylist.setVolume(((float) Settings.instance.audio.masterVolume * (float) Settings.instance.audio.menuMusicVolume) / 10000f);
			}

			number.setText("[" + (index + 1) + "/" + maps.size() + "] Pack: " + map.info.pack);
			name.setText(map.info.name);
			description.setText(map.info.description);
			author.setText("Author: " + map.info.author);
			music.setText("Music: " + map.info.songName + " by " + map.info.songAuthor);
			info.pack();
			info.setPosition(5, Gdx.graphics.getHeight()-5-info.getHeight());
		}

	}

	public static MapSelect getInstance() {
		return instance;
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