package lightning3d.Engine;

import lightning3d.DynamicEntities.Ability;
import lightning3d.DynamicEntities.DynamicEntity;
import lightning3d.DynamicEntities.Enemy;
import lightning3d.DynamicEntities.Player;
import lightning3d.DynamicEntities.Projectile;
import lightning3d.DynamicEntities.Weapon;
import lightning3d.Network.Net;
import lightning3d.Network.NetClient;
import lightning3d.Network.NetClientEventManager;
import lightning3d.Network.NetServer;
import lightning3d.Network.NetServerEvent;
import lightning3d.Network.NetServerEventManager;
import lightning3d.Network.Net.CollisionPacket;
import lightning3d.Network.Net.NewPlayer;
import lightning3d.Network.Net.NewProjectile;
import lightning3d.Network.Net.PlayerPacket;
import lightning3d.Network.Net.ProjectilePacket;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {
	public static final float PLAYER_SIZE = 0.2f;
    public static boolean isWireframeEnabled;
	public static Player player;
	public static ParticleManager particleManager;
	public static SoundManager soundManager;
	public static Array<Enemy> enemyInstances;
	public static EntityManager entityManager;
	public static ClientEventManager eventManager;
	public static btDynamicsWorld dynamicsWorld;
	public static NetServerEventManager serverEventManager;
	public static EntityManager entManager;
	public static short PROJECTILE_FLAG = 1<<8;
	public static short ENEMY_FLAG = 1<<9;
	public static short PLAYER_FLAG = 1<<10;
	public boolean bulletDebugDrawEnabled=false;
	public boolean bulletDebugDrawMeshLevelWiresEnabled=true;
	public Array<Player> playerInstances;
	public Array<ModelInstance> wireInstances;
	public Array<Projectile> projectileInstances;
	public Vector3 startVector = new Vector3(2f, 1.5f, 2f);
	private MeshLevel meshLevel;
	private Ray ray;
	private Array<BoundingBox> boxes;
	private Vector3 position;
	private Vector3 out;
    private static DistanceTrackerMap distanceMap;
    private FilterEffect filterEffect;
    private NetClient client;
    private NetServer server;
    private int NetIdCurrent;
	private NetClientEventManager clientEventManager;
	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private BulletContactListener contactListener;
	private btBroadphaseInterface broadPhase;
	private btConstraintSolver constraintSolver;
	private BulletTickCallback tickCallback;
    
	public World() {
		playerInstances = new Array<Player>();
		projectileInstances = new Array<Projectile>();
		enemyInstances = new Array<Enemy>();
		setBoxes(new Array<BoundingBox>());
		setPosition(new Vector3());
		setOut(new Vector3());
		wireInstances = new Array<ModelInstance>();
		isWireframeEnabled = false;
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		//Octree octree = new Octree(null, new BoundingBox(new Vector3(0,0,0), new Vector3(4,4,4)), this);
		broadPhase = new btDbvtBroadphase();
		setContactListener(new BulletContactListener(this));
		setContraintSolver(new btSequentialImpulseConstraintSolver());
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, -0f, 0));
		eventManager = new ClientEventManager(this);
		setTickCallback(new BulletTickCallback(dynamicsWorld));
		soundManager = new SoundManager();
	}
	
	//Order matters here
	public void loadOfflineWorld(TiledMap map, boolean skySphere) {
		try {
			Assets.loadModels();
			player = new Player(this, 100, null, 2, true, true, startVector, new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), null);
			particleManager = new ParticleManager(this);
			entityManager = new EntityManager(this);
			soundManager = new SoundManager();
			player.initAbilities();
			player.initWeapons();
			setPlayer(player);
			ClientEvent.CreateEntity event = new ClientEvent.CreateEntity(player);
			eventManager.addEvent(event);
			setMeshLevel(new MeshLevel(map, skySphere, this));
			distanceMap = new DistanceTrackerMap(getMeshLevel(), 2 + 32 * 2);
			meshLevel.generatePatrolPath();
		}
		catch (Exception e) {
			System.err.println(e);
		}
		// TODO fix merge
		/*
		//distanceMap = new DistanceTrackerMap((TiledMapTileLayer)meshLevel.getTiledMap().getLayers().get(0), 2 + 32 * 2);
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
		meshLevel.generatePatrolPath();
		Entity.entityInstances.add(player);
		enemyInstances = new Array<Enemy>();
		boxes = new Array<BoundingBox>();
		position = new Vector3();
		out = new Vector3();
		wireInstances = new Array<ModelInstance>();
		isWireframeEnabled = false;
		
		Octree octree = new Octree(null, new BoundingBox(new Vector3(0,0,0), new Vector3(4,4,4)), this);
		*/
		//Octree octree = new Octree(null, new BoundingBox(new Vector3(0,0,0), new Vector3(4,4,4)), this);

	}
	
	public void enterDungeon() {
		Entity.entityInstances.clear();
		enemyInstances.clear();
		getMeshLevel().getInstances().clear();
		particleManager.system.removeAll();
		Render.environment.pointLights.removeRange(0, Render.environment.pointLights.size - 1);
		setMeshLevel(new MeshLevel(true, this));
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
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
		meshLevel.generatePatrolPath();
	}
	
	public void loadLevel(TiledMap map) {
		Entity.entityInstances.clear();
		enemyInstances.clear();
		getMeshLevel().getInstances().clear();
		particleManager.system.removeAll();
		Render.environment.pointLights.removeRange(0, Render.environment.pointLights.size - 1);
		try {
			setMeshLevel(new MeshLevel(map, true, this));
		} catch(Exception e) {
			System.err.println("Error loading specified map. Loading default.");
			setMeshLevel(new MeshLevel(Assets.castle3, true, this));
		}
		GridPoint2 playerPos = new GridPoint2();
		playerPos.set(getMeshLevel().getStartingPoint());
		player.camera.position.set(playerPos.x+0.5f, player.camera.position.y, playerPos.y+0.5f);
		player.camera.lookAt(50f, 1.5f, 50f);
		Entity.entityInstances.add(player);
		initializeEntities();
		getBoxes().clear();
		createBoundingBoxes();
		distanceMap = new DistanceTrackerMap(meshLevel, 2 + 32 * 2);
		meshLevel.generatePatrolPath();
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
		//dynamicsWorld.stepSimulation(delta, 5, 1f/360f);
		dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
		eventManager.processEvents();
		wireInstances.clear();
		
		soundManager.updateEnvironmentSounds(this.getPlayer().getPosition(), this.getPlayer().camera.direction);
		
		for (int i = 0; i < Entity.entityInstances.size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			
			if (entity.isActive()) {
				entity.update(delta, this);
				if(entity.shader!=null)
					entity.shader.time+=delta;
				
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
//			}
			
//			else {
//				//System.out.println("Removed: " + size);
//				//entity.dispose();
//				Entity.entityInstances.removeIndex(i);
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
	
	public boolean checkProjectileCollisions(Projectile projectile) {
		boolean collision = false;
		
		for (Enemy enemy : enemyInstances) {
			//collision = checkCollision(enemy.getBulletObject(), projectile.getBulletObject());
			return collision;
		}
		
		return collision;
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
	
	public boolean checkCollision(btCollisionObject obj0, btCollisionObject obj1) {
		CollisionObjectWrapper co0 = new CollisionObjectWrapper(obj0);
	    CollisionObjectWrapper co1 = new CollisionObjectWrapper(obj1);
	     
	    btCollisionAlgorithm algorithm = dispatcher.findAlgorithm(co0.wrapper, co1.wrapper);
	 
	    btDispatcherInfo info = new btDispatcherInfo();
	    btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);
	     
	    algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);
	 
	    boolean r = result.getPersistentManifold().getNumContacts() > 0;
	 
	    dispatcher.freeCollisionAlgorithm(algorithm.getCPointer());
	    result.dispose();
	    info.dispose();
	    co1.dispose();
	    co0.dispose();
	    
	    return r;
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

	public void handleCollisionProjectileEnemy(int bulletId1, int bulletId2) {
		Projectile projectile = null;
		Enemy enemy = null;
		
		if (bulletId1 < Entity.entityInstances.size && bulletId2 < Entity.entityInstances.size) {
			if (Entity.entityInstances.get(bulletId1) instanceof Projectile) {
				projectile = (Projectile)Entity.entityInstances.get(bulletId1);
				projectile.getBulletBody().setContactCallbackFilter(0);
				projectile.setMoving(false);
			}
			
			else if (Entity.entityInstances.get(bulletId2) instanceof Projectile) {
				projectile = (Projectile)Entity.entityInstances.get(bulletId2);
				projectile.getBulletBody().setContactCallbackFilter(0);
				projectile.setMoving(false);
			}
			
			if (Entity.entityInstances.get(bulletId1) instanceof Enemy) {
				enemy = (Enemy)Entity.entityInstances.get(bulletId1);
			}
			
			else if (Entity.entityInstances.get(bulletId2) instanceof Enemy) {
				enemy = (Enemy)Entity.entityInstances.get(bulletId2);
			}
			
			if (enemy != null && projectile != null) {
				enemy.takeDamage(projectile.getDamage());
				projectile.setMoving(false);
			}
		}
	}
	
	public void handleCollisionProjectilePlayer(int bulletId1, int bulletId2) {
		Projectile projectile = null;
		Player player = null;
		
		if (bulletId1 < Entity.entityInstances.size) {
			if (Entity.entityInstances.get(bulletId1) instanceof Projectile) {
				projectile = (Projectile)Entity.entityInstances.get(bulletId1);
			}
			
			else if (bulletId1 < playerInstances.size) {
				if (playerInstances.get(bulletId1) instanceof Player) {
					player = playerInstances.get(bulletId1);
				}
			}
		}
		
		if (bulletId2 < Entity.entityInstances.size) {
			if (Entity.entityInstances.get(bulletId2) instanceof Projectile) {
				projectile = (Projectile)Entity.entityInstances.get(bulletId2);
			}
			
			else if (bulletId2 < playerInstances.size) {
				if (playerInstances.get(bulletId2) instanceof Player) {
					player = playerInstances.get(bulletId2);
				}
			}
		}
			
		if (player != null && projectile != null && projectile.getOriginID() != player.getNetId()) {
			projectile.setMoving(false);
			if (GameScreen.mode == GameScreen.Mode.Server) {
				Net.CollisionPacket packet = new Net.CollisionPacket();
				packet.playerOriginID = projectile.getOriginID();
				packet.playerID = player.getNetId();
				packet.projectileID = projectile.getNetId();
				packet.damage = projectile.getDamage();
				packet.position = new Vector3();
				if (projectile.getBulletBody() != null) {
					packet.position.set(projectile.getBulletBody().getWorldTransform().getTranslation(new Vector3()));
					NetServerEvent.ProjectileCollision event = new NetServerEvent.ProjectileCollision(packet);
					World.serverEventManager.addNetEvent(event);
				}
			}
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
	
	public static DistanceTrackerMap getDistanceMap() {
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
		dispatcher.dispose();
		dynamicsWorld.dispose();
		broadPhase.dispose();
		dispatcher.dispose();
		constraintSolver.dispose();
		collisionConfig.dispose();
	}

	public void setMeshLevel(MeshLevel meshLevel) {
		if(this.meshLevel!=null)
			this.meshLevel.dispose();
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

	public NetClientEventManager getNetEventManager() {
		return clientEventManager;
	}


	public void setNetEventManager(NetClientEventManager eventManager) {
		this.clientEventManager = eventManager;
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

	public void checkProjectileCollisionsServer(Projectile projectile) {
		// TODO Auto-generated method stub
		
	}

	public void setEventManager(ClientEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public ClientEventManager getClientEventManager() {
		return eventManager;
	}

	public BulletContactListener getContactListener() {
		return contactListener;
	}

	public void setContactListener(BulletContactListener contactListener) {
		this.contactListener = contactListener;
	}

	public btDynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}

	public void setDynamicsWorld(btDynamicsWorld dynamicsWorld) {
		this.dynamicsWorld = dynamicsWorld;
	}

	public btConstraintSolver getContraintSolver() {
		return constraintSolver;
	}

	public void setContraintSolver(btConstraintSolver contraintSolver) {
		this.constraintSolver = contraintSolver;
	}

	public BulletTickCallback getTickCallback() {
		return tickCallback;
	}

	public void setTickCallback(BulletTickCallback tickCallback) {
		this.tickCallback = tickCallback;
	}
}
