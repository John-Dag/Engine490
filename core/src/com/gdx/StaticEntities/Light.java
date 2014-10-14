package com.gdx.StaticEntities;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

public class Light extends StaticEntity {
	public Light() {
		super();
	}
	
	public Light(Vector3 position, int id, boolean isActive, boolean isRenderable, PointLight light) {
		super(position, id, isActive, isRenderable, false);
		this.setPointLight(light);
	}
}
