package com.gdx.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class World {
	public Player player;
	public Array<Player> playerInstances = new Array<Player>();
	
	public World() {
		player = new Player(new Vector3(1.5f, 0.1f, 1.5f), true);
		playerInstances.add(player);
	}
	
	public void update() {
		
	}
}
