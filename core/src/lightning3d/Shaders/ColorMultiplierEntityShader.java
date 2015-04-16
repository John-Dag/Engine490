package lightning3d.Shaders;

import com.badlogic.gdx.math.Vector3;

public class ColorMultiplierEntityShader extends EntityShader {

	public Vector3 multiplier=new Vector3(0,1,0);
	public Vector3 defaultMultiplier=new Vector3(1,1,1);
public void apply(WiznerdShader wes)
{
	
}

	@Override
	public void begin(WiznerdShader wiznerdShader) {
		//multiplier.set(multiplier);
		wiznerdShader.set(wiznerdShader.u_colorMultiplier, multiplier);
		
	}

	@Override
	public void end(WiznerdShader wiznerdShader) {
		//multiplier.set(1.0f,1.0f,1.0f);
		//wiznerdShader.colorMult.set(1.0f,1.0f,1.0f);
		wiznerdShader.set(wiznerdShader.u_colorMultiplier, defaultMultiplier);
	}

}
