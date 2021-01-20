package lightning3d.FilterEffects;

import lightning3d.Engine.FilterEffect;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;

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
