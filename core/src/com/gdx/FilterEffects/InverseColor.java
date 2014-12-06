package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class InverseColor extends FilterEffect {	
	public InverseColor()
	{
		effectName="inverse";
		loadShaderProgram();
		//setShaderParams();
		initializeFrameBuffer();
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}

}
