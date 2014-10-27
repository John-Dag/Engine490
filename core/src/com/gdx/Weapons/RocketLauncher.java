package com.gdx.Weapons;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.StaticEntities.StaticWeapon;
import com.gdx.engine.Assets;

public class RocketLauncher extends StaticWeapon {
	public RocketLauncher() {
		super();
	}
	
	public RocketLauncher(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model) {
		super(position, id, isActive, isRenderable, isDecalFacing, model);
	}
	
	@Override
	public StaticWeapon spawn() {
		RocketLauncher launcher = new RocketLauncher(this.getPosition().cpy(), 2, true, true, true, Assets.manager.get("GUNFBX.g3db", Model.class));
		BoundingBox temp = new BoundingBox();
		launcher.getModel().calculateBoundingBox(temp);
		launcher.setBoundingBox(temp);
		launcher.getModel().transform.setToTranslation(launcher.getPosition());
		launcher.getModel().transform.scale(0.005f, 0.005f, 0.005f);
		return launcher;
	}
}
