package com.gdx.DynamicEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gdx.engine.World;

public class Player extends DynamicEntity {
	private static final float ROTATION_SPEED = 0.2f;
	private static final float MOVEMENT_SPEED = 8.0f;
	private static final float CROUCH_SPEED = 2.0f;
	private static final float PLAYER_SIZE = 0.2f;
	private static final float PLAYER_HEIGHT = 0.5f;
	private static final float CROUCH_HEIGHT = 0.25f;
	private static final float PLAYER_HEIGHT_OFFSET = 0.5f;
	private static final float JUMP_SPEED = 10f;
	private static final float GRAVITY = 30f;
	private static final int MIN_HEALTH = 0;
	private static final int MAX_HEALTH = 100;
	private int health;
	public PerspectiveCamera camera;
	private boolean mouseLocked, mouseLeft, inCollision, isCrouching;
	public Vector3 temp;
	private World world;
	private Vector3 collisionVector, movementVector, newPos, oldPos;
	private boolean isJumping;
	private Vector3 v;
	private float jumpVelocity;
	private float currentHeightOffset;
	private float currentMovementSpeed;
	
	public Player() {
		super();
	}
	
	public Player(World world, int health, Weapon weapon, int id, boolean isActive, boolean isRenderable,
			      Vector3 position, Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, 
			      ModelInstance model) {
		super(id, isActive, isRenderable, position, rotation, scale, velocity,
			  acceleration, model);
		this.world = world;
		this.setCurrentWeapon(weapon);
		this.camera = new PerspectiveCamera();
		this.collisionVector = new Vector3(1, 1, 1);
		this.mouseLocked = false;
		this.temp = new Vector3();
		this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.position.set(position.x, position.y, position.z);
		this.camera.lookAt(position.x + 1, position.y, position.z + 1);
		this.camera.near = 0.01f;
		this.camera.far = 15f;
		this.movementVector = new Vector3(0,0,0);
		this.newPos = new Vector3(0,0,0);
		this.oldPos = new Vector3(0,0,0);
		this.isJumping = false;
		this.inCollision = true;
		this.isCrouching = false;
		this.health = MAX_HEALTH;
		this.currentHeightOffset = PLAYER_HEIGHT_OFFSET;
		this.currentMovementSpeed = MOVEMENT_SPEED;
	}
	
	@Override
	public void update(float delta) {
		input(delta);
		
		float heightValueLvl1 = currentHeightOffset // has to do with crouching, name change may be in order
				+ world.getMeshLevel().mapHeight(this.camera.position.x, this.camera.position.z, 1);
		float heightValueLvl2 = 6
				+ currentHeightOffset // has to do with crouching, name change may be in order
				+ world.getMeshLevel().mapHeight(this.camera.position.x, this.camera.position.z, 2);
		
		if(inCollision){
			if(isJumping) {
				float jumpAmt = jumpVelocity * delta;
				if(this.camera.position.y + jumpAmt > 6){
					if (this.camera.position.y + jumpAmt > heightValueLvl2){
						this.camera.position.y += jumpAmt;
						jumpVelocity -= GRAVITY * delta;
					}
					else{
						isJumping = false;
						jumpVelocity = 0f;
					}
				}
				if(this.camera.position.y + jumpAmt <= 6){
					if (this.camera.position.y + jumpAmt > heightValueLvl1){
						this.camera.position.y += jumpAmt;
						jumpVelocity -= GRAVITY * delta;
					}
					else{
						isJumping = false;
						jumpVelocity = 0f;
					}
				}
			} else {
				if(this.camera.position.y >= 6) {
					this.camera.position.y = heightValueLvl2;
				}
				if(this.camera.position.y < 6 + currentHeightOffset) {
					this.camera.position.y = heightValueLvl1;
				}
			}
		}
		
		float movAmt = currentMovementSpeed * delta;
		movementVector.y = 0;	// jumping is done separately from the movementVector
		movementVector.nor();
		
		oldPos.set(this.camera.position);
		newPos.set(oldPos.x + movementVector.x * movAmt, oldPos.y + movementVector.y * movAmt, oldPos.z + movementVector.z * movAmt);
		
		// This makes it so that the player falls with gravity when running off ledges
		
		int oldPosLevel, newPosLevel;
		if(oldPos.y >= 6 + currentHeightOffset){
			oldPosLevel = 2;
		}else {
			oldPosLevel = 1;
		}
		if(newPos.y >= 6 + currentHeightOffset){
			newPosLevel = 2;
		}else{
			newPosLevel = 1;
		}

		if (world.getMeshLevel().mapHeight(newPos.x, newPos.z, newPosLevel) < 
		    world.getMeshLevel().mapHeight(oldPos.x, oldPos.z, oldPosLevel)){
			isJumping = true;
		}

		// calculate collision vector (x, y, z) where 0 is collision, and 1 is no collision. This vector is then multiplied by the movementVector.
		if(inCollision){
			collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, PLAYER_SIZE, PLAYER_HEIGHT, PLAYER_SIZE);
			movementVector.set(movementVector.x * collisionVector.x,
					movementVector.y * collisionVector.y,
					movementVector.z * collisionVector.z);
		}

		this.camera.position.mulAdd(movementVector, movAmt);
		
		//world.getMeshLevel().updateHeightOffset(this.camera.position.y - currentHeightOffset);
		
		this.camera.update();
		this.getModel().transform.translate(this.camera.position.x, this.camera.position.y, this.camera.position.z);
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
				// rotate xz plane
				camera.direction.rotate(camera.up, -Gdx.input.getDeltaX() * ROTATION_SPEED);
				
				// calculates up and down rotation vector
				temp.set(camera.direction).crs(camera.up).nor();
				
				Vector3 v = camera.direction;
				float pitch = (float) ((Math.atan2( Math.sqrt(v.x*v.x + v.z*v.z),v.y) * MathUtils.radiansToDegrees));
				
				float pr = -Gdx.input.getDeltaY() * 0.1f; // pitch rotation
				
				if(pitch-pr > 165) {
					pr = -(165 - pitch);
				}
				else if(pitch-pr < 15) {
					pr = pitch - 15;
				}

				// rotates up and down
				camera.direction.rotate(temp, pr);
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
		if (Gdx.input.isKeyPressed(Keys.SPACE)){
			if(!isJumping){
				isJumping = true;
				jumpVelocity = JUMP_SPEED;
			}
		}
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
			isCrouching = true;
			currentHeightOffset = CROUCH_HEIGHT;
			currentMovementSpeed = CROUCH_SPEED;
		}
		if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			isCrouching = false;
			currentHeightOffset = PLAYER_HEIGHT_OFFSET;
			currentMovementSpeed = MOVEMENT_SPEED;
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
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public boolean isMouseLocked() {
		return mouseLocked;
	}

	public boolean isMouseLeft() {
		return mouseLeft;
	}

	public boolean isInCollision() {
		return inCollision;
	}

	public boolean isCrouching() {
		return isCrouching;
	}

	public boolean isJumping() {
		return isJumping;
	}

	public float getJumpVelocity() {
		return jumpVelocity;
	}

	public void setMouseLocked(boolean mouseLocked) {
		this.mouseLocked = mouseLocked;
	}

	public void setMouseLeft(boolean mouseLeft) {
		this.mouseLeft = mouseLeft;
	}

	public void setInCollision(boolean inCollision) {
		this.inCollision = inCollision;
	}

	public void setCrouching(boolean isCrouching) {
		this.isCrouching = isCrouching;
	}

	public void setJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public void setJumpVelocity(float jumpVelocity) {
		this.jumpVelocity = jumpVelocity;
	}
}