package com.gdx.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
	public static AssetManager manager = new AssetManager();
	public static Texture crosshair;
	
	public static void loadAssets() {
		crosshair = new Texture("crosshair.png");
	}
}
