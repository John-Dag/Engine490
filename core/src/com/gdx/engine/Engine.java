package com.gdx.engine;

import com.badlogic.gdx.Game;

public class Engine extends Game {	
	@Override
	public void create () {
		Assets.loadAssets();
<<<<<<< HEAD
		setScreen(new GameScreen(this));
=======
>>>>>>> 0937d15f15d5c20e2cc9ab926438be519e751fb2
	}

	@Override
	public void render () {
<<<<<<< HEAD
		super.render();
=======

>>>>>>> 0937d15f15d5c20e2cc9ab926438be519e751fb2
	}
}
