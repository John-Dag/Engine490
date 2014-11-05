package com.gdx.engine;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
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
	private Vector3 position;
	private Vector3 out;
    private DistanceTrackerMap distanceMap;
	
	public World() {
		boolean bspDungeon = false;

		if(bspDungeon){

			// must come after meshlevel
			player = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.modelBuilder.createBox(1f, 1f, 1f, 
							Assets.floorMat, Usage.Position | Usage.Normal | Usage.TextureCoordinates)));

			particleManager = new ParticleManager(this);
			meshLevel = new MeshLevel(true);
			GridPoint2 playerPos = new GridPoint2();
			playerPos.set(meshLevel.getStartingPoint());
			player.camera.position.set(playerPos.x, player.camera.position.y, playerPos.y);

		}else{

			player = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.modelBuilder.createBox(1f, 1f, 1f, 
							Assets.floorMat, Usage.Position | Usage.Normal | Usage.TextureCoordinates)));

			// must come before meshlevel, and after player
			particleManager = new ParticleManager(this);
			meshLevel = new MeshLevel(Assets.castle, true);

		}
		//distanceMap = new DistanceTrackerMap((TiledMapTileLayer)meshLevel.getTiledMap().getLayers().get(0), 2 + 32 * 2);
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
		Entity.entityInstances.add(player);
		enemyInstances = new Array<Enemy>();
		boxes = new Array<BoundingBox>();
		position = new Vector3();
		out = new Vector3();
	}
	
	public void update(float delta) {
		updateEntities(delta);
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
				//projectile.initializeBloodEffect();
				projectile.initializeCollisionExplosionEffect();
				enemy.takeDamage(player.getWeapon().getDamage());
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
