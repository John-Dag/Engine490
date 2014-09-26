package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;

//For testing purposes

public class Object {
	public Decal decal;
	public int id;
	public boolean active;
	public Vector3 position;
	public Color color;
	public float intensity;
	public PointLight light;
	
	public Object(Vector3 position, Color color, float intensity, int id, boolean active) {
		this.position = position;
		this.color = color;
		this.intensity = intensity;
		this.id = id;
		this.active = false;
		this.light = new PointLight().set(color, position, intensity);
		this.active = false;
	}
	
	public Object(Vector3 position, TextureRegion region, float scale, int direction, int id, boolean active) {
		this.decal = Decal.newDecal(region, true);
		this.decal.setScale(scale);
		this.id = id;
		rotate(direction);
		this.decal.setPosition(position);
		this.active = false;
	}
	
	private void rotate(int direction) {
		switch (direction) {
			case(0):
				decal.rotateZ(60f);
			case(1):
				decal.rotateZ(-30f);
				break;
			case(2):
				decal.rotateX(-30f);
				decal.rotateY(90f);
				break;
			case(3):
				decal.rotateX(30f);
				decal.rotateY(90f);
				break;
		}
	}
}
