package com.gdx.engine;

import com.badlogic.gdx.math.Vector3;

public class Entity {
	public Vector3 position;
	public boolean active;
	public int id;
	
	public Entity() {
		position = new Vector3(0, 0, 0);
		active = false;
		id = 0;
	}
	
	public Entity(Vector3 position, boolean active, int id) {
		this.position = position;
		this.active = active;
		this.id = id;
	}
	
	public void update() {
		
	}
}
