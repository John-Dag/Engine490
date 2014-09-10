package com.gdx.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;

public class Assets {
	public static AssetManager manager = new AssetManager();
	
	public static void loadAssets() {
		manager.load("borncg.g3db", Model.class);
		manager.load("walkableTile.png", Texture.class);
	}
}
