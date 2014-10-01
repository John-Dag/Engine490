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
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class MeshLevel {
	private static final float ROOT_PT5 = 0.70710678f;
	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	
	private static final float SPOT_WIDTH = 1;
	private static final float SPOT_LENGTH = 1;
	private static final float SPOT_HEIGHT = 1;
	
	private final int RED = 0;
	private final int WHITE = 1;
	private final int GREEN = 2;
	private final int BLUE = 3;
	
	private TiledMap tiledMap;
	private ModelBuilder modelBuilder;
	private Array<ModelInstance> instances;
	private Array<Object> objectInstances;
	private Model model, skySphere;
	private ModelInstance instance;
	private int triCount = 0;
	private MeshPartBuilder meshPartBuilder;
	private boolean isSkySphereActive;
	private int heightDimension = 0;
	private int widthDimension = 0;
	private TiledMapTileLayer tiledMapLayer0;
	
	public MeshLevel(TiledMap tiledMap, boolean isSkySphereActive) {
		modelBuilder = new ModelBuilder();
		instances  = new Array<ModelInstance>();
		objectInstances = new Array<Object>();
		modelBuilder = new ModelBuilder();
		this.tiledMap = tiledMap;
		this.isSkySphereActive = isSkySphereActive;
		tiledMapLayer0 = (TiledMapTileLayer) tiledMap.getLayers().get(0);
	}
	
	public Array<ModelInstance> generateLevel() {
		if (isSkySphereActive) {
			skySphere = modelBuilder.createSphere(100f, 100f, 100f, 20, 20, new Material(ColorAttribute.createDiffuse(Color.BLACK)), Usage.Position | Usage.Normal);
			skySphere.materials.get(0).set(new IntAttribute(IntAttribute.CullFace, 0));
			instance = new ModelInstance(skySphere);
			instance.transform.setToTranslation(0, 0, 0);
			instances.add(instance);
		}
		
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		// on each cell
		for(int i = 0; i < layer.getHeight(); i++){
			for(int j = 0; j < layer.getWidth(); j++){
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

						if (direction.equals("up"))	{
							meshPartBuilder.rect(0,0,1, 1,1,1, 1,1,0, 0,0,0, -ROOT_PT5,ROOT_PT5,0);
						}	
						else if (direction.equals("down")) {
							meshPartBuilder.rect(0,1,1, 1,0,1, 1,0,0, 0,1,0, ROOT_PT5,ROOT_PT5,0);
						}	
						else if(direction.equals("left")) {
							meshPartBuilder.rect(0,0,1, 1,0,1, 1,1,0, 0,1,0, 0,ROOT_PT5,-ROOT_PT5);
						}	
						else if (direction.equals("right"))	{
							meshPartBuilder.rect(0,1,1, 1,1,1, 1,0,0, 0,0,0, 0,ROOT_PT5,ROOT_PT5);
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
		setObjectInstances();
		
		return instances;
	}
	
	//Objects are read from the "objects" layer in the tile map
	private void setObjectInstances() {
		Vector3 objPosition = new Vector3();
		MapObjects objects = tiledMap.getLayers().get("objects").getObjects();
		Color color;
		int direction = 0;
		
		for (int i = 0; i < objects.getCount(); i++) {
			RectangleMapObject rectObj = (RectangleMapObject) objects.get(i);
			
			if (rectObj.getName().contains("Torch")) {
				float scale = 0.003f;
				int height = getObjectHeight(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				if (rectObj.getProperties().containsKey("N"))
					direction = 0;
				else if (rectObj.getProperties().containsKey("S"))
					direction = 1;
				else if (rectObj.getProperties().containsKey("E")) 
					direction = 2;
				else if (rectObj.getProperties().containsKey("W"))
					direction = 3;
				
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, Assets.torch, color, scale, direction, 1, false);
				//System.out.println("X: " + rectObj.getRectangle().getY() / 32 + " Y: " + rectObj.getRectangle().getX() / 32);
				objectInstances.add(object);
			}
			
			else if (rectObj.getName().contains("Light")) {
				int height = getObjectHeight(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				//System.out.println("X: " + rectObj.getRectangle().getY() / 32 + " Y: " + rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, new ColorAttribute(ColorAttribute.AmbientLight).color.set(color), 20f, 2, false);
				objectInstances.add(object);
			}
			
			else if (rectObj.getName().contains("Emitter")) {
				int height = getObjectHeight(rectObj);
				objPosition.set(rectObj.getRectangle().getY() / 32, height, rectObj.getRectangle().getX() / 32);
				color = getLightColor(rectObj);
				Object object = new Object(objPosition, new ColorAttribute(ColorAttribute.AmbientLight).color.set(color), 20f, 3, false);
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
			normalVector.set(1f,0f,0f);
			
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
			normalVector.set(1f,0f,0f);
			
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
	
	public int getHeight(TiledMapTile tile) {
		String height = "0";
		if (tile.getProperties().containsKey("height")){
			height = tile.getProperties().get("height").toString();
		}
		return Integer.parseInt(height);
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
	
	public Vector3 checkCollision(Vector3 oldPos, Vector3 newPos, float objectWidth, float objectLength){
		Vector2 collisionVector = new Vector2(1,1);
		Vector3 movementVector = new Vector3(newPos.x - oldPos.x, newPos.y - oldPos.y, newPos.z - oldPos.z);
		
		if(movementVector.len() > 0){
			Vector2 blockSize = new Vector2(SPOT_WIDTH, SPOT_LENGTH);
			Vector2 objectSize = new Vector2(objectWidth, objectLength);
			
			Vector2 oldPos2 = new Vector2(oldPos.z, oldPos.x);
			Vector2 newPos2 = new Vector2(newPos.z, newPos.x);
			
			GridPoint2 tileCoords = getTileCoords(oldPos);
			
			// iterate through all tiles near the player
			for(int i = tileCoords.x - 1; i <= tileCoords.x + 1; i++){
				if(i < 0 || i >= tiledMapLayer0.getWidth()) {continue;}
				for(int j = tileCoords.y - 1; j <= tileCoords.y + 1; j++){
					if(j<0 || j >= tiledMapLayer0.getHeight()) {continue;}
					// the iterated tile is higher than the oldPos tile
					if(getHeight(tiledMapLayer0.getCell(i, j).getTile()) > getHeight(tiledMapLayer0.getCell(tileCoords.x, tileCoords.y).getTile())){
						//check collision
						Vector2 tilePos = new Vector2(i * blockSize.x, j * blockSize.y);
						Vector2 rectCollideVec = rectCollide(oldPos2, newPos2, objectSize, tilePos, blockSize);

						collisionVector.set(collisionVector.x * rectCollideVec.x, collisionVector.y * rectCollideVec.y);
					}
				}
			}
			
		}
		
		return new Vector3(collisionVector.x, 0, collisionVector.y);
	}
	
	private Vector2 rectCollide(Vector2 oldPos, Vector2 newPos, Vector2 size1, Vector2 pos2, Vector2 size2) {
		Vector2 result = new Vector2(0,0);
		
		if(     newPos.x + size1.x < pos2.x ||
                newPos.x - size1.x > pos2.x + size2.x * size2.x ||
                oldPos.y + size1.y < pos2.y ||
                oldPos.y - size1.y > pos2.y + size2.y * size2.y) {

            result.y = 1;
        }

        if(     oldPos.x + size1.x < pos2.x ||
                oldPos.x - size1.x > pos2.x + size2.x * size2.x ||
                newPos.y + size1.y < pos2.y ||
                newPos.y - size1.y > pos2.y + size2.y * size2.y) {

            result.x = 1;
        }
		
		return result;
	}
	
	private GridPoint2 getTileCoords(Vector3 position){
		int tileX = (int)position.z;
		int tileY = (int)position.x;
		return new GridPoint2(tileX, tileY);
	}
}
