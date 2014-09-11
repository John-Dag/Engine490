package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

public class Level {
	
	private TiledMap tiledMap;
	private ModelBatch modelBatch;
	private ModelBuilder modelBuilder;
	private Model box;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	
	public Level() {
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		tiledMap = new TmxMapLoader().load("mymap.tmx");
		generateLevel();
	}
	
	public void render(PerspectiveCamera camera, Environment environment) {
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}
	
	private void generateLevel(){
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		for(int i = 0; i < layer.getWidth(); i++){
			for(int j = 0; j < layer.getHeight(); j++){
				if(layer.getCell(i,j) != null && layer.getCell(i,j).getTile().getProperties().containsKey("height")){
					//System.out.print("one  , ");
				}
				else{
					//System.out.print("solid, ");
					box = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
					ModelInstance boxInstance = new ModelInstance(box);
					boxInstance.transform.setToTranslation(i, 0, j);
					instances.add(boxInstance);
				}
				
				box = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)), Usage.Position | Usage.Normal);
				ModelInstance boxInstance = new ModelInstance(box);
				boxInstance.transform.setToTranslation(i, -1, j);
				instances.add(boxInstance);
			}
			System.out.println("");
		}
	}
}
