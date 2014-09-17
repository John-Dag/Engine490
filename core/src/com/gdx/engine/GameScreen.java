package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	public Game game;
	public Level level;
	public PerspectiveCamera camera;
	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;
	public ModelBuilder modelBuilder;
	public Model box;
	public Environment environment;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public BitmapFont bitmapFont;
	public World world;
	public Vector2 center;
	public Vector3 temp;
	public boolean loading, mouseLocked;
	
	public GameScreen(Game game) {
		this.game = game;
		this.level = new Level();
		this.world = new World();
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		//Camera settings
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(world.player.position.x, world.player.position.y, 2f);
		camera.lookAt(0, 0, 5);
		camera.near = 0.5f;
		camera.far = 100f;
	
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		temp = new Vector3();

		mouseLocked = false;
		loading = true;
	}

	@Override
	public void render(float delta) {
		if (loading && Assets.manager.update()) {
			System.out.println("Assets loaded.");
			loading = false;
		}
		camera.position.set(world.player.position);
		camera.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,  1,  1,  1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		level.render(camera, environment);
		spriteBatch.begin();
		spriteBatch.draw(Assets.crosshair, center.x - 8, center.y - 8);
		renderFps();
		spriteBatch.end();
		world.update();
		updateInput(delta);
	}
	
	public void updateInput(float delta) {
		//Lock the cursor with rmb
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			Gdx.input.setCursorCatched(true);
			mouseLocked = true;
		}
		//ESC cancels cursor lock
		else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.input.setCursorCatched(false);
			mouseLocked = false;
		}
		
		if (mouseLocked) {
			Vector2 deltaPos = new Vector2(Gdx.input.getX() - center.x, Gdx.input.getY() - center.y);
			
			boolean rotX = deltaPos.x != 0;
			boolean rotY = deltaPos.y != 0;
			
			//Testing purposes
			//System.out.println("Pointer Position: " + (int)deltaPos.x);
			
			if (rotX || rotY) {
				Gdx.input.setCursorPosition((int)center.x, (int)center.y);
				camera.direction.rotate(camera.up, -Gdx.input.getDeltaX() * world.player.ROTATION_SPEED);
				temp.set(camera.direction).crs(camera.up).nor();
				camera.direction.rotate(temp, -Gdx.input.getDeltaY() * world.player.ROTATION_SPEED);
			}
		}
		
		//Keyboard input
		if (Gdx.input.isKeyPressed(Keys.D)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(delta * world.player.MOVEMENT_SPEED);
			world.player.position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(-delta * world.player.MOVEMENT_SPEED);
			world.player.position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			temp.set(camera.direction).nor().scl(delta * world.player.MOVEMENT_SPEED);
			world.player.position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			temp.set(camera.direction).nor().scl(-delta * world.player.MOVEMENT_SPEED);
			world.player.position.add(temp.x, 0, temp.z);
		}
	}
	
	public void renderFps() {
		int fps = Gdx.graphics.getFramesPerSecond();
		bitmapFont.draw(spriteBatch, "FPS: " + fps, 10f, 20f);
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		Assets.manager.dispose();
	}

	@Override
	public void resize(int width, int height) {
		//viewport.update(width, height);
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
