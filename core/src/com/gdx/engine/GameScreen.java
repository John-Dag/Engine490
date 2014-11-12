package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Player;
import com.gdx.Enemies.Zombie;

public class GameScreen implements Screen {
	public static Vector2 center;
	public static boolean isConsoleActive;
	private Game game;
	private World world;
	private Render renderer;
	private SpriteBatch spriteBatch;
	private BitmapFont bitmapFont;
	private Stage stage;
	private Skin skin;
	private Window consoleWindow;
	private TextField consoleInputField;
	private String consoleVal;
	
	public GameScreen(Game game) {
		this.game = game;
		this.world = new World();
		this.renderer = new Render(world);
		this.world.initializeEntities();
	
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		initializeConsoleWindow();
	}
	
	public void initializeConsoleWindow() {
		consoleVal = "";
		consoleWindow = new Window("Console", skin);
		consoleWindow.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
		consoleWindow.setPosition(0, Gdx.graphics.getHeight());
		consoleInputField = new TextField("", skin);
		consoleWindow.add(consoleInputField).width(consoleWindow.getWidth()).height(consoleWindow.getHeight());
		stage.setKeyboardFocus(consoleInputField);
		stage.addActor(consoleWindow);
		isConsoleActive = false;
		
		consoleInputField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				if (key == ']') {
					processConsoleInput(consoleVal);
				}
				else {
					consoleVal += key;
				}
			}
		});

		consoleInputField.setTextFieldFilter(new TextFieldFilter() {
			@Override
			public boolean acceptChar(TextField textField, char c) {
				if (c == '`' || c == ' ')
					return false;
				return true;
			}
		});
	}
	
	public void processConsoleInput(String value) {
		if (value.contentEquals("kill")) {
			world.player.respawnPlayer(world.getPlayer());
			System.out.println("Player killed");
		}
		else if (value.contentEquals("fly")) {
			if (world.player.isClipping()) {
				world.player.setClipping(false);
				System.out.println("Clipping disabled");
			}
			else {
				world.player.setClipping(true);
				System.out.println("Clipping enabled");
			}
		}
		else if (value.contentEquals("god")) {
			world.player.setHealth(100000);
		}
		else if (value.contentEquals("fog")) {
			if (world.getPlayer().camera.far == Player.FOG_DISTANCE) {
				world.getPlayer().camera.far = 100f;
				System.out.println("Distance fog disabled");
			}
			else {
				world.getPlayer().camera.far = Player.FOG_DISTANCE;
				System.out.println("Distance fog enabled");
			}
		}
		else if (value.contains("exit")) {
			Gdx.app.exit();
		}
		else
			System.err.println("Unknown command");
		consoleVal = "";
		consoleInputField.setText("");
	}

	@Override
	public void render(float delta) {	
		//Call the main renderer
		world.update(delta);
		renderer.RenderWorld(delta);
		
		//UI components are rendered here
		spriteBatch.begin();
		spriteBatch.draw(Assets.crosshair, center.x - 8, center.y - 8);
		renderFps();
		renderPos();
		renderTilePos();
		renderUI();
		spriteBatch.end();
		stage.act();
		stage.draw();
	}
	
	public void renderFps() {
		int fps = Gdx.graphics.getFramesPerSecond();
		bitmapFont.draw(spriteBatch, "FPS: " + fps, 10f, 530f);
		bitmapFont.draw(spriteBatch, "Rendered Models: " + Render.renderCount, 10f, 510f); 
	}
	
	// TODO: need to make sure that player position always matches camera position, then apply movement to player position, not just camera -Cory
	public void renderPos() {
		int posX = (int)world.getPlayer().camera.position.x;
		int posY = (int)world.getPlayer().camera.position.y;
		int posZ = (int)world.getPlayer().camera.position.z;
		bitmapFont.draw(spriteBatch,  "Position: " + posX + ", " + posY + ", " + posZ, 10f, 490f);
	}
	
	public void renderTilePos(){
		GridPoint2 tileIndex = world.getPlayer().getPlayerTileCoords();
		bitmapFont.draw(spriteBatch, "Tile (" + tileIndex.x + ", " + tileIndex.y +")", 10f, 470f);
	}
	
	public void renderUI() {
		bitmapFont.draw(spriteBatch, "Health: " + World.player.getHealth(), 0f, 20f);

		if (isConsoleActive && !consoleWindow.isVisible()) {
			consoleWindow.setVisible(true);
			Gdx.input.setInputProcessor(stage);
		}
		else if (!isConsoleActive && consoleWindow.isVisible()) {
			consoleWindow.setVisible(false);
			Gdx.input.setInputProcessor(null);
		}
	}

	@Override
	public void dispose() {
		game.dispose();
		Assets.manager.dispose();
		spriteBatch.dispose();
		World.particleManager.rocketEffect.dispose();
		World.particleManager.mistEffect.dispose();
		World.particleManager.torchEffect.dispose();
		World.particleManager.rocketExplosionEffect.dispose();
		stage.dispose();
		skin.dispose();
		Assets.castle.dispose();
		Assets.crosshair.dispose();
		Assets.darkWood.dispose();
		Assets.dungeon1.dispose();
		Assets.floor.dispose();
		Assets.hole.dispose();
		Assets.level.dispose();
		Assets.level2.dispose();
	}

	@Override
	public void resize(int width, int height) {
		center.set(width / 2, height / 2);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
