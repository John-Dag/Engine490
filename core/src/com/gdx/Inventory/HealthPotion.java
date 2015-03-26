package com.gdx.Inventory;

import com.gdx.engine.Assets;
import com.gdx.engine.World;

public class HealthPotion extends Item {
	
	public HealthPotion() {
		texture = Assets.healthPotion;
		name = "Health Potion";
	}
	
	public void effect() {
		World.player.setHealth(100);
	}
}
