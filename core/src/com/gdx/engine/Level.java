package com.gdx.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Level {
	private static final float ROOT_PT5 = 0.70710678f;
	private static final int NORTH = 0;
	private static final int SOUTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	
	private TiledMap tiledMap;
	private ModelBatch modelBatch;
	private ModelBuilder modelBuilder = new ModelBuilder();
	private Model box;
	private Array<ModelInstance> instances = new Array<ModelInstance>();
	private Array<Renderable> renderables = new Array<Renderable>();
	private Array<Mesh> meshes = new Array<Mesh>();
	private Model model;
	private ModelInstance instance;
	private int triCount = 0;
	
	public Level(Environment environment) {
		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		tiledMap = new TmxMapLoader().load("mymap.tmx");
	}
	
	public Array<Renderable> getRenderables() {
		return renderables;
	}
	
	public Array<ModelInstance> getInstances() {
		return instances;
	}
	
	public void render(PerspectiveCamera camera, Environment environment) {
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}
	
	public Array<ModelInstance> generateLevel() {
		TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0);
		// on each cell
		// TODO: There's a problem here with i's and j's because somehow they got mixed up and directions are all whack. Fix it Cory.
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
						MeshPartBuilder meshPartBuilder;
						Node node = modelBuilder.node();
						node.translation.set(j,height,i);
						meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
								GL20.GL_TRIANGLES, 
								Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
								new Material(TextureAttribute.createDiffuse(new Texture("stonefloor.png"))));

						if (direction.equals("up")){
							meshPartBuilder.rect(0,0,1, 1,1,1, 1,1,0, 0,0,0, -ROOT_PT5,ROOT_PT5,0);
						}else if (direction.equals("down")){
							meshPartBuilder.rect(0,1,1, 1,0,1, 1,0,0, 0,1,0, ROOT_PT5,ROOT_PT5,0);
						}else if(direction.equals("left")){
							meshPartBuilder.rect(0,0,1, 1,0,1, 1,1,0, 0,1,0, 0,ROOT_PT5,-ROOT_PT5);
						}else if (direction.equals("right")){
							meshPartBuilder.rect(0,1,1, 1,1,1, 1,0,0, 0,0,0, 0,ROOT_PT5,ROOT_PT5);
						}else{
							System.err.println("generateLevel(): Direction not recognized");
						}
						model = modelBuilder.end();
						instance = new ModelInstance(model);
						instances.add(instance);
						
						// check for triangles
						if(direction.equals("up") | direction.equals("down")){
							// make any north-facing triangles (look south)
							makeTriangles(i,j,NORTH);

							// make any south-facing triangles (look north)
							makeTriangles(i,j,SOUTH);
						}

						if(direction.equals("left") | direction.equals("right")){
							// make any east-facing triangles
							makeTriangles(i,j,EAST);

							// make any west-facing triangles
							makeTriangles(i,j,WEST);
						}
					}
					// else not a ramp
					else{
						int height = getHeight(tile);
						modelBuilder.begin();
						MeshPartBuilder meshPartBuilder;
						Node node = modelBuilder.node();
						node.translation.set(j,height,i);
						meshPartBuilder = modelBuilder.part("floor" + i + "_" + j, 
								GL20.GL_TRIANGLES, 
								Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
								new Material(TextureAttribute.createDiffuse(new Texture("floor.png"))));

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
		return instances;
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
			
		}else if(tileHeight <= adjacentHeight){
			
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
		MeshPartBuilder meshPartBuilder;
		meshPartBuilder = modelBuilder.part(dirString + "_wall" + celli + "_" + cellj, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				new Material(TextureAttribute.createDiffuse(new Texture("wall.png"))));

		meshPartBuilder.rect(p1, p2, p3, p4, normalVector);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
	}
	
	private void genTriangle(Vector3 p1, Vector3 p2, Vector3 p3){
		modelBuilder.begin();
		MeshPartBuilder meshPartBuilder;
		meshPartBuilder = modelBuilder.part("triangle" + triCount++, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				new Material(TextureAttribute.createDiffuse(new Texture("wall.png"))));

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
		MeshPartBuilder meshPartBuilder;
		meshPartBuilder = modelBuilder.part(dirString + "_wall" + celli + "_" + cellj, 
				GL20.GL_TRIANGLES, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				new Material(TextureAttribute.createDiffuse(new Texture("wall.png"))));

		meshPartBuilder.rect(p1, p2, p3, p4, normalVector);
		model = modelBuilder.end();
		instance = new ModelInstance(model);
		instances.add(instance);
	}
	
	private int getHeight(TiledMapTile tile) {
		String height = tile.getProperties().get("height").toString();
		return Integer.parseInt(height);
	}
	
	private String getRampDirection(TiledMapTile tile) {
		String direction = tile.getProperties().get("ramp").toString();
		return direction;
	}
}
