package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.gdx.Shaders.EntityShader;

public class Entity {
	private int id, index;
	private boolean isActive, isRenderable, moving, hasCollided;
	public static Array<Entity> entityInstances = new Array<Entity>();
	
	public EntityShader shader;
	
	public Entity() {
		id = 0;
		isActive = false;
		isRenderable = false;
	}
	
	public Entity(int id, boolean isActive, boolean isRenderable) {
		this.id = id;
		this.isActive = isActive;
		this.isRenderable = isRenderable;
		this.index = entityInstances.size;
		this.setMoving(true);
		this.setHasCollided(false);
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
	
	public BoundingBox getTransformedBoundingBox() {
		return new BoundingBox();
	}
	
	// Overridden by classes that extend Entity
	public void render(ModelBatch modelBatch, DecalBatch decalBatch, ModelBatch shadowBatch) {
		
	}
	
	public void handleCollision(int bulletId1, int bulletId2) {
		
	}
	
	public void decrementBulletIndex() {
		
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
	
	public void dispose() {
		
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isHasCollided() {
		return hasCollided;
	}

	public void setHasCollided(boolean hasCollided) {
		this.hasCollided = hasCollided;
	}

	public EntityShader getShader() {
		return shader;
	}

	public void setShader(EntityShader shader) {
		this.shader = shader;
	}
}

