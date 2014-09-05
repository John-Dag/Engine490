package com.gdx.engine;

import com.badlogic.gdx.Game;

public class Engine extends Game {	
	@Override
	public void create () {
		Assets.loadAssets();
	}

	@Override
	public void render () {

	}
}
