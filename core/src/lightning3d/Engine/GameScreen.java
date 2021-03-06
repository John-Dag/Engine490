package lightning3d.Engine;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import lightning3d.Commands.FireCommand;
import lightning3d.Commands.MatchCommand;
import lightning3d.Commands.ReconnectCommand;
import lightning3d.Network.Net;
import lightning3d.Network.NetClient;
import lightning3d.Network.NetServer;
import lightning3d.Network.NetStatField;
import lightning3d.Network.NetWorld;
import lightning3d.UI.UIBase;
import lightning3d.UI.UIChat;
import lightning3d.UI.UIConsole;
import lightning3d.UI.UIForm;
import lightning3d.UI.UIInventory;
import lightning3d.UI.UIMap;
import lightning3d.UI.UIMenu;
import lightning3d.UI.UIOverlay;
import lightning3d.UI.UIVirtualJoystick;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	public static Vector2 center;
	public static State state;
	//public static State mode;
	public static Mode mode;
	public static NetClient client;
	private Game game;
	private Render renderer;
	private SpriteBatch spriteBatch;
	private BitmapFont bitmapFont;
	private Stage stage;
	public static Skin skin;
	private UIConsole console;
	private UIBase base;
	private UIMenu menu;
	private UIMenu networkMenu;
	private UIOverlay overlay;
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private UIMap map;
	private UIChat chat;
	private UIInventory inventory;
	private WorldInputProcessor screenInputProcessor;
	private UIVirtualJoystick virtualJoystick;
	private UIForm form, statForm;
	private NetServer server;
	//private NetWorld world;
	private TextButtonStyle style;
	private boolean uiGenerated = false;
	private World world;
	
	public enum State {
		Running, Paused
	}
	
	public enum Mode {
		Server, Client, Offline
	}

	public GameScreen(Game game, boolean consoleActive) {
		this.game = game;
	
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		style = new TextButtonStyle();
		style.font = bitmapFont;
		state = State.Paused;
		stage = new Stage() {
			@Override
			public boolean keyDown(int keyCode) {
				if (keyCode == Keys.GRAVE) {
					console.show();
				}
				
				else if (keyCode == Keys.I && !UIBase.uiSelected) {
					inventory.show();
				}
				
				else if (keyCode == Keys.ALT_RIGHT) {
					chat.show();
				}
				
				else if (keyCode == Keys.K && !UIBase.uiSelected && mode == mode.Client) {
					statForm.show();
				}
				
				return false;
			}
		};
		base = new UIBase(stage);
		Gdx.input.setInputProcessor(stage);
		createNetworkMenu();
	}
	
	public void generateUI(World world) {
		screenInputProcessor = new WorldInputProcessor(world);
		
		console = new UIConsole(stage, world);
		console.initializeConsoleWindow(null, UIConsole.ENTER, UIConsole.GRAVE);
		console.initializeFilterEffects();
		console.setWindowOpacity(0.5f);
		
		base.setWorld(world);
		Array<TextButton> buttons = new Array<TextButton>();
		
		final TextButton button1 = new TextButton("Pause", style);
		button1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (GameScreen.state == State.Running) {
					button1.setText("Resume");
					GameScreen.state = State.Paused;
				}
				else if (GameScreen.state == State.Paused) {
					button1.setText("Pause");
					GameScreen.state = State.Running;
				}
			}
		});
		buttons.add(button1);
		TextButton button2 = new TextButton("Exit", style);
		button2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		buttons.add(button2);
		menu = new UIMenu(stage, skin, buttons, "Engine 490", 0, 0);
		menu.generateVerticalMenu(10);
		inventory = new UIInventory(stage, world.getPlayer().getInventory(), skin);
		overlay = new UIOverlay(stage, spriteBatch, bitmapFont);
		overlay.addCrosshair(Assets.crosshair, center);
		overlay.setCursorImage("cursor.png", 6, 3);
		map = new UIMap(world, stage, spriteBatch, skin, Color.BLACK, 5, 5, 3, 0);
		map.generateMap(world.getMeshLevel().getLevelArray(), world.getMeshLevel().getMapMaterials());
		map.setWindowOpacity(0.5f);
		TextureRegionDrawable barTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("barRed2.png"))));
		ProgressBarStyle barStyle = new ProgressBarStyle(skin.newDrawable("white", Color.RED), barTexture);
		ProgressBar bar = new ProgressBar(world.getPlayer().MIN_HEALTH, world.getPlayer().getHealth(), 1f, false, barStyle);
		overlay.addProgressBarWidget(bar, true, world.getPlayer().getHealth());
		chat = new UIChat(stage, skin, "Chat");
		chat.addChatWidget(300, 200, Gdx.graphics.getWidth() - chat.getWindow().getWidth(), 0, 0.9f);
		chat.addChatWidgetListeners();
		chat.getWindow().setColor(Color.GRAY);
		chat.setWindowOpacity(0.7f);
		chat.getWindow().setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("logo.png")))));
		virtualJoystick = new UIVirtualJoystick(stage, Assets.joystickBackground, 
												Assets.joystickKnob, 1920/2 - 100, 0, 100, 100);
		//virtualJoystick.addVirtualJoystick(world.getPlayer(), world.getPlayer().camera, 8.0f);
		multiplexer.addProcessor(screenInputProcessor);
		multiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(multiplexer);
		FireCommand command = new FireCommand("fireweapon", world);
		console.getCommands().add(command);
		ReconnectCommand commandB = new ReconnectCommand("reconnect", world);
		console.getCommands().add(commandB);
		MatchCommand commandC = new MatchCommand("startmatch", world);
		console.getCommands().add(commandC);
		uiGenerated = true;
	}
	
	public void generateMultiplayerClient() {
		mode = Mode.Client;
		this.world = new NetWorld();
		this.renderer = new Render(world);
		this.world.initializeEntities();
		state = State.Running;
		networkMenu.getTable().setVisible(false);
		generateUI(world);
		startClient();
	}
	
	public void generateMultiplayerServer() {
		mode = Mode.Server;
		this.world = new NetWorld();
		this.renderer = new Render(world);
		this.world.initializeEntities();
		state = State.Running;
		networkMenu.getTable().setVisible(false);
		generateUI(world);
		chat.getTextArea().setText("Hosting at " + Net.serverIP.toString());
		startServer();
	}
	
	public void generateOffline() {
		mode = Mode.Offline;
		this.world = new World();
		world.loadOfflineWorld(Assets.rift, true);
		this.renderer = new Render(world);
		this.world.initializeEntities();
		state = State.Running;
		networkMenu.getTable().setVisible(false);
		generateUI(world);
	}
	
	// Set the IP in Net class
	public void createNetworkMenu() {
		form = new UIForm(stage, skin, "Name/IP");
		form.generateWindow(center.x - 70, center.y + 60, 150, 150, false);
		form.addTextField("Name", 0, 100, 150, 25);
		//form.addTextField("192.168.0.6", 0, 50, 150, 25);
		form.addTextField(Net.serverIP, 0, 50, 150, 25);
		UIBase.uiSelected = true;

		form.getFields().get(0).addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				form.getFields().get(0).setText("");
			}
		});
		
		form.getFields().get(1).addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				form.getFields().get(1).setText("");
			}
		});
		
		Array<TextButton> buttons2 = new Array<TextButton>();
		final TextButton button3 = new TextButton("Join", style);
		button3.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Net.name = form.getFields().get(0).getText();
				Net.serverIP = form.getFields().get(1).getText();
				form.getWindow().setVisible(false);
				generateMultiplayerClient();
			}
		});
		
		buttons2.add(button3);
		TextButton button4 = new TextButton("Host", style);
		button4.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Net.name = "Server";
				try {
					Net.serverIP = Inet4Address.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				form.getWindow().setVisible(false);
				generateMultiplayerServer();
			}
		});
		
		buttons2.add(button4);
		TextButton button5 = new TextButton("Offline", style);
		button5.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				form.getWindow().setVisible(false);
				generateOffline();
			}
		});
		
		buttons2.add(button5);
		networkMenu = new UIMenu(stage, skin, buttons2, "FPS Demo", 0, 0);
		networkMenu.generateVerticalMenu(10);
		networkMenu.getTable().setVisible(true);
		state = State.Paused;
	}
	
	public void startServer() {
		try {
			server = new NetServer(world);
			world.setServer(server);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startClient() {
		try {
			client = new NetClient(world, this);
			if (!client.getClient().isConnected()) {
				System.err.println("startClient(): Failed to connect to host. Exiting");
				Gdx.app.exit();
			}
			
			world.setClient(client);
			statForm = new UIForm(stage, skin, "Stats");
			statForm.generateWindow(center.x  / 2, center.y + 300, 300, 40, true);
			statForm.getWindow().setVisible(false);
			statForm.setWindowOpacity(.5f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(float delta) {	
		//Call the main renderer
		switch (state) {
			case Running:
				world.update(delta);
				if (mode == Mode.Server) {
					server.serverUpdate();
				}
				if (mode == Mode.Client) {
					client.clientUpdate();
				}
				break;
			case Paused:
				break;
		}
		
		if (mode == Mode.Server) {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			base.render(delta);
			return;
		}
		
		if (uiGenerated) {
			renderer.RenderWorld(delta);
			
			//UI components are rendered here
			spriteBatch.begin();
			overlay.renderFPS(delta, -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			overlay.renderPosition(world.getPlayer().getPosition(), delta, -Gdx.graphics.getWidth() / 2, 
					               Gdx.graphics.getHeight() / 2 - 20f);
			overlay.renderTilePosition(world.getPlayer().getPlayerTileCoords(), delta, 
					                   -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 40f);
			overlay.renderModelCount(-Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 60f);
			spriteBatch.end();
			base.render(delta);
			overlay.updateWidgets(delta, world.getPlayer().getHealth());
			map.renderIndicator(delta, world.getPlayer().getPosition());
		}
		
		else
			base.render(delta);
		//virtualJoystick.render(delta);
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
		Assets.manager.dispose();
		this.world.dispose();
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

	public UIBase getBase() {
		return base;
	}

	public void setBase(UIBase base) {
		this.base = base;
	}

	public UIChat getChat() {
		return chat;
	}

	public void setChat(UIChat chat) {
		this.chat = chat;
	}

	public UIForm getStatForm() {
		return statForm;
	}

	public void setStatForm(UIForm statForm) {
		this.statForm = statForm;
	}
}
