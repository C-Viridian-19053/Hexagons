package me.wieku.hexagons.engine.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import me.wieku.hexagons.Main;
import me.wieku.hexagons.map.MapLoader;
import me.wieku.hexagons.utils.GUIHelper;
import me.wieku.hexagons.utils.Json;
import me.wieku.hexagons.utils.Utils;

import java.io.File;

public class Updater implements Screen {

	public static Updater instance = new Updater();
	private Stage stage;
	private Table loadTable;
	private Label status;
	private ProgressBar bar;
	private boolean ended = false;
	private String text = "Checking for updates...";

	private Updater(){

		stage = new Stage(new ScreenViewport());

		loadTable = new Table();
		loadTable.setBackground(GUIHelper.getTxRegion(new Color(0.1f, 0.1f, 0.1f, 1f)));
		loadTable.top();

		loadTable.add(status = new Label("Checking for updates...", GUIHelper.getLabelStyle(Color.WHITE, 10))).left().padLeft(5).padBottom(5).bottom().expand().row();
		loadTable.add(bar = new ProgressBar(0, 100, 1, false, GUIHelper.getProgressBarStyle(Color.DARK_GRAY, Color.WHITE,20))).fillX().center().bottom().height(20).colspan(2).row();

		stage.addActor(loadTable);
	}

	float delta1 = 1.0f;

	@Override
	public void render(float delta) {

		if((delta1+=delta) >= 1f/60){
			status.setText(text);
			delta1 = 0;
			if(ended){
				Main.getInstance().maps = MapLoader.load();
				Main.getInstance().setScreen(new Menu(Main.getInstance().maps));
				ended = false;
			}
		}

		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		loadTable.setBounds(0, 0, width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}


	@Override
	public void show() {

		new Thread(()-> {

			if(!Main.noupdate){

				try {

					String infoPath = Utils.getGameDir() + ".hexUpdateInfo";

					Utils.downloadFileWithProgressBar("https://upd.hexagons.wieku.me/update.json", infoPath, bar);

					String sha1 = Files.hash(new File(Utils.getGameFile()), Hashing.sha1()).toString();
					String build = Utils.getBuildNumber();

					Utils.sleep(500);

					Json json = Json.load(new File(infoPath));

					String newBuild = json.getString("latest");
					String newSha1 = json.getString(newBuild + ".sha1");

					if(build == null) {
						setStatus("Error while reading buildNumber! Downloading newest version!");
						downloadUpdate(json, newBuild);
					} else {
						if(newBuild.equals(build)) {
							if(sha1.equals(newSha1)) {
								setStatus("Game is up to date, starting game...");
							} else {
								setStatus("Game is up to date but file is changed/corrupted, downloading new file...");
								downloadUpdate(json, newBuild);
							}
						} else {
							setStatus("You have older version, downloading newest one...");
							downloadUpdate(json, newBuild);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					setStatus("Error downloading update, starting game...");
				}

			} else {
				setStatus("Downloading update skipped, starting game...");
			}

			bar.setValue(bar.getMaxValue());
			Utils.sleep(2000);
			ended = true;

		}).start();

	}

	private void downloadUpdate(Json json, String version) throws Exception{
		Utils.sleep(1000);

		String newPath = Utils.getGameFile()+".download";

		Utils.downloadFileWithProgressBar("https://upd.hexagons.wieku.me/"+json.getString(version+".file"), newPath, bar);

		String sha1 = Files.hash(new File(newPath), Hashing.sha1()).toString();

		if(!sha1.equals(json.getString(version + ".sha1"))){
			setStatus("Downloaded file is corrupted, downloading again...");
			downloadUpdate(json, version);
		}

		setStatus("Update finished! Moving file!");

		Utils.sleep(500);

		Files.move(new File(newPath), new File(Utils.getGameFile()));
		setStatus("File moved! Restarting game!");
		Utils.restartGame();
	}

	public void setStatus(String text){
		this.text = text.replaceAll("\\[", "\\[\\[").replaceAll("\\]", "\\]\\]");
	}

}