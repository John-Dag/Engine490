package com.gdx.Network;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class NetStatField extends TextField {
	private int playerID;
	private String playerName;
	private String stats;

	public NetStatField(String text, Skin skin) {
		super(text, skin);
		this.setDisabled(true);
		stats = new String();
		playerName = new String();
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}
}
