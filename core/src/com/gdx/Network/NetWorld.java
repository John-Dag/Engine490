package com.gdx.Network;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.Network.Net.NewProjectile;
import com.gdx.Network.Net.PlayerPacket;
import com.gdx.Network.NetClientEvent.ProjectileCollision;
import com.gdx.Weapons.RocketLauncher;
import com.gdx.Shaders.ColorMultiplierEntityShader;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;
import com.gdx.engine.EntityManager;
import com.gdx.engine.MeshLevel;
import com.gdx.engine.ParticleManager;
import com.gdx.engine.World;

public class NetWorld extends World {
	public NetWorld() {
		Assets.loadModels();
		player = new Player(this, 100, null, 2, true, true, startVector, new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
							new Vector3(0, 0, 0), new Vector3(0, 0, 0), null);
		setPlayer(player);
		//playerInstances.add(player);
		particleManager = new ParticleManager(this);
		player.initAbilities();
		player.initWeapons();
		setMeshLevel(new MeshLevel(Assets.castle3Multi, true, this));
		Entity.entityInstances.add(player);
		//distanceMap = new DistanceTrackerMap(getMeshLevel(), 2 + 32 * 2);
		entityManager = new EntityManager(this);
		setNetEventManager(new NetClientEventManager(this));
		setServerEventManager(new NetServerEventManager(this));
		
//		RocketLauncher launcher = (RocketLauncher) new RocketLauncher().spawn(getPlayer().getPosition());
//		Weapon noWeapon = new Weapon();
//		getPlayer().setWeapon(noWeapon);
	}

	@Override
	public void updatePlayers(PlayerPacket packet) {
		for (int i = 0; i < playerInstances.size; i++) {
			if (this.playerInstances.get(i).getNetId() == packet.id) {
				//System.out.println(packet.id);
				playerInstances.get(i).camera.position.set(packet.position);
				playerInstances.get(i).camera.direction.set(packet.direction);
				//Check this out later. 
				playerInstances.get(i).setPosition(playerInstances.get(i).camera.position);
				playerInstances.get(i).setTarget(playerInstances.get(i).getTarget().idt());
				playerInstances.get(i).setTarget(playerInstances.get(i).getTarget().translate(playerInstances.get(i).getPosition()));
				playerInstances.get(i).getBulletObject().setWorldTransform(playerInstances.get(i).getTarget());
			}
		}
	}
	
	@Override
	public void updateProjectiles(Net.ProjectilePacket packet) {
		for (int i = 0; i < Entity.entityInstances.size; i++) {
			if (Entity.entityInstances.get(i) instanceof Projectile) {
				Projectile projectile = (Projectile)Entity.entityInstances.get(i);
				//System.out.println(" " + projectile.getNetId() + " " + packet.id);
				if (projectile.getNetId() == packet.id) {
					projectile.getPosition().set(packet.position);
					//System.out.println(packet.position);
				}
			}
		}
	}

	@Override
	public void addProjectile(Net.NewProjectile packet) {
		System.out.println("added projectile");
		Projectile projectile = NetWorld.entityManager.projectilePool.obtain();
		projectile.reset();
		projectile.setProjectileSpeed(20f);
		projectile.setDamage(RocketLauncher.DAMAGE);
		projectile.setPosition(packet.position);
		projectile.setVelocity(packet.cameraPos);
		projectile.setAcceleration(packet.cameraPos);
		projectile.setNetId(packet.id);
		//projectile.getMotionState().transform = projectile.calculateTarget(packet.cameraPos);
		projectile.getBulletBody().setWorldTransform(projectile.calculateTarget(packet.cameraPos));
		Ray ray = new Ray(packet.rayOrigin, packet.rayDirection);
		projectile.getBulletBody().applyCentralImpulse(ray.direction.scl(300f));
		Entity.entityInstances.add(projectile);
		World.dynamicsWorld.addRigidBody(projectile.getBulletBody());
	}
	
	@Override
	public void sendProjectilePositionUpdate(Projectile projectile) {
		Net.ProjectilePacket packet = new Net.ProjectilePacket();
		packet.position = projectile.getPosition();
		packet.id = projectile.getNetId();
		this.getServer().updateProjectiles(packet);
	}
	
	@Override
	public void addPlayer(Net.NewPlayer playerPacket) {
		try {
			Player player1 = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
			player1.setAnimation(new AnimationController(player1.getModel()));
			GridPoint2 playerPos = new GridPoint2();
			playerPos.set(getMeshLevel().getStartingPoint());
			player1.camera.position.set(playerPacket.position.x, playerPacket.position.y - .5f, playerPacket.position.z);
			player1.getModel().transform.setToTranslation(player1.getPosition());
			player1.setNetId(playerPacket.id);
			player1.setNetName(playerPacket.name);
			player1.getAnimation().setAnimation("Walking", -1);
			ColorMultiplierEntityShader es=new ColorMultiplierEntityShader();
			es.multiplier.y=(float)Math.random();
			es.multiplier.x=(float)Math.random();
			es.multiplier.z=(float)Math.random();
			player1.setShader(es);
			playerInstances.add(player1);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	//Collisions between projectile/player are checked by the server only.
//	@Override
//	public void checkProjectileCollisions(Projectile projectile) {
//		for (Player player : playerInstances) {
//			if (projectile.getBoundingBox().intersects(player.getTransformedBoundingBox()) && projectile.getOriginID() != player.getNetId()
//				&& !projectile.hasDealtDamage()) {
//				createCollisionEvent(projectile, player);
//			}
//		}
//	}
	
	@Override
	public void checkProjectileCollisionsServer(Projectile projectile) {
		boolean collision = false;
		
		for (Player player : playerInstances) {
			if (projectile.getOriginID() != player.getNetId() && !projectile.hasDealtDamage()) {
				collision = checkCollision(player.getBulletObject(), projectile.getBulletObject());
				
				if (collision)
					createCollisionEvent(projectile, player);
			}
		}
	}
	
	public void createCollisionEvent(Projectile projectile, Player player) {
		Net.CollisionPacket packet = new Net.CollisionPacket();
		packet.projectileID = projectile.getNetId();
		packet.playerID = player.getNetId();
		packet.playerOriginID = projectile.getOriginID();
		packet.position = projectile.getPosition();
		packet.damage = projectile.getDamage();
		NetServerEvent.ProjectileCollision event = new NetServerEvent.ProjectileCollision(packet);
		this.getServerEventManager().addNetEvent(event);
		projectile.setDealtDamage(true);
	}
	
	public void createExplosionEffect(Net.CollisionPacket packet) {
		for (int i = 0; i < Entity.entityInstances.size; i++) {
			Entity entity = Entity.entityInstances.get(i);
			if (entity instanceof Projectile) {
				Projectile projectile = (Projectile)entity;
				if (projectile.getNetId() == packet.projectileID) {
//					projectile.initializeCollisionExplosionEffect();
				}
			}
		}
	}
}
