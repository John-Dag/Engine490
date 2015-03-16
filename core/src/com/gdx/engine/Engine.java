package com.gdx.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.bullet.Bullet;

public class Engine extends Game {
	public static GameScreen gameScreen;
	public static Bullet bullet;
	
	@Override
	public void create () {
		Assets.loadAssets();
		Bullet.init(true);
		setScreen(gameScreen = new GameScreen(this, true));
	}

	@Override
	public void render () {
		super.render();
	}
}
