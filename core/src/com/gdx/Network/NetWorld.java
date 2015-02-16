package com.gdx.Network;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.Network.Net.newProjectile;
import com.gdx.Network.Net.playerPacket;
import com.gdx.engine.Assets;
import com.gdx.engine.DistanceTrackerMap;
import com.gdx.engine.Entity;
import com.gdx.engine.EntityManager;
import com.gdx.engine.MeshLevel;
import com.gdx.engine.ParticleManager;
import com.gdx.engine.World;

public class NetWorld extends World {
	//public static EntityManager entManager;
	
	public NetWorld() {
		Assets.loadModels();
		player = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
							new Vector3(0, 0, 0), new Vector3(0, 0, 0), null);
		setPlayer(player);
		playerInstances.add(player);
		particleManager = new ParticleManager(this);
		player.initAbilities();
		setMeshLevel(new MeshLevel(Assets.castle3Multi, true));
		Entity.entityInstances.add(player);
		//entManager = new EntityManager(this);
		//distanceMap = new DistanceTrackerMap(getMeshLevel(), 2 + 32 * 2);
	}

	@Override
	public synchronized void updatePlayers(playerPacket packet) {
		for (int i = 0; i < playerInstances.size; i++) {
			if (this.playerInstances.get(i).getNetId() == packet.id) {
				//System.out.println(packet.id);
				playerInstances.get(i).camera.position.set(packet.position);
			}
		}
	}
	
	@Override
	public synchronized void updateProjectiles(Net.projectile packet) {
		for (int i = 0; i < Entity.entityInstances.size; i++) {
			if (Entity.entityInstances.get(i) instanceof Projectile) {
				Projectile projectile = (Projectile)Entity.entityInstances.get(i);
				System.out.println(" " + projectile.getNetId() + " " + packet.id);
				if (projectile.getNetId() == packet.id) {
					projectile.getPosition().set(packet.position);
					System.out.println(packet.position);
				}
			}
		}
	}

	@Override
	public synchronized void addProjectile(newProjectile packet) {
		Vector3 rotation = new Vector3(0, 0, 0);
		Vector3 scale = new Vector3(0, 0, 0);
		Projectile projectile = new Projectile(6, true, true, packet.position, 
				   rotation, scale, packet.cameraPos,  packet.cameraPos, 
				   World.particleManager.projectilePool.obtain(), World.particleManager.rocketExplosionPool.obtain(), this);
		projectile.setProjectileSpeed(5.0f);
		projectile.setDamage(10);
		projectile.setNetId(packet.id);
		Entity.entityInstances.add(projectile);
	}
	
	@Override
	public void sendProjectilePositionUpdate(Projectile projectile) {
		Net.projectile packet = new Net.projectile();
		packet.position = projectile.getPosition();
		packet.id = projectile.getNetId();
		this.getServer().updateProjectiles(packet);
	}
	
	@Override
	public void addPlayer(Net.playerNew playerPacket) {
		try {
			Player player1 = new Player(this, 100, null, 2, true, true, new Vector3(2f, 1.5f, 2f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					new Vector3(0, 0, 0), new Vector3(0, 0, 0), new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
			player1.setAnimation(new AnimationController(player1.getModel()));
			GridPoint2 playerPos = new GridPoint2();
			playerPos.set(getMeshLevel().getStartingPoint());
			player1.camera.position.set(playerPos.x + 0.5f, player.camera.position.y, playerPos.y + 0.5f);
			player1.getModel().transform.setToTranslation(player1.getPosition());
			player1.setNetId(playerPacket.id);
			playerInstances.add(player1);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	@Override
	public void checkProjectileCollisions(Projectile projectile) {
		for (Entity entity : Entity.entityInstances) {
			if (entity instanceof Player) {
				if (projectile.getBoundingBox().intersects(entity.getTransformedBoundingBox()) && !projectile.isPlayerProjectile()) {
					projectile.initializeCollisionExplosionEffect();
					player.takeDamage(projectile.getDamage());
					projectile.removeProjectile();
				}
			}
		}
	}
}
