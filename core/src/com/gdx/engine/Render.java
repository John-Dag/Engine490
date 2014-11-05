package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

public class Render implements Disposable {
	public static int renderCount;
	public static Environment environment;
	private World world;
	private ModelBatch modelBatch;
	private DecalBatch decalBatch;
	private DirectionalShadowLight shadowLight;
	private ModelBatch shadowBatch;
	private DefaultShaderProvider shaderProvider;
	private Vector3 position;
	private Model weapon = new Model();
	private boolean loading;
	
	public Render(World world) {
		this.world = world;
		position = new Vector3();
	
		//Changes the max number point lights in the default shader
		shaderProvider = new DefaultShaderProvider();
		shaderProvider.config.numPointLights = 30;
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .7f, .7f, .7f, .2f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, MeshLevel.skyColor.r, MeshLevel.skyColor.g, MeshLevel.skyColor.b, 1f));
	    environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 10f, 10f, .1f, 50f))                  
                		 .set(.5f, .5f, .5f, 20.0f, -35f, -35f)); 
	    environment.shadowMap = shadowLight;
	    shadowBatch = new ModelBatch(new DepthShaderProvider());

		Assets.loadModels();
		modelBatch = new ModelBatch(shaderProvider);
		loading = true;
		decalBatch = new DecalBatch(new CameraGroupStrategy(world.getPlayer().camera));
		world.createBoundingBoxes();
	}

	//g3db files loaded here
	private void doneLoading() {
		if (world.getPlayer().getWeapon() != null) {
			loading = false;
		}
	}
	
	public void renderParticles() {
		World.particleManager.getSystem().update();
		World.particleManager.getSystem().begin();
		World.particleManager.getSystem().draw();
		World.particleManager.getSystem().end();
		modelBatch.render(World.particleManager.getSystem());
	}
	
	public void RenderWorld(float delta) {
		if (loading && Assets.manager.update()) {
			doneLoading();
		}
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(MeshLevel.skyColor.r, MeshLevel.skyColor.g, MeshLevel.skyColor.b, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		Gdx.gl.glCullFace(GL20.GL_FRONT);
		modelBatch.begin(world.getPlayer().camera);
		renderParticles();
		shadowLight.begin(world.getPlayer().camera.position, world.getPlayer().camera.direction);
		shadowBatch.begin(shadowLight.getCamera());
		
		//Viewport culling
		renderCount = 0;
		for (int i = 0; i < world.getMeshLevel().getInstances().size; i++) {
			ModelInstance instance = world.getMeshLevel().getInstances().get(i);
			//shadowBatch.render(instance);
			if (isVisible(world.getPlayer().camera, instance, world.getBoundingBoxes().get(i))) {
				renderModels(instance);
				renderCount++;
			}
		}
		
		for (Entity entity : Entity.entityInstances) {
			if (entity.isRenderable() && entity.isActive()) {
				entity.render(modelBatch, decalBatch, shadowBatch);
			}
		}
		
		shadowBatch.end();
		shadowLight.end();
		modelBatch.end();
		
		//Render decals
		decalBatch.flush();
	}
	
	public void renderModels(ModelInstance instance) {
		modelBatch.render(instance, environment);
	}
	
	private boolean isVisible(final Camera cam, final ModelInstance instance, BoundingBox box) {
		instance.transform.getTranslation(position);
		position.add(box.getCenter());
		return cam.frustum.boundsInFrustum(position, box.getDimensions());
	}
	
	public DecalBatch getDecalbatch() {
		return decalBatch;
	}
	
	public ModelBatch getModelbatch() {
		return modelBatch;
	}

	@Override
	public void dispose() {
		world.getBoundingBoxes().clear();
		world.getMeshLevel().getInstances().clear();
		modelBatch.dispose();
		decalBatch.dispose();
		shadowLight.dispose();
		shaderProvider.dispose();
		shadowBatch.dispose();
		weapon.dispose();
	}
}
