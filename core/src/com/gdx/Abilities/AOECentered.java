package com.gdx.Abilities;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gdx.DynamicEntities.Ability;
import com.gdx.engine.World;

public class AOECentered extends Ability {
	private Vector3 min, max;
	
	public AOECentered() {
		super();
	}
	
	public AOECentered(int id, boolean isActive, boolean isRenderable, Vector3 position, float duration, float cooldown) {
		super(id, isActive, isRenderable, position, duration, cooldown);
		this.setTarget(new Matrix4());
		min = new Vector3();
		max = new Vector3();
		initAbility();
	}
	
	@Override
	public void update(float delta, World world) {
		this.getTarget().idt();
		this.getTarget().translate(world.getPlayer().getPosition());
		this.getParticleEffect().setTransform(this.getTarget());
		this.setPosition(world.getPlayer().getPosition());

		this.getBoundingBox().set(min.set(this.getPosition().x - this.getSize(), this.getPosition().y, this.getPosition().z  - this.getSize()),
				  				  max.set(this.getPosition().x + this.getSize(), this.getPosition().y + this.getSize(), this.getPosition().z + this.getSize()));
		world.checkAbilityCollision(this);
		
		if (!this.isRendered() && this.getParticleEffect() != null) 
			this.initializeAbilityEffect();
	}
	
	public void initializeAbilityEffect() {
		this.setRendered(true);
		this.getParticleEffect().init();
		this.getParticleEffect().start();
		this.setBoundingBox(this.getParticleEffect().getBoundingBox());
		World.particleManager.system.add(this.getParticleEffect());
	}
}
