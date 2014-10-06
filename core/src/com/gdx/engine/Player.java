package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class Player extends Entity {
	private static final float ROTATION_SPEED = 0.2f;
	private static final float MOVEMENT_SPEED = 5.0f;
	private static final float PLAYER_HEIGHT = 0.5f;
	private static final float HEIGHT_OFFSET = 0.5f;
	private static final float JUMP_SPEED = 10f;
	private static final float GRAVITY = 30f;
	public PerspectiveCamera camera;
	public boolean mouseLocked, mouseLeft;
	public Vector3 temp;
	public Ray ray;
	private World world;
	private Vector3 collisionVector;
	private Vector3 movementVector;
	private Vector3 newPos;
	private Vector3 oldPos;
	private boolean isJumping;
	private float jumpVelocity;
	private Weapon weapon;
	
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
		this.camera.near = 0.1f;
		this.camera.far = 30f;
		this.model = model;
		this.movementVector = new Vector3(0,0,0);
		this.newPos = new Vector3(0,0,0);
		this.oldPos = new Vector3(0,0,0);
		this.isJumping = false;
		this.weapon = new Weapon(Assets.weapon1Region, 0.5f, 5, true, true);
	}
	
	public void update(float delta) {
		// gets the height of the map at the players x,z location
		float heightValue = HEIGHT_OFFSET + world.getMeshLevel().mapHeight(this.camera.position.x, this.camera.position.z);
		
		// Jumping code
		if (isJumping) {
			float jumpAmt = jumpVelocity * delta;
			if (this.camera.position.y + jumpAmt > heightValue) {
				this.camera.position.y += jumpAmt;
				jumpVelocity -= GRAVITY * delta;
			}
			else {
				this.camera.position.y = heightValue;
				isJumping = false;
				jumpVelocity = 0f;
			}

		} else {
			// update height from ramps
			this.camera.position.y = heightValue;
		}
		
		float movAmt = MOVEMENT_SPEED * delta;
		movementVector.y = 0;	// jumping is done separately from the movementVector
		movementVector.nor();
		
		oldPos.set(this.camera.position);
		newPos.set(oldPos.x + movementVector.x * movAmt, oldPos.y + movementVector.y * movAmt, oldPos.z + movementVector.z * movAmt);
		
		// This makes it so that the player falls with gravity when running off ledges
		if(world.getMeshLevel().getHeight(newPos) < world.getMeshLevel().getHeight(oldPos) && 
				world.getMeshLevel().getHeight(newPos) != -1){
			isJumping = true;
		}

		// calculate collision vector (x, y, z) where 0 is collision, and 1 is no collision. This vector is then multiplied by the movementVector.
		collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, world.PLAYER_SIZE, PLAYER_HEIGHT, world.PLAYER_SIZE);
		movementVector.set(movementVector.x * collisionVector.x,
							movementVector.y * collisionVector.y,
							movementVector.z * collisionVector.z);

		this.camera.position.mulAdd(movementVector, movAmt);
		this.camera.update();
		this.model.transform.translate(this.camera.position.x, this.camera.position.y, this.position.z);
	}
	
	public void input(float delta) {
		//Lock the cursor with right mouse button
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
		}else{
			mouseLeft = false;
		}
		
		movementVector.set(0,0,0);
		
		// camera rotation based on mouse looking
		if (mouseLocked) {
			Vector2 deltaPos = new Vector2(Gdx.input.getX() - GameScreen.center.x, GameScreen.center.y);
			
			boolean rotX = deltaPos.x != 0;
			boolean rotY = deltaPos.y != 0;
			
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
		if (Gdx.input.isKeyPressed(Keys.NUM_1)) {
			weapon.isParticleWeapon = false;
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_2)) {
			weapon.isParticleWeapon = true;
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)){
			if(!isJumping){
				isJumping = true;
				jumpVelocity = JUMP_SPEED;
			}
		}
		// This is temporary, but useful for testing. Press 'O' if you ever get stuck.
		if (Gdx.input.isKeyPressed(Keys.O)){
			Vector2 tileCenter = world.getMeshLevel().getTileCenter(camera.position.x, camera.position.z);
			Vector2 camPosition = new Vector2(camera.position.x, camera.position.z);
			Vector2 movVect = new Vector2(0,0);
			movVect = tileCenter.sub(camPosition);
			camera.position.add(movVect.x * delta, 0, movVect.y * delta);
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
	
	public Weapon getWeapon() {
		return weapon;
	}
}