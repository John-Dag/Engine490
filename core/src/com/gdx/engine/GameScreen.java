package com.gdx.engine;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.gdx.Network.NetClient;
import com.gdx.Network.NetServer;
import com.gdx.Network.NetWorld;
import com.gdx.UI.UIChat;
import com.gdx.UI.UIConsole;
import com.gdx.UI.UIBase;
import com.gdx.UI.UIGrid;
import com.gdx.UI.UIMap;
import com.gdx.UI.UIMenu;
import com.gdx.UI.UIOverlay;
import com.gdx.UI.UIForm;
import com.gdx.UI.UIVirtualJoystick;

public class GameScreen implements Screen {
	public static Vector2 center;
	public static State state;
	public static State mode;
	public static NetClient client;
	private Game game;
	private Render renderer;
	private SpriteBatch spriteBatch;
	private BitmapFont bitmapFont;
	private Stage stage;
	private Skin skin;
	private UIConsole console;
	private UIBase base;
	private UIMenu menu;
	private UIMenu networkMenu;
	private UIOverlay overlay;
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private UIMap map;
	private UIChat chat;
	private UIGrid grid;
	private WorldInputProcessor screenInputProcessor;
	private UIVirtualJoystick virtualJoystick;
	private UIForm form;
	private NetServer server;
	private NetWorld world;
	private TextButtonStyle style;
	private boolean uiGenerated = false;
	private World offlineWorld;
	
	public enum State {
		Running, Paused, Server, Client, Offline
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
					grid.show();
				}
				
				else if (keyCode == Keys.ALT_LEFT) {
					chat.activateChatField();
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
		console.initializeConsoleWindow();
		console.initializeFilterEffects();
		
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
		grid = new UIGrid(stage, skin, Color.GREEN, "Inventory", Assets.gridslot);
		grid.generateGrid(Align.bottom, 30, 30, 5, 5, 3);
		grid.setWindowSize(300, 300);
		overlay = new UIOverlay(stage, spriteBatch, bitmapFont);
		overlay.addCrosshair(Assets.crosshair, center);
		map = new UIMap(world, stage, spriteBatch, skin, Color.BLACK, 5, 5, 3, 0);
		map.generateMap(world.getMeshLevel().getLevelArray(), world.getMeshLevel().getMapMaterials());
		TextureRegionDrawable barTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("barRed.png"))));
		ProgressBarStyle barStyle = new ProgressBarStyle(skin.newDrawable("white", Color.RED), barTexture);
		ProgressBar bar = new ProgressBar(world.getPlayer().MIN_HEALTH, world.getPlayer().getHealth(), 1f, false, barStyle);
		overlay.addProgressBarWidget(bar, true, world.getPlayer().getHealth());
		chat = new UIChat(stage, skin, "Chat");
		chat.addChatWidget(300, 200, 0, 30, 0.9f);
		chat.addChatWidgetListeners();
		virtualJoystick = new UIVirtualJoystick(stage, Assets.joystickBackground, 
												Assets.joystickKnob, 1920/2 - 100, 0, 100, 100);
		//virtualJoystick.addVirtualJoystick(world.getPlayer(), world.getPlayer().camera, 8.0f);
		form = new UIForm(stage, skin, "Name/IP");
		form.generateWindow(center.x, center.y, 150, 150);
		form.addTextField("Name", 0, 100, 150, 25);
		form.addTextField("IP Address", 0, 50, 150, 25);
		
		multiplexer.addProcessor(screenInputProcessor);
		multiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(multiplexer);
		uiGenerated = true;
	}
	
	public void generateMultiplayerClient() {
		mode = State.Client;
		this.world = new NetWorld();
		this.renderer = new Render(world);
		this.world.initializeEntities();
		state = State.Running;
		networkMenu.getTable().setVisible(false);
		generateUI(world);
		startClient();
	}
	
	public void generateMultiplayerServer() {
		mode = State.Server;
		this.world = new NetWorld();
		this.renderer = new Render(world);
		this.world.initializeEntities();
		state = State.Running;
		networkMenu.getTable().setVisible(false);
		generateUI(world);
		startServer();
	}
	
	public void generateOffline() {
		mode = State.Offline;
		this.offlineWorld = new World();
		offlineWorld.loadOfflineWorld(Assets.castle3, true);
		this.renderer = new Render(offlineWorld);
		this.offlineWorld.initializeEntities();
		state = State.Running;
		networkMenu.getTable().setVisible(false);
		generateUI(offlineWorld);
	}
	
	public void createNetworkMenu() {
		Array<TextButton> buttons2 = new Array<TextButton>();
		final TextButton button3 = new TextButton("Join", style);
		button3.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				generateMultiplayerClient();
			}
		});
		
		buttons2.add(button3);
		TextButton button4 = new TextButton("Host", style);
		button4.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				generateMultiplayerServer();
			}
		});
		
		buttons2.add(button4);
		TextButton button5 = new TextButton("Offline", style);
		button5.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				generateOffline();
			}
		});
		
		buttons2.add(button5);
		networkMenu = new UIMenu(stage, skin, buttons2, "Network Testing", 0, 0);
		networkMenu.generateVerticalMenu(10);
		networkMenu.getTable().setVisible(true);
		state = State.Paused;
		//We need to update the world once to avoid client crash, since the renderer will still be updating
		//world.update(Gdx.graphics.getDeltaTime());
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
			world.setClient(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(float delta) {	
		//Call the main renderer
		switch (state) {
			case Running:
				if (mode == State.Server) {
					world.update(delta);
					server.serverUpdate();
				}
				if (mode == State.Client) {
					world.update(delta);
					client.clientUpdate();
				}
				if (mode == State.Offline) {
					offlineWorld.update(delta);
				}
				break;
			case Paused:
				break;
		}

		if (uiGenerated) {
			if (mode != State.Server)
			renderer.RenderWorld(delta);
	
			//UI components are rendered here
			if (mode == State.Offline) {
				spriteBatch.begin();
				overlay.renderFPS(delta, -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				overlay.renderPosition(offlineWorld.getPlayer().getPosition(), delta, -Gdx.graphics.getWidth() / 2, 
						               Gdx.graphics.getHeight() / 2 - 20f);
				overlay.renderTilePosition(offlineWorld.getPlayer().getPlayerTileCoords(), delta, 
						                   -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 40f);
				spriteBatch.end();
				base.render(delta);
				overlay.updateWidgets(delta, offlineWorld.getPlayer().getHealth());
				map.renderIndicator(delta, offlineWorld.getPlayer().getPosition());
			}
			
			else if (mode == State.Server || mode == State.Client) {
				spriteBatch.begin();
				overlay.renderFPS(delta, -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				overlay.renderPosition(world.getPlayer().getPosition(), delta, -Gdx.graphics.getWidth() / 2, 
						               Gdx.graphics.getHeight() / 2 - 20f);
				overlay.renderTilePosition(world.getPlayer().getPlayerTileCoords(), delta, 
						                   -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 40f);
				spriteBatch.end();
				base.render(delta);
				overlay.updateWidgets(delta, world.getPlayer().getHealth());
				map.renderIndicator(delta, world.getPlayer().getPosition());
			}
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
}
