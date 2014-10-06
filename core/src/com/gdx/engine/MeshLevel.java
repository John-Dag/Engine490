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
		modelBuilder = new ModelBuilder();
		this.tiledMap = tiledMap;
		this.isSkySphereActive = isSkySphereActive;
		tiledMapLayer0 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
	}
	
	public Array<ModelInstance> generateLevel() {
		
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		
		if (isSkySphereActive) {
			skySphere = modelBuilder.createSphere(100f, 100f, 100f, 20, 20, new Material(ColorAttribute.createDiffuse(skyColor)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			instance = new ModelInstance(skySphere);
			instance.transform.setToTranslation(layer.getHeight()/2, 0, layer.getWidth()/2);
			instances.add(instance);
		}
		
		// on each cell
		for(int i = 0; i < layer.getWidth(); i++){
			for(int j = 0; j < layer.getHeight(); j++){
				TiledMapTile tile = layer.getCell(i,j).getTile();
				// make the flat surfaces
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
						
						// check for triangles
						if(direction.equals("up") | direction.equals("down")) {
							// make any north-facing triangles (look south)
							makeTriangles(i,j,NORTH);

							// make any south-facing triangles (look north)
							makeTriangles(i,j,SOUTH);
						}

						if(direction.equals("left") | direction.equals("right")) {
							// make any east-facing triangles
							makeTriangles(i,j,EAST);

							// make any west-facing triangles
							makeTriangles(i,j,WEST);
						}
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
								Assets.floorMat);

						meshPartBuilder.rect(0,0,1, 1,0,1, 1,0,0, 0,0,0, 0,1,0);
						model = modelBuilder.end();
						instance = new ModelInstance(model);
						instances.add(instance);
					}
				}

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
		
		if (tiledMap.getLayers().get("objects") != null)
			setObjectInstances();
		else
			System.err.println("TileMap - No object layer in current map");
		
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
		}
	}
	
	private void makeTriangles(int i, int j, int direction){
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		TiledMapTile tile = layer.getCell(i,j).getTile();
		String rampDirection = getRampDirection(tile);
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
		
		float adjacentHeight = (float)getHeight(layer.getCell(looki, lookj).getTile());
		float tileHeight = (float)getHeight(tile);
		if(tileHeight > adjacentHeight){
			
		}
		else if(tileHeight <= adjacentHeight){
			
		}
		
	}
	
	private void makeWalls(int i, int j, int direction) {
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
		}
		// case where the current tile is not a ramp, but the adjacent tile is
		if(layer.getCell(looki, lookj)!=null &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("height") &&
				layer.getCell(looki, lookj).getTile().getProperties().containsKey("ramp")&&
				!tile.getProperties().containsKey("ramp") &&
				getHeight(layer.getCell(looki, lookj).getTile()) > getHeight(tile)){
			float top = (float)getHeight(layer.getCell(looki, lookj).getTile());
			float bottom = (float)getHeight(tile);
			for(float b1 = bottom; b1 < top; b1++){
				genWall(i,j,b1+1, b1, direction);
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
	
	private void genWallNextToRamp(float celli, float cellj, float top, float bottom, int direction){
		Vector3 p1 = new Vector3();
		Vector3 p2 = new Vector3();
		Vector3 p3 = new Vector3();
		Vector3 p4 = new Vector3();
		Vector3 normalVector = new Vector3();
		String dirString;
		
		switch(direction){
		case NORTH:
			dirString = "North";
			
			p1.set(cellj+1f, bottom, celli+1f);
			p2.set(cellj, bottom, celli+1f);
			p3.set(cellj, top, celli+1f);
			p4.set(cellj+1f, top, celli+1f);
			normalVector.set(0f,0f,-1f);
			
			break;
		case SOUTH:
			dirString = "South";
			
			p1.set(cellj, bottom, celli);
			p2.set(cellj+1f, bottom, celli);
			p3.set(cellj+1f, top, celli);
			p4.set(cellj, top, celli);
			normalVector.set(0f,0f,1f);
			
			break;
		case EAST:
			dirString = "East";
		
			p1.set(cellj, bottom, celli+1f);
			p2.set(cellj, bottom, celli);
			p3.set(cellj, top, celli);
			p4.set(cellj, top, celli+1f);
			normalVector.set(1f,0f,0f);
			
			break;
		case WEST:
			dirString = "West";
		
			p1.set(cellj+1f, bottom+1f, celli);
			p2.set(cellj+1f, bottom, celli+1f);
			p3.set(cellj+1f, top, celli+1f);
			p4.set(cellj+1f, top, celli);
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
	
	private void genTriangle(Vector3 p1, Vector3 p2, Vector3 p3){
		modelBuilder.begin();
		meshPartBuilder = modelBuilder.part("triangle" + triCount++, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				Assets.wallMat);

		meshPartBuilder.triangle(p1, p2, p3);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
	}
	
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
	
	public int getHeight(Vector3 position){
		TiledMapTile tile = getTileAt(getTileCoords(position));
		if(tile != null){
			return getHeight(tile);
		}else{
			return -1;
		}
	}
	
	public int getHeight(TiledMapTile tile) {
		String height = "0";
		if (tile.getProperties().containsKey("height")){
			height = tile.getProperties().get("height").toString();
		}
		return Integer.parseInt(height);
	}
	
	public TiledMapTile getTileAt(GridPoint2 position){
		if(position.x >= 0 && position.x < tiledMapLayer0.getWidth() && position.y >= 0 && position.y < tiledMapLayer0.getHeight()){
			return tiledMapLayer0.getCell(position.x, position.y).getTile();
		}
		else{
			return null;
		}
	}
	
	public int getObjectHeight(RectangleMapObject object) {
		String height = "0";
		
		if (object.getProperties().containsKey("height")) {
			height = object.getProperties().get("height").toString();
		}
		
		return Integer.parseInt(height);
	}
	
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
			System.err.println("Tile color not set. Default color used");
			color.set(0, 0, 0, 1);
		}
		
		return color;
	}
	
	private String getRampDirection(TiledMapTile tile) {
		String direction = tile.getProperties().get("ramp").toString();
		return direction;
	}
	
	public int getLevelHeight() {
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		return layer.getHeight();
	}
	
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
	public Vector3 checkCollision(Vector3 oldPos, Vector3 newPos, float playerWidth, float playerHeight, float playerLength){
		Vector3 collisionVector = new Vector3(1,1,1);
		Vector3 movementVector = new Vector3(newPos.x - oldPos.x, newPos.y - oldPos.y, newPos.z - oldPos.z);
		//System.out.println("X: " + movementVector.x + " Y: " + movementVector.y + " Z: " + movementVector.z);
		if(movementVector.len() > 0){
			Vector3 blockSize = new Vector3(SPOT_WIDTH, 0, SPOT_LENGTH);
			Vector3 playerSize = new Vector3(playerWidth, playerHeight, playerLength);
			
			// returns tiled coords (as opposed to world coords)
			GridPoint2 tileCoords = getTileCoords(oldPos);
			
			int startX, endX, startY, endY;
			
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
			
//			if (movementVector.x > 0) {
//				startY = tileCoords.y;
//				endY = tileCoords.y + 3;
//			} else if (movementVector.x < 0) {
//				startY = tileCoords.y - 3;
//				endY = tileCoords.y;
//			} else {
//				startY = tileCoords.y;
//				endY = tileCoords.y;
//			}
//			if (movementVector.z > 0) {
//				startX = tileCoords.x;
//				endX = tileCoords.x + 3;
//			} else if(movementVector.z < 0) {
//				startX = tileCoords.x - 3;
//				endX = tileCoords.x;
//			} else {
//				startX = tileCoords.x;
//				endX = tileCoords.x;
//			}
			
			// iterate through certain tiles in front of the player, depending on direction
			for (int i = startX; i <= endX; i++) {
				
				// don't check tiles outside the map which don't exist
				if (i < 0 || i >= tiledMapLayer0.getWidth()) { continue; }
				
				for (int j = startY; j <= endY; j++) {
					
					// don't check tiles outside the map which don't exist
					if (j<0 || j >= tiledMapLayer0.getHeight()) { continue; }
					
					// TODO: Fix known bug: Player can access a ramp from the side, but should not be able to.
					if (tiledMapLayer0.getCell(tileCoords.x, tileCoords.y) == null)
						return collisionVector.set(0, 0, 0);
					// if oldPos tile is a ramp, it can lead us up one space
					if (tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile().getProperties().containsKey("ramp")) {
						if (getHeight(tiledMapLayer0.getCell(i, j).getTile()) > getHeight(tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile()) + 1) {
							//check collision
							//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
							Vector3 tilePos = new Vector3(j * blockSize.x, getHeight(tiledMapLayer0.getCell(i, j).getTile()), i * blockSize.z);
							blockSize.y = getHeight(tiledMapLayer0.getCell(i, j).getTile());
							Vector3 rectCollideVec = rectCollide(oldPos, newPos, playerSize, tilePos, blockSize);

							collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
						}
					}
					else if (getHeight(tiledMapLayer0.getCell(i, j).getTile()) > getHeight(tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile())) {
						//check collision
						//Note: Switched i,j        here                             and                                 here (translating tiled coords to world coords)
						Vector3 tilePos = new Vector3(j * blockSize.x, getHeight(tiledMapLayer0.getCell(i, j).getTile()), i * blockSize.z);
						blockSize.y = getHeight(tiledMapLayer0.getCell(i, j).getTile());
						Vector3 rectCollideVec = rectCollide(oldPos, newPos, playerSize, tilePos, blockSize);

						collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y, collisionVector.z * rectCollideVec.z);
					}
				}
			}
		}
		
		if(newPos.x - World.PLAYER_SIZE < 0 || newPos.x + World.PLAYER_SIZE > tiledMapLayer0.getHeight()) {
			collisionVector.x = 0;
		}
		if(newPos.z - World.PLAYER_SIZE < 0 || newPos.z + World.PLAYER_SIZE > tiledMapLayer0.getWidth()) {
			collisionVector.z = 0;
		}
		
		return new Vector3(collisionVector.x, collisionVector.y, collisionVector.z);
	}
	
	public Vector3 rectCollide(Vector3 oldPos, Vector3 newPos, Vector3 size1, Vector3 pos2, Vector3 size2) {
		Vector3 result = new Vector3(0,0,0);
	
		if (     newPos.x + size1.x < pos2.x ||
                 newPos.x - size1.x > pos2.x + size2.x * size2.x ||
                 oldPos.y - size1.y > pos2.y ||
                 oldPos.z + size1.z < pos2.z ||
                 oldPos.z - size1.z > pos2.z + size2.z * size2.z) {
			result.x = 1;
        }
		
		if (     oldPos.x + size1.x < pos2.x ||
				 oldPos.x - size1.x > pos2.x + size2.x * size2.x ||
                 newPos.y - size1.y > pos2.y ||
                 oldPos.z + size1.z < pos2.z ||
                 oldPos.z - size1.z > pos2.z + size2.z * size2.z) {
			result.y = 1;
        } 

        if (     oldPos.x + size1.x < pos2.x ||
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
	
	public Vector2 getTileCenter(float x, float z){
		Vector2 returnVec = new Vector2(0,0);
		GridPoint2 tileCoords = getTileCoords(x, z);
		returnVec.set(tileCoords.y + 0.5f, tileCoords.x + 0.5f);
		
		return returnVec;
	}
	
	private GridPoint2 getTileCoords(Vector3 position) {
		return getTileCoords(position.x, position.z);
	}
	
	private GridPoint2 getTileCoords(float x, float z) {
		int tileX = (int)z;
		int tileY = (int)x;
//		if(tileX < 0 || tileY < 0 || tileX > tiledMapLayer0.getHeight() || tileY > tiledMapLayer0.getWidth()) {
//			return new GridPoint2(-1, -1);	// special invalid point
//		}
		return new GridPoint2(tileX, tileY);
	}
}