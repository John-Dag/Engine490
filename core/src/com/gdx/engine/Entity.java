package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Entity {
	private int id;
	private boolean isActive;
	private boolean isRenderable;
	public static Array<Entity> entityInstances = new Array<Entity>();
	
	public Entity() {
		id = 0;
		isActive = false;
		isRenderable = false;
	}
	
	public Entity(int id, boolean isActive, boolean isRenderable) {
		this.id = id;
		this.isActive = isActive;
		this.isRenderable = isRenderable;
	}
	
	public void removeEntity(int i) {
		entityInstances.removeIndex(i);
	}
	
	// Overridden by classes that extend Entity
	public void initialize(World world) {
		
	}
	
	// Overridden by classes that extend Entity
	public void update(float delta, World world) {

	}
	
	// Overridden by classes that extend Entity
	public void render(ModelBatch modelBatch, DecalBatch decalBatch, ModelBatch shadowBatch) {
		
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean isActive() {
		return this.isActive;
	}
	
	public boolean isRenderable() {
		return this.isRenderable;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setIsRenderable(boolean isRenderable) {
		this.isRenderable = isRenderable;
	}

	public void render(float delta) {
		
	}

	public Entity spawn() {
		return new Entity();
	}
	
	public Entity spawn(Vector3 spawnPos) {
		return new Entity();
	}
}

