package com.gdx.engine;

import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.Enemies.Zombie;

public class ClientEvent {
	public boolean eventHandled;
	
	public ClientEvent() {
		super();
		eventHandled = false;
	}
	
	public static class CreateEntity extends ClientEvent {
		public Entity entity;
		
		public CreateEntity(Entity entity) {
			this.entity = entity;
		}
		
		@Override
		public void handleEvent(World world) {
			DynamicEntity temp = (DynamicEntity)entity;
			if (temp.getBulletBody() != null) {
				World.dynamicsWorld.addRigidBody(temp.getBulletBody());
				temp.getBulletBody().setUserValue(Entity.entityInstances.size);
			}
			else if (temp.getBulletObject() != null) {
				World.dynamicsWorld.addCollisionObject(temp.getBulletObject());
				temp.getBulletObject().setUserValue(Entity.entityInstances.size);
			}

			temp.setIndex(Entity.entityInstances.size);
			Entity.entityInstances.add(temp);
			this.eventHandled = true;
		}
	}
	
	public static class ProjectileCollision extends ClientEvent {
		public Entity entity;
		public int bulletId1, bulletId2;
		
		public ProjectileCollision(int bulletId1, int bulletId2) {
			this.bulletId1 = bulletId1;
			this.bulletId2 = bulletId2;
		}
		
		@Override
		public void handleEvent(World world) {
			if (GameScreen.mode == GameScreen.Mode.Client ||
				GameScreen.mode == GameScreen.Mode.Offline)
				world.handleCollisionProjectileEnemy(bulletId1, bulletId2);
			else
				world.handleCollisionProjectilePlayer(bulletId1, bulletId2);
			
			this.eventHandled = true;
		}
	}
	
	public static class RemoveEntity extends ClientEvent {
		public Entity entity;
		
		public RemoveEntity(Entity entity) {
			this.entity = entity;
		}
		
		@Override
		public void handleEvent(World world) {
			int removalIndex = -1;
			entity.setIsActive(false);

			for (int i = 0; i < Entity.entityInstances.size; i++) {
				if (!Entity.entityInstances.get(i).isActive()) {
						//System.out.println("REMOVING : " + entity + " INDEX: " + entity.getIndex());
						Entity.entityInstances.get(i).dispose();
						Entity.entityInstances.removeIndex(i);
						removalIndex = i;
						this.eventHandled = true;
					}
			}
			
			if (removalIndex != -1) {
				for (int j = removalIndex; j < Entity.entityInstances.size; j++) {
					Entity entity = Entity.entityInstances.get(j);
					entity.decrementBulletIndex();
				}
			}
		}
	}
	
	public void handleEvent(World world) {
		
	}
}
