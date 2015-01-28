package com.gdx.engine;

import com.badlogic.gdx.Game;

public class Engine extends Game {
	public static GameScreen gameScreen;
	
	@Override
	public void create () {
		Assets.loadAssets();
		//Bullet.init();
		setScreen(gameScreen = new GameScreen(this, true));
	}

	@Override
	public void render () {
		super.render();
	}
}
