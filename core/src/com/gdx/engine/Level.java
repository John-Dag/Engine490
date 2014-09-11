package com.gdx.engine;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Level {
	
	TiledMap tiledMap;
	
	public Level() {
		tiledMap = new TmxMapLoader().load("mymap.tmx");
		generateLevel();
	}
	
	public void render() {
		
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
				}
				
			}
			System.out.println("");
		}
	}
}
