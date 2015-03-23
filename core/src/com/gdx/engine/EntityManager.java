package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Explosion;
import com.gdx.DynamicEntities.Projectile;

public class EntityManager {
	public World world;
	public Pool<Projectile> projectilePool;
	public ExplosionPool explosionPool;
	
	public EntityManager(World world) {
		this.world = world;
		Projectile projectile = new Projectile(6, true, true, world.getPlayer().camera.position.cpy(), 
				   new Vector3(0, 0, 0), new Vector3(0, 0, 0), world.getPlayer().camera.direction.cpy(), world.getPlayer().camera.direction.cpy(), 
				   World.particleManager.projectilePool.obtain(), World.particleManager.rocketExplosionPool.obtain(), world);
		Explosion explosion = new Explosion(6, true, true, world.getPlayer().camera.position.cpy(), 
				   new Vector3(0, 0, 0), new Vector3(0, 0, 0), world.getPlayer().camera.direction.cpy(), world.getPlayer().camera.direction.cpy(), 
				   World.particleManager.rocketExplosionPool.obtain(), world);

		projectilePool = new EntityPool(projectile);
		explosionPool = new ExplosionPool(explosion);
	}
	
	public static class EntityPool extends Pool<Projectile> {
	    private Projectile entity;

	    public EntityPool(Projectile entity) {
	        this.entity = entity;
	    }

	    @Override
	    public void free(Projectile ent) {
	        ent.reset();
	        super.free(ent);
	    }

	    @Override
	    protected Projectile newObject() {
	    	Projectile projectile = new Projectile();
			projectile = (Projectile)entity.copy();
		
			return projectile;
	    }
	}
	
	public static class ExplosionPool extends Pool<Explosion> {
		private Explosion explosion;
		
		public ExplosionPool(Explosion explosion) {
			this.explosion = explosion;
		}
		
		@Override
		public void free(Explosion explosion) {
			explosion.reset();
			super.free(explosion);
		}
		
		@Override
		protected Explosion newObject() {
			Explosion explosion = new Explosion();
			explosion = (Explosion) explosion.copy();
			
			return explosion;
		}
	}
}
