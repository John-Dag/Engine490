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
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gdx.DynamicEntities.Dynamic;
import com.gdx.StaticEntities.Static;

public class Render implements Disposable {
	public static int renderCount;
	private World world;
	private ModelBatch modelBatch;
	private Environment environment;
	private Array<ModelInstance> instances;
	private DecalBatch decalBatch;
	private DefaultShaderProvider shaderProvider;
	private Vector3 position;
	private Vector3 startXZ = new Vector3(-1, 0, 0);
	private Vector3 startY = new Vector3();
	private Vector3 camDirXZ = new Vector3();
	private ModelInstance gunInstance;
	private Model gun = new Model();
	private boolean loading;
	
	public Render(World world) {
		this.world = world;
		position = new Vector3();
	
		//Changes the max number point lights in the default shader
		shaderProvider = new DefaultShaderProvider();
		shaderProvider.config.numPointLights = 30;
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, MeshLevel.skyColor.r, MeshLevel.skyColor.g, MeshLevel.skyColor.b, 1f));
		//environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		//environment.add(new PointLight().set(new ColorAttribute(ColorAttribute.Diffuse).color.set(255, 0, 0, 1), 1f, 2f, 1f, 100f));

		Assets.loadModels();
		modelBatch = new ModelBatch(shaderProvider);
		loading = true;
		decalBatch = new DecalBatch(new CameraGroupStrategy(world.getPlayer().camera));
		instances = new Array<ModelInstance>(world.getLevelMesh());
		world.createBoundingBoxes();
		renderStaticEntities();
	}
	
	private void renderStaticEntities() {
		Static stat;

		for (int j = 0; j < Entity.entityInstances.size; j++) {
			Entity entity = Entity.entityInstances.get(j);
			if (entity instanceof Static) {
				stat = (Static)entity;
				if (stat.isRenderable()) {
					if (stat.getPointLight() != null)
						environment.add(stat.getPointLight());
					if (stat.getEffect() != null) {
						stat.getEffect().init();
						stat.getEffect().start();
						stat.getEffect().translate(stat.getPosition());
						World.particleManager.system.add(stat.getEffect());
					}
				}
			}
		}
	}
	
	private void renderDynamicEntities(float delta) {
		Dynamic dyn;
		
		for (int i = 0; i < Entity.entityInstances.size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			if (entity instanceof Dynamic) {
				dyn = (Dynamic)entity;
				if (dyn.isRenderable()) {
					if (dyn.getParticleEffect() != null && !dyn.isRendered()) {
						dyn.setRendered(true);
						dyn.getParticleEffect().init();
						dyn.getParticleEffect().start();
						World.particleManager.system.add(dyn.getParticleEffect());
					}
				}
			}
		}
	}
	
	//g3db files loaded here
	private void doneLoading() {
		gun = Assets.manager.get(world.getPlayer().getCurrentWeapon().getWeaponModelName(), Model.class);
		gunInstance = new ModelInstance(gun);
		gunInstance.transform.setToTranslation(world.getPlayer().camera.position.x, world.getPlayer().camera.position.y, world.getPlayer().camera.position.z);
		gunInstance.transform.scale(0.001f, 0.001f, 0.001f);
		
		loading = false;
	}
	
	public void updateDecals() {
		int size = Entity.entityInstances.size;
		
		for (int i = 0; i < size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			if (entity instanceof Static) {
				Static stat = (Static)entity;
				if (stat.isRenderable() && stat.getDecal() != null) {
					decalBatch.add(stat.getDecal());
					if (stat.isDecalFacing())
						stat.getDecal().lookAt(world.getPlayer().camera.position, world.getPlayer().camera.up);
				}
			}
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
		//System.out.println(pfxPoolWeapon.peak);
		//updateEntityMesh();
		modelBatch.begin(world.getPlayer().camera);
		//renderProjectiles();
		renderDynamicEntities(delta);
		renderParticles();
		
		//Viewport culling
		renderCount = 0;
		for (int i = 0; i < instances.size; i++) {
			ModelInstance instance = instances.get(i);
			if (isVisible(world.getPlayer().camera, instance, world.getBoundingBoxes().get(i))) {
				renderModels(instance);
				renderCount++;
			}
		}

		gunInstance.transform.setToTranslation(world.getPlayer().camera.position.x, 
											   world.getPlayer().camera.position.y - 0.1f, 
											   world.getPlayer().camera.position.z);
		startY.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
		camDirXZ.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
	
		gunInstance.transform.rotate(startY, world.getPlayer().camera.direction.nor());
		gunInstance.transform.rotate(startXZ, camDirXZ.nor());
		gunInstance.transform.scale(0.005f, 0.005f, 0.005f);
		renderModels(gunInstance);
	
		modelBatch.end();
		updateDecals();
		
		//Render decals
		decalBatch.flush();
	}
	
	private void renderModels(ModelInstance instance) {
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
		world.getProjectiles().clear();
		world.getBoundingBoxes().clear();
		world.getMeshLevel().getInstances().clear();
		world.getMeshLevel().getEntityInstances().clear();
		//world.getMeshLevel().getObjectInstances().clear();
		world.getDecals().clear();
		Assets.manager.dispose();
		modelBatch.dispose();
	}
}
