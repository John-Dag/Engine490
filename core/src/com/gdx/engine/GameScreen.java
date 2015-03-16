package com.gdx.engine;

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
import com.gdx.Inventory.TestInventory;
import com.gdx.UI.UIChat;
import com.gdx.UI.UIConsole;
import com.gdx.UI.UIBase;
import com.gdx.UI.UIGrid;
import com.gdx.UI.UIMap;
import com.gdx.UI.UIMenu;
import com.gdx.UI.UIOverlay;
import com.gdx.UI.UIVirtualJoystick;

public class GameScreen implements Screen {
	public static Vector2 center;
	public static State state;
	private Game game;
	private World world;
	private Render renderer;
	private SpriteBatch spriteBatch;
	private BitmapFont bitmapFont;
	private Stage stage;
	private Skin skin;
	private UIConsole console;
	private UIBase base;
	private UIMenu menu;
	private UIOverlay overlay;
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private UIMap map;
	private UIChat chat;
	private UIGrid grid;
	private WorldInputProcessor screenInputProcessor;
	private UIVirtualJoystick virtualJoystick;
	//private TestInventory testInv = new TestInventory();
	
	public enum State {
		Running, Paused
	}
	
	public GameScreen(Game game, boolean consoleActive) {
		this.game = game;
		this.world = new World();
		this.renderer = new Render(world);
		this.world.initializeEntities();
	
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		state = State.Running;
		
		stage = new Stage() {
			@Override
			public boolean keyDown(int keyCode) {
				if (keyCode == Keys.GRAVE) {
					console.show();
				}
				
				else if (keyCode == Keys.I && !UIBase.uiSelected) {
					grid.show();
					if (grid.getWindow().isVisible())
						Gdx.input.setCursorCatched(false);
					else{
						Gdx.input.setCursorCatched(true);
					}
				}
		
				return false;
			}
		};
		screenInputProcessor = new WorldInputProcessor(world);
		
		console = new UIConsole(stage, world);
		console.initializeConsoleWindow();
		console.initializeFilterEffects();
		
		base = new UIBase(stage);
		Array<TextButton> buttons = new Array<TextButton>();
		TextButtonStyle style = new TextButtonStyle();
		style.font = bitmapFont;
		
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
		//grid.setImages(testInv.getImages());
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
		virtualJoystick.addVirtualJoystick(world.getPlayer(), world.getPlayer().camera, 8.0f);
		
		multiplexer.addProcessor(screenInputProcessor);
		multiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	@Override
	public void render(float delta) {	
		//Call the main renderer
		switch (state) {
			case Running:
				world.update(delta);
				break;
			case Paused:
				break;
		}
		renderer.RenderWorld(delta);

		//UI components are rendered here
		spriteBatch.begin();
		overlay.renderFPS(delta, -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		overlay.renderPosition(world.getPlayer().getPosition(), delta, -Gdx.graphics.getWidth() / 2, 
				               Gdx.graphics.getHeight() / 2 - 20f);
		overlay.renderTilePosition(world.getPlayer().getPlayerTileCoords(), delta, 
				                   -Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 40f);
		spriteBatch.end();
		base.render(delta);
		overlay.updateWidgets(delta, world.player.getHealth());
		map.renderIndicator(delta, world.getPlayer().getPosition());
		virtualJoystick.render(delta);
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
