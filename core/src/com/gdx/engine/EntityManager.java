package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Projectile;

public class EntityManager {
	public World world;
	public EntityPool projectilePool;
	public Projectile projectile;
	
	public EntityManager(World world) {
		this.world = world;
		Projectile projectile = new Projectile(6, true, true, world.getPlayer().camera.position.cpy(), 
				   new Vector3(0, 0, 0), new Vector3(0, 0, 0), world.getPlayer().camera.direction.cpy(), world.getPlayer().camera.direction.cpy(), 
				   World.particleManager.projectilePool.obtain(), World.particleManager.rocketExplosionPool.obtain(), world);
		projectilePool = new EntityPool(projectile);
	}
	
	public static class EntityPool extends Pool<DynamicEntity> {
	    private DynamicEntity entity;

	    public EntityPool(DynamicEntity entity) {
	        this.entity = entity;
	    }

	    @Override
	    public void free(DynamicEntity ent) {
	        ent.reset();
	        super.free(ent);
	    }

	    @Override
	    protected DynamicEntity newObject() {
	        return (DynamicEntity) entity.copy();
	    }
	}
}
