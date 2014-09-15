package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	public Game game;
	public Level level;
	public static PerspectiveCamera camera;
	public CameraInputController cameraController;
	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;
	public ModelBuilder modelBuilder;
	public Model box;
	public Environment environment;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public BitmapFont bitmapFont;
	public World world;
	public Vector2 center;
	public boolean loading;
	
	public GameScreen(Game game) {
		this.game = game;
		this.level = new Level();
		this.world = new World();
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		//Camera settings
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(world.player.position.x, world.player.position.y, 2f);
		camera.lookAt(0, 0, 5);
		camera.near = 0.5f;
		camera.far = 100f;
		camera.update();
	
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		Gdx.input.setInputProcessor(cameraController);
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

		loading = true;
	}
	
	private void doneLoading() {
		box = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		ModelInstance boxInstance = new ModelInstance(box);
		instances.add(boxInstance);
		loading = false;
	}

	@Override
	public void render(float delta) {
		if (loading && Assets.manager.update()) {
			System.out.println("Assets loaded.");
			doneLoading();
		}
		camera.position.set(world.player.position);
		camera.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		level.instances.get(0).transform.setToTranslation(world.player.position.x, world.player.position.y, world.player.position.z + 2.0f);
		//modelBatch.begin(camera);
		//modelBatch.render(instances, environment);
		//modelBatch.end();
		
		level.render(camera, environment);
		spriteBatch.begin();
		renderFps();
		spriteBatch.end();
		world.update();
		updateInput();
	}
	
	public void updateInput() {
		//MOUSE INPUT
		if (Gdx.input.justTouched()) {
			Vector2 deltaPos = new Vector2(Gdx.input.getX() - center.x, Gdx.input.getY() - center.y);
			
			boolean rotX = deltaPos.x != 0;
			//boolean rotY = deltaPos.y != 0;
			
			System.out.println(" " + (int)deltaPos.x);
			
			if (rotX)
				Gdx.input.setCursorPosition((int)center.x, (int)center.y);
		}
			
		//KEYBOARD INPUT
		if (Gdx.input.isKeyPressed(Keys.D))
			world.player.position.x -= 0.05f;
		if (Gdx.input.isKeyPressed(Keys.A))
			world.player.position.x += 0.05f;
		if (Gdx.input.isKeyPressed(Keys.W))
			world.player.position.z += 0.05f;
		if (Gdx.input.isKeyPressed(Keys.S))
			world.player.position.z -= 0.05f;
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
		// TODO Auto-generated method stub
		
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
