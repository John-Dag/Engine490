package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.StaticEntities.Light;
import com.gdx.StaticEntities.Mist;
import com.gdx.StaticEntities.Spawn;
import com.gdx.StaticEntities.StaticWeapon;
import com.gdx.StaticEntities.Torch;

// Important note to the team: (this can be confusing)
// World coordinates have x, y, z, with +x pointing East, and +z pointing South
// Tiled coordinates have x, y with +x pointing South and +y pointing East
// Many methods convert between these two coordinate systems, so its important to know which variables are in which coordinate system.

public class MeshLevel {
	public static Color skyColor = Color.BLACK;
	private static final float ROOT_PT5 = 0.70710678f;
	private static final int NORTH = 1;
	private static final int SOUTH = 2;
	private static final int EAST = 3;
	private static final int WEST = 4;
	// the direction of the ramp gradient (left = north, etc)
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int UP = 3;
	private static final int DOWN = 4;
	
	// array finals
	private static final int ARRAY_EMPTY = 0;
	private static final int ARRAY_BLOCK = 1;
	private static final int ARRAY_RAMP_LEFT = 2;
	private static final int ARRAY_RAMP_RIGHT = 3;
	private static final int ARRAY_RAMP_UP = 4;
	private static final int ARRAY_RAMP_DOWN = 5;
	
	// the length and width of one tile
	private static final float SPOT_WIDTH = 1;
	private static final float SPOT_LENGTH = 1;
	
	// colors of point lights
	private final int RED = 0;
	private final int WHITE = 1;
	private final int GREEN = 2;
	private final int BLUE = 3;
	private final int PURPLE = 4;
	
	private TiledMap tiledMap;
	private ModelBuilder modelBuilder;
	private Array<ModelInstance> instances;
	private Array<Object> objectInstances;
	private Array<Entity> entityInstances;
	private Model model, skySphere;
	private ModelInstance instance;
	private int triCount = 0;
	private MeshPartBuilder meshPartBuilder;
	private boolean isSkySphereActive;
	//private TiledMapTileLayer tiledMapLayer0;
	private TiledMapTileLayer currentLayer;
	private TiledMapTileLayer layer1;
	private TiledMapTileLayer layer2;
	private float heightOffset;
	private float levelHeight;
	
	private int[][][] levelArray;
	private int tileLayerCount;
	
	// These vectors and vertex info objects are used in the generation of the level mesh
	private Vector3 p1 = new Vector3();
	private Vector3 p2 = new Vector3();
	private Vector3 p3 = new Vector3();
	private Vector3 p4 = new Vector3();
	private VertexInfo v1 = new VertexInfo();
	private VertexInfo v2 = new VertexInfo();
	private VertexInfo v3 = new VertexInfo();
	private Vector3 normal = new Vector3();
	
	private int numLevel = 1;
	
	public MeshLevel(TiledMap tiledMap, boolean isSkySphereActive) {
		modelBuilder = new ModelBuilder();
		instances  = new Array<ModelInstance>();
		objectInstances = new Array<Object>();
		entityInstances = new Array<Entity>();
		this.tiledMap = tiledMap;
		this.isSkySphereActive = isSkySphereActive;
		//tiledMapLayer0 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		this.currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		this.layer1 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		this.layer2 = (TiledMapTileLayer) tiledMap.getLayers().get(2);
		this.heightOffset = 0f;
		this.levelHeight = 0f;
		
		// count the tile layers
		this.tileLayerCount = 0;
		for(int k = 0; k < tiledMap.getLayers().getCount(); k++){
			if(tiledMap.getLayers().get(k).getName().startsWith("Tile Layer")){
				tileLayerCount++;
			}
		}
		// initialize the levelArray
		levelArray = new int[this.layer1.getHeight()][this.layer1.getWidth()][tileLayerCount*6];
		for(int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				for(int k = 0; k < levelArray[i][j].length; k++){
					levelArray[i][j][k] = 0;
				}
			}
		}
		//generateLevelArray();
		//printLevelArray();
		
