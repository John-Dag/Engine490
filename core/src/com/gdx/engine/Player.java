package com.gdx.engine;

import com.badlogic.gdx.math.Vector3;

public class Player extends Entity {
	public final float ROTATION_SPEED = 0.2f;
	public final float MOVEMENT_SPEED = 2.0f;
	
	public Player(Vector3 position, boolean active) {
		this.position = position;
		this.active = true;
		this.id = 1;
	}
}
