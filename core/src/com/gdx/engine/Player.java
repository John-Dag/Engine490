package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class Player extends Entity {
	public final float ROTATION_SPEED = 0.2f;
	public final float MOVEMENT_SPEED = 5.0f;
	public PerspectiveCamera camera;
	public boolean mouseLocked, mouseLeft;
	public Vector3 temp;
	public Ray ray;
	private World world;
	private Vector2 collisionVec;
	
	public Player(World world, Vector3 position, boolean active, ModelInstance model) {
		super(position, true, 1, model);
		this.world = world;
		this.camera = new PerspectiveCamera();
		this.collisionVec = new Vector2(1,1);
		this.mouseLocked = false;
		this.temp = new Vector3();
		this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.position.set(this.position.x, this.position.y, this.position.z);
		this.camera.lookAt(0, 1.5f, 5);
		this.camera.near = 0.1f;
		this.camera.far = 100f;
	}
	
	public void update(float delta) {
		this.camera.update();
		this.model.transform.translate(this.camera.position.x, this.camera.position.y, this.position.z);
	}
	
	public void input(float delta) {
		//Lock the cursor with rmb
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			Gdx.input.setCursorCatched(true);
			mouseLocked = true;
		}
		//ESC cancels cursor lock
		else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.input.setCursorCatched(false);
			mouseLocked = false;
		}
		
		else if (Gdx.input.isButtonPressed(Buttons.LEFT) && mouseLocked) {
			mouseLeft = true;
		}
		
		else {
			mouseLeft = false;
		}
		
		if (mouseLocked) {
			Vector2 deltaPos = new Vector2(Gdx.input.getX() - GameScreen.center.x, GameScreen.center.y);
			
			boolean rotX = deltaPos.x != 0;
			boolean rotY = deltaPos.y != 0;
			
			//Testing purposes
			//System.out.println("Pointer Position: " + (int)deltaPos.x);
			
			if (rotX || rotY) {
				Gdx.input.setCursorPosition((int)GameScreen.center.x - 8, (int)GameScreen.center.y - 8);
				camera.direction.rotate(camera.up, -Gdx.input.getDeltaX() * ROTATION_SPEED);
				temp.set(camera.direction).crs(camera.up).nor();
				camera.direction.rotate(temp, -Gdx.input.getDeltaY() * ROTATION_SPEED);
			}
		}
		
		//Keyboard input
		if (Gdx.input.isKeyPressed(Keys.D)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(delta * MOVEMENT_SPEED);
			collisionVec = checkCollisionXZ(temp.x, temp.z);
			this.camera.position.add(temp.x * collisionVec.x, 0, temp.z * collisionVec.y);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(-delta * MOVEMENT_SPEED);
			collisionVec = checkCollisionXZ(temp.x, temp.z);
			this.camera.position.add(temp.x * collisionVec.x, 0, temp.z * collisionVec.y);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			temp.set(camera.direction).nor().scl(delta * MOVEMENT_SPEED);
			collisionVec = checkCollisionXZ(temp.x, temp.z);
			this.camera.position.add(temp.x * collisionVec.x, 0, temp.z * collisionVec.y);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			temp.set(camera.direction).nor().scl(-delta * MOVEMENT_SPEED);
			collisionVec = checkCollisionXZ(temp.x, temp.z);
			this.camera.position.add(temp.x * collisionVec.x, 0, temp.z * collisionVec.y);
		}
	}
	
	private Vector2 checkCollisionXZ(float x, float z){
		Vector2 returnVec = new Vector2(1,1);
		Vector3 oldPos = new Vector3(this.camera.position.x, this.camera.position.y, this.camera.position.z);
		Vector3 newPos = new Vector3(oldPos.x + x, oldPos.y, oldPos.z + z);
		GridPoint2 movTileCoords = getTileCoords(newPos.x, newPos.z);
		TiledMapTileLayer layer = (TiledMapTileLayer) world.getMeshLevel().getTiledMap().getLayers().get(0);
		TiledMapTile movTile = layer.getCell(movTileCoords.x, movTileCoords.y).getTile();
		TiledMapTile currTile = layer.getCell(getPlayerTileCoords().x, getPlayerTileCoords().y).getTile();
		
//		if(movTileCoords.x != getPlayerTileCoords().x || movTileCoords.y != getPlayerTileCoords().y){
//			System.out.println("MovTile: (" + movTileCoords.x + "," +
//					+ movTileCoords.y + ") PlayerTile: (" +
//					+ getPlayerTileCoords().x + "," +
//					+ getPlayerTileCoords().y + ")");
//		}
		
		if(world.getMeshLevel().getHeight(movTile) > world.getMeshLevel().getHeight(currTile)){
//			System.out.println("Collision: MovTile: (" + movTileCoords.x + "," +
//					+ movTileCoords.y + ") Height: " + world.getMeshLevel().getHeight(movTile) + " PlayerTile: (" +
//					+ getPlayerTileCoords().x + "," +
//					+ getPlayerTileCoords().y + ") Height: " + world.getMeshLevel().getHeight(currTile));
			if(movTileCoords.y != getPlayerTileCoords().y){
				// zero the x component of the movement vector
				returnVec.x = 0;
				//System.out.println("Zero X");
			}
			if(movTileCoords.x != getPlayerTileCoords().x){
				// zero the z component of the movement vector
				returnVec.y = 0;
				//System.out.println("Zero Y");
			}
		}
		return returnVec;
	}
	
	
	// input world coordinates to get tile coords
	public GridPoint2 getPlayerTileCoords(){
		return getTileCoords(camera.position.x, camera.position.z);
	}
	
	private GridPoint2 getTileCoords(float x, float z){
		int tileX = (int)z;
		//int tileY = world.getMeshLevel().getLevelHeight() - 1 - (int)x;
		int tileY = (int)x;
		return new GridPoint2(tileX, tileY);
	}
}
