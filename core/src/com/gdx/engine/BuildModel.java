package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public class BuildModel {
	public static ModelInstance buildBoxColorModel(float x, float y, float z, Color color) {
		Model model = Assets.modelBuilder.createBox(x, y, z, new Material(ColorAttribute.createDiffuse(color)), 
													Usage.Position | Usage.Normal);
		ModelInstance instance = new ModelInstance(model);
		instance.transform.setToTranslation(x, y, z);
		return instance;
	}
	
	public static ModelInstance buildBoxTextureModel(float x, float y, float z, Material material) {
		Model model = Assets.modelBuilder.createBox(x, y, z, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		ModelInstance instance = new ModelInstance(model);
		instance.transform.setToTranslation(x, y, z);
		return instance;
	}
}
