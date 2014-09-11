package com.gdx.engine;

import com.badlogic.gdx.math.Vector3;

public class Player extends Entity {
	public Player(float x, float y, float z, boolean active) {
		this.position = new Vector3(0, 0, 0);
		this.active = true;
	}
}
