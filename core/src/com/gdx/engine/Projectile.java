package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Projectile extends Entity {
	public float damage;
	public float accuracy;
	public ParticleEffect effect;
	
	public Projectile(Vector3 position, Vector3 rotation, Vector3 scale, Vector3 angVelocity, Vector3 velocity,
			  		  Vector3 angAccel, Vector3 acceleration, boolean active, int index, boolean collision) {
		super(position, rotation, scale, angVelocity, velocity, angAccel, acceleration, active, index, collision);
	}
}
