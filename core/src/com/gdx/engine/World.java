package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class World {
	private Player player;
	private Level level;
	private Material floorMat, wallMat;
	private Array<Player> playerInstances = new Array<Player>();
	
	public World() {
		player = new Player(new Vector3(1.5f, 0.1f, 1.5f), true);
		floorMat = new Material(TextureAttribute.createDiffuse(Assets.floor));
		wallMat = new Material(TextureAttribute.createDiffuse(Assets.wall));
		level = new Level(Assets.level, 1f, 1f, 1f, true, floorMat, wallMat);
		playerInstances.add(player);
	}
	
	public void update(float delta) {
		for (int i = 0; i < playerInstances.size; i++) {
			Player player = playerInstances.get(i);
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public Array<Player> getPlayers() {
		return playerInstances;
	}
}
