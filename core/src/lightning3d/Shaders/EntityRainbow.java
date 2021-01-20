package lightning3d.Shaders;

public class EntityRainbow extends EntityShader {
	//public float time=0;
	
	@Override
	public void begin(WiznerdShader wiznerdShader) {
		wiznerdShader.set(wiznerdShader.u_time, time/4);
	}

	@Override
	public void end(WiznerdShader wiznerdShader) {

	}

}