		generateLevel();
	}
	
	// print levelArray
	public void printLevelArray(){
		for(int i = 0; i < levelArray.length; i++){
			for(int j = 0; j < levelArray[i].length; j++){
				System.out.print("(");
				for(int k = 0; k < levelArray[i][j].length; k++){
					System.out.print(levelArray[i][j][k] + ", ");
				}
				System.out.print("), ");
			}
			System.out.println("");
		}
	}
	
	// TODO: updateHeightOffset
	public void updateHeightOffset(float yPosPlayer){
		if(yPosPlayer > 5f && numLevel == 1){
			currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(2);
			System.out.print("Current Layer = " + currentLayer.getName() + ". ");
			System.out.println("Level 2");
			numLevel = 2;
		}
		if(yPosPlayer <= 5f && numLevel == 2){
			currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
			System.out.print("Current Layer = " + currentLayer.getName() + ". ");
			System.out.println("Level 1");
			numLevel = 1;
		}
	}
	
	public void generateLevelArray() {

		TiledMapTile tile = null;
		
		for(int k = 0; k < tiledMap.getLayers().getCount(); k++){
			if(tiledMap.getLayers().get(k).getName().startsWith("Tile Layer")){
				currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(k);
				if(currentLayer.getName().equals("Tile Layer 1")){
					heightOffset = 0;
				}else if(currentLayer.getName().equals("Tile Layer 2")){
					heightOffset = 6;
				}
				
				// on each cell
				for(int i = 0; i < currentLayer.getWidth(); i++){
					for(int j = 0; j < currentLayer.getHeight(); j++){
						tile = currentLayer.getCell(i, j).getTile();
						// place blocks
						if(tile.getProperties().containsKey("height")){
							for(int h=0; h<getHeight(tile); h++){
								levelArray[i][j][h + (int)heightOffset] = ARRAY_BLOCK;
							}
						}
						// place ramps
						if(tile.getProperties().containsKey("ramp")){
							levelArray[i][j][getHeight(tile) + 1] = getIntRampDirection(tile) + 1;
						}
					}
				}
			}
		}
	}
	
	public Array<ModelInstance> generateLevelMesh() {
		return instances;
	}
	
	// TODO: generateLevel
	public Array<ModelInstance> generateLevel() {
		
		if (isSkySphereActive) {
			skySphere = modelBuilder.createSphere(100f, 100f, 100f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.BLACK)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			instance = new ModelInstance(skySphere);
			instance.transform.setToTranslation(currentLayer.getHeight()/2, 0, currentLayer.getWidth()/2);
			instances.add(instance);
		}
		
		//modelBuilder.begin();

		TiledMapTile tile1 = null;
		TiledMapTile tile2 = null;
		//TiledMapTile currentTile = null;
		
		for(int k = 0; k < tiledMap.getLayers().getCount(); k++){
			if(tiledMap.getLayers().get(k).getName().startsWith("Tile Layer")){
				//TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(k);
				currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(k);
				if(currentLayer.getName().equals("Tile Layer 1")){
					heightOffset = 0;
				}else if(currentLayer.getName().equals("Tile Layer 2")){
					heightOffset = 6;
				}
				//modelBuilder.begin();

				// on each cell
				for(int i = 0; i < currentLayer.getWidth(); i++){
					for(int j = 0; j < currentLayer.getHeight(); j++){
						
						if(layer1.getCell(i,j) != null){
							tile1 = layer1.getCell(i,j).getTile();
						}
						if(layer2.getCell(i,j) != null){
							tile2 = layer2.getCell(i,j).getTile();
						}
						
						TiledMapTile tile = null;
						if(currentLayer.getCell(i,j) != null){
							tile = currentLayer.getCell(i,j).getTile();
						}
						
						// make lvl2 ceilings
						if(tile2 != null && tile2.getProperties().containsKey("height")
								&& tile1 != null && getHeight(tile1) != 5){
							modelBuilder.begin();
							Node node = modelBuilder.node();
							node.translation.set(j,5,i);
							meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
									GL20.GL_TRIANGLES,
									Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
									Assets.wallMat);
		
							meshPartBuilder.rect(0,0,0, 1,0,0, 1,0,1, 0,0,1, 0,-1,0);
							model = modelBuilder.end();
							instance = new ModelInstance(model);
							instances.add(instance);
						}
						
						// make the floor tiles
						if(currentLayer.getCell(i,j) != null && tile.getProperties().containsKey("height")){
							// if ramp
							if(currentLayer.getCell(i,j) != null && currentLayer.getCell(i,j).getTile().getProperties().containsKey("ramp")){
								String direction = getRampDirection(tile);
								int height = getHeight(tile);
								modelBuilder.begin();
								Node node = modelBuilder.node();
								node.translation.set(j,height+heightOffset,i);
								meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
										GL20.GL_TRIANGLES, 
										Usage.Position | Usage.Normal | Usage.TextureCoordinates,
										Assets.stoneFloorMat);

								if (direction.equals("up"))	{ // -x direction
									meshPartBuilder.rect(0,0,1, 1,1,1, 1,1,0, 0,0,0, -ROOT_PT5,ROOT_PT5,0);
								}	
								else if (direction.equals("down")) { // +x direction
									meshPartBuilder.rect(0,1,1, 1,0,1, 1,0,0, 0,1,0, ROOT_PT5,ROOT_PT5,0);
								}	
								else if(direction.equals("left")) { // +z direction
									meshPartBuilder.rect(0,0,1, 1,0,1, 1,1,0, 0,1,0, 0,ROOT_PT5,ROOT_PT5);
								}	
								else if (direction.equals("right"))	{ // -z direction
									meshPartBuilder.rect(0,1,1, 1,1,1, 1,0,0, 0,0,0, 0,ROOT_PT5,-ROOT_PT5);
								}	
								else {
									System.err.println("generateLevel(): Direction not recognized");
								}
								model = modelBuilder.end();
								instance = new ModelInstance(model);
								instances.add(instance);

							}
							// else not a ramp
							else {
								int height = getHeight(tile);
								modelBuilder.begin();
								Node node = modelBuilder.node();
								node.translation.set(j,height+heightOffset,i);
								meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
										GL20.GL_TRIANGLES,
										Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
										Assets.stoneFloorMat);

								meshPartBuilder.rect(0,0,1, 1,0,1, 1,0,0, 0,0,0, 0,1,0);
								model = modelBuilder.end();
								instance = new ModelInstance(model);
								instances.add(instance);
							}
						}
						//				Node node = modelBuilder.node();
						//				node.translation.set(0,0,0);

						// make any north-facing walls (look south)
						makeWalls(i,j,NORTH);

						// make any south-facing walls (look north)
						makeWalls(i,j,SOUTH);

						// make any east-facing walls
						makeWalls(i,j,EAST);

						// make any west-facing walls
						makeWalls(i,j,WEST);
					}
				}
				//		model = modelBuilder.end();
				//		instance = new ModelInstance(model);
				//		instances.add(instance);
			}
		}
		
		heightOffset = 0;
		
		if (tiledMap.getLayers().get("objects") != null){
			setObjectInstances();
		}
		else{
			System.err.println("TileMap - No object layer in current map");
		}
		
		currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Tile Layer 1");
		return instances;
	}
	
	// TODO: makeWalls
	// calling this will make the walls at tile (i,j) that are facing the given direction
	private void makeWalls(int i, int j, int direction) {
		TiledMapTileLayer layer = currentLayer;
		TiledMapTile tile = layer.getCell(i,j).getTile();
		int looki = 0;
		int lookj = 0;
		// direction: which direction the wall is facing
		switch(direction) {
			case NORTH:	// look south to check for north-facing wall
				looki = i+1;
				lookj = j;
				break;
			case SOUTH:	// look north to check for south-facing wall
				looki = i-1;
				lookj = j;
				break;
			case EAST:	// look west to check for east-facing wall
				looki = i;
				lookj = j-1;
				break;
			case WEST:	// look east to check for west-facing wall
				looki = i;
				lookj = j+1;
				break;
			default:
				System.err.println("makeWalls: Direction not recognized");
		}
		
		TiledMapTile adj;
		if( layer.getCell(looki, lookj) != null){
			adj = layer.getCell(looki, lookj).getTile();
		} else {
			adj = null;
		}
		
		// case where current tile and adjacent tile are not ramps
		if(adj!=null &&
				adj.getProperties().containsKey("height") &&
				!adj.getProperties().containsKey("ramp")&&
				!tile.getProperties().containsKey("ramp") &&
				getHeight(adj) > getHeight(tile)){
			int bottom = getHeight(tile);
			if( layer.getName().equals("Tile Layer 2")
					&& ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getCell(i,j).getTile().getProperties().containsKey("ramp")
					&& (getHeight((((TiledMapTileLayer) tiledMap.getLayers().get(0)).getCell(i,j)).getTile()) == 5)){
				bottom++;
			}
			for(float b1 = bottom; b1 < getHeight(adj); b1++){
				genWall(i, j, b1, direction);
			}
		}
		// case where current tile is a ramp, but the adjacent tile is not
		if(adj!=null &&
				adj.getProperties().containsKey("height") &&
				tile.getProperties().containsKey("ramp") &&
				!adj.getProperties().containsKey("ramp")&&
				getHeight(adj) > getHeight(tile)){
			for(float b1 = getHeight(tile)+1; b1 < getHeight(adj); b1++){
				genWall(i, j, b1, direction);
			}
			// generate the walls at the bottom of ramp slopes (if any)
			if(getIntRampDirection(tile) == direction){
				genWall(i, j, getHeight(tile), direction);
			}
			
			
			// generates the triangle segments and puts them in the right spot
			makeTriangleSegments(getIntRampDirection(tile), getHeight(tile),
					getIntRampDirection(adj), getHeight(adj), direction, j, i);
			
		}
		// case where the current tile is not a ramp, but the adjacent tile is
		if(adj!=null &&
				adj.getProperties().containsKey("height") &&
				adj.getProperties().containsKey("ramp")&&
				!tile.getProperties().containsKey("ramp")
				&& getHeight(adj) >= getHeight(tile)){
			int bottom = getHeight(tile);
			if( layer.getName().equals("Tile Layer 2")
					&& ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getCell(i,j).getTile().getProperties().containsKey("ramp")
					&& (getHeight((((TiledMapTileLayer) tiledMap.getLayers().get(0)).getCell(i,j)).getTile()) == 5)){
				bottom++;
			}
			for(int b1 = bottom; b1 < getHeight(adj); b1++){
				genWall(i, j, b1, direction);
			}
			
			// generates the triangle segments and puts them in the right spot
			makeTriangleSegments(getIntRampDirection(tile), getHeight(tile),
					getIntRampDirection(adj), getHeight(adj), direction, j, i);
			
		}
		// case where both the current tile and the adjacent tile are ramps
		if(adj!=null &&
				adj.getProperties().containsKey("height") &&
				adj.getProperties().containsKey("ramp")&&
				tile.getProperties().containsKey("ramp") &&
				getHeight(adj) > getHeight(tile)){
			for(float b1 = getHeight(tile)+1; b1 < getHeight(adj); b1++){
				genWall(i, j, b1, direction);
			}
			
			// generates the triangle segments and puts them in the right spot
			makeTriangleSegments(getIntRampDirection(tile), getHeight(tile),
					getIntRampDirection(adj), getHeight(adj), direction, j, i);
			
		}
	}
	
	private void makeTriangleSegments(int currRampDir, int currHeight, int adjRampDir, int adjHeight, 
			int wallFaceDir, int j, int i){
		
		// case where the adjacent tile is a ramp
		if(adjRampDir != 0){
			switch(wallFaceDir){
			case(NORTH):
				if(adjRampDir == LEFT){
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if(adjRampDir == UP){
					// need to make a triangle
					p1.set(0,0,1);
					p2.set(1,1,1);
					p3.set(1,0,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if(adjRampDir == DOWN){
					// need to make a triangle
					p1.set(0,1,1);
					p2.set(1,0,1);
					p3.set(0,0,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			case(SOUTH):
				if(adjRampDir == RIGHT){
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if(adjRampDir == UP){
					// need to make a triangle
					p1.set(1,1,0);
					p2.set(0,0,0);
					p3.set(1,0,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if(adjRampDir == DOWN){
					// need to make a triangle
					p1.set(1,0,0);
					p2.set(0,1,0);
					p3.set(0,0,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			case(EAST):
				if(adjRampDir == UP){
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if(adjRampDir == LEFT){
					// need to make a triangle
					p1.set(0,1,0);
					p2.set(0,0,1);
					p3.set(0,0,0);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if(adjRampDir == RIGHT){
					// need to make a triangle
					p1.set(0,0,0);
					p2.set(0,1,1);
					p3.set(0,0,1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			case(WEST):
				if(adjRampDir == DOWN){
					// need to make a rectangle
					genWall(i, j, adjHeight, wallFaceDir);
				}
				if(adjRampDir == LEFT){
					// need to make a triangle
					p1.set(1,0,1);
					p2.set(1,1,0);
					p3.set(1,0,0);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				if(adjRampDir == RIGHT){
					// need to make a triangle
					p1.set(1,1,1);
					p2.set(1,0,0);
					p3.set(1,0,1);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3, j, adjHeight, i);
				}
				break;
			default:
				break;
			}
		}
		// case where the current tile is a ramp
		if(currRampDir != 0){
			switch(currRampDir){
			case(UP):
				//
				if(wallFaceDir == NORTH){
					p1.set(0,0,1);
					p2.set(0,1,1);
					p3.set(1,1,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if(wallFaceDir == SOUTH){
					p1.set(1,1,0);
					p2.set(0,1,0);
					p3.set(0,0,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			case(DOWN):
				//
				if(wallFaceDir == NORTH){
					p1.set(0,1,1);
					p2.set(1,1,1);
					p3.set(1,0,1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if(wallFaceDir == SOUTH){
					p1.set(1,0,0);
					p2.set(1,1,0);
					p3.set(0,1,0);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			case(LEFT):
				//
				if(wallFaceDir == WEST){
					p1.set(1,0,1);
					p2.set(1,1,1);
					p3.set(1,1,0);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if(wallFaceDir == EAST){
					p1.set(0,1,0);
					p2.set(0,1,1);
					p3.set(0,0,1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			case(RIGHT):
				//
				if(wallFaceDir == WEST){
					p1.set(1,1,1);
					p2.set(1,1,0);
					p3.set(1,0,0);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
				if(wallFaceDir == EAST){
					p1.set(0,0,0);
					p2.set(0,1,0);
					p3.set(0,1,1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3, j, currHeight, i);
				}
			break;
			default:
				System.err.println("makeWalls(): Ramp direction not recognized");
				break;
			}
		}
	}
	
	// TODO: genTriangle
	// Generates a triangle given the VertexInfo (these include normals, and UV texture coordinates)
	private void genTriangle(VertexInfo v1, VertexInfo v2, VertexInfo v3, int offset1, int offset2, int offset3){
		modelBuilder.begin();
		Node node = modelBuilder.node();
		node.translation.set(offset1, offset2+heightOffset, offset3);
		meshPartBuilder = modelBuilder.part("triangle" + triCount++, 
				GL20.GL_TRIANGLES, 

				Usage.Position
				| Usage.Normal
				| Usage.TextureCoordinates
				, Assets.wallMat
				);

		meshPartBuilder.triangle(v1, v2, v3);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
	}
	// TODO: genWall
	// Generates a wall segment
	private void genWall(float cellj, float celli, float bottom, int direction){
		String dirString;
		
		switch(direction){
		case NORTH:
			dirString = "North";
			
			p1.set(1, 0, 1);
			p2.set(0, 0, 1);
			p3.set(0, 1, 1);
			p4.set(1, 1, 1);
			normal.set(0f,0f,-1f);
			
			break;
		case SOUTH:
			dirString = "South";
			
			p1.set(0, 0, 0);
			p2.set(1, 0, 0);
			p3.set(1, 1, 0);
			p4.set(0, 1, 0);
			normal.set(0f,0f,1f);
			
			break;
		case EAST:
			dirString = "East";
		
			p1.set(0, 0, 1);
			p2.set(0, 0, 0);
			p3.set(0, 1, 0);
			p4.set(0, 1, 1);
			normal.set(1f,0f,0f);
			
			break;
		case WEST:
			dirString = "West";
		
			p1.set(1, 0, 0);
			p2.set(1, 0, 1);
			p3.set(1, 1, 1);
			p4.set(1, 1, 0);
			normal.set(-1f,0f,0f);
			
			break;
		default:
			dirString = "Error";
			System.err.println("Error: direction not recognized");
		}
		
		modelBuilder.begin();
		Node node = modelBuilder.node();
		node.translation.set(celli,bottom+heightOffset,cellj);
		meshPartBuilder = modelBuilder.part(dirString + "_wall" + celli + "_" + cellj, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				Assets.wallMat);

		meshPartBuilder.rect(p1, p2, p3, p4, normal);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
	}
	
	//Objects are read from the "objects" layer in the tile map
	private void setObjectInstances() {
		Vector3 objPosition;
		MapObjects objects = tiledMap.getLayers().get("objects").getObjects();

		for (int i = 0; i < objects.getCount(); i++) {
			RectangleMapObject rectObj = (RectangleMapObject) objects.get(i);
			//decal, color, intensity, pointLight, id, isActive, isRenderable
			if (rectObj.getName().contains("Torch")) {
				objPosition = new Vector3();
				int height = getObjectHeight(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				Torch torch = new Torch(objPosition, 'W', 1, true, true);

				if (rectObj.getProperties().containsKey("N"))
					torch.setDirection('N');
				else if (rectObj.getProperties().containsKey("S"))
					torch.setDirection('S');
				else if (rectObj.getProperties().containsKey("E")) 
					torch.setDirection('E');
				else if (rectObj.getProperties().containsKey("W"))
					torch.setDirection('W');

				torch.setDecal(Decal.newDecal(Assets.torch, true));
				torch.setColor(getLightColor(rectObj));
				torch.getDecal().setScale(0.003f);
				torch.setRotations(torch.getDirection());
				PointLight light = new PointLight();
				torch.setPointLight(light.set(getLightColor(rectObj), objPosition, 2f));
				Entity.entityInstances.add(torch);
			}

			else if (rectObj.getName().contains("Light")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				PointLight pointLight = new PointLight();
				pointLight.set(getLightColor(rectObj), objPosition, 20f);
				Light light = new Light(objPosition, 2, true, true, pointLight);
				Entity.entityInstances.add(light);
			}

			/*
			else if (rectObj.getName().contains("Emitter")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, new ColorAttribute(ColorAttribute.AmbientLight).color.set(color), 20f, 3, false);
				objectInstances.add(object);
			}
			 */

			else if (rectObj.getName().contains("Enemy")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				ModelInstance test = BuildModel.buildBoxTextureModel(1f, 1f, 1f, Assets.wallMat);
				test.transform.setToTranslation(objPosition);
				Vector3 rotation = new Vector3(0f, 0f, 0f);
				Vector3 scale = new Vector3(2f, 2f, 2f);
				//				int health, Weapon currentWeapon, int id, boolean isActive,
				//				boolean isRenderable, Vector3 position, Vector3 rotation,
				//				Vector3 scale, Vector3 velocity, Vector3 acceleration,
				//				ModelInstance model
				//				Enemy enemy = new Enemy(7, true, true, objPosition, rotation, scale, new Vector3(0, 0, 0),
				//										new Vector3(0, 0, 0), test);
				//				Entity.entityInstances.add(enemy);
			}

			else if (rectObj.getName().contains("Weapon")) {
				int height = getObjectHeight(rectObj);
				//float scale = 0.005f;
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				Assets.loadModels();
				StaticWeapon weapon = new StaticWeapon(objPosition, 1, true, true, true, Assets.manager.get("GUNFBX.g3db", Model.class));
				BoundingBox temp = new BoundingBox();
				weapon.getModel().calculateBoundingBox(temp);
				weapon.setBoundingBox(temp);
				weapon.getModel().transform.setToTranslation(weapon.getPosition());
				weapon.getModel().transform.scale(0.005f, 0.005f, 0.005f);
				PointLight pointLight = new PointLight();
				pointLight.set(getLightColor(rectObj), objPosition, 1f);
				weapon.setPointLight(pointLight);
				Entity.entityInstances.add(weapon);
			}

			else if (rectObj.getName().contains("Mist")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				PointLight pointLight = new PointLight();
				pointLight.set(getLightColor(rectObj), objPosition, 20f);
				Mist mist = new Mist(objPosition, 2, true, true, pointLight);
				Entity.entityInstances.add(mist);
			}

			else if (rectObj.getName().contains("Spawn")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				Assets.loadModels();
				Enemy enemy = new Enemy(9, false, true, objPosition, new Vector3(0, 0, 0), 
						new Vector3(0.8f, 0.8f, 0.8f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
						new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
				enemy.setAnimation(new AnimationController(enemy.getModel()));
				enemy.getStateMachine().Current = enemy.spawn;
				enemy.setInCollision(true);
				Spawn spawn = new Spawn(objPosition, 8, true, false, false, getSpawnTime(rectObj), enemy);
				Entity.entityInstances.add(spawn);
			}

			else {
				System.err.println("setObjectInstances(): Object does not exist " + rectObj.getName());
			}
		}
	}

	// returns the tile height at the given position
	public int getHeight(Vector3 position){
		TiledMapTile tile = getTileAt(getTileCoords(position));
		if(tile != null){
			return getHeight(tile);
		}else{
			return -1;
		}
	}
	
	// returns the height of the given tile
	public int getHeight(TiledMapTile tile) {
		String height = "-1";
		if (tile.getProperties().containsKey("height")){
			height = tile.getProperties().get("height").toString();
		}
		return Integer.parseInt(height);
	}
	
	// returns the tile object at the given position
	public TiledMapTile getTileAt(GridPoint2 position){
		if(position.x >= 0 && position.x < currentLayer.getWidth() && position.y >= 0 && position.y < currentLayer.getHeight()){
			return currentLayer.getCell(position.x, position.y).getTile();
		}
		else{
			return null;
		}
	}
	
	public float getSpawnTime(RectangleMapObject object) {
		String time = "0";
		
		if (object.getProperties().containsKey("time")) {
			time = object.getProperties().get("time").toString();
		}
		
		return Float.parseFloat(time);
	}
	
	// returns the height of the given map object
	public int getObjectHeight(RectangleMapObject object) {
		String height = "0";
		
		if (object.getProperties().containsKey("height")) {
			height = object.getProperties().get("height").toString();
		}
		
		return Integer.parseInt(height) + (int)heightOffset;
	}
	
	// returns the color of a light map object
	private Color getLightColor(RectangleMapObject object) {
		Color color = new Color();
		int temp;
		
		if (object.getProperties().containsKey("color")) {
			String type = object.getProperties().get("color").toString();
			temp = Integer.parseInt(type);
			
			switch(temp) {
				case(RED):
					color.set(1f, 0f, 0f, 1f);
					break;
				case(WHITE):
					color.set(240f/255f, 1f, 186f/255f, 1f);
					break;
				case(GREEN):
					color.set(0f, 1f, 68f/255f, 1f);
					break;
				case(BLUE):
					color.set(0f, 0f, 1f, 1f);
					break;
				case(PURPLE):
					color.set(127f/255f, 0f, 1f, 1f);
					break;
				default:
					color.set(0f, 0f, 0f, 1f);
					break;
			}
		}
		else {
			System.err.println("Tile color not set. Default color used");
			color.set(0f, 0f, 0f, 1f);
		}
		
		return color;
	}
	
	// returns the direction of the upward slope. Up = East, Down = West, Left = North, Right = South, NAR = not a ramp
	private String getRampDirection(TiledMapTile tile) {
		if(tile.getProperties().containsKey("ramp")){
			String direction = tile.getProperties().get("ramp").toString();
			return direction;
		} else {
			return "NAR";
		}
	}
	
	// returns the direction of the upward slope
	private int getIntRampDirection(TiledMapTile tile) {
		String direction = getRampDirection(tile);
		int intDir;
		if (direction.equals("up"))	{ // -x direction
			intDir = UP;
		}	
		else if (direction.equals("down")) { // +x direction
			intDir = DOWN;
		}	
		else if(direction.equals("left")) { // +z direction
			intDir = LEFT;
		}	
		else if (direction.equals("right"))	{ // -z direction
			intDir = RIGHT;
		}
		else{
			// not a ramp
			intDir = 0;
		}
		return intDir;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
	
	public Array<ModelInstance> getInstances() {
		return instances;
	}
	
	public Array<Object> getObjectInstances() {
		return objectInstances;
	}
	
	public Array<Entity> getEntityInstances() {
		return entityInstances;
	}
	
	// TODO: Check Collision
	// true ultimate 3d collision maths
	// This returns a Vector3. A '0' in that vector represents collision on the corresponding axis,
	// and a '1' represents no collision on the corresponding axis
	// The vector returned is then multiplied component by component with the movement vector in Player.update()
	public Vector3 checkCollision(Vector3 oldPos, Vector3 newPos, float objectWidth, float objectHeight, float objectLength){
		Vector3 collisionVector = new Vector3(1,1,1);
		Vector3 movementVector = new Vector3(newPos.x - oldPos.x, newPos.y - oldPos.y, newPos.z - oldPos.z);
		
		TiledMapTileLayer oldPosLayer, newPosLayer;
		int oldHeightOffset, newHeightOffset;
		if(oldPos.y >= 6.5f){ 
			oldPosLayer = (TiledMapTileLayer)tiledMap.getLayers().get(2);
			oldHeightOffset = 6;
		}
		else {
			oldPosLayer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
			oldHeightOffset = 0;
		}
		if(newPos.y >= 6.5f) {
			newPosLayer = (TiledMapTileLayer)tiledMap.getLayers().get(2);
			newHeightOffset = 6;
		}
		else { 
			newPosLayer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
			newHeightOffset = 0;
		}
		
		if(movementVector.len() > 0){
			Vector3 blockSize = new Vector3(SPOT_WIDTH, 0, SPOT_LENGTH);
			Vector3 objectSize = new Vector3(objectWidth, objectHeight, objectLength);
			
			// returns tiled coords (as opposed to world coords)
			GridPoint2 tileCoords = getTileCoords(oldPos);
			
			int startX, endX, startY, endY;
			
			// This checks only the spaces in the direction of motion
			if(movementVector.x > 0){
				startY = tileCoords.y;
				endY = tileCoords.y + 1;
			}else if(movementVector.x < 0){
				startY = tileCoords.y - 1;
				endY = tileCoords.y;
			}else{
				startY = tileCoords.y;
				endY = tileCoords.y;
			}
			if(movementVector.z > 0){
				startX = tileCoords.x;
				endX = tileCoords.x + 1;
			}else if(movementVector.z < 0){
				startX = tileCoords.x - 1;
				endX = tileCoords.x;
			}else{
				startX = tileCoords.x;
				endX = tileCoords.x;
			}
			
			// iterate through certain tiles in front of the player, depending on direction
			for(int i = startX; i <= endX; i++) {
				
				// don't check tiles outside the map which don't exist
				if(i < 0 || i >= oldPosLayer.getWidth()) {continue;}
				
				for(int j = startY; j <= endY; j++) {
					
					// don't check tiles outside the map which don't exist
					if (j<0 || j >= oldPosLayer.getHeight()) { continue; }
					
					// TODO: Fix known bug: Player can access a ramp from the side, but should not be able to.
					
					// if oldPos tile is a ramp, it can lead us up one space
					if (oldPosLayer.getCell(tileCoords.x, tileCoords.y)!= null) {
						if (oldPosLayer.getCell(tileCoords.x, tileCoords.y).getTile().getProperties().containsKey("ramp")) {

							if (getHeight(newPosLayer.getCell(i, j).getTile()) + newHeightOffset > getHeight(oldPosLayer.getCell(tileCoords.x, tileCoords.y).getTile()) + oldHeightOffset + 1) {
								//check collision
								//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
								Vector3 tilePos = new Vector3(j * blockSize.x, getHeight(newPosLayer.getCell(i, j).getTile()) + newHeightOffset, i * blockSize.z);
								Vector3 rectCollideVec = rectCollide(oldPos, newPos, objectSize, tilePos, blockSize);
	
								collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
							}
						}
						else if (getHeight(newPosLayer.getCell(i, j).getTile()) + newHeightOffset > getHeight(oldPosLayer.getCell(tileCoords.x, tileCoords.y).getTile()) + oldHeightOffset) {
							//check collision
							//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
							Vector3 tilePos = new Vector3(j * blockSize.x, getHeight(newPosLayer.getCell(i, j).getTile()) + newHeightOffset, i * blockSize.z);
							Vector3 rectCollideVec = rectCollide(oldPos, newPos, objectSize, tilePos, blockSize);
	
							collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
						}
					}
				}
			}
		}
		
		// This prevents the player from going out of bounds of the level
		if(newPos.x - objectWidth < 0 || newPos.x + objectWidth > oldPosLayer.getHeight()) {
			collisionVector.x = 0;
		}
		if(newPos.z - objectLength < 0 || newPos.z + objectLength > oldPosLayer.getWidth()) {
			collisionVector.z = 0;
		}
		
		return new Vector3(collisionVector.x, collisionVector.y, collisionVector.z);
	}
	
	// collision of 1 rectangular object with another
	// called by checkCollision, this takes the old and new positions of the moving object (maybe a player)
	// and the position of the object to test collision with, as well as the size of both objects
	// This returns a Vector3. A '0' in that vector represents collision on the corresponding axis,
	// and a '1' represents no collision on the corresponding axis
	private Vector3 rectCollide(Vector3 oldPos, Vector3 newPos, Vector3 size1, Vector3 pos2, Vector3 size2) {
		Vector3 result = new Vector3(0,0,0);
	
		if(     newPos.x + size1.x < pos2.x ||
				newPos.x - size1.x > pos2.x + size2.x * size2.x ||
				oldPos.y - size1.y > pos2.y ||
                oldPos.z + size1.z < pos2.z ||
                oldPos.z - size1.z > pos2.z + size2.z * size2.z) {
			result.x = 1;
        }
		if(     oldPos.x + size1.x < pos2.x ||
				oldPos.x - size1.x > pos2.x + size2.x * size2.x ||
                newPos.y - size1.y > pos2.y ||
                oldPos.z + size1.z < pos2.z ||
                oldPos.z - size1.z > pos2.z + size2.z * size2.z) {
			result.y = 1;
        } 
        if(     oldPos.x + size1.x < pos2.x ||
        		oldPos.x - size1.x > pos2.x + size2.x * size2.x ||
        		oldPos.y - size1.y > pos2.y ||
                newPos.z + size1.z < pos2.z ||
                newPos.z - size1.z > pos2.z + size2.z * size2.z) {

            result.z = 1;
        }
        
        // This is useful if you want to do debugging on collision. Simply un-comment it in that case.
//        if(result.x == 0 || result.y == 0 || result.z == 0){
//        	System.out.println("Collision ("+result.x+", "+result.y+", "+result.z+")" + ", oldPos ("+oldPos.x+", "+oldPos.y+", "+oldPos.z+")"
//        			+", newPos ("+newPos.x+", "+newPos.y+", "+newPos.z+")"
//        			+", pos2 ("+pos2.x+", "+pos2.y+", "+pos2.z+")");
//        }
        
		return result;
	}
	
	// TODO: Map Height
	// given x,z world coordinates, this method returns the height value of the map at that point (including on ramps)
	public float mapHeight(float x, float z) {
		
		float height = 0;
		GridPoint2 tileCoords = getTileCoords(x, z);

		TiledMapTile tile = currentLayer.getCell(tileCoords.x, tileCoords.y).getTile();
		
		// this is in case the player is on level 2 but there is no floor there, so level 1 is used
		height = (float)getHeight(tile);
		if(height == -1f){
			currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
			heightOffset = 0;
		}
		
		if(tile.getProperties().containsKey("ramp")) {
			
			//height = (float)getHeight(tile);
			int temp = 0;
			String direction = getRampDirection(tile);
			
			if (direction.equals("up"))	{ // -x direction
				temp = (int)x;
				height += x - temp;
			}
			else if (direction.equals("down")) { // +x direction
				temp = (int)x;
				height += 1 - (x - temp);
			}
			else if(direction.equals("left")) { // -z direction
				temp = (int)z;
				height += 1 - (z - temp);
			}
			else if (direction.equals("right"))	{ // +z direction
				temp = (int)z;
				height += z - temp;
			}
			else {
				System.err.println("MeshLevel.rampHeight() - Direction not recognized");
			}
		}
		return height;
	}
	
public float mapHeight(float x, float z, int heightLevel) {
		
		float height = 0;
		GridPoint2 tileCoords = getTileCoords(x, z);

		TiledMapTile tile;
		if(heightLevel == 1){
			tile = layer1.getCell(tileCoords.x, tileCoords.y).getTile();
		}else{
			tile = layer2.getCell(tileCoords.x, tileCoords.y).getTile();
		}
		
		// this is in case the player is on level 2 but there is no floor there, so level 1 is used
		height = (float)getHeight(tile);
		if(height == -1f){
			currentLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
			heightOffset = 0;
		}
		
		if(tile.getProperties().containsKey("ramp")) {
			
			//height = (float)getHeight(tile);
			int temp = 0;
			String direction = getRampDirection(tile);
			
			if (direction.equals("up"))	{ // -x direction
				temp = (int)x;
				height += x - temp;
			}
			else if (direction.equals("down")) { // +x direction
				temp = (int)x;
				height += 1 - (x - temp);
			}
			else if(direction.equals("left")) { // -z direction
				temp = (int)z;
				height += 1 - (z - temp);
			}
			else if (direction.equals("right"))	{ // +z direction
				temp = (int)z;
				height += z - temp;
			}
			else {
				System.err.println("MeshLevel.rampHeight() - Direction not recognized");
			}
		}
		return height;
	}
	
	// gets world coords of the center of the tile at world coords (x, y, z)
	public Vector2 getTileCenter(float x, float z){
		Vector2 returnVec = new Vector2(0,0);
		GridPoint2 tileCoords = getTileCoords(x, z);
		returnVec.set(tileCoords.y + 0.5f, tileCoords.x + 0.5f);
		return returnVec;
	}
	
	// input Vector3 position (world coordinates) and output tile coordinates
	private GridPoint2 getTileCoords(Vector3 position) {
		return getTileCoords(position.x, position.z);
	}
	
	// input x,z position (world coordinates) and output tile coordinates
	private GridPoint2 getTileCoords(float x, float z) {
		int tileX = (int)z;
		int tileY = (int)x;
		return new GridPoint2(tileX, tileY);
	}
	
	public int getHeightOffset(){
		return (int)heightOffset;
	}
	
	public int getHeightOffset(Vector3 position){
		if(position.y > 6){ 
			return 6;
		}
		else {
			return 0;
		}
	}
}
