package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class RedFade extends ColorMultiplier {

	
	
	public RedFade()
	{
		super();
		multipliers.x=3;
		multipliers.y=0.3f;
		multipliers.z=0.3f;
	}
	
	@Override
	public void Update(World world, float delta) {
		if(multipliers.x>1)
		{
			multipliers.x-=0.5*2*delta;
			multipliers.y+=0.5*0.7*delta;
			multipliers.z+=0.5*0.7*delta;
		}
		else
		{
			world.setFilterEffect(null);
		}
		
	}
	


}
