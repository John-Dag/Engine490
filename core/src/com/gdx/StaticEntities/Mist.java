package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class Mist extends Static {
	public Mist() {
		super();
	}
	
	public Mist(Vector3 position, int id, boolean isActive, boolean isRenderable, PointLight light) {
		super(position, id, isActive, isRenderable, false);
		this.setEffect(World.particleManager.mistPool.obtain());
	}
}
