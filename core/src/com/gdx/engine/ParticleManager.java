package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Pool;

public class ParticleManager {
	public World world;
	public PFXPool projectilePool;
	public PFXPool torchPool;
	public PFXPool mistPool;
	public PFXPool rocketExplosionPool;
	public BillboardParticleBatch billboardBatch;
	public ParticleSystem system;
	public ParticleEffect rocketEffect;
	public ParticleEffect torchEffect;
	public ParticleEffect mistEffect;
	public ParticleEffect rocketExplosionEffect;
    public ParticleEffectLoadParameter loadParam;
	public ParticleEffectLoader loader;
	
	public ParticleManager(World world) {
		this.world = world;
		billboardBatch = new BillboardParticleBatch();
		billboardBatch.setCamera(world.getPlayer().camera);
		system = ParticleSystem.get();
		system.add(billboardBatch);
		Assets.loadParticleEffects(system);
		torchEffect = Assets.manager.get("torcheffect.pfx");
		rocketEffect = Assets.manager.get("rocketeffect.pfx");
		mistEffect = Assets.manager.get("mistGreenWeapon.pfx");
		rocketExplosionEffect = Assets.manager.get("rocketExplosionEffect.pfx");
		projectilePool = new PFXPool(rocketEffect);
		torchPool = new PFXPool(torchEffect);
		mistPool = new PFXPool(mistEffect);
		rocketExplosionPool = new PFXPool(rocketExplosionEffect);
	}
	
	public PFXPool getRocketExplosionPool() {
		return rocketExplosionPool;
	}

	public void setRocketExplosionPool(PFXPool rocketExplosionPool) {
		this.rocketExplosionPool = rocketExplosionPool;
	}

	public PFXPool getMistPool() {
		return mistPool;
	}

	public void setMistPool(PFXPool mistPool) {
		this.mistPool = mistPool;
	}
	
	public PFXPool getProjectilePool() {
		return projectilePool;
	}

	public PFXPool getTorchPool() {
		return torchPool;
	}

	public BillboardParticleBatch getBillboardBatch() {
		return billboardBatch;
	}

	public ParticleSystem getSystem() {
		return system;
	}

	public ParticleEffect getRocketEffect() {
		return rocketEffect;
	}

	public ParticleEffect getTorchEffect() {
		return torchEffect;
	}

	public ParticleEffect getMistEffect() {
		return mistEffect;
	}

	public World getWorld() {
		return world;
	}

	public void setBillboardBatch(BillboardParticleBatch billboardBatch) {
		this.billboardBatch = billboardBatch;
	}

	public void setSystem(ParticleSystem system) {
		this.system = system;
	}

	public void setRocketEffect(ParticleEffect rocketEffect) {
		this.rocketEffect = rocketEffect;
	}

	public void setTorchEffect(ParticleEffect torchEffect) {
		this.torchEffect = torchEffect;
	}

	public void setMistEffect(ParticleEffect mistEffect) {
		this.mistEffect = mistEffect;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public static class PFXPool extends Pool<ParticleEffect> {
	    private ParticleEffect sourceEffect;

	    public PFXPool(ParticleEffect sourceEffect) {
	        this.sourceEffect = sourceEffect;
	    }

	    @Override
	    public void free(ParticleEffect pfx) {
	        pfx.reset();
	        super.free(pfx);
	    }

	    @Override
	    protected ParticleEffect newObject() {
	        return sourceEffect.copy();
	    }
	}
}
