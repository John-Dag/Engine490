package com.gdx.Shaders;

public class BlackHole extends EntityShader {

	//public float time=0;
	
	@Override
	public void begin(WiznerdShader wiznerdShader) {
		wiznerdShader.set(wiznerdShader.u_time, time);
	}

	@Override
	public void end(WiznerdShader wiznerdShader) {

	}

}
