package com.gdx.engine;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gdx.FilterEffects.*;
import com.gdx.UI.UIConsole;
import com.gdx.UI.UIBase;
import com.gdx.UI.UIGrid;
import com.gdx.UI.UIInputProcessor;
import com.gdx.UI.UIMap;
import com.gdx.UI.UIMenu;
import com.gdx.UI.UIOverlay;
import com.gdx.Weapons.RocketLauncher;
import com.gdx.Weapons.Sword;

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
	private boolean consoleActive;
	private UIBase base;
	private UIMenu menu;
	private UIOverlay overlay;
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private UIMap map;
	
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
		stage = new Stage(new ScreenViewport());
		this.consoleActive = consoleActive;
		if (consoleActive) {
			console = new UIConsole(stage, world);
			console.initializeConsoleWindow();
			console.initializeFilterEffects();
			console.addConsoleInputListener(Keys.GRAVE, 0);
		}

		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		state = State.Running;
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
		final UIGrid grid = new UIGrid(stage, skin, Color.GREEN, "Inventory", Assets.gridslot);
		grid.generateGrid(Align.bottom, 30, 30, 5, 5, 3);
		grid.setWindowSize(500, 500);
		overlay = new UIOverlay(stage, bitmapFont);
		overlay.addCrosshair(Assets.crosshair, center);
		overlay.setCursorImage("crosshair.png", 0, 0);
		grid.addVisibleInputListener(Keys.I, 2);
		menu.addVisibleInputListener(Keys.ESCAPE, 1);
		map = new UIMap(stage, spriteBatch);
		map.generateMap(world.getMeshLevel().getInstances());
		//multiplexer.addProcessor(new UIInputProcessor());
		//Gdx.input.setInputProcessor(multiplexer);
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
		//renderFps();
		//renderPos();
		//renderTilePos();
		map.render(delta);
		//renderUI();
		spriteBatch.end();
		base.render(delta);
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
