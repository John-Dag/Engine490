package lightning3d.FilterEffects;

import lightning3d.Engine.FilterEffect;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;

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
