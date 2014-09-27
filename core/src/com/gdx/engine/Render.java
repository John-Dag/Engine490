package com.gdx.engine;
import java.awt.Color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Render extends DefaultShaderProvider {
	private static class PFXPool extends Pool<ParticleEffect> {
	    private ParticleEffect sourceEffect;

	    public PFXPool(ParticleEffect sourceEffect) {
	        this.sourceEffect = sourceEffect;
	    }

	    @Override
	    public void free(ParticleEffect pfx) {
	        pfx.reset();
	        super.free(pfx);
	    }

	    @Override
	    protected ParticleEffect newObject() {
	        return sourceEffect.copy();
	    }
	}
	
	private World world;
	private ModelBatch modelBatch;
	private Environment environment;
	private Array<ModelInstance> instances;
	private boolean loading;
	private DecalBatch decalBatch;
	public static int renderCount;
	Vector3 position = new Vector3();
	public static AssetManager manager = new AssetManager();
	private ParticleSystem particleSystem;
	private BillboardParticleBatch pointSpriteBatch;
	private ParticleEffectLoader.ParticleEffectLoadParameter loadParam;
	private ParticleEffectLoader loader;
	private ParticleEffect originalEffect;
	private PFXPool pfxPool;
	
	public Render(World world) {
		this.world = world;
	
		//Changes the max number point lights in the default shader
		this.config.numPointLights = 20;
		
		//Environment settings
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		//environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		//environment.add(new PointLight().set(new ColorAttribute(ColorAttribute.Diffuse).color.set(255, 0, 0, 1), 1f, 2f, 1f, 100f));
		
		//Particles
		particleSystem = ParticleSystem.get();
		pointSpriteBatch = new BillboardParticleBatch();
		pointSpriteBatch.setCamera(world.getPlayer().camera);
		particleSystem.add(pointSpriteBatch);
		
		loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		loader = new ParticleEffectLoader(new InternalFileHandleResolver());

	    manager.setLoader(ParticleEffect.class, loader);
	    manager.load("torcheffect.pfx", ParticleEffect.class, loadParam);
	    manager.finishLoading();
		
		originalEffect = manager.get("torcheffect.pfx");
		pfxPool = new PFXPool(originalEffect);
		//End particles

		instances = new Array<ModelInstance>(world.getLevelMesh());
		
		modelBatch = new ModelBatch(this);
		loading = true;
		decalBatch = new DecalBatch(new CameraGroupStrategy(world.getPlayer().camera));
		
		world.createBoundingBoxes();
		world.setObjectDecals();
		renderObjects();
	}
	
	private void doneLoading() {
//		if (world.getLevel().getSkyboxActive())
//			instances.add(world.getLevel().getSkySphere());
		loading = false;
	}
	
	public void renderObjects() {
		int size = world.getMeshLevel().getObjectInstances().size;
		Vector3 objectCoords = new Vector3();
		
		for (int i = 0; i < size; i++) {
			Object object = world.getMeshLevel().getObjectInstances().get(i);

			if (!object.active) {
				if (object.id == 1) {
					objectCoords.set(object.decal.getPosition().x, object.decal.getPosition().y + 0.1f, object.decal.getPosition().z);
					environment.add(new PointLight().set(new ColorAttribute(ColorAttribute.AmbientLight).color.set(object.color), objectCoords, 5f));
					ParticleEffect effect = new ParticleEffect();
					spawnParticleEffect(effect, objectCoords);
					object.active = true;
				}
				
				else if (object.id == 2) {
					environment.add(object.light);
					object.active = true;
				}
				
				else if (object.id == 3) {
					ParticleEffect effect = new ParticleEffect();
					pfxPool.newObject();
					spawnParticleEffect(effect, objectCoords);
					object.active = true;
				}
			}
		}
	}
	
	private void spawnParticleEffect(ParticleEffect effect, Vector3 position) {
		if (pfxPool.getFree() != -1) {
			effect = pfxPool.obtain();
			effect.init();
			effect.start();
			effect.translate(position);
			particleSystem.add(effect);
		}
	}
	
	public void updateDecals() {
		int length = world.getDecals().size;
		
		for (int i = 0; i < length; i++) {
			Decal decal = world.getDecals().get(i);
			//System.out.println(i);
			//Vector3 test = new Vector3(0, 10, 10);
			//decal.lookAt(world.getPlayer().camera.position, );
			if (decal != null)
				decalBatch.add(decal);
		}
	}
	
//	public void updateEntityMesh() {
//		int length = world.getLevel().getInstances().size;
//		
//		for (int i = 0; i < length; i++) {
//			Entity entity = world.getLevel().getInstances().get(i);
//			
//			if (!entity.active) {
//				world.getLevel().getInstances().removeIndex(i);
//				instances.removeIndex(i);
//				length -= 1;
//				break;
//			}
//			
//			entity.model.transform.setToTranslation(entity.position.x, entity.position.y, entity.position.z);
//			
//			if (entity.id == 1) {
//				entity.model.transform.setToTranslation(world.getPlayer().camera.position.x, world.getPlayer().camera.position.y, world.getPlayer().camera.position.z);
//				entity.model.calculateBoundingBox(entity.boundingBox).mul(entity.model.transform.scale(0.5f, 0.5f, 0.5f));
//			}
//			
//			if (!entity.isRendered) {
//				instances.add(entity.model);
//				entity.isRendered = true;
//			}
//		}
//	}
	
	public void renderParticles() {
		//Vector3 test = new Vector3(world.getPlayer().camera.position.x, world.getPlayer().camera.position.y, world.getPlayer().camera.position.z + 3f);
		particleSystem.update();
		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();
	}
	
	public void RenderWorld(float delta) {
		if (loading && manager.update()) {
			doneLoading();
		}
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,  1,  1,  1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);

		//updateEntityMesh();
		modelBatch.begin(world.getPlayer().camera);
		renderParticles();
		modelBatch.render(particleSystem);
		
		//Viewport culling
		renderCount = 0;
		for (int i = 0; i < instances.size; i++) {
			ModelInstance instance = instances.get(i);
			if (isVisible(world.getPlayer().camera, instance, world.getBoundingBoxes().get(i))) {
				modelBatch.render(instance, environment);
				renderCount++;
			}
		}
		modelBatch.end();
		updateDecals();
		
		//Render decals
		decalBatch.flush();
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
}
