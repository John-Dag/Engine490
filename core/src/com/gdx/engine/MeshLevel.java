package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
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
import com.badlogic.gdx.utils.Array;

// Important note to the team: (this can be confusing)
// World coordinates have x, y, z, with +x pointing East, and +z pointing South
// Tiled coordinates have x, y with +x pointing South and +y pointing East
// Many methods convert between these two coordinate systems, so its important to know which variables are in which coordinate system.

public class MeshLevel {
	public static Color skyColor = Color.BLACK;
	private static final float ROOT_PT5 = 0.70710678f;
	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;
	
	private static final float SPOT_WIDTH = 1;
	private static final float SPOT_LENGTH = 1;
	
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
	private TiledMapTileLayer tiledMapLayer0;
	
	public MeshLevel(TiledMap tiledMap, boolean isSkySphereActive) {
		modelBuilder = new ModelBuilder();
		instances  = new Array<ModelInstance>();
		objectInstances = new Array<Object>();
		entityInstances = new Array<Entity>();
		this.tiledMap = tiledMap;
		this.isSkySphereActive = isSkySphereActive;
		tiledMapLayer0 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
	}
	
	public Array<ModelInstance> generateLevel() {
		
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		
		if (isSkySphereActive) {
			skySphere = modelBuilder.createSphere(100f, 100f, 100f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.BLACK)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			instance = new ModelInstance(skySphere);
			instance.transform.setToTranslation(layer.getHeight()/2, 0, layer.getWidth()/2);
			instances.add(instance);
		}
		
		//modelBuilder.begin();
		
		// on each cell
		for(int i = 0; i < layer.getWidth(); i++){
			for(int j = 0; j < layer.getHeight(); j++){
				TiledMapTile tile = layer.getCell(i,j).getTile();
				// make the floor tiles
				if(layer.getCell(i,j) != null && tile.getProperties().containsKey("height")){
					// if ramp
					if(layer.getCell(i,j) != null && layer.getCell(i,j).getTile().getProperties().containsKey("ramp")){
						String direction = getRampDirection(tile);
						int height = getHeight(tile);
						modelBuilder.begin();
						Node node = modelBuilder.node();
						node.translation.set(j,height,i);
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
						node.translation.set(j,height,i);
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
		
		if (tiledMap.getLayers().get("objects") != null)
			setObjectInstances();
		else
			System.err.println("generateLevel(): No object layer exists in current map");
		
		return instances;
	}
	
	//Objects are read from the "objects" layer in the tile map
	private void setObjectInstances() {
		Vector3 objPosition;
		MapObjects objects = tiledMap.getLayers().get("objects").getObjects();
		Color color;
		int direction = 0;
		
		for (int i = 0; i < objects.getCount(); i++) {
			RectangleMapObject rectObj = (RectangleMapObject) objects.get(i);
			
			if (rectObj.getName().contains("Torch")) {
				float scale = 0.003f;
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				if (rectObj.getProperties().containsKey("W"))
					direction = 0;
				else if (rectObj.getProperties().containsKey("E"))
					direction = 1;
				else if (rectObj.getProperties().containsKey("N")) 
					direction = 2;
				else if (rectObj.getProperties().containsKey("S"))
					direction = 3;
				
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, Assets.torch, color, scale, direction, 1, false);
				object.rotate(direction);
				//System.out.println("X: " + rectObj.getRectangle().getY() / 32 + " Y: " + rectObj.getRectangle().getX() / 32);
				objectInstances.add(object);
			}
			
			else if (rectObj.getName().contains("Light")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				//System.out.println("X: " + rectObj.getRectangle().getY() / 32 + " Y: " + rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, new ColorAttribute(ColorAttribute.AmbientLight).color.set(color), 20f, 2, false);
				objectInstances.add(object);
			}
			
			else if (rectObj.getName().contains("Emitter")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, new ColorAttribute(ColorAttribute.AmbientLight).color.set(color), 20f, 3, false);
				objectInstances.add(object);
			}
			
			else if (rectObj.getName().contains("Enemy")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				ModelInstance test = BuildModel.buildBoxTextureModel(1f, 1f, 1f, Assets.wallMat);
				test.transform.setToTranslation(objPosition);
				Vector3 rotation = new Vector3(0f, 0f, 0f);
				Vector3 scale = new Vector3(2f, 2f, 2f);
				Enemy enemy = new Enemy(objPosition, rotation, scale, true, 4, false, test);
				entityInstances.add(enemy);
			}
			
			else if (rectObj.getName().contains("Weapon")) {
				int height = getObjectHeight(rectObj);
				float scale = 0.005f;
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, Assets.weapon1Region, color, scale, direction, 5, false);
				objectInstances.add(object);
			}
			
			else if (rectObj.getName().contains("Mist")) {
				int height = getObjectHeight(rectObj);
				objPosition = new Vector3();
				objPosition.set(rectObj.getRectangle().getY() / 32, height + .5f, rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, new ColorAttribute(ColorAttribute.AmbientLight).color.set(color), 20f, 6, false);
				objectInstances.add(object);
			}
			
			else {
				System.err.println("setObjectInstances(): Object does not exist " + rectObj.getName());
			}
		}
	}
	
	private void makeWalls(int i, int j, int direction) {
		// used to generate triangles
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();
		Vector3 p3 = new Vector3();
		
		VertexInfo v1 = new VertexInfo();
		VertexInfo v2 = new VertexInfo();
		VertexInfo v3 = new VertexInfo();
		
		Vector3 normal = new Vector3();
		
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		TiledMapTile tile = layer.getCell(i,j).getTile();
		int looki = 0;
		int lookj = 0;
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
		// case where current tile and adjacent tile are not ramps
		if(layer.getCell(looki, lookj)!=null &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("height") &&
				!layer.getCell(looki, lookj).getTile().getProperties().containsKey("ramp")&&
				!tile.getProperties().containsKey("ramp") &&
				getHeight(layer.getCell(looki, lookj).getTile()) > getHeight(tile)){
			float top = (float)getHeight(layer.getCell(looki, lookj).getTile());
			float bottom = (float)getHeight(tile);
			for(float b1 = bottom; b1 < top; b1++){
				genWall(i,j,b1+1, b1, direction);
			}
		}
		// case where current tile is a ramp, but the adjacent tile is not
		if(layer.getCell(looki, lookj)!=null &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("height") &&
				!layer.getCell(looki, lookj).getTile().getProperties().containsKey("ramp")&&
				tile.getProperties().containsKey("ramp") &&
				getHeight(layer.getCell(looki, lookj).getTile()) > getHeight(tile)){
			float top = (float)getHeight(layer.getCell(looki, lookj).getTile());
			float bottom = (float)getHeight(tile);
			for(float b1 = bottom+1; b1 < top; b1++){
				genWall(i,j,b1+1, b1, direction);
			}
			// generate the walls at the bottom of ramp slopes (if any)
			if(getIntRampDirection(tile) == UP && direction == EAST ||
					getIntRampDirection(tile) == DOWN && direction == WEST ||
					getIntRampDirection(tile) == LEFT && direction == NORTH ||
					getIntRampDirection(tile) == RIGHT && direction == SOUTH){
				switch(direction){
				case(NORTH):
					genWall(i+1,j,bottom, bottom+1, SOUTH);
					break;
				case(SOUTH):
					genWall(i-1,j,bottom, bottom+1, NORTH);
					break;
				case(EAST):
					genWall(i,j-1,bottom, bottom+1, WEST);
					break;
				case(WEST):
					genWall(i,j+1,bottom, bottom+1, EAST);
					break;
				default:
					break;
				}
			}
			
			// generate needed triangles
			switch(getIntRampDirection(tile)){
			case(UP):
				//
				if(direction == NORTH){
					p1.set(j,bottom,i+1);
					p2.set(j,bottom+1,i+1);
					p3.set(j+1,bottom+1,i+1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3);
				}
				if(direction == SOUTH){
					p1.set(j+1,bottom+1,i);
					p2.set(j,bottom+1,i);
					p3.set(j,bottom,i);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
			break;
			case(DOWN):
				//
				if(direction == NORTH){
					p1.set(j,bottom+1,i+1);
					p2.set(j+1,bottom+1,i+1);
					p3.set(j+1,bottom,i+1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				if(direction == SOUTH){
					p1.set(j+1,bottom,i);
					p2.set(j+1,bottom+1,i);
					p3.set(j,bottom+1,i);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3);
				}
			break;
			case(LEFT):
				//
				if(direction == WEST){
					p1.set(j+1,bottom,i+1);
					p2.set(j+1,bottom+1,i+1);
					p3.set(j+1,bottom+1,i);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3);
				}
				if(direction == EAST){
					p1.set(j,bottom+1,i);
					p2.set(j,bottom+1,i+1);
					p3.set(j,bottom,i+1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
			break;
			case(RIGHT):
				//
				if(direction == WEST){
					p1.set(j+1,bottom+1,i+1);
					p2.set(j+1,bottom+1,i);
					p3.set(j+1,bottom,i);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				if(direction == EAST){
					p1.set(j,bottom,i);
					p2.set(j,bottom+1,i);
					p3.set(j,bottom+1,i+1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(1.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 0.0f);
					genTriangle(v1, v2, v3);
				}
			break;
			default:
				System.err.println("makeWalls(): Ramp direction not recognized");
				break;
			}
		}
		// case where the current tile is not a ramp, but the adjacent tile is
		if(layer.getCell(looki, lookj)!=null &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("height") &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("ramp")&&
				!tile.getProperties().containsKey("ramp")
				&& getHeight(layer.getCell(looki, lookj).getTile()) >= getHeight(tile)
				){
			float top = (float)getHeight(layer.getCell(looki, lookj).getTile());
			float bottom = (float)getHeight(tile);
			for(float b1 = bottom; b1 < top; b1++){
				genWall(i,j,b1+1, b1, direction);
			}
			
			switch(direction){
			case(NORTH):
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == LEFT){
					// need to make a rect
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					for(float b1 = bottom; b1 < top; b1++){
						genWall(i,j,top,bottom, direction);
					}
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == UP){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j,bottom,i+1);
					p2.set(j+1,bottom+1,i+1);
					p3.set(j+1, bottom, i+1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == DOWN){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j,bottom+1,i+1);
					p2.set(j+1,bottom,i+1);
					p3.set(j,bottom,i+1);
					normal.set(0,0,-1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				break;
			case(SOUTH):
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == RIGHT){
					// need to make a rect
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					for(float b1 = bottom; b1 < top; b1++){
						genWall(i,j,top,bottom, direction);
					}
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == UP){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j+1,bottom+1,i);
					p2.set(j,bottom,i);
					p3.set(j+1,bottom,i);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == DOWN){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j+1,bottom,i);
					p2.set(j,bottom+1,i);
					p3.set(j,bottom,i);
					normal.set(0,0,1);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				break;
			case(EAST):
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == UP){
					// need to make a rect
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					for(float b1 = bottom; b1 < top; b1++){
						genWall(i,j,top,bottom, direction);
					}
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == LEFT){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j,bottom+1,i);
					p2.set(j,bottom,i+1);
					p3.set(j,bottom,i);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == RIGHT){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j,bottom,i);
					p2.set(j,bottom+1,i+1);
					p3.set(j,bottom,i+1);
					normal.set(1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				break;
			case(WEST):
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == DOWN){
					// need to make a rect
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					for(float b1 = bottom; b1 < top; b1++){
						genWall(i,j,top,bottom, direction);
					}
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == LEFT){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j+1,bottom,i+1);
					p2.set(j+1,bottom+1,i);
					p3.set(j+1,bottom,i);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 1.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 0.0f);
					v3.setPos(p3).setNor(normal).setUV(0.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				if(getIntRampDirection(layer.getCell(looki, lookj).getTile()) == RIGHT){
					// need to make a triangle
					bottom = (float)getHeight(layer.getCell(looki, lookj).getTile());
					top = bottom + 1;
					p1.set(j+1,bottom+1,i+1);
					p2.set(j+1,bottom,i);
					p3.set(j+1,bottom,i+1);
					normal.set(-1,0,0);
					v1.setPos(p1).setNor(normal).setUV(1.0f, 0.0f);
					v2.setPos(p2).setNor(normal).setUV(0.0f, 1.0f);
					v3.setPos(p3).setNor(normal).setUV(1.0f, 1.0f);
					genTriangle(v1, v2, v3);
				}
				break;
			default:
				break;
			}
		}
		// case where both the current tile and the adjacent tile are ramps
		if(layer.getCell(looki, lookj)!=null &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("height") &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("ramp")&&
				tile.getProperties().containsKey("ramp") &&
				getHeight(layer.getCell(looki, lookj).getTile()) > getHeight(tile)){
			float top = (float)getHeight(layer.getCell(looki, lookj).getTile());
			float bottom = (float)getHeight(tile);
			for(float b1 = bottom+1; b1 < top; b1++){
				genWall(i,j,b1+1, b1, direction);
			}
		}
	}
	
	// Generates a triangle given the VertexInfo (these include normals, and UV texture coordinates)
	private void genTriangle(VertexInfo v1, VertexInfo v2, VertexInfo v3){
		modelBuilder.begin();
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
	// Generates a wall segment
	private void genWall(float cellj, float celli, float top, float bottom, int direction){
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();
		Vector3 p3 = new Vector3();
		Vector3 p4 = new Vector3();
		Vector3 normalVector = new Vector3();
		String dirString;
		
		switch(direction){
		case NORTH:
			dirString = "North";
			
			p1.set(celli+1f, bottom, cellj+1f);
			p2.set(celli, bottom, cellj+1f);
			p3.set(celli, top, cellj+1f);
			p4.set(celli+1f, top, cellj+1f);
			normalVector.set(0f,0f,-1f);
			
			break;
		case SOUTH:
			dirString = "South";
			
			p1.set(celli, bottom, cellj);
			p2.set(celli+1f, bottom, cellj);
			p3.set(celli+1f, top, cellj);
			p4.set(celli, top, cellj);
			normalVector.set(0f,0f,1f);
			
			break;
		case EAST:
			dirString = "East";
		
			p1.set(celli, bottom, cellj+1f);
			p2.set(celli, bottom, cellj);
			p3.set(celli, top, cellj);
			p4.set(celli, top, cellj+1f);
			normalVector.set(1f,0f,0f);
			
			break;
		case WEST:
			dirString = "West";
		
			p1.set(celli+1f, bottom, cellj);
			p2.set(celli+1f, bottom, cellj+1f);
			p3.set(celli+1f, top, cellj+1f);
			p4.set(celli+1f, top, cellj);
			normalVector.set(-1f,0f,0f);
			
			break;
		default:
			dirString = "Error";
			System.err.println("Error: direction not recognized");
		}
		
		modelBuilder.begin();
		meshPartBuilder = modelBuilder.part(dirString + "_wall" + celli + "_" + cellj, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				Assets.wallMat);

		meshPartBuilder.rect(p1, p2, p3, p4, normalVector);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
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
		String height = "0";
		if (tile.getProperties().containsKey("height")){
			height = tile.getProperties().get("height").toString();
		}
		return Integer.parseInt(height);
	}
	
	// returns the tile object at the given position
	public TiledMapTile getTileAt(GridPoint2 position){
		if(position.x >= 0 && position.x < tiledMapLayer0.getWidth() && position.y >= 0 && position.y < tiledMapLayer0.getHeight()){
			return tiledMapLayer0.getCell(position.x, position.y).getTile();
		}
		else{
			return null;
		}
	}
	
	// returns the height of the given map object
	public int getObjectHeight(RectangleMapObject object) {
		String height = "0";
		
		if (object.getProperties().containsKey("height")) {
			height = object.getProperties().get("height").toString();
		}
		
		return Integer.parseInt(height);
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
					color.set(255, 0, 0, 1);
					break;
				case(WHITE):
					color.set(240, 255, 186, 1);
					break;
				case(GREEN):
					color.set(0, 255, 68, 1);
					break;
				case(BLUE):
					color.set(0, 0, 255, 1);
					break;
				case(PURPLE):
					color.set(255,0,255, 1);
					break;
				default:
					color.set(0, 0, 0, 1);
					break;
			}
		}
		else {
			System.err.println("getLightColor(): Tile color not set. Default color used.");
			color.set(0, 0, 0, 1);
		}
		
		return color;
	}
	
	// returns the direction of the upward slope. Up = East, Down = West, Left = North, Right = South
	private String getRampDirection(TiledMapTile tile) {
		String direction = tile.getProperties().get("ramp").toString();
		return direction;
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
			intDir = -1;
			System.err.println("getIntRampDirection(): Ramp direction not recognized");
		}
		return intDir;
	}
	
