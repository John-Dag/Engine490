package lightning3d.FilterEffects;

import lightning3d.Engine.FilterEffect;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class BlueScreenColorMultiplier extends ColorMultiplier {
	public BlueScreenColorMultiplier()
	{
		super();
		multipliers.x=0.5f;
		multipliers.y=0.5f;
		multipliers.z=2;
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}
}
