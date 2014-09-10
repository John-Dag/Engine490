package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
	public Game game;
	public PerspectiveCamera camera;
	public CameraInputController cameraController;
	public ModelBatch modelBatch;
	public Environment environment;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public boolean loading;
	public Texture image = new Texture(Gdx.files.internal("walkableTile.png"));
	public Decal sprite;
	public DecalBatch decalBatch;
	
	public GameScreen(Game game) {
		this.game = game;
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(3f, 3f, 3f);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 300f;
		camera.update();
	
		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);
		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		sprite = Decal.newDecal(64, 64, new TextureRegion(image));
		sprite.setPosition(0, 0, -100);
		
		loading = true;
	}
	
	private void doneLoading() {
		Model ship = Assets.manager.get("borncg.g3db", Model.class);
		ModelInstance shipInstance = new ModelInstance(ship);
		instances.add(shipInstance);
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
		
		//modelBatch.begin(camera);
		//modelBatch.render(instances, environment);
		//modelBatch.end();
		//sprite.lookAt(camera.position, camera.up);
		decalBatch.add(sprite);
		decalBatch.flush();
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
