package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class Render implements Disposable {
	public static int renderCount;
	public static AssetManager manager;
	private World world;
	private ModelBatch modelBatch;
	private Environment environment;
	private Array<ModelInstance> instances;
	private boolean loading;
	private DecalBatch decalBatch;
	private Vector3 position;
	private ParticleSystem particleSystem;
	private BillboardParticleBatch pointSpriteBatch;
	private ParticleEffectLoader.ParticleEffectLoadParameter loadParam;
	private ParticleEffectLoader loader;
	private ParticleEffect originalEffect;
	private ParticleEffect weaponEffect;
	private ParticleEffect sparkEffect;
	private PFXPool pfxPool;
	private PFXPool pfxPoolWeapon;
	private PFXPool pfxPoolSparks;
	private ParticleEffect mistEffect;
	private DefaultShaderProvider shaderProvider;
	private Matrix4 target;
	private Vector3 movementVector = new Vector3();
	private Vector3 oldPos = new Vector3();
	private Vector3 newPos = new Vector3();
	private Vector3 collisionVector;
	private ModelInstance gunInstance;
	private Model gun = new Model();
	
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
		
		//Particles
		particleSystem = ParticleSystem.get();
		pointSpriteBatch = new BillboardParticleBatch();
		pointSpriteBatch.setCamera(world.getPlayer().camera);
		particleSystem.add(pointSpriteBatch);
		
		loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		loader = new ParticleEffectLoader(new InternalFileHandleResolver());

		manager = new AssetManager();
	    manager.load("GUNFBX.g3db", Model.class);
	    manager.setLoader(ParticleEffect.class, loader);
	    manager.load("torcheffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("rocketeffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("dropletsGreen.pfx", ParticleEffect.class, loadParam);
	    //manager.load("sparks.pfx", ParticleEffect.class, loadParam);
	    manager.finishLoading();
		
		originalEffect = manager.get("torcheffect.pfx");
		weaponEffect = manager.get("rocketeffect.pfx");
		mistEffect = manager.get("dropletsGreen.pfx");
		//sparkEffect = manager.get("sparks.pfx");
		pfxPool = new PFXPool(originalEffect);
		pfxPoolWeapon = new PFXPool(weaponEffect);
		pfxPoolSparks = new PFXPool(sparkEffect);
		//End particles

		instances = new Array<ModelInstance>(world.getLevelMesh());
		
		modelBatch = new ModelBatch(shaderProvider);
		loading = true;
		decalBatch = new DecalBatch(new CameraGroupStrategy(world.getPlayer().camera));
		target = new Matrix4();
		
		world.createBoundingBoxes();
		world.setObjectDecals();
		renderObjects();
	}
	
	//g3db files loaded here
	private void doneLoading() {
//		if (world.getLevel().getSkyboxActive())
//			instances.add(world.getLevel().getSkySphere());
		
		//Temporary model for testing
		gun = manager.get(world.getPlayer().getWeapon().weaponModelName, Model.class);
		gunInstance = new ModelInstance(gun);
		gunInstance.transform.setToTranslation(world.getPlayer().camera.position.x, world.getPlayer().camera.position.y, world.getPlayer().camera.position.z);
		gunInstance.transform.scale(0.001f, 0.001f, 0.001f);
		
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
					spawnParticleEffect(pfxPool.obtain(), objectCoords);
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
				
				else if (object.id == 5) {
					objectCoords.set(object.decal.getPosition().x, object.decal.getPosition().y + 0.1f, object.decal.getPosition().z);
					environment.add(new PointLight().set(new ColorAttribute(ColorAttribute.AmbientLight).color.set(object.color), objectCoords, 2f));
					object.active = true;
				}
				
				else if (object.id == 6) {
					spawnParticleEffect(mistEffect.copy(), object.position);
					object.active = true;
				}
			}
		}
	}
	
	//Renders projectiles as particle effects. Particle effects are recycled in pfxPoolWeapon. 
	//Uses checkCollision in MeshLevel for collisions.
	private void renderProjectiles() {
		int length = world.getProjectiles().size;

		for (int i = 0; i < length; i++) {
			Projectile projectile = world.getProjectiles().get(i);
			if (!projectile.active) {
				projectile.active = true;
				projectile.effect = pfxPoolWeapon.obtain();
				projectile.effect.init();
				projectile.effect.start();
				particleSystem.add(projectile.effect);
				System.out.println("Pool pfx: " + length);
			}

			projectile.UpdatePosition(world.getPlayer().getWeapon().firingDelay);
			if (projectile.effect != null) {
				target.idt();
				target.translate(projectile.position);
				projectile.effect.setTransform(target);
			}
			
			movementVector.set(0, 0, 0);
			movementVector.set(world.getPlayer().camera.direction);
			movementVector.nor();
			float moveAmt = world.getPlayer().getWeapon().firingDelay * Gdx.graphics.getDeltaTime();
			oldPos.set(projectile.position);
			newPos.set(oldPos.x + movementVector.x * moveAmt, oldPos.y + movementVector.y * moveAmt, oldPos.z + movementVector.z * moveAmt);
			collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, 0.5f, 0.5f, 0.5f);

			movementVector.set(movementVector.x * collisionVector.x,
						       movementVector.y * collisionVector.y,
					           movementVector.z * collisionVector.z);
			
			//System.out.println("X: " + movementVector.x + " Y: " + movementVector.y + " Z: " + movementVector.z);
			if (collisionVector.x == 0 || collisionVector.y == 0 || collisionVector.z == 0) {
				particleSystem.remove(projectile.effect);
				pfxPoolWeapon.free(projectile.effect);
				world.getProjectiles().removeIndex(i);
				length -= 1;
			}
			
			/*
			if (projectile.position.epsilonEquals(world.getOut(), 1f)) {
				particleSystem.remove(projectile.effect);
				pfxPoolWeapon.free(projectile.effect);
				world.getProjectiles().removeIndex(i);
				length -= 1;
			}
			*/
		}
	}
	
	//Spawn a stationary particle effect
	private void spawnParticleEffect(ParticleEffect effect, Vector3 position) {
		if (pfxPool.getFree() != -1) {
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
			if (decal != null) {
				if (decal.value == 5) {
					decal.lookAt(world.getPlayer().camera.position, world.getPlayer().camera.up);
				}
				
				decalBatch.add(decal);
			}
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
		Gdx.gl.glClearColor(MeshLevel.skyColor.r, MeshLevel.skyColor.g, MeshLevel.skyColor.b, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		//System.out.println(pfxPoolWeapon.peak);
		//updateEntityMesh();
		modelBatch.begin(world.getPlayer().camera);
		renderProjectiles();
		renderParticles();
		modelBatch.render(particleSystem);
		
		//Viewport culling
		renderCount = 0;
		for (int i = 0; i < instances.size; i++) {
			ModelInstance instance = instances.get(i);
			if (isVisible(world.getPlayer().camera, instance, world.getBoundingBoxes().get(i))) {
				renderModels(instance);
				renderCount++;
			}
		}
		
		//Render entities
		for (int j = 0; j < world.getMeshLevel().getEntityInstances().size; j++) {
			ModelInstance instance = world.getMeshLevel().getEntityInstances().get(j).model;
			renderModels(instance);
		}
		
		gunInstance.transform.setToTranslation(world.getPlayer().camera.position.x, world.getPlayer().camera.position.y - 0.1f, world.getPlayer().camera.position.z);
		Vector3 start = new Vector3(-1, 0, 0);
		Vector3 camDir = new Vector3(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
		gunInstance.transform.rotate(start, camDir.nor());
		gunInstance.transform.scale(0.01f, 0.01f, 0.01f);
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
		world.getMeshLevel().getObjectInstances().clear();
		world.getDecals().clear();
		manager.dispose();
		modelBatch.dispose();
	}
	
	//Pool for particle effects
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
}
