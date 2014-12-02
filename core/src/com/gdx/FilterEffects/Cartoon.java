package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class Cartoon extends FilterEffect {

	
	public Cartoon()
	{
		effectName="cartoon";
		loadShaderProgram();
		//setShaderParams();
		initializeFrameBuffer();
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		
	}

}
