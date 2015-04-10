package com.gdx.Network;

import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Player;
import com.gdx.engine.World;

public abstract class NetMatch {
	private Array<Player> players;
	private Array<NetStat> stats;
	private World world;
	private float startDelay;
	
	public NetMatch(World world, Array<Player> players, Array<NetStat> stats, float startDelay) {
		this.setPlayers(players);
		this.setStats(stats);
		this.setWorld(world);
		this.setStartDelay(startDelay);
	}
	
	public abstract void update();
	public abstract void startMatch();
	public abstract void broadcastStartMessage();

	public Array<NetStat> getStats() {
		return stats;
	}

	public void setStats(Array<NetStat> stats) {
		this.stats = stats;
	}

	public Array<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Array<Player> players) {
		this.players = players;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public float getStartDelay() {
		return startDelay;
	}

	public void setStartDelay(float startDelay) {
		this.startDelay = startDelay;
	}
}
