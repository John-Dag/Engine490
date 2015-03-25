package com.gdx.Shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class EntityShader {

	//public WiznerdShader shader;
	public float time=0;
	public Vector2 resolution=new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
	public void apply(WiznerdShader wiznerdShader)
	{
		//shader=wiznerdShader;
	}
	
	public abstract void begin(WiznerdShader wiznerdShader);
	
	public abstract void end(WiznerdShader wiznerdShader);
	
}
