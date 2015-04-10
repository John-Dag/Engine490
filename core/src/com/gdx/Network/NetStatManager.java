package com.gdx.Network;

import com.badlogic.gdx.utils.Array;

public class NetStatManager {
	private Array<NetStat> stats;
	
	public NetStatManager() {
		setStats(new Array<NetStat>());
	}
	
	public void resetStats() {
		for (NetStat stat : stats) {
			stat.setKills(0);
			stat.setDeaths(0);
		}
	}

	public Array<NetStat> getStats() {
		return stats;
	}

	public void setStats(Array<NetStat> stats) {
		this.stats = stats;
	}
}
