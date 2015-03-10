package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
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
import com.esotericsoftware.kryonet.Client;
import com.gdx.DynamicEntities.Ability;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.Network.Net;
import com.gdx.Network.Net.CollisionPacket;
import com.gdx.Network.NetEventManager;
import com.gdx.Network.NetServer;
import com.gdx.Network.Net.NewProjectile;
import com.gdx.Network.Net.NewPlayer;
import com.gdx.Network.Net.PlayerPacket;
import com.gdx.Network.Net.ProjectilePacket;
import com.gdx.Network.NetClient;
import com.gdx.Network.NetServerEventManager;
import com.gdx.Weapons.RocketLauncher;

public class World implements Disposable {
	public static final float PLAYER_SIZE = 0.2f;
    public static boolean isWireframeEnabled;
	public static Player player;
	public static ParticleManager particleManager;
	public static Array<Enemy> enemyInstances;
	public Array<Player> playerInstances;
	public Array<ModelInstance> wireInstances;
	public Array<Projectile> projectileInstances;
	private MeshLevel meshLevel;
	private Ray ray;
	private Array<BoundingBox> boxes;
	private Vector3 position;
	private Vector3 out;
    private DistanceTrackerMap distanceMap;
    private FilterEffect filterEffect;
    private NetClient client;
    private NetServer server;
    private int NetIdCurrent;
	public static EntityManager entManager;
	private NetEventManager eventManager;
	private NetServerEventManager serverEventManager;
	public Vector3 startVector = new Vector3(2f, 1.5f, 2f);
    
