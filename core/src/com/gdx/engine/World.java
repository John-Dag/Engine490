package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.DynamicEntities.Weapon;

public class World {
	public static final float PLAYER_SIZE = 0.2f;
	private Player player;
	//private Enemy enemy;
	//private Level level;
	private MeshLevel meshLevel;
	private Ray ray;
	private Array<Decal> decalInstances;
	private Array<BoundingBox> boxes;
	private Array<Projectile> projectiles;
	private float timer;
	private Vector3 position;
	private Vector3 out;
	public static ParticleManager particleManager;
    //private TiledMapTileLayer layer = (TiledMapTileLayer)Assets.level2.getLayers().get(0);
	
	public World() {
		Weapon weapon = new Weapon(0.1f, true, "GUNFBX.g3db", 0, null, 1, true, true, new Vector3(-1, 0, 0), 
								   new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0));
		player = new Player(this, 100, weapon, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
						    new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.modelBuilder.createBox(1f, 1f, 1f, 
						    Assets.floorMat, 1)));
		particleManager = new ParticleManager(this);
		meshLevel = new MeshLevel(Assets.castle, true);
		Entity.entityInstances.add(player);
		//meshLevel.getInstances().add(player.model);
		decalInstances = new Array<Decal>();
		boxes = new Array<BoundingBox>();
		projectiles = new Array<Projectile>();
		position = new Vector3();
		out = new Vector3();
	}
	
	public void update(float delta) {
		rayPick();
		updateEntities(delta);
		timer += delta;
	}
	
	private void updateEntities(float delta) {
		int size = Entity.entityInstances.size;
		
		for (int i = 0; i < size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			
			if (entity.isActive()) {
				entity.update(delta);
			}
			
			else {
				System.out.println("Removed: " + size);
				entity.removeEntity(i);
				size -= 1;
			}
		}
	}
	
	//Bounding boxes used for frustum culling and entities
	public void createBoundingBoxes() {
		int size = meshLevel.getInstances().size;
		//int length = meshLevel.getEntityInstances().size;
		
		for (int i = 0; i < size; i++) {
			BoundingBox box = new BoundingBox();
			meshLevel.getInstances().get(i).calculateBoundingBox(box);
			boxes.add(box);
		}
		
		/*
		for (int j = 0; j < length; j++) {
			Entity entity = meshLevel.getEntityInstances().get(j);
			entity.model.calculateBoundingBox(entity.boundingBox).mul(entity.model.transform);
		}
		*/
	}
	
	//Temporary
	public void rayPick() {
		if (player.isMouseLeft() && player.getCurrentWeapon() != null && 
			timer >= player.getCurrentWeapon().getFiringDelay()) {
			ray = player.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			timer = 0;
			rayPickLevel();
			rayPickEntities();
			fireWeapon();
		}
	}
	
	public void rayPickLevel() {
		int size = meshLevel.getInstances().size;
		int result = -1;
		float distance = -1;
		
		if (ray != null) {
			for (int i = 0; i < size; i++) {
				ModelInstance model = meshLevel.getInstances().get(i);
				
				model.transform.getTranslation(position);
				position.add(boxes.get(i).getCenter());
				float dist2 = ray.origin.dst2(position);
				
				if (distance >= 0f && dist2 > distance)
					continue;
		
				if (Intersector.intersectRayBoundsFast(ray, boxes.get(i))) {
					result = i;
					distance = dist2;
					
					if (result > -1) {
						Intersector.intersectRayBounds(ray, boxes.get(i), out);
					}
				}
			}
		}
	}
	
	public void rayPickEntities() {
		/*
		int size = meshLevel.getEntityInstances().size;
		int result = -1;
		float distance = -1;
		
		if (ray != null) {
			for (int i = 0; i < size; i++) {
				Entity entity = meshLevel.getEntityInstances().get(i);
				
				entity.get.transform.getTranslation(position);
				position.add(entity.boundingBox.getCenter());
				float dist2 = ray.origin.dst2(position);
				
				if (distance >= 0f && dist2 > distance)
					continue;
		
				if (Intersector.intersectRayBoundsFast(ray, entity.boundingBox)) {
					result = i;
					distance = dist2;
					
					if (result > -1) {
						Intersector.intersectRayBounds(ray, entity.boundingBox, out);
					}
				}
			}
		}
		*/
	}
	
	//Temporary
	public void fireWeapon() {
		if (player.getCurrentWeapon().isParticleWeapon()) {
			Vector3 rotation = new Vector3(0, 0, 0);
			Vector3 scale = new Vector3(0, 0, 0);
			
			//position, rotation, scale, angVelocity, velocity, angAccel, acceleration, active, index, collision
			Projectile projectile = new Projectile(player.getCurrentWeapon(), 6, true, true, player.camera.position.cpy(), 
												   rotation, scale, player.camera.direction.cpy(), player.camera.direction.cpy(), 
												   10, 0.1f, particleManager.mistPool.obtain(), this);
			Entity.entityInstances.add(projectile);
		}
	}
	
	public void setDecals() {
		Decal decal = Decal.newDecal(Assets.test1, true);
		decal.setPosition(out);
		decal.lookAt(player.camera.position, player.camera.position.cpy().nor());
		decal.setScale(0.001f);
		decal.value = 6;
		decalInstances.add(decal);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Array<Decal> getDecals() {
		return decalInstances;
	}
	
	public Array<BoundingBox> getBoundingBoxes() {
		return boxes;
	}
	
	public Array<ModelInstance> getLevelMesh() {
		return meshLevel.generateLevel();
	}
	
	public MeshLevel getMeshLevel() {
		return meshLevel;
	}
	
	public Ray getRay() {
		return ray;
	}
	
	public Vector3 getOut() {
		return out;
	}
	
	public Array<Projectile> getProjectiles() {
		return projectiles;
	}
}
