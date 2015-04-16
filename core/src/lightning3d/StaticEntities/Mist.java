package lightning3d.StaticEntities;

import lightning3d.Engine.World;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class Mist extends StaticEntity {
	public Mist() {
		super();
	}
	
	public Mist(Vector3 position, int id, boolean isActive, boolean isRenderable, PointLight light) {
		super(position, id, isActive, isRenderable, false);
		this.setEffect(World.particleManager.mistPool.obtain());
	}
}
