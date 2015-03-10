package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.engine.World;

public class HealthPotion extends Item {
	public HealthPotion(Image image, String name) {
		super(image, name);
	}
	
	public void effect() {
		World.player.setHealth(100);
	}
}
