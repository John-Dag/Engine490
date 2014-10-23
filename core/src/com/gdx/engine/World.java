package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.DynamicEntities.Enemy;

public class World {
	public static final float PLAYER_SIZE = 0.2f;
	public static Player player;
	public static ParticleManager particleManager;
	public static Array<Enemy> enemyInstances;
	private MeshLevel meshLevel;
	private Ray ray;
	private Array<BoundingBox> boxes;
	private float timer;
	private Vector3 position;
	private Vector3 out;
    private DistanceTrackerMap distanceMap;
	
	public World() {
		player = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
						    new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.modelBuilder.createBox(1f, 1f, 1f, 
						    Assets.floorMat, Usage.Position | Usage.Normal | Usage.TextureCoordinates)));
		particleManager = new ParticleManager(this);
		meshLevel = new MeshLevel(Assets.castle, true);
		distanceMap = new DistanceTrackerMap((TiledMapTileLayer)meshLevel.getTiledMap().getLayers().get(0), 400);
		Entity.entityInstances.add(player);
		enemyInstances = new Array<Enemy>();
		boxes = new Array<BoundingBox>();
		position = new Vector3();
		out = new Vector3();
	}
	
	public void update(float delta) {
		rayPick();
		updateEntities(delta);
		timer += delta;
	}
	
	public void initializeEntities() {
		for (Entity entity : Entity.entityInstances) {
			entity.initialize(this);
		}
	}
	
	private void updateEntities(float delta) {
		int size = Entity.entityInstances.size;
		
		for (int i = 0; i < size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			
			if (entity.isActive()) {
				entity.update(delta, this);
			}
			
			else {
				//System.out.println("Removed: " + size);
				entity.removeEntity(i);
				size -= 1;
			}
		}
	}
	
	public void checkProjectileCollisions(Projectile projectile) {
		for (Enemy enemy : enemyInstances) {
			if (projectile.getBoundingBox().intersects(enemy.getTransformedEnemyBoundingBox())) {
				enemy.takeDamage(projectile.getDamage());
				projectile.removeProjectile();
			}
		}
	}
	
	//Bounding boxes used for frustum culling
	public void createBoundingBoxes() {
		int size = meshLevel.getInstances().size;
		
		for (int i = 0; i < size; i++) {
			BoundingBox box = new BoundingBox();
			meshLevel.getInstances().get(i).calculateBoundingBox(box);
			boxes.add(box);
		}
	}
	
	//Temporary
	public void rayPick() {
		if (player.isMouseLeft() && player.getWeapon() != null && 
			timer >= player.getWeapon().getFiringDelay()) {
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
		int size = Entity.entityInstances.size;
		int result = -1;
		float distance = -1;
		Enemy enemy;
		
		if (ray != null) {
			for (int i = 0; i < size; i++) {
				Entity entity = Entity.entityInstances.get(i);
				if (entity instanceof Enemy) {
					enemy = (Enemy)entity;
					enemy.getModel().transform.getTranslation(position);
					position.add(enemy.getTransformedBoundingBox().getCenter());
					float dist2 = ray.origin.dst2(position);
					
					if (distance >= 0f && dist2 > distance)
						continue;
			
					if (Intersector.intersectRayBoundsFast(ray, enemy.getTransformedBoundingBox())) {
						result = i;
						distance = dist2;
						
						if (result > -1) {
							Intersector.intersectRayBounds(ray, enemy.getTransformedBoundingBox(), out);
						}
					}
				}
			}
		}
	}
	
	//Temporary
	public void fireWeapon() {
		if (player.getWeapon().isParticleWeapon()) {
			Vector3 rotation = new Vector3(0, 0, 0);
			Vector3 scale = new Vector3(0, 0, 0);
			
			//position, rotation, scale, angVelocity, velocity, angAccel, acceleration, active, index, collision
			Projectile projectile = new Projectile(6, true, true, player.camera.position.cpy(), 
												   rotation, scale, player.camera.direction.cpy(), player.camera.direction.cpy(), 
												   10, 0.1f, particleManager.projectilePool.obtain(), this);
			Entity.entityInstances.add(projectile);
		}
	}
	
	/*
	public void setDecals() {
		Decal decal = Decal.newDecal(Assets.test1, true);
		decal.setPosition(out);
		decal.lookAt(player.camera.position, player.camera.position.cpy().nor());
		decal.setScale(0.001f);
		decal.value = 6;
		decalInstances.add(decal);
	}
	*/
	
	public DistanceTrackerMap getDistanceMap() {
		return distanceMap;
	}

	public void setDistanceMap(DistanceTrackerMap distanceMap) {
		this.distanceMap = distanceMap;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Array<BoundingBox> getBoundingBoxes() {
		return boxes;
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
}
