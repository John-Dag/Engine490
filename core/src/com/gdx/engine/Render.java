package com.gdx.engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Render {
	private World world;
	private ModelBatch modelBatch;
	private Environment environment;
	private PerspectiveCamera camera;
	private Vector2 center;
	private Vector3 temp;
	private Model playerModel;
	private ModelBuilder modelBuilder;
	private boolean mouseLocked;
	private Array<ModelInstance> playerInstances = new Array<ModelInstance>();
	
	public Render(World world) {
		this.world = world;
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		//Camera settings
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(world.getPlayer().position.x, world.getPlayer().position.y, 2f);
		camera.lookAt(0, 0, 5);
		camera.near = 0.1f;
		camera.far = 100f;
		
		modelBatch = new ModelBatch();
	
		temp = new Vector3();
		center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		modelBuilder = new ModelBuilder();
		playerModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), Usage.Position | Usage.Normal);

		mouseLocked = false;
		setPlayers();
	}
	
	public void setPlayers() {
		int length = world.getPlayers().size;
		
		for (int i = 0; i < length; i++) {
			Player player = world.getPlayers().get(i);
			ModelInstance boxInstance = new ModelInstance(playerModel);
			boxInstance.transform.setToTranslation(player.position);
			playerInstances.add(boxInstance);
		}
	}
	
	public void updatePlayers() {
		int length = world.getPlayers().size;
		
		for (int i = 0; i < length; i++) {
			Player player = world.getPlayers().get(i);
			playerInstances.get(i).transform.setToTranslation(player.position.x, player.position.y, player.position.z + 2f);
		}
	}
	
	public void RenderWorld(float delta) {
		camera.position.set(world.getPlayer().position);
		camera.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,  1,  1,  1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(camera);
		modelBatch.render(world.getLevel().getInstances(), environment);
		modelBatch.render(playerInstances, environment);
		modelBatch.end();
		updateInput(delta);
		updatePlayers();
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
				camera.direction.rotate(camera.up, -Gdx.input.getDeltaX() * world.getPlayer().ROTATION_SPEED);
				temp.set(camera.direction).crs(camera.up).nor();
				camera.direction.rotate(temp, -Gdx.input.getDeltaY() * world.getPlayer().ROTATION_SPEED);
			}
		}
		
		//Keyboard input
		if (Gdx.input.isKeyPressed(Keys.D)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(delta * world.getPlayer().MOVEMENT_SPEED);
			world.getPlayer().position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(-delta * world.getPlayer().MOVEMENT_SPEED);
			world.getPlayer().position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			temp.set(camera.direction).nor().scl(delta * world.getPlayer().MOVEMENT_SPEED);
			world.getPlayer().position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			temp.set(camera.direction).nor().scl(-delta * world.getPlayer().MOVEMENT_SPEED);
			world.getPlayer().position.add(temp.x, 0, temp.z);
		}
	}
}
