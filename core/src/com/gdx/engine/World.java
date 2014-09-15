package com.gdx.engine;

import com.badlogic.gdx.utils.Array;

public class World {
	public Player player;
	public Array<Player> playerInstances = new Array<Player>();
	
	public World() {
		player = new Player(2f, 0f, 0f, true);
		playerInstances.add(player);
	}
	
	public void update() {
		
	}
}
