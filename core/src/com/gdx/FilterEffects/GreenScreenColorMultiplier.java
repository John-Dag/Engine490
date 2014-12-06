package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class GreenScreenColorMultiplier extends ColorMultiplier {
	public GreenScreenColorMultiplier()
	{
		super();
		multipliers.x=0.5f;
		multipliers.y=2;
		multipliers.z=0.5f;
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}
}
