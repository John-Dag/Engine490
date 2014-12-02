package com.gdx.FilterEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.gdx.engine.FilterEffect;
import com.gdx.engine.World;

public class Lights extends FilterEffect {

	private Vector2 position=new Vector2();
	private Vector2 res=new Vector2();
	private float time=0;
	public Lights()
	{
		effectName="lights";
		loadShaderProgram();
		//setShaderParams();
		initializeFrameBuffer();
		
		position.set(0,0);
		res.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void Update(World world, float delta) {
		// TODO Auto-generated method stub
		time+=delta;
	}

	@Override
	protected void setShaderParams() {
		// TODO Auto-generated method stub
		super.setShaderParams();
		shader.setUniformf("time", time);
		shader.setUniformf("resolution", res);
		shader.setUniformf("mouse", position);
	}
}
