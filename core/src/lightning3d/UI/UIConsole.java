package lightning3d.UI;

import java.util.LinkedList;
import java.util.List;

import lightning3d.DynamicEntities.Enemy;
import lightning3d.DynamicEntities.Player;
import lightning3d.DynamicEntities.Weapon;
import lightning3d.Engine.Assets;
import lightning3d.Engine.Entity;
import lightning3d.Engine.FilterEffect;
import lightning3d.Engine.GameScreen;
import lightning3d.Engine.World;
import lightning3d.Engine.GameScreen.State;
import lightning3d.FilterEffects.BlueScreenColorMultiplier;
import lightning3d.FilterEffects.Cartoon;
import lightning3d.FilterEffects.GreenScreenColorMultiplier;
import lightning3d.FilterEffects.InverseColor;
import lightning3d.FilterEffects.Lights;
import lightning3d.FilterEffects.Rainbow;
import lightning3d.FilterEffects.RedScreenColorMultiplier;
import lightning3d.FilterEffects.Sepia;
import lightning3d.Inventory.Inventory;
import lightning3d.Items.HealthPotion;
import lightning3d.Shaders.BlackHole;
import lightning3d.Shaders.ColorMultiplierEntityShader;
import lightning3d.Shaders.EntityRainbow;
import lightning3d.Shaders.FireBallShader;
import lightning3d.Shaders.LavaShader;
import lightning3d.Weapons.RocketLauncher;
import lightning3d.Weapons.Sword;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Array;

public class UIConsole extends UIBase {
	public static final int ENTER = 13;
	public static final int GRAVE = 96;
	public static boolean isConsoleActive;
	private BitmapFont bitmapFont;
	private Skin skin;
	private TextField consoleInputField;
	private String consoleVal;
	private World world;
	private List<FilterEffect> filterEffects = new LinkedList<FilterEffect>();
	private int currentFilter = 0;
	private Array<UIConsoleCommand> commands;
	
	public UIConsole(Stage stage, World world) {
		super(stage);
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.world = world;
		this.commands = new Array<UIConsoleCommand>();
	}
	
	/***
	 * Initializes the console window
	 * @param filter TextFieldFilter
	 * @param processInputKey Key to trigger processing of console input
	 * @param showConsoleKey Key to show/hide the console that is specified in the stages input listener.
	 */
	
	public void initializeConsoleWindow(TextFieldFilter filter, final int processInputKey, final int showConsoleKey) {
		consoleVal = "";
		this.setWindow(new Window("Console", skin));
		this.getWindow().setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3);
		this.getWindow().setPosition(0, Gdx.graphics.getHeight());
		consoleInputField = new TextField("", skin);
		this.getWindow().add(consoleInputField).width(this.getWindow().getWidth()).height(this.getWindow().getHeight());
		this.getStage().addActor(this.getWindow());
		this.getWindow().setVisible(false);
		
