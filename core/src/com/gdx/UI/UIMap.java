package com.gdx.UI;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gdx.engine.MapTile;
import com.gdx.engine.World;

public class UIMap extends UIBase {
	private OrthographicCamera mapCamera;
	private SpriteBatch batch;
	private World world;
	private Sprite[][] sprites;
	private TiledMapTileLayer currentLayer;
	private Image mapShot;
	private Table table;
	private Window window;
	private int mapWidth, mapHeight;
	private ShapeRenderer shapeRenderer;
	
	public UIMap(World world, Stage stage, SpriteBatch batch, Skin skin, Color color, int mapWidth, int mapHeight) {
		super(stage);
		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), 
				                           Gdx.graphics.getHeight());
		this.batch = batch;
		this.world = world;
		currentLayer = (TiledMapTileLayer) world.getMeshLevel().getTiledMap().getLayers().get(0);
		sprites = new Sprite[currentLayer.getWidth()][currentLayer.getHeight()];
		table = new Table();
		table.setFillParent(true);
		window = new Window("", skin);
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(color);
	}
	
	public void generateMap(MapTile[][][] levelArray, Map<Integer, Material> MapMaterials) {
		for (int i = 0; i < currentLayer.getWidth(); i++) {
			for (int j = 0; j < currentLayer.getHeight(); j++) {
				Texture texture = null;
				Material material = MapMaterials.get(levelArray[i][j][0].getTextureId());
				TextureAttribute attribute = (TextureAttribute)material.get(TextureAttribute.Diffuse);
				if (attribute != null)
					texture = attribute.textureDescription.texture;
				Sprite sprite = new Sprite(texture);
				sprites[i][j] = sprite;
			}
		}
		
		batch.begin();
		generateMapShot();
		batch.end();
		mapShot = new Image(ScreenUtils.getFrameBufferTexture(Gdx.graphics.getWidth() / 2, 
							Gdx.graphics.getHeight() / 2, currentLayer.getWidth() * mapWidth, 
							currentLayer.getHeight() * mapHeight));
		table.setHeight(mapShot.getHeight());
		table.setWidth(mapShot.getWidth());
		table.addActor(mapShot);
		window.setWidth(mapShot.getWidth());
		window.setHeight(mapShot.getHeight() + 20);
		window.add(table);
		window.addActor(mapShot);
		window.setPosition(Gdx.graphics.getWidth() - window.getWidth(), Gdx.graphics.getHeight() - window.getHeight());
		this.getStage().addActor(window);
	}
	
	public void generateMapShot() {
		mapCamera.update();
		batch.setProjectionMatrix(mapCamera.combined);
		
		for (int i = 0; i < currentLayer.getWidth(); i++) {
			for (int j = 0; j < currentLayer.getHeight(); j++) {
				batch.draw(sprites[i][j], i * mapWidth, j * mapHeight, mapWidth, mapHeight);
			}
		}
	}
	
	@Override
	public void render(float delta) {
		mapCamera.update();
		batch.setProjectionMatrix(mapCamera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.circle(window.getX() + world.getPlayer().getPosition().z * mapHeight, 
				             window.getY() + world.getPlayer().getPosition().x * mapWidth, 5);
		shapeRenderer.end();
	}
}
