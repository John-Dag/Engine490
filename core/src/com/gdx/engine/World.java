package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class World {
	private Player player, playerB;
	private Level level;
	private Material floorMat, wallMat;
	private Array<Player> playerInstances = new Array<Player>();
	
	public World() {
		player = new Player(new Vector3(1.5f, 0.1f, 1.5f), true);
		playerB = new Player(new Vector3(2.0f, 2.0f, 2.0f), true);
		floorMat = new Material(TextureAttribute.createDiffuse(Assets.floor));
		wallMat = new Material(TextureAttribute.createDiffuse(Assets.wall));
		level = new Level(Assets.level2, 1f, 1f, 1f, true, floorMat, wallMat);
		playerInstances.add(player);
		playerInstances.add(playerB);
	}
	
	public void update(float delta) {
		playerInstances.get(0).input(delta);
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
