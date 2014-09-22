package com.gdx.engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;

public class Render {
	private World world;
	private ModelBatch modelBatch;
	private Environment environment;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private boolean loading;
	private DecalBatch decalBatch;
	
	public Render(World world) {
		this.world = world;
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		modelBatch = new ModelBatch();
		loading = true;
		decalBatch = new DecalBatch(new CameraGroupStrategy(world.getPlayer().camera));
	}
	
	private void doneLoading() {
		if (world.getLevel().getSkyboxActive())
			instances.add(world.getLevel().getSkySphere());
		loading = false;
	}
	
	public void updateDecals() {
		int length = world.getDecals().size;
		
		for (int i = 0; i < length; i++) {
			Decal decal = world.getDecals().get(i);
			//System.out.println(i);
			decalBatch.add(decal);
		}
	}
	
	public void updateEntityMesh() {
		int length = world.getLevel().getInstances().size;
		
		for (int i = 0; i < length; i++) {
			Entity entity = world.getLevel().getInstances().get(i);
			
			if (!entity.active) {
				world.getLevel().getInstances().removeIndex(i);
				instances.removeIndex(i);
				length -= 1;
				break;
			}
			
			entity.model.transform.setToTranslation(entity.position.x, entity.position.y, entity.position.z);
			
			if (entity.id == 1) {
				entity.model.transform.setToTranslation(world.getPlayer().camera.position.x, world.getPlayer().camera.position.y, world.getPlayer().camera.position.z);
				entity.model.calculateBoundingBox(entity.boundingBox).mul(entity.model.transform.scale(0.5f, 0.5f, 0.5f));
			}
			
			if (!entity.isRendered) {
				instances.add(entity.model);
				entity.isRendered = true;
			}
		}
	}
	
	public void RenderWorld(float delta) {
		if (loading && Assets.manager.update()) {
			doneLoading();
		}
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,  1,  1,  1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		updateEntityMesh();
		modelBatch.begin(world.getPlayer().camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
		updateDecals();
		
		decalBatch.flush();
	}
	
	public DecalBatch getDecalbatch() {
		return decalBatch;
	}
}
