package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
	private Game game;
	private World world;
	private Render renderer;
	private SpriteBatch spriteBatch;
	private BitmapFont bitmapFont;
	public static Vector2 center;
	
	public GameScreen(Game game) {
		this.game = game;
		this.world = new World();
		this.renderer = new Render(world);
	
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		bitmapFont.setScale(0.9f);
	}

	@Override
	public void render(float delta) {	
		//Call the main renderer
		renderer.RenderWorld(delta);
		
		//UI components are rendered here
		spriteBatch.begin();
		spriteBatch.draw(Assets.crosshair, center.x - 8, center.y - 8);
		renderFps();
		spriteBatch.end();
		
		world.update(delta);
	}
	
	public void renderFps() {
		int fps = Gdx.graphics.getFramesPerSecond();
		bitmapFont.draw(spriteBatch, "FPS: " + fps, 10f, 530f);
		bitmapFont.draw(spriteBatch, "Rendered Models: " + Render.renderCount, 10f, 510f); 
	}

	@Override
	public void dispose() {
		Assets.manager.dispose();
		game.dispose();
		renderer.getDecalbatch().dispose();
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
