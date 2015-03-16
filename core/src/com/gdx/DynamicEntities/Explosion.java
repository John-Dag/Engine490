package com.gdx.DynamicEntities;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.gdx.engine.ClientEvent;
import com.gdx.engine.World;

public class Explosion extends DynamicEntity {
	private Emitter emitter;
	private RegularEmitter emitterReg;
	private World world;

	public Explosion() {
		super();
	}
	
	public Explosion(int id, boolean isActive, boolean isRenderable, Vector3 position,
					  Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration,
					  ParticleEffect effect, World world) {
		super(id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, effect);
		this.setRendered(false);
		this.emitter = this.getParticleEffect().getControllers().first().emitter;
		this.emitterReg = (RegularEmitter) emitter;
		this.setTarget(new Matrix4());
		this.world = world;
	}
	
	public void initialize() {
		this.getParticleEffect().setTransform(this.getTarget());
		this.getParticleEffect().init();
		this.getParticleEffect().start();
		World.particleManager.system.add(this.getParticleEffect());
	}
		
	@Override
	public void update(float delta, World world) {
//		if (this.emitterReg != null) {
//			this.emitterReg.update();
//			if (this.emitterReg.isComplete()) {
//				World.particleManager.system.remove(this.getParticleEffect());
//				World.particleManager.rocketExplosionPool.free(this.getParticleEffect());
//				ClientEvent.RemoveEntity event = new ClientEvent.RemoveEntity(this);
//				World.eventManager.addEvent(event);
//			}
//		}
	}

	public Explosion copy() {
		Explosion explosion = new Explosion(10, true, true, new Vector3(), new Vector3(), new Vector3(), 
				new Vector3(), new Vector3(), World.particleManager.rocketExplosionPool.obtain(), world);
		
		return explosion;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
