package com.gdx.Network;

public class NetStat {
	private String name;
	private int id, kills, deaths;
	
	public NetStat(int id, String name) {
		this.setName("");
		this.setId(id);
		this.setKills(0);
		this.setDeaths(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
}
