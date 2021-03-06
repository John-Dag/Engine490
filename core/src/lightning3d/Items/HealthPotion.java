package lightning3d.Items;

import lightning3d.Engine.Assets;
import lightning3d.Engine.World;

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
