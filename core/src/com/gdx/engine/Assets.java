package com.gdx.engine;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {
	public static AssetManager manager;
	public static Texture crosshair;
	public static Texture floor;
	//public static Texture wall;
	public static Texture stoneFloor;
	public static Texture darkWood;
	public static TiledMap mymap, mymap2, dungeon1, castle2, castle3;
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
	public static Texture aoeTexture;
	public static TextureRegion aoeTextureRegion;
	private static ParticleEffectLoader loader;
	private static ParticleEffectLoadParameter loadParam;
	public  static String vertexShader;
	public static String fragmentShader;
	public static ShaderProgram shaderProgram;
	
	public static void loadAssets() {
		manager = new AssetManager();
		torchTexture = new Texture("torch2.png");
		torch = new TextureRegion(torchTexture);
		hole = new Texture("hole.png");
		crosshair = new Texture("crosshair.png");
		test1 = new TextureRegion(hole);
		dungeon1 = new TmxMapLoader().load("dungeon1.tmx");
		mymap = new TmxMapLoader().load("mymap.tmx");
		mymap2 = new TmxMapLoader().load("mymap2.tmx");
		castle2 = new TmxMapLoader().load("castle2.tmx");
		castle3 = new TmxMapLoader().load("castle3.tmx");
		darkWood = new Texture("darkWoodTex.png");
		//floor = new Texture("floorHighRes.png");
		//wall = new Texture("wallHighRes.png");
		stoneFloor = new Texture("stonefloor.png");
		modelBuilder = new ModelBuilder();
		floorMat = new Material(TextureAttribute.createDiffuse(floor));
		//wallMat = new Material(TextureAttribute.createDiffuse(wall));
		//wallNormal = new Texture("wall_normal.jpg");
		//floorNormal = new Texture("floor_normal_map.jpg");//new Texture("floor_normal_map.jpg");
		//triangleWallMat = new Material(TextureAttribute.createDiffuse(wall));
		stoneFloorMat = new Material(TextureAttribute.createDiffuse(stoneFloor));
		aoeTexture = new Texture("aoecircle.png");
		aoeTextureRegion = new TextureRegion(aoeTexture);
		weapon1 = new Texture("weapon1.png");
		weapon1Region = new TextureRegion(weapon1);
		//wall.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		loadShaders();
	}
	
	public static void loadModels() {
	    manager.load("GUNFBX.g3db", Model.class);
	    manager.load("zombie_fast.g3db", Model.class);
	    manager.load("sword2.g3db", Model.class);
	    manager.load("FireFlower.g3db", Model.class);
	    manager.finishLoading();
	}
	
	public static void loadParticleEffects(ParticleSystem particleSystem) {
		loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		loader = new ParticleEffectLoader(new InternalFileHandleResolver());
	    manager.setLoader(ParticleEffect.class, loader);
	    manager.load("bloodeffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("torcheffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("rocketeffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("mistGreenWeapon.pfx", ParticleEffect.class, loadParam);
	    manager.load("portalEffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("rocketExplosionEffect.pfx", ParticleEffect.class, loadParam);
	    manager.load("blizzard.pfx", ParticleEffect.class, loadParam);
	    manager.load("poisonCloudEffect.pfx", ParticleEffect.class, loadParam);
	    manager.finishLoading();
	}
	
	public static void loadMeshLevelTextures(TiledMap tiledMap,
		MapTile[][][] levelArray, Map<Integer, Material> MapMaterials,
		Map<String, Integer> MaterialIds) {
		TiledMapTile tile = null;
		int layerNumber = 0;
		TiledMapTileLayer currentLayer;

		TextureParameter param = new TextureParameter();
		param.minFilter = TextureFilter.Linear;
		param.genMipMaps = true;

		// This loop loads the textures from tile map, if it has texture layers
		if (tiledMap != null) // If there is no tilemap skip this part
			for (int k = 0; k < tiledMap.getLayers().getCount(); k++) {
				if (tiledMap.getLayers().get(k).getName()
						.startsWith("Texture Layer")) {
					currentLayer = (TiledMapTileLayer) tiledMap.getLayers()
							.get(k);
					if (currentLayer.getName().equals("Texture Layer 1")) {
						layerNumber = 0;
					} else if (currentLayer.getName().equals("Texture Layer 2")) {
						layerNumber = 1;
					}

					// on each cell
					for (int i = 0; i < currentLayer.getWidth(); i++) {
						for (int j = 0; j < currentLayer.getHeight(); j++) {
							if (currentLayer.getCell(i, j) == null)
								continue; // no texture for this cell
							tile = currentLayer.getCell(i, j).getTile();
							// set texture
							if (tile.getProperties().containsKey("filename")) {
								String filename = tile.getProperties()
										.get("filename").toString();
								if (filename != null) {

									if (!MaterialIds.containsKey(filename)) // load
																			// only
																			// if
																			// not
																			// loaded
																			// already
									{
										int max = -1;
										for (int val : MaterialIds.values()) {
											if (val > max)
												max = val;
										}
										max++;
										MaterialIds.put(filename, max);
										manager.load(filename, Texture.class,
												param);
										manager.finishLoading();
										Texture texture = manager.get(filename,
												Texture.class);
										texture.setFilter(
												TextureFilter.MipMapLinearNearest,
												TextureFilter.Nearest);
										texture.setWrap(TextureWrap.Repeat,
												TextureWrap.Repeat);
										Material material = new Material(
												TextureAttribute
														.createDiffuse(texture));
										MapMaterials.put(max, material);
										if (tile.getProperties().containsKey("normal"))
										{
											String normal = tile.getProperties()
													.get("normal").toString();
											manager.load(normal, Texture.class,
													param);
											manager.finishLoading();
											texture = manager.get(normal,
													Texture.class);
											texture.setWrap(TextureWrap.Repeat,
													TextureWrap.Repeat);
											material.set(TextureAttribute.createNormal(texture));

										}
										
										
									}
									levelArray[i][j][layerNumber]
											.setTextureId(MaterialIds
													.get(filename));
								}
							}
						}
					}
				}

				// Load Wall Textures
				else if (tiledMap.getLayers().get(k).getName()
						.startsWith("Wall Texture Layer")) {
					currentLayer = (TiledMapTileLayer) tiledMap.getLayers()
							.get(k);
					if (currentLayer.getName().equals("Wall Texture Layer 1")) {
						layerNumber = 0;
					} else if (currentLayer.getName().equals(
							"Wall Texture Layer 2")) {
						layerNumber = 1;
					}

					// on each cell
					for (int i = 0; i < currentLayer.getWidth(); i++) {
						for (int j = 0; j < currentLayer.getHeight(); j++) {
							if (currentLayer.getCell(i, j) == null)
								continue; // no texture for this cell
							tile = currentLayer.getCell(i, j).getTile();
							// set texture
							if (tile.getProperties().containsKey("filename")) {
								String filename = tile.getProperties()
										.get("filename").toString();
								if (filename != null) {

									if (!MaterialIds.containsKey(filename)) // load
																			// only
																			// if
																			// not
																			// loaded
																			// already
									{
										int max = -1;
										for (int val : MaterialIds.values()) {
											if (val > max)
												max = val;
										}
										max++;
										MaterialIds.put(filename, max);
										manager.load(filename, Texture.class,
												param);
										manager.finishLoading();
										Texture texture = manager.get(filename,
												Texture.class);
										texture.setFilter(
												TextureFilter.MipMapLinearNearest,
												TextureFilter.Nearest);
										texture.setWrap(TextureWrap.Repeat,
												TextureWrap.Repeat);
										Material material = new Material(
												TextureAttribute
														.createDiffuse(texture));
										MapMaterials.put(max, material);
										if (tile.getProperties().containsKey("normal"))
										{
											String normal = tile.getProperties()
													.get("normal").toString();
											manager.load(normal, Texture.class,
													param);
											manager.finishLoading();
											texture = manager.get(normal,
													Texture.class);
											texture.setWrap(TextureWrap.Repeat,
													TextureWrap.Repeat);
											material.set(TextureAttribute.createNormal(texture));

										}
										
									}
									levelArray[i][j][layerNumber]
											.setWallTextureId(MaterialIds
													.get(filename));
								}
							}
						}
					}
				}
			}

		manager.load("stonefloor.png", Texture.class, param);
		manager.load("wall.png", Texture.class, param);
		//manager.load("floorHighres.png", Texture.class, param);
		//manager.load("wallHighres.png", Texture.class, param);
		
		manager.load("wall_normal.jpg", Texture.class, param);
		manager.load("floor_normal_map.jpg", Texture.class, param);
		manager.finishLoading();

		// Load default Textures and create materials
		MaterialIds.put("stonefloor.png", -1);
		Texture texture = manager.get("stonefloor.png", Texture.class);
		//MaterialIds.put("floorHighres.png", -1);
		//Texture texture = manager.get("floorHighres.png", Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearNearest,
				TextureFilter.Nearest);
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		Material material = new Material(
				TextureAttribute.createDiffuse(texture));
		texture = manager.get("floor_normal_map.jpg", Texture.class);
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		material.set(TextureAttribute.createNormal(texture));
		MapMaterials.put(-1, material);

		MaterialIds.put("wall.png", -2);
		texture = manager.get("wall.png", Texture.class);
		//MaterialIds.put("wallHighres.png", -2);
		//texture = manager.get("wallHighres.png", Texture.class);
		
		texture.setFilter(TextureFilter.MipMapLinearNearest,
				TextureFilter.Nearest);
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		material = new Material(TextureAttribute.createDiffuse(texture));
		texture = manager.get("wall_normal.jpg", Texture.class);
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		//material.set(TextureAttribute.createBump(wallNormal));
		material.set(TextureAttribute.createNormal(texture));
		MapMaterials.put(-2, material);
		
		
		// End loading default textures

		// Here is how you can load additional textures manually
		// Example
		//
		// manager.load("filename", Texture.class,param); //<-- add this with
		// other files above before manager.finishLoading();, then...

		// MaterialIds.put("filename", 100);
		// texture = manager.get("filename", Texture.class);
		// texture.setFilter(TextureFilter.MipMapLinearLinear,
		// TextureFilter.Linear);
		// texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		// material = new Material(TextureAttribute.createDiffuse(texture));
		// MapMaterials.put(100, material);
		//
		// So the new texture has id 100, and you can set it that texture ID in
		// mesh tiles...

		// End Load Textures

	}

	public static void loadShaders()
	{
		vertexShader = Gdx.files.internal("shaders/vertex.glsl.txt").readString();
        fragmentShader = Gdx.files.internal("shaders/fragment.glsl.txt").readString();
        //shaderProgram = new ShaderProgram(vertexShader,fragmentShader);	
	}
}