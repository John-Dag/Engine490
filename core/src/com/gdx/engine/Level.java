package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

//Cube level

public class Level {
	private TiledMap tiledMap;
	private Model skySphere;
	private ModelInstance sphereInstance;
	private boolean isSkyboxActive;
	private Array<Entity> instances = new Array<Entity>();
	
	public Level(TiledMap tiledMap, float x, float y, float z, boolean isSkyboxActive, Material floorMat, Material wallMat) {
		this.tiledMap = tiledMap;
		this.isSkyboxActive = isSkyboxActive;
		generateLevel(x, y, z, isSkyboxActive, floorMat, wallMat);
	}
	
	private void generateLevel(float x, float y, float z, boolean isSkyboxActive, Material floorMat, Material wallMat) {
		if (isSkyboxActive) {
			skySphere = Assets.modelBuilder.createSphere(50f, 50f, 50f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.TEAL)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			sphereInstance = new ModelInstance(skySphere);
			sphereInstance.transform.setToTranslation(0, 0, 0);
		}
		
		for (int k = 0; k < tiledMap.getLayers().getCount(); k++) {
			TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(k);
			for(int i = 0; i < layer.getWidth(); i++){
				for(int j = 0; j < layer.getHeight(); j++){
					if(layer.getCell(i,j) != null && layer.getCell(i,j).getTile().getProperties().containsKey("height")){
						
					}
					else {
						Entity entity;
						entity = new Entity(new Vector3(i, 0, j), true, 2, BuildModel.buildBoxTextureModel(x, y, z, Assets.wallMat));
						entity.model.transform.setToTranslation(i, 0, j);
						entity.model.calculateBoundingBox(entity.boundingBox).mul(entity.model.transform);
						instances.add(entity);
					}
					
					Entity entity;
					entity = new Entity(new Vector3(i, -1, j), true, 3, BuildModel.buildBoxTextureModel(x, y, z, Assets.floorMat));
					entity.model.transform.setToTranslation(i, -1, j);
					entity.model.calculateBoundingBox(entity.boundingBox).mul(entity.model.transform);
					instances.add(entity);
				}
			}
		}
	}
	
	public Array<Entity> getInstances() {
		return instances;
	}
	
	public ModelInstance getSkySphere() {
		return sphereInstance;
	}
	
	public boolean getSkyboxActive() {
		return isSkyboxActive;
	}
}
