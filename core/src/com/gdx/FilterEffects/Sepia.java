package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class Sepia extends FilterEffect {

	
	public Sepia()
	{
		effectName="sepia";
		loadShaderProgram();
		//setShaderParams();
		initializeFrameBuffer();
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}

}
