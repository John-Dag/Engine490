package lightning3d.FilterEffects;

import lightning3d.Engine.FilterEffect;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class RedScreenColorMultiplier extends ColorMultiplier {
	public RedScreenColorMultiplier()
	{
		super();
		multipliers.x=2;
		multipliers.y=0.5f;
		multipliers.z=0.5f;
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}
}
