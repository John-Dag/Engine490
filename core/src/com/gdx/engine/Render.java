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
	private Model playerModel;
	private ModelBuilder modelBuilder;
	private Array<ModelInstance> playerInstances = new Array<ModelInstance>();
	
	public Render(World world) {
		this.world = world;
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		//Camera settings
		
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		playerModel = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), Usage.Position | Usage.Normal);

		setPlayerMesh();
	}
	
	public void setPlayerMesh() {
		int length = world.getPlayers().size;
		
		for (int i = 0; i < length; i++) {
			Player player = world.getPlayers().get(i);
			ModelInstance boxInstance = new ModelInstance(playerModel);
			boxInstance.transform.setToTranslation(player.position);
			playerInstances.add(boxInstance);
		}
	}
	
	public void updatePlayerMesh() {
		int length = world.getPlayers().size;
		
		for (int i = 0; i < length; i++) {
			Player player = world.getPlayers().get(i);
			playerInstances.get(i).transform.setToTranslation(player.position.x, player.position.y, player.position.z + 2f);
		}
	}
	
	public void RenderWorld(float delta) {
		world.getPlayer().camera.position.set(world.getPlayer().position);
		world.getPlayer().camera.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,  1,  1,  1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(world.getPlayer().camera);
		modelBatch.render(world.getLevel().getInstances(), environment);
		modelBatch.render(playerInstances, environment);
		modelBatch.end();
		updatePlayerMesh();
	}
}
