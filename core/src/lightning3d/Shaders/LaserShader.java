package lightning3d.Shaders;

public class LaserShader extends EntityShader {
	//public float time=0;
	
	@Override
	public void begin(WiznerdShader wiznerdShader) {
		wiznerdShader.set(wiznerdShader.u_time, time);
		wiznerdShader.set(wiznerdShader.u_resolution, resolution);
	}

	@Override
	public void end(WiznerdShader wiznerdShader) {

	}
}