	public World() {
		playerInstances = new Array<Player>();
		projectileInstances = new Array<Projectile>();
		enemyInstances = new Array<Enemy>();
		setBoxes(new Array<BoundingBox>());
		setPosition(new Vector3());
		setOut(new Vector3());
		wireInstances = new Array<ModelInstance>();
		isWireframeEnabled = false;
		//Octree octree = new Octree(null, new BoundingBox(new Vector3(0,0,0), new Vector3(4,4,4)), this);
	}
	
	
	//Order matters here
	public void loadOfflineWorld(TiledMap map, boolean skySphere) {
		try {
			Assets.loadModels();
			player = new Player(this, 100, null, 2, true, true, startVector, new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), null);
			particleManager = new ParticleManager(this);
			player.initAbilities();
			playerInstances.add(player);
			Entity.entityInstances.add(player);
			setMeshLevel(new MeshLevel(map, skySphere));
			distanceMap = new DistanceTrackerMap(getMeshLevel(), 2 + 32 * 2);
			entManager = new EntityManager(this);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void enterDungeon() {
		Entity.entityInstances.clear();
		enemyInstances.clear();
		getMeshLevel().getInstances().clear();
		particleManager.system.removeAll();
		Render.environment.pointLights.removeRange(0, Render.environment.pointLights.size - 1);
		setMeshLevel(new MeshLevel(true));
		GridPoint2 playerPos = new GridPoint2();
		playerPos.set(getMeshLevel().getStartingPoint());
		player.camera.position.set(playerPos.x+0.5f, player.camera.position.y, playerPos.y+0.5f);
		player.camera.lookAt(50f, 1.5f, 50f);
		Entity.entityInstances.add(player);
		if (player.getWeapon() != null)
			Entity.entityInstances.add(player.getWeapon());
		initializeEntities();
		getBoxes().clear();
		createBoundingBoxes();
		distanceMap = new DistanceTrackerMap(getMeshLevel(), 2 + 32 * 2);
	}
	
	public void loadLevel(TiledMap map) {
		Entity.entityInstances.clear();
		enemyInstances.clear();
		getMeshLevel().getInstances().clear();
		particleManager.system.removeAll();
		Render.environment.pointLights.removeRange(0, Render.environment.pointLights.size - 1);
		try {
			setMeshLevel(new MeshLevel(map, true));
		} catch(Exception e) {
			System.err.println("Error loading specified map. Loading default.");
			setMeshLevel(new MeshLevel(Assets.castle3, true));
		}
		GridPoint2 playerPos = new GridPoint2();
		playerPos.set(getMeshLevel().getStartingPoint());
		player.camera.position.set(playerPos.x+0.5f, player.camera.position.y, playerPos.y+0.5f);
		player.camera.lookAt(50f, 1.5f, 50f);
		Entity.entityInstances.add(player);
		initializeEntities();
		getBoxes().clear();
		createBoundingBoxes();
		distanceMap = new DistanceTrackerMap(getMeshLevel(), 2 + 32 * 2);
	}
	
	public void update(float delta) {
		updateEntities(delta);
		updateFilterEffect(this,delta);
	}
	
	private void updateFilterEffect(World world, float delta) {
		if(filterEffect!=null)
		{
			filterEffect.Update(world, delta);
		}
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
							getMeshLevel().instance = new ModelInstance(getMeshLevel().model);
							wireInstances.add(getMeshLevel().instance);
						}
					}
					if(entity instanceof Projectile) {
						box = ((DynamicEntity) entity).getBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							getMeshLevel().meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							getMeshLevel().instance = new ModelInstance(getMeshLevel().model);
							wireInstances.add(getMeshLevel().instance);
						}
					}
					if(entity instanceof Ability) {
						box = ((Ability) entity).getBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							getMeshLevel().meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							getMeshLevel().instance = new ModelInstance(getMeshLevel().model);
							wireInstances.add(getMeshLevel().instance);
						}
					}
					if(entity instanceof Weapon) {
						box = ((Weapon) entity).getTransformedBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							getMeshLevel().meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							getMeshLevel().instance = new ModelInstance(getMeshLevel().model);
							wireInstances.add(getMeshLevel().instance);
						}
					}
					if(entity instanceof Player) {
						box = ((Player) entity).getTransformedBoundingBox();
						if(box != null) {
							corners = box.getCorners();
							Color color = new Color(Color.GREEN);
							getMeshLevel().meshPartBuilder.setColor(color);
							createWireframeBox(corners, material);
							getMeshLevel().instance = new ModelInstance(getMeshLevel().model);
							wireInstances.add(getMeshLevel().instance);
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
		getMeshLevel().modelBuilder.begin();
		getMeshLevel().meshPartBuilder = getMeshLevel().modelBuilder.part("boxes", 
				GL20.GL_LINES,
				Usage.Position | Usage.Color, material);
		getMeshLevel().meshPartBuilder.line(corners[0], corners[1]);
		getMeshLevel().meshPartBuilder.line(corners[1], corners[2]);
		getMeshLevel().meshPartBuilder.line(corners[2], corners[3]);
		getMeshLevel().meshPartBuilder.line(corners[3], corners[0]);
		getMeshLevel().meshPartBuilder.line(corners[4], corners[5]);
		getMeshLevel().meshPartBuilder.line(corners[5], corners[6]);
		getMeshLevel().meshPartBuilder.line(corners[6], corners[7]);
		getMeshLevel().meshPartBuilder.line(corners[7], corners[4]);
		getMeshLevel().meshPartBuilder.line(corners[0], corners[4]);
		getMeshLevel().meshPartBuilder.line(corners[1], corners[5]);
		getMeshLevel().meshPartBuilder.line(corners[2], corners[6]);
		getMeshLevel().meshPartBuilder.line(corners[3], corners[7]);
		getMeshLevel().model = getMeshLevel().modelBuilder.end();
		return new ModelInstance(getMeshLevel().model);
	}
	
	public void checkProjectileCollisions(Projectile projectile) {
		for (Enemy enemy : enemyInstances) {
			if (projectile.getBoundingBox().intersects(enemy.getTransformedBoundingBox())) {
				//projectile.initializeBloodEffect();
				if (!projectile.hasDealtDamage())
					enemy.takeDamage(player.getWeapon().getDamage());
					projectile.initializeCollisionExplosionEffect();
			}
		}
	}
	
	public void checkAbilityCollision(Ability ability) {
		if (ability.isTicking()) {
			for (Enemy enemy : enemyInstances) {
				if (ability.getBoundingBox().intersects(enemy.getTransformedBoundingBox())) {
					if (ability.isStunAbility()) 
						enemy.setVelocity(new Vector3(0, 0, 0));
					enemy.takeDamage(ability.getDamage());
					ability.setTicking(false);
				}
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
		int size = getMeshLevel().getInstances().size;
		
		for (int i = 0; i < size; i++) {
			BoundingBox box = new BoundingBox();
			getMeshLevel().getInstances().get(i).calculateBoundingBox(box);
			getBoxes().add(box);
		}
	}
	
	//Temporary
//	public void rayPick() {
//
//	}
//	
//	public void rayPickLevel() {
//		int size = getMeshLevel().getInstances().size;
//		int result = -1;
//		float distance = -1;
//		
//		if (ray != null) {
//			for (int i = 0; i < size; i++) {
//				ModelInstance model = getMeshLevel().getInstances().get(i);
//				
//				model.transform.getTranslation(getPosition());
//				getPosition().add(getBoxes().get(i).getCenter());
//				float dist2 = ray.origin.dst2(getPosition());
//				
//				if (distance >= 0f && dist2 > distance)
//					continue;
//		
//				if (Intersector.intersectRayBoundsFast(ray, getBoxes().get(i))) {
//					result = i;
//					distance = dist2;
//					
//					if (result > -1) {
//						Intersector.intersectRayBounds(ray, getBoxes().get(i), getOut());
//					}
//				}
//			}
//		}
//	}
//	
//	public void rayPickEntities() {
//		int size = Entity.entityInstances.size;
//		int result = -1;
//		float distance = -1;
//		Enemy enemy;
//		
//		if (ray != null) {
//			for (int i = 0; i < size; i++) {
//				Entity entity = Entity.entityInstances.get(i);
//				if (entity instanceof Enemy) {
//					enemy = (Enemy)entity;
//					enemy.getModel().transform.getTranslation(getPosition());
//					getPosition().add(enemy.getTransformedBoundingBox().getCenter());
//					float dist2 = ray.origin.dst2(getPosition());
//					
//					if (distance >= 0f && dist2 > distance)
//						continue;
//			
//					if (Intersector.intersectRayBoundsFast(ray, enemy.getTransformedBoundingBox())) {
//						result = i;
//						distance = dist2;
//						
//						if (result > -1) {
//							Intersector.intersectRayBounds(ray, enemy.getTransformedBoundingBox(), getOut());
//						}
//					}
//				}
//			}
//		}
//	}
	
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

	public NetServer getServer() {
		return server;
	}

	public NetClient getClient() {
		return client;
	}

	public void setClient(NetClient client) {
		this.client = client;
	}

	public Array<Player> getPlayerInstances() {
		return playerInstances;
	}

	public void setPlayerInstances(Array<Player> playerInstances) {
		this.playerInstances = playerInstances;
	}

	public void setDistanceMap(DistanceTrackerMap distanceMap) {
		this.distanceMap = distanceMap;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	// these bounding boxes are for the levelMesh only
	public Array<BoundingBox> getBoundingBoxes() {
		return getBoxes();
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
	
	public FilterEffect getFilterEffect() {
		return filterEffect;
	}

	public void setFilterEffect(FilterEffect filterEffect) {
		if (this.filterEffect != null) {
			this.filterEffect.dispose();
		}
		
		this.filterEffect = filterEffect;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void setMeshLevel(MeshLevel meshLevel) {
		this.meshLevel = meshLevel;
	}

	public Array<BoundingBox> getBoxes() {
		return boxes;
	}

	public void setBoxes(Array<BoundingBox> boxes) {
		this.boxes = boxes;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setOut(Vector3 out) {
		this.out = out;
	}

	public void updatePlayers(PlayerPacket packet) {
		// TODO Auto-generated method stub
		
	}

	public void updateProjectiles(ProjectilePacket packet) {
		// TODO Auto-generated method stub
		
	}

	public void addProjectile(NewProjectile packet) {
		// TODO Auto-generated method stub
		
	}

	public void addPlayer(NewPlayer playerPacket) {
		// TODO Auto-generated method stub
		
	}

	public void setServer(NetServer server) {
		this.server = server;
	}

	public void sendProjectilePositionUpdate(Projectile projectile) {
		// TODO Auto-generated method stub
		
	}


	public int getNetIdCurrent() {
		return NetIdCurrent;
	}


	public void setNetIdCurrent(int netIdCurrent) {
		NetIdCurrent = netIdCurrent;
	}


	public NetEventManager getEventManager() {
		return eventManager;
	}


	public void setEventManager(NetEventManager eventManager) {
		this.eventManager = eventManager;
	}


	public NetServerEventManager getServerEventManager() {
		return serverEventManager;
	}


	public void setServerEventManager(NetServerEventManager serverEventManager) {
		this.serverEventManager = serverEventManager;
	}

	public void removeProjectile(CollisionPacket packet) {
		// TODO Auto-generated method stub
		
	}

	public void checkClientProjectileCollisions(Projectile projectile) {
		// TODO Auto-generated method stub
		
	}

	public void createExplosionEffect(CollisionPacket packet) {
		// TODO Auto-generated method stub
		
	}
}
