package lightning3d.StaticEntities;

import lightning3d.Engine.Assets;
import lightning3d.Shaders.BlackHole;
import lightning3d.Shaders.EntityRainbow;
import lightning3d.Shaders.FireBallShader;
import lightning3d.Shaders.LavaShader;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;

public class Statue extends StaticEntity {
	
	
	public Statue(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		
		super(position, id, isActive, isRenderable, isDecalFacing, Assets.manager.get("AngelStatue.g3db", Model.class));
		this.getModel().transform.translate(position);

		

	}
}
