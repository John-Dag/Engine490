package lightning3d.FilterEffects;

import lightning3d.Engine.FilterEffect;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

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
