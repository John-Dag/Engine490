package lightning3d.StaticEntities;

import lightning3d.Engine.Assets;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class Tree extends StaticEntity {
	public Tree(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing) {
		super(position, id, isActive, isRenderable, isDecalFacing, Assets.manager.get("tree.g3db", Model.class));
		this.getModel().transform.translate(position);
		this.getModel().transform.scale(0.05f, 0.05f, 0.05f);
	}
}
