package com.gdx.Network;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class NetStatField extends TextField {
	private int playerID;

	public NetStatField(String text, Skin skin) {
		super(text, skin);
		this.setDisabled(true);
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
}
