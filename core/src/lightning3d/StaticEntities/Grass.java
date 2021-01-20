package lightning3d.StaticEntities;

import lightning3d.Engine.Assets;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class Grass extends StaticEntity {
	public Grass(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		super(position, id, isActive, isRenderable, isDecalFacing, Assets.manager.get("grass.g3db", Model.class));
		this.getModel().transform.translate(position);
		this.getModel().transform.scale(0.5f, 0.5f, 0.5f);
	}
}
