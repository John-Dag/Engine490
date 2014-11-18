package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gdx.DynamicEntities.Ability;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Weapon;

public class World implements Disposable {
	public static final float PLAYER_SIZE = 0.2f;
    public static boolean isWireframeEnabled;
	public static Player player;
	public static ParticleManager particleManager;
	public static Array<Enemy> enemyInstances;
	public Array<ModelInstance> wireInstances;
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
			player.camera.position.set(playerPos.x + 0.5f, player.camera.position.y, playerPos.y + 0.5f);
		} else {
			player = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.modelBuilder.createBox(1f, 1f, 1f, 
							Assets.floorMat, Usage.Position | Usage.Normal | Usage.TextureCoordinates)));
			// must come before meshlevel, and after player
			particleManager = new ParticleManager(this);
			player.initAbilities();
			meshLevel = new MeshLevel(Assets.castle3, true);
		}
		
		//distanceMap = new DistanceTrackerMap((TiledMapTileLayer)meshLevel.getTiledMap().getLayers().get(0), 2 + 32 * 2);
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
		Entity.entityInstances.add(player);
		enemyInstances = new Array<Enemy>();
		boxes = new Array<BoundingBox>();
		position = new Vector3();
		out = new Vector3();
		wireInstances = new Array<ModelInstance>();
		isWireframeEnabled = false;
	}
	
	public void enterDungeon() {
		Entity.entityInstances.clear();
		enemyInstances.clear();
		meshLevel.getInstances().clear();
		particleManager.system.removeAll();
		Render.environment.pointLights.removeRange(0, Render.environment.pointLights.size - 1);
		meshLevel = new MeshLevel(true);
		GridPoint2 playerPos = new GridPoint2();
		playerPos.set(meshLevel.getStartingPoint());
		player.camera.position.set(playerPos.x+0.5f, player.camera.position.y, playerPos.y+0.5f);
		player.camera.lookAt(50f, 1.5f, 50f);
		Entity.entityInstances.add(player);
		if (player.getWeapon() != null)
			Entity.entityInstances.add(player.getWeapon());
		initializeEntities();
		boxes.clear();
		createBoundingBoxes();
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
	}
	
	public void loadLevel(TiledMap map) {
		Entity.entityInstances.clear();
		enemyInstances.clear();
		meshLevel.getInstances().clear();
		particleManager.system.removeAll();
		Render.environment.pointLights.removeRange(0, Render.environment.pointLights.size - 1);
		try {
			meshLevel = new MeshLevel(map, true);
		} catch(Exception e) {
			System.err.println("Error loading specified map. Loading default.");
			meshLevel = new MeshLevel(Assets.castle3, true);
		}
		GridPoint2 playerPos = new GridPoint2();
		playerPos.set(meshLevel.getStartingPoint());
		player.camera.position.set(playerPos.x+0.5f, player.camera.position.y, playerPos.y+0.5f);
		player.camera.lookAt(50f, 1.5f, 50f);
		Entity.entityInstances.add(player);
		initializeEntities();
		boxes.clear();
		createBoundingBoxes();
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
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
		
		wireInstances.clear();
		
		for (int i = 0; i < size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			
			if (entity.isActive()) {
				entity.update(delta, this);
				
				//
				if(isWireframeEnabled) {
					Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
					BoundingBox box;
					Vector3[] corners;
					if(entity instanceof Enemy) {
						box = ((Enemy) entity).getTransformedBoundingBox();
						if(box != null) {
							
							corners = box.getCorners();
							//meshLevel.meshPartBuilder.setColor(Color.GREEN);
							createWireframeBox(corners, material);
							meshLevel.instance = new ModelInstance(meshLevel.model);
							wireInstances.add(meshLevel.instance);
						}
					}
					if(entity instanceof Projectile) {
						box = ((DynamicEntity) entity).getBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							meshLevel.meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							meshLevel.instance = new ModelInstance(meshLevel.model);
							wireInstances.add(meshLevel.instance);
						}
					}
					if(entity instanceof Ability) {
						box = ((Ability) entity).getBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							meshLevel.meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							meshLevel.instance = new ModelInstance(meshLevel.model);
							wireInstances.add(meshLevel.instance);
						}
					}
					if(entity instanceof Weapon) {
						box = ((Weapon) entity).getTransformedBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							meshLevel.meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							meshLevel.instance = new ModelInstance(meshLevel.model);
							wireInstances.add(meshLevel.instance);
						}
					}
					if(entity instanceof Player) {
						box = ((Player) entity).getTransformedBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							meshLevel.meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							meshLevel.instance = new ModelInstance(meshLevel.model);
							wireInstances.add(meshLevel.instance);
						}
					}
				}
			}
			
			else {
				//System.out.println("Removed: " + size);
				entity.removeEntity(i);
				size -= 1;
			}
		}
	}
	
	private ModelInstance createWireframeBox(Vector3[] corners, Material material) {
		meshLevel.modelBuilder.begin();
		meshLevel.meshPartBuilder = meshLevel.modelBuilder.part("boxes", 
				GL20.GL_LINES,
				Usage.Position | Usage.Color, material);
		meshLevel.meshPartBuilder.line(corners[0], corners[1]);
		meshLevel.meshPartBuilder.line(corners[1], corners[2]);
		meshLevel.meshPartBuilder.line(corners[2], corners[3]);
		meshLevel.meshPartBuilder.line(corners[3], corners[0]);
		meshLevel.meshPartBuilder.line(corners[4], corners[5]);
		meshLevel.meshPartBuilder.line(corners[5], corners[6]);
		meshLevel.meshPartBuilder.line(corners[6], corners[7]);
		meshLevel.meshPartBuilder.line(corners[7], corners[4]);
		meshLevel.meshPartBuilder.line(corners[0], corners[4]);
		meshLevel.meshPartBuilder.line(corners[1], corners[5]);
		meshLevel.meshPartBuilder.line(corners[2], corners[6]);
		meshLevel.meshPartBuilder.line(corners[3], corners[7]);
		meshLevel.model = meshLevel.modelBuilder.end();
		return new ModelInstance(meshLevel.model);
	}
	
	public void checkProjectileCollisions(Projectile projectile) {
		for (Enemy enemy : enemyInstances) {
			if (projectile.getBoundingBox().intersects(enemy.getTransformedBoundingBox())) {
				//projectile.initializeBloodEffect();
				projectile.initializeCollisionExplosionEffect();
				enemy.takeDamage(player.getWeapon().getDamage());
				projectile.removeProjectile();
			}
		}
	}
	
	public void checkAbilityCollision(Ability ability) {
		for (Enemy enemy : enemyInstances) {
			if (ability.getBoundingBox().intersects(enemy.getTransformedBoundingBox())) {
				enemy.takeDamage(ability.getDamage());
				if (ability.isStunAbility()) 
					enemy.setVelocity(new Vector3(0, 0, 0));
			}
		}
	}
	
	public void checkWeaponCollision(Weapon weapon) {
		for (Enemy enemy : enemyInstances) {
			if (weapon.getTransformedBoundingBox().intersects(enemy.getTransformedBoundingBox())) {
				enemy.takeDamage(weapon.getDamage());
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

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
