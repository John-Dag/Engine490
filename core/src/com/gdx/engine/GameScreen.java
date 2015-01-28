package com.gdx.engine;

import java.util.LinkedList;
import java.util.List;

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
import com.gdx.FilterEffects.*;
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
	private Console console;
	private boolean consoleActive;
	
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
		this.consoleActive = consoleActive;
		if (consoleActive) {
			console = new Console(world);
			console.initializeConsoleWindow();
			console.initializeFilterEffects();
		}
		
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		state = State.Running;
	}
	
	@Override
	public void render(float delta) {	
		//Call the main renderer
		switch (state) {
			case Running:
				world.update(delta);
				renderer.RenderWorld(delta);
				break;
			case Paused:
				break;
		}
		
		//UI components are rendered here
		spriteBatch.begin();
		spriteBatch.draw(Assets.crosshair, center.x - 8, center.y - 8);
		renderFps();
		renderPos();
		renderTilePos();
		renderUI();
		spriteBatch.end();
		if (console != null) {
			console.getStage().act();
			console.getStage().draw();
		}
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
		
		if (console != null) {
			console.update();
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
