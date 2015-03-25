package com.gdx.Network;

import com.badlogic.gdx.utils.Array;

public class NetStatManager {
	private Array<NetStat> stats;
	
	public NetStatManager() {
		setStats(new Array<NetStat>());
	}

	public Array<NetStat> getStats() {
		return stats;
	}

	public void setStats(Array<NetStat> stats) {
		this.stats = stats;
	}
}
