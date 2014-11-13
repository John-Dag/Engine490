package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import com.gdx.Weapons.RocketLauncher;
import com.gdx.Weapons.Sword;

public class GameScreen implements Screen {
	public static Vector2 center;
	public static boolean isConsoleActive;
	private final int ENTER = 13;
	private final int GRAVE = 96;
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
	
	//Initializes the console window and textfield.
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
		
		//Sends console commands to be parsed once the user hits enter
		consoleInputField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				if (key == ENTER) { //Enter key ASCII value is 13
					consoleVal = consoleInputField.getText();
					processConsoleInput(consoleVal);
				}
			}
		});
		
		//Filters keys that shouldn't be entered into the console textfield
		consoleInputField.setTextFieldFilter(new TextFieldFilter() {
			@Override
			public boolean acceptChar(TextField textField, char c) {
				if (c == GRAVE)
					return false;
				return true;
			}
		});
	}
	
	//Handles commands entered from the console
	public void processConsoleInput(String value) {
		if (value.contentEquals("kill")) {
			world.getPlayer().respawnPlayer(world.getPlayer());
			System.out.println("Player killed");
		}
		
		else if (value.contentEquals("noclip")) {
			if (world.getPlayer().isClipping()) {
				world.getPlayer().setClipping(false);
				System.out.println("Clipping disabled");
			}
			else {
				world.getPlayer().setClipping(true);
				System.out.println("Clipping enabled");
			}
		}
		
		else if (value.contentEquals("god")) {
			world.getPlayer().setHealth(100000000);
		}
		
		else if (value.startsWith("fog")) {
			float fogValue = parseConsoleValueFloat(value);
			if(fogValue >= 15 && fogValue <= 100 ) {
				world.getPlayer().camera.far = fogValue;
			} else {
				System.err.println("set fog to a range between 15 and 100");
			}
		}
		
		else if (value.startsWith("projectilespeed")) {
			float projectileValue = parseConsoleValueFloat(value);
			if (projectileValue >= 0.1 && projectileValue <= 100)
				if (world.getPlayer().getWeapon() != null)
					world.getPlayer().getWeapon().setProjectileSpeed(projectileValue);
			else
				System.err.println("set projectile speed to a range between 0.1 and 100");
		}

		else if (value.startsWith("loadlevel")) {
			String map = parseConsoleValueString(value);
			if (map.contentEquals("mymap")) {
				world.loadLevel(Assets.mymap);
			}
			else if (map.contentEquals("mymap2")) {
				world.loadLevel(Assets.mymap2);
			}
			else if (map.contentEquals("castle2")) {
				world.loadLevel(Assets.castle2);
			}
			else if (map.contentEquals("castle3")) {
				world.loadLevel(Assets.castle3);
			}
			else {
				System.err.println("Level doesn't exist");
			}			
		}
		
		else if (value.contains("playerweapon")) {
			String weapon = parseConsoleValueString(value);
			if (weapon.contentEquals("rocketlauncher")) {
				RocketLauncher launcher = (RocketLauncher) new RocketLauncher().spawn(world.getPlayer().getPosition());
				world.getPlayer().setWeapon(launcher);
			}
			
			else if (weapon.contentEquals("sword")) {
				Sword sword = (Sword) new Sword().spawn(world.getPlayer().getPosition());
				world.getPlayer().setWeapon(sword);
			}
		}
		
		else if (value.contains("wireframes")) {
			if (!World.isWireframeEnabled)
				World.isWireframeEnabled = true;
			else
				World.isWireframeEnabled = false;
		}

		else if (value.contains("exit")) {
			Gdx.app.exit();
		}
		
		else
			System.err.println("Unknown command");
		
		consoleVal = "";
		consoleInputField.setText("");
	}
	
	//Parse float value from console string
	public float parseConsoleValueFloat(String value) {
		float floatValue = 0;
		String[] tokens = value.split(" ");
		if (tokens.length > 1 && tokens.length < 3) {
			try {
				floatValue = Float.parseFloat(tokens[1]);
			} catch (Exception e) {
				System.err.println("Invalid argument");
			}
		}
		
		return floatValue;
	}
	
	//Parse string value from console string
	public String parseConsoleValueString(String value) {
		String temp = "";
		String[] tokens = value.split(" ");
		if (tokens.length > 1 && tokens.length < 3) {
			temp = tokens[1];
		}
		
		return temp;
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
		
		if (Gdx.input.isKeyJustPressed(Keys.GRAVE)) {
			if (!GameScreen.isConsoleActive) {
				GameScreen.isConsoleActive = true;
				world.player.setCurrentMovementSpeed(0);
			}
			else
				GameScreen.isConsoleActive = false;
		}

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
		Assets.mymap.dispose();
		Assets.mymap2.dispose();
		Assets.castle2.dispose();
		Assets.castle3.dispose();
		Assets.crosshair.dispose();
		Assets.darkWood.dispose();
		Assets.dungeon1.dispose();
		Assets.floor.dispose();
		Assets.hole.dispose();
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
