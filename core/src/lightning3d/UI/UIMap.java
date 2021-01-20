package lightning3d.UI;

import java.util.Map;

import lightning3d.Engine.MapTile;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;

public class UIMap extends UIBase {
	private OrthographicCamera mapCamera;
	private SpriteBatch batch;
	private World world;
	private Sprite[][] sprites;
	private TiledMapTileLayer currentLayer;
	private Image mapShot;
	private Table table;
	private int mapWidth, mapHeight;
	private ShapeRenderer shapeRenderer;
	private int indicatorSize;
	
	/***
	 * Creates a map widget
	 * @param color Color of the map indicator
	 * @param mapLayer The map layer that will be used to generate the map
	 */
	
	public UIMap(World world, Stage stage, SpriteBatch batch, Skin skin, Color color, int mapWidth, int mapHeight, int indicatorSize, int mapLayer) {
		super(stage); 
		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), 
				                           Gdx.graphics.getHeight());
		this.batch = batch;
		this.world = world;
		currentLayer = (TiledMapTileLayer) world.getMeshLevel().getTiledMap().getLayers().get(mapLayer);
		sprites = new Sprite[currentLayer.getWidth()][currentLayer.getHeight()];
		table = new Table();
		table.setFillParent(true);
		this.setWindow(new Window("", skin));
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.indicatorSize = indicatorSize;
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(color);
	}
	
	/***
	 * Generates a 2D minimap from a mesh level.
	 * @param levelArray The mesh level array that contains the Texture ids
	 * @param MapMaterials The materials used in the mesh level
	 */
	
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
		
		try {
			mapShot = generateMapShot();
		}
		catch(Exception e) {
			System.err.println("generateMapShot(): Error generating map image");
			System.err.println(e);
			return;
		}
		
		table.setHeight(mapShot.getHeight());
		table.setWidth(mapShot.getWidth());
		table.addActor(mapShot);
		getWindow().setWidth(mapShot.getWidth());
		getWindow().setHeight(mapShot.getHeight() + 20);
		getWindow().add(table);
		getWindow().addActor(mapShot);
		getWindow().setPosition(Gdx.graphics.getWidth() - getWindow().getWidth(), Gdx.graphics.getHeight() - getWindow().getHeight());
		this.getStage().addActor(getWindow());
	}
	
	/***
	 * Generates a 2D map which is displayed and captured as an image. The image is then added to the map widget.
	 */
	
	private Image generateMapShot() {
		mapCamera.update();
		batch.begin();
		batch.setProjectionMatrix(mapCamera.combined);
		
		for (int i = 0; i < currentLayer.getWidth(); i++) {
			for (int j = 0; j < currentLayer.getHeight(); j++) {
				batch.draw(sprites[i][j], i * mapWidth, j * mapHeight, mapWidth, mapHeight);
			}
		}
		
		batch.end();
		
		return new Image(ScreenUtils.getFrameBufferTexture(Gdx.graphics.getWidth() / 2, 
						 Gdx.graphics.getHeight() / 2, currentLayer.getWidth() * mapWidth, 
						 currentLayer.getHeight() * mapHeight));
	}
	
	/***
	 * Renders an indicator 
	 * @param delta
	 * @param position The entities position
	 */
	
	public void renderIndicator(float delta, Vector3 position) {
		mapCamera.update();
		batch.setProjectionMatrix(mapCamera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.circle(getWindow().getX() + position.z * mapHeight, 
				             getWindow().getY() + position.x * mapWidth, indicatorSize);
		shapeRenderer.end();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}
