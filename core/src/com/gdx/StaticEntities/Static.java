package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.Entity;

public class Static extends Entity {
	private Vector3 position;
	private Decal decal;
	private Color color;
	private float intensity;
	private PointLight pointLight;
	private boolean isDecalFacing;
	private ParticleEffect effect;
	
	public Static() {
		super();
		decal = new Decal();
		color = new Color();
		intensity = 0f;
		pointLight = new PointLight();
		effect = new ParticleEffect();
	}

	public Static(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.isDecalFacing = isDecalFacing;
		this.effect = new ParticleEffect();
	}
	
	public ParticleEffect getEffect() {
		return effect;
	}

	public void setEffect(ParticleEffect effect) {
		this.effect = effect;
	}
	
	@Override
	public void update(float delta) {
		
	}

	public Decal getDecal() {
		return decal;
	}

	public Color getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public void setDecal(Decal decal) {
		this.decal = decal;
		this.decal.setPosition(this.position);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}

	public Vector3 getPosition() {
		return position;
	}

	public boolean isDecalFacing() {
		return isDecalFacing;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setDecalFacing(boolean isDecalFacing) {
		this.isDecalFacing = isDecalFacing;
	}
}