	// returns the y or j dimension (in tiles) of the map
	public int getLevelHeight() {
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		return layer.getHeight();
	}
	
	// returns the x or i dimension (in tiles) of the map
	public int getLevelWidth() {
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		return layer.getWidth();
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
	
	// true ultimate 3d collision maths
	// This returns a Vector3. A '0' in that vector represents collision on the corresponding axis,
	// and a '1' represents no collision on the corresponding axis
	// The vector returned is then multiplied component by component with the movement vector in Player.update()
	public Vector3 checkCollision(Vector3 oldPos, Vector3 newPos, float objectWidth, float objectHeight, float objectLength){
		Vector3 collisionVector = new Vector3(1,1,1);
		Vector3 movementVector = new Vector3(newPos.x - oldPos.x, newPos.y - oldPos.y, newPos.z - oldPos.z);
		
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
				if(i < 0 || i >= tiledMapLayer0.getWidth()) {continue;}
				
				for(int j = startY; j <= endY; j++) {
					
					// don't check tiles outside the map which don't exist
					if (j<0 || j >= tiledMapLayer0.getHeight()) { continue; }
					
					// TODO: Fix known bug: Player can access a ramp from the side, but should not be able to.
					
					// if oldPos tile is a ramp, it can lead us up one space
					
					if (tiledMapLayer0.getCell(tileCoords.x, tileCoords.y)!= null && tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile().getProperties().containsKey("ramp")) {
						if (getHeight(tiledMapLayer0.getCell(i, j).getTile()) > getHeight(tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile()) + 1) {
							//check collision
							//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
							Vector3 tilePos = new Vector3(j * blockSize.x, getHeight(tiledMapLayer0.getCell(i, j).getTile()), i * blockSize.z);
							Vector3 rectCollideVec = rectCollide(oldPos, newPos, objectSize, tilePos, blockSize);

							collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
						}
					}
					else if (getHeight(tiledMapLayer0.getCell(i, j).getTile()) > getHeight(tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile())) {
						//check collision
						//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
						Vector3 tilePos = new Vector3(j * blockSize.x, getHeight(tiledMapLayer0.getCell(i, j).getTile()), i * blockSize.z);
						Vector3 rectCollideVec = rectCollide(oldPos, newPos, objectSize, tilePos, blockSize);

						collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
					}
				}
			}
		}
		
		// This prevents the player from going out of bounds of the level
		if(newPos.x - objectWidth < 0 || newPos.x + objectWidth > tiledMapLayer0.getHeight()) {
			collisionVector.x = 0;
		}
		if(newPos.z - objectLength < 0 || newPos.z + objectLength > tiledMapLayer0.getWidth()) {
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
		return result;
	}
	
	// given x,z world coordinates, this method returns the height value of the map at that point (including on ramps)
	public float mapHeight(float x, float z) {
		
		float height = 0;
		GridPoint2 tileCoords = getTileCoords(x, z);
		TiledMapTile tile = tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile();
		height = (float)getHeight(tile);
		
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
}