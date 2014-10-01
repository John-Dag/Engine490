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
	public final float PLAYER_SIZE = 0.2f;
	public final float TILE_HALF_WIDTH = 0.5f;
	public PerspectiveCamera camera;
	public boolean mouseLocked, mouseLeft;
	public Vector3 temp;
	public Ray ray;
	private World world;
	private Vector3 collisionVector;
	private Vector3 movementVector;
	private Vector3 newPos;
	private Vector3 oldPos;
	
	public Player(World world, Vector3 position, boolean active, ModelInstance model) {
		super(position, true, 1, model);
		this.world = world;
		this.camera = new PerspectiveCamera();
		this.collisionVector = new Vector3(1,1,1);
		this.mouseLocked = false;
		this.temp = new Vector3();
		this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.position.set(this.position.x, this.position.y, this.position.z);
		this.camera.lookAt(5, 1.5f, 5);
		this.camera.near = 0.001f;
		this.camera.far = 100f;
		this.model = model;
		this.movementVector = new Vector3(0,0,0);
		this.newPos = new Vector3(0,0,0);
		this.oldPos = new Vector3(0,0,0);
	}
	
	public void update(float delta) {
		float movAmt = (float)(MOVEMENT_SPEED * delta);
		movementVector.y = 0;
		
		movementVector.nor();
		
		oldPos.set(this.camera.position);
		newPos.set(oldPos.x + movementVector.x * movAmt, oldPos.y + movementVector.y * movAmt, oldPos.z + movementVector.z * movAmt);
		
		collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, PLAYER_SIZE, PLAYER_SIZE);
		
		movementVector.set(movementVector.x * collisionVector.x,
							movementVector.y * collisionVector.y,
							movementVector.z * collisionVector.z);
		
		this.camera.position.mulAdd(movementVector, movAmt);
		
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
		
		movementVector.set(0,0,0);
		
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
		if (Gdx.input.isKeyPressed(Keys.W)) {
			movementVector.add(camera.direction);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			movementVector.sub(camera.direction);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			temp.set(camera.direction).crs(camera.up);
			movementVector.add(temp);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			temp.set(camera.up).crs(camera.direction);
			movementVector.add(temp);
		}
	}
	
	// input world coordinates to get tile coords
	public GridPoint2 getPlayerTileCoords(){
		return getTileCoords(camera.position.x, camera.position.z);
	}
	
	// This take in x and z from world coordinates and returns the tile position (tile index)
	// Note: The coords are flipped because of Tiled Map Editor and LibGDX being slightly inconsistent there.
	private GridPoint2 getTileCoords(float x, float z){
		int tileX = (int)z;
		int tileY = (int)x;
		return new GridPoint2(tileX, tileY);
	}
}
