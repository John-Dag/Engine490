package com.gdx.engine;

import com.badlogic.gdx.math.Vector3;

public class Entity {
	public Vector3 position;
	public boolean active;
	
	public Entity() {
		position = new Vector3(0, 0, 0);
		this.active = false;
	}
	
	public Entity(float x, float y, float z, boolean active) {
		position = new Vector3(x, y, z);
		this.active = true;
	}
}
