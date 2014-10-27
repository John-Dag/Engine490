package com.gdx.Weapons;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.StaticEntities.StaticWeapon;
import com.gdx.engine.Assets;

public class Sword extends StaticWeapon {
	public Sword() {
		super();
	}
	
	public Sword(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, Model model) {
		super(position, id, isActive, isRenderable, isDecalFacing, model);
	}
	
	@Override
	public StaticWeapon spawn() {
		Sword sword = new Sword(this.getPosition().cpy(), 1, true, true, true, Assets.manager.get("sword.g3db", Model.class));
		BoundingBox temp = new BoundingBox();
		sword.getModel().calculateBoundingBox(temp);
		temp.ext(5, -5, 5);
		sword.setBoundingBox(temp);
		sword.getModel().transform.setToTranslation(sword.getPosition());
		sword.getModel().transform.scale(0.009f, 0.009f, 0.009f);
		return sword;
	}
}
