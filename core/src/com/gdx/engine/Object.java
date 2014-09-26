package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;

//For testing purposes

public class Object {
	Decal decal;
	int id;
	boolean active;
	Vector3 position = new Vector3();
	Color color;
	float intensity;
	PointLight light;
	
	public Object(Vector3 position, Color color, float intensity, int id, boolean active) {
		this.position.set(position);
		this.color = color;
		this.intensity = intensity;
		this.id = id;
		this.active = false;
		decal = null;
		this.light = new PointLight().set(color, position, intensity);
	}
	
	public Object(Vector3 position, TextureRegion region, float scale, int direction, int id, boolean active) {
		decal = Decal.newDecal(region, true);
		decal.setScale(scale);
		this.id = id;
		rotate(direction);
		decal.setPosition(position);
		active = false;
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
