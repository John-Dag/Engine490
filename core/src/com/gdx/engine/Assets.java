package com.gdx.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {
	public static AssetManager manager;
	public static Texture crosshair;
	public static Texture floor;
	public static Texture wall;
	public static Texture stoneFloor;
	public static Texture darkWood;
	public static TiledMap level;
	public static TiledMap level2;
	public static TiledMap castle;
	public static TiledMap dungeon1;
	public static ModelBuilder modelBuilder;
	public static Material floorMat;
	public static Material wallMat;
	public static Material triangleWallMat;
	public static Material stoneFloorMat;
	public static TextureRegion test1;
	public static Texture hole;
	public static Texture torchTexture;
	public static TextureRegion torch;
	public static Texture weapon1;
	public static TextureRegion weapon1Region;
	private static ParticleEffectLoader loader;
	private static ParticleEffectLoadParameter loadParam;
	
	public static void loadAssets() {
		manager = new AssetManager();
		torchTexture = new Texture("torch2.png");
		torch = new TextureRegion(torchTexture);
		hole = new Texture("hole.png");
		crosshair = new Texture("crosshair.png");
		test1 = new TextureRegion(hole);
		dungeon1 = new TmxMapLoader().load("dungeon1.tmx");
		level = new TmxMapLoader().load("mymap.tmx");
		level2 = new TmxMapLoader().load("mymap2.tmx");
		castle = new TmxMapLoader().load("castle2.tmx");
		darkWood = new Texture("darkWoodTex.png");
		floor = new Texture("floor.png");
		wall = new Texture("wall.png");
		stoneFloor = new Texture("stonefloor.png");
		modelBuilder = new ModelBuilder();
		floorMat = new Material(TextureAttribute.createDiffuse(floor));
		wallMat = new Material(TextureAttribute.createDiffuse(wall));
		triangleWallMat = new Material(TextureAttribute.createDiffuse(wall));
		stoneFloorMat = new Material(TextureAttribute.createDiffuse(stoneFloor));
		weapon1 = new Texture("weapon1.png");
		weapon1Region = new TextureRegion(weapon1);
		wall.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
	
	public static void loadModels() {
	    manager.load("GUNFBX.g3db", Model.class);
	    manager.load("zombie_fast.g3db", Model.class);
	    manager.load("sword.g3db", Model.class);
	    manager.finishLoading();
	}
	
	public static void loadParticleEffects(ParticleSystem particleSystem) {
		loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		loader = new ParticleEffectLoader(new InternalFileHandleResolver());
	    manager.setLoader(ParticleEffect.class, loader);
	    manager.load("torcheffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("rocketeffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("mistGreenWeapon.pfx", ParticleEffect.class, loadParam);
	    manager.load("rocketExplosionEffect.pfx", ParticleEffect.class, loadParam);
	    manager.finishLoading();
	}
}
