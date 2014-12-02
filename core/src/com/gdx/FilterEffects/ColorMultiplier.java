package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class ColorMultiplier extends FilterEffect {

	protected Vector3 multipliers=new Vector3();
	
	public ColorMultiplier()
	{
		effectName="colormultiplier";
		loadShaderProgram();
		//setShaderParams();
		initializeFrameBuffer();
		multipliers.set(1, 1, 1);
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void setShaderParams() {
		// TODO Auto-generated method stub
		super.setShaderParams();
		shader.setUniformf("multiplier", multipliers);
	}

}
