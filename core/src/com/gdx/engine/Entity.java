package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Entity {
	public Vector3 position;
	public boolean active, isRendered;
	public int id;
	public ModelInstance model;
	public BoundingBox boundingBox = new BoundingBox();
	
	public Entity() {
		position = new Vector3(0, 0, 0);
		active = false;
		id = 0;
	}
	
	public Entity(Vector3 position, boolean active, int id, ModelInstance model) {
		this.model = model;
		this.position = position;
		this.active = active;
		this.id = id;
		this.isRendered = false;
	}
	
	public Entity(Vector3 position, boolean active, int id, BoundingBox boundingBox) {
		this.position = position;
		this.active = active;
		this.isRendered = false;
		this.id = id;
		this.boundingBox = boundingBox;
	}
}
