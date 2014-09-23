package com.gdx.engine;

import com.badlogic.gdx.Game;

public class Engine extends Game {	
	@Override
	public void create () {
		Assets.loadAssets();
		setScreen(new GameScreen(this));
		//setScreen(new TestScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
