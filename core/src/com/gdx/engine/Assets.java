package com.gdx.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {
	public static AssetManager manager = new AssetManager();
	public static Texture crosshair;
	public static Texture floor;
	public static Texture wall;
	public static TiledMap level;
	public static TiledMap level2;
	
	public static void loadAssets() {
		crosshair = new Texture("crosshair.png");
		level = new TmxMapLoader().load("mymap.tmx");
		floor = new Texture("floor.png");
		wall = new Texture("wall.png");
		level2 = new TmxMapLoader().load("mymap2.tmx");
	}
}
