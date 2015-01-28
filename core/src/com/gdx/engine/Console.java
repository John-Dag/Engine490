package com.gdx.engine;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.gdx.FilterEffects.BlueScreenColorMultiplier;
import com.gdx.FilterEffects.Cartoon;
import com.gdx.FilterEffects.GreenScreenColorMultiplier;
import com.gdx.FilterEffects.InverseColor;
import com.gdx.FilterEffects.Lights;
import com.gdx.FilterEffects.Rainbow;
import com.gdx.FilterEffects.RedScreenColorMultiplier;
import com.gdx.FilterEffects.Sepia;
import com.gdx.Weapons.RocketLauncher;
import com.gdx.Weapons.Sword;
import com.gdx.engine.GameScreen.State;

public class Console {
	public static boolean isConsoleActive;
	private final int ENTER = 13;
	private final int GRAVE = 96;
	private BitmapFont bitmapFont;
	private Stage stage;
	private Skin skin;
	private Window consoleWindow;
	private TextField consoleInputField;
	private String consoleVal;
	private World world;
	private List<FilterEffect> filterEffects = new LinkedList<FilterEffect>();
	private int currentFilter = 0;
	
	public Console(World world) {
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.world = world;
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
		consoleWindow.setVisible(false);
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
		else if(value.toLowerCase().contains("nofx"))
		{
			world.setFilterEffect(null);
		}
	
		else if(value.toLowerCase().contains("fx+"))
		{
			if (filterEffects.size() > 0){
				currentFilter+=1;
				currentFilter=currentFilter%filterEffects.size();
				filterEffects.get(currentFilter).loadShaderProgram();
				filterEffects.get(currentFilter).initializeFrameBuffer();
				world.setFilterEffect(filterEffects.get(currentFilter));
			}
		}
		else if (value.toLowerCase().contains("fx-")) {
			if (filterEffects.size() > 0){
				currentFilter-=1;
				if(currentFilter<0)
					currentFilter=filterEffects.size()-1;
				filterEffects.get(currentFilter).loadShaderProgram();
				filterEffects.get(currentFilter).initializeFrameBuffer();
				world.setFilterEffect(filterEffects.get(currentFilter));
			}
		}
		
		else
			System.err.println("Unknown command");
		consoleVal = "";
		consoleInputField.setText("");
	}

	public void initializeFilterEffects() {
		filterEffects.add(new BlueScreenColorMultiplier());
		filterEffects.add(new RedScreenColorMultiplier());
		filterEffects.add(new GreenScreenColorMultiplier());
		filterEffects.add(new Rainbow());
		filterEffects.add(new InverseColor());
		filterEffects.add(new Sepia());
		filterEffects.add(new Lights());
		filterEffects.add(new Cartoon());
		
		for(FilterEffect e:filterEffects)
		{
			e.dispose();
		}
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
	
	public void update() {
		if (Gdx.input.isKeyJustPressed(Keys.GRAVE)) {
			if (!isConsoleActive) {
				isConsoleActive = true;
				GameScreen.state = State.Paused;
				consoleWindow.setVisible(true);
				Gdx.input.setInputProcessor(stage);
			}
			else {
				isConsoleActive = false;
				GameScreen.state = State.Running;
				consoleWindow.setVisible(false);
				Gdx.input.setInputProcessor(null);
			}
		}
	}
	
	public Window getConsoleWindow() {
		return consoleWindow;
	}
	
	public Stage getStage() {
		return stage;
	}
}