package Items;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.engine.Assets;
import com.gdx.engine.World;

// Gives the player a temporary speed boost.
public class SpeedPotion extends ItemBase implements Item{
	
	private float SPEEDBOOST = 1.5f;
	private float DEFAULTSPEED = 1f;
	private float duration = 10; // In seconds.
	
	public SpeedPotion() {
		name = "Speed Potion";
		texture = Assets.speedPotion;
	}
	
	@Override
	public void effect() {
		World.player.setSpeedScalar(SPEEDBOOST);
		Timer.schedule(new Task() {
			public void run() { 
				World.player.setSpeedScalar(DEFAULTSPEED);
			}
		}, duration);
	}
}
