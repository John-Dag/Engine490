package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

public class Level {
	private TiledMap tiledMap;
	private ModelBuilder modelBuilder;
	private Model wallBox, floorBox, skySphere;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	
	public Level(TiledMap tiledMap, float x, float y, float z, boolean isSkyboxActive, Material floorMat, Material wallMat) {
		modelBuilder = new ModelBuilder();
		this.tiledMap = tiledMap;
		generateLevel(x, y, z, isSkyboxActive, floorMat, wallMat);
	}
	
	private void generateLevel(float x, float y, float z, boolean isSkyboxActive, Material floorMat, Material wallMat){
		floorBox = modelBuilder.createBox(x, y, z, floorMat, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		wallBox = modelBuilder.createBox(x, y, z, wallMat, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		
		if (isSkyboxActive) {
			skySphere = modelBuilder.createSphere(50f, 50f, 50f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.TEAL)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			ModelInstance sphereInstance = new ModelInstance(skySphere);
			sphereInstance.transform.setToTranslation(0, 0, 0);
			instances.add(sphereInstance);
		}
		
		for (int k = 0; k < tiledMap.getLayers().getCount(); k++) {
			TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
			for(int i = 0; i < layer.getWidth(); i++){
				for(int j = 0; j < layer.getHeight(); j++){
					if(layer.getCell(i,j) != null && layer.getCell(i,j).getTile().getProperties().containsKey("height")){
					}
					else {
						ModelInstance boxInstance = new ModelInstance(wallBox);
						boxInstance.transform.setToTranslation(i, 0, j);
						instances.add(boxInstance);
					}
					
					ModelInstance boxInstance = new ModelInstance(floorBox);
					boxInstance.transform.setToTranslation(i, -1, j);
					instances.add(boxInstance);
				}
			}
		}
	}
	
	public Array<ModelInstance> getInstances() {
		return instances;
	}
}
