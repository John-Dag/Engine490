package com.gdx.engine;

import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Projectile;

public class ClientEvent {
	public ClientEvent() {
		super();
	}
	
	public static class CreateEntity extends ClientEvent {
		public Entity entity;
		
		public CreateEntity(Entity entity) {
			this.entity = entity;
		}
		
		@Override
		public void handleEvent(World world) {
			Entity.entityInstances.add(entity);
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
			if (bulletId1 < Entity.entityInstances.size && bulletId2 < Entity.entityInstances.size) {
				Entity entity = Entity.entityInstances.get(bulletId1);
				System.out.println(entity);
				entity.handleCollision(20, bulletId2);
			}
		}
	}
	
	public static class RemoveEntity extends ClientEvent {
		public Entity entity;
		
		public RemoveEntity(Entity entity) {
			this.entity = entity;
		}
		
		@Override
		public void handleEvent(World world) {
			entity.setIsActive(false);
			Entity.entityInstances.removeValue(entity, true);
		}
	}
	
	public void handleEvent(World world) {
		
	}
}
