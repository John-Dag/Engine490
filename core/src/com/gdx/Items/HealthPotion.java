package com.gdx.Items;

import com.gdx.engine.Assets;
import com.gdx.engine.World;

// Restores the player to full health.
public class HealthPotion extends ItemBase implements Item {
	
	public HealthPotion() {
		texture = Assets.healthPotion;
		name = "Health Potion";
	}
	
	@Override
	public void effect() {
		World.player.setHealth(100);
	}
}
