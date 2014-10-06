package com.gdx.engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {
	public static Texture crosshair;
	public static Texture floor;
	public static Texture wall;
	public static Texture stoneFloor;
	public static TiledMap level;
	public static TiledMap level2;
	public static TiledMap castle;
	public static ModelBuilder modelBuilder;
	public static Material floorMat;
	public static Material wallMat;
	public static Material stoneFloorMat;
	public static TextureRegion test1;
	public static Texture hole;
	public static Texture torchTexture;
	public static TextureRegion torch;
	public static Texture weapon1;
	public static TextureRegion weapon1Region;
	
	public static void loadAssets() {
		torchTexture = new Texture("torch2.png");
		torch = new TextureRegion(torchTexture);
		hole = new Texture("hole.png");
		crosshair = new Texture("crosshair.png");
		test1 = new TextureRegion(hole);
		level = new TmxMapLoader().load("mymap.tmx");
		level2 = new TmxMapLoader().load("mymap2.tmx");
		castle = new TmxMapLoader().load("castle.tmx");
		floor = new Texture("floor.png");
		wall = new Texture("wall.png");
		stoneFloor = new Texture("stonefloor.png");
		modelBuilder = new ModelBuilder();
		floorMat = new Material(TextureAttribute.createDiffuse(floor));
		wallMat = new Material(TextureAttribute.createDiffuse(wall));
		stoneFloorMat = new Material(TextureAttribute.createDiffuse(stoneFloor));
		weapon1 = new Texture("weapon1.png");
		weapon1Region = new TextureRegion(weapon1);
	}
}