		//Sends console commands to be parsed once the user hits enter
		consoleInputField.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				if (key == processInputKey) { //Enter key ASCII value is 13
					consoleVal = consoleInputField.getText();
					processConsoleInput(consoleVal);
				}
			}
		});
		
		//Filters keys that shouldn't be entered into the console textfield
		if (filter != null)
			consoleInputField.setTextFieldFilter(filter);
		else {
			//Create a default filter
			consoleInputField.setTextFieldFilter(new TextFieldFilter() {
				@Override
				public boolean acceptChar(TextField textField, char c) {
					if (c == showConsoleKey)
						return false;
					return true;
				}
			});
		}
	}
	
	private void processConsoleInput(String value) {
		boolean customCommand = false;
		
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
			
			else if (weapon.contentEquals("none")) {
				Weapon none = new Weapon();
				world.getPlayer().setWeapon(none);
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
		
		else if (value.toLowerCase().contains("bulletwires")) {
			world.bulletDebugDrawEnabled=!world.bulletDebugDrawEnabled;
		}
		else if (value.toLowerCase().contains("levelwire")) {
			world.bulletDebugDrawMeshLevelWiresEnabled=!world.bulletDebugDrawMeshLevelWiresEnabled;
		}
		else if (value.toLowerCase().contains("efx1")) {
			LavaShader shdr=new LavaShader();
			for(Entity e:Entity.entityInstances)
			{
				if(e instanceof Enemy||e instanceof Player)
				e.setShader(shdr);
			}
		}
		else if (value.toLowerCase().contains("efx3")) {
			BlackHole shdr=new BlackHole();
			for(Entity e:Entity.entityInstances)
			{
				if(e instanceof Enemy||e instanceof Player)
				e.setShader(shdr);
			}
		}
		else if (value.toLowerCase().contains("efx4")) {
			EntityRainbow shdr=new EntityRainbow();
			for(Entity e:Entity.entityInstances)
			{
				if(e instanceof Enemy||e instanceof Player)
				e.setShader(shdr);
			}
		}
		else if (value.toLowerCase().contains("efx0")) {
			
			for(Entity e:Entity.entityInstances)
			{
				if(e instanceof Enemy||e instanceof Player){
				ColorMultiplierEntityShader es=new ColorMultiplierEntityShader();
							es.multiplier.y=(float)Math.random();
							es.multiplier.x=(float)Math.random();
							es.multiplier.z=(float)Math.random();
				e.setShader(es);
				}
			}
		}
		else if (value.toLowerCase().contains("efx2")) {
			
			for(Entity e:Entity.entityInstances)
			{
				if(e instanceof Enemy||e instanceof Player)
				e.setShader(new FireBallShader());
			}
		}
		
		else if (value.toLowerCase().startsWith("givehealthpotion")) {
			int itemNum = parseConsoleValueInt(value);
			Inventory inv = world.getPlayer().getInventory();
			if (itemNum > 1)
				for (int i = 0; i < itemNum; i++)
					inv.store(new HealthPotion(), 1);
			else
				inv.store(new HealthPotion(), 1);
		}
		
		else if (commands.size > 0) {
			//Check all custom commands
			for (int i = 0; i < commands.size; i++) {
				if (commands.get(i) != null && value.toLowerCase().startsWith(commands.get(i).getCommand())) {
					customCommand = true;
					commands.get(i).triggerCommand(value);
				}
			}
			
			if (customCommand != true)
				System.err.println("Unknown command");
		}
		
		consoleVal = "";
		consoleInputField.setText("");
	}

	/***
	 * Initializes shader filter effects that can be accessed through the console (Use command fx+, fx-).
	 */
	
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
	public static float parseConsoleValueFloat(String value) {
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
	
	public static int parseConsoleValueInt(String value) {
		int intValue = 0;
		String[] tokens = value.split(" ");
		if (tokens.length > 1 && tokens.length < 3) {
			try {
				intValue = Integer.parseInt(tokens[1]);
			} catch (Exception e) {
				System.err.println("Invalid argument");
			}
		}
		
		return intValue;
	}
	
	//Parse string value from console string
	public static String parseConsoleValueString(String value) {
		String temp = "";
		String[] tokens = value.split(" ");
		if (tokens.length > 1 && tokens.length < 3) {
			temp = tokens[1];
		}
		
		return temp;
	}
	
	/***
	 * Adds an input listener to the console. 
	 * @param key Key used to show/hide the console. 
	 * @param actorIndex The actor index of the console within the stage (Default is 0).
	 */
	
	public void addConsoleInputListener(final int key, final int actorIndex) {
		final Stage stage = this.getStage();
		final Actor actor = this.getStage().getRoot().findActor("console");
		this.getStage().setKeyboardFocus(actor);
		
		actor.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keyCode) {
				if (keyCode == key && actor.isVisible()) {
					actor.setVisible(false);
					GameScreen.state = State.Running;
				}
				else if (keyCode == key && !actor.isVisible()) {
					actor.setVisible(true);
					GameScreen.state = State.Paused;
					stage.setKeyboardFocus(consoleInputField);
				}
				
				return true;
			}
		});
	}
	
	@Override
	public void show() {
		if (!this.getWindow().isVisible()) {
			this.getWindow().setVisible(true);
			this.consoleInputField.setDisabled(false);
			UIBase.uiSelected = true;
			this.getStage().setKeyboardFocus(consoleInputField);
			if (GameScreen.mode != GameScreen.Mode.Server)
				GameScreen.state = State.Paused;
			else
				this.getStage().setKeyboardFocus(consoleInputField);
		}
		else {
			this.getWindow().setVisible(false);
			UIBase.uiSelected = false;
			this.consoleInputField.setDisabled(true);
			if (GameScreen.mode != GameScreen.Mode.Server)
				GameScreen.state = State.Running;
		}
	}

	public Array<UIConsoleCommand> getCommands() {
		return commands;
	}

	public void setCommands(Array<UIConsoleCommand> commands) {
		this.commands = commands;
	}
}
