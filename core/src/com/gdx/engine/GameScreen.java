package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	public Game game;
	public Level level;
	public PerspectiveCamera camera;
	public MyCamInputController cameraController;
	public Shader shader;
	public RenderContext renderContext;
	public Model model;
	public ModelBatch modelBatch;
	public ModelBuilder modelBuilder;
	public Model box;
	public Environment environment;
	public Renderable renderable;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	Array<Renderable> renderables;
	public boolean loading;
	
	public GameScreen(Game game) {
		this.game = game;
		
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		this.level = new Level(environment);
		
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(7f, 7f, 7f);
		camera.lookAt(0, 0, 0);
		camera.near = 0.5f;
		camera.far = 100f;
		camera.update();
	
		cameraController = new MyCamInputController(camera);
		Gdx.input.setInputProcessor(cameraController);
		
		//here we get the model instances from level
		instances = level.generateLevel();
	}

	@Override
	public void render(float delta) {

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
