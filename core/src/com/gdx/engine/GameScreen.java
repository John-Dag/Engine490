package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	public Game game;
	public Level level;
	public PerspectiveCamera camera;
	public CameraInputController cameraController;
	public ModelBatch modelBatch;
	public ModelBuilder modelBuilder;
	public Model box;
	public Environment environment;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public boolean loading;
	
	public GameScreen(Game game) {
		this.game = game;
		this.level = new Level();
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(7f, 7f, 7f);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 300f;
		camera.update();
	
		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);

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
			System.out.println("Assets are loaded.");
			doneLoading();
		}
		cameraController.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
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

	@Override
	public void dispose() {
		modelBatch.dispose();
		Assets.manager.dispose();
	}
}
