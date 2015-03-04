package com.gdx.DynamicEntities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.gdx.Abilities.Blizzard;
import com.gdx.Abilities.PoisonCloud;
import com.gdx.UI.UIBase;
import com.gdx.UI.UIConsole;
import com.gdx.engine.Assets;
import com.gdx.engine.DistanceTrackerMap;
import com.gdx.engine.Entity;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;
import com.gdx.engine.GameScreen.State;

public class Player extends DynamicEntity {
	public static float FOG_DISTANCE = 15f;
	//public static float FOG_DISTANCE = 15f;
	private static final float ROTATION_SPEED = 0.2f;
	private static final float MOVEMENT_SPEED = 8.0f;
	private static final float CROUCH_SPEED = 2.0f;
	private static final float PLAYER_SIZE = 0.2f;
	private static final float PLAYER_HEIGHT = 0.5f;
	private static final float CROUCH_HEIGHT = 0.25f;
	private static final float PLAYER_HEIGHT_OFFSET = 0.5f;
	private static final float JUMP_SPEED = 10f;
	private static final float GRAVITY = 30f;
	public static final int MIN_HEALTH = 0;
	public static final int MAX_HEALTH = 100;
	public PerspectiveCamera camera;
	public Vector3 temp;
	private World world;
	private int health;
	private boolean mouseLocked, mouseLeft, clipping, isCrouching, isJumping, isFiring, isCooldownActive, isPlayerTargeting, 
				    moveForward = false, moveBackward = false, strafeLeft = false, strafeRight = false, jump = false, crouch = false, 
				    ability1 = false, ability2 = false, ESCAPE = false, respawning, isRotating = false;
	private Vector3 collisionVector, newPos, oldPos;
	private float jumpVelocity, currentMovementSpeed, currentHeightOffset, speedScalar, fireDelayTimer;
	private DistanceTrackerMap distanceMap;
	private Array<Ability> abilities;
	private String netName;
	
	public Player() {
		super();
	}
	
	public Player(World world, int health, Weapon weapon, int id, boolean isActive, boolean isRenderable,
			      Vector3 position, Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, 
			      ModelInstance model) {
		super(id, isActive, isRenderable, position, rotation, scale, velocity,
			  acceleration, model);
		this.world = world;
		this.camera = new PerspectiveCamera();
		this.collisionVector = new Vector3(1, 1, 1);
		this.mouseLocked = false;
		this.temp = new Vector3();
		this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.position.set(position.x, position.y, position.z);
		this.camera.lookAt(position.x + 1, position.y, position.z + 1);
		this.camera.near = 0.01f;
		this.camera.far = FOG_DISTANCE;
		this.getMovementVector().set(new Vector3(0, 0, 0));
		this.newPos = new Vector3(0,0,0);
		this.oldPos = new Vector3(0,0,0);
		this.isJumping = false;
		this.clipping = true;
		this.isCrouching = false;
		this.isCooldownActive = false;
		this.isFiring = false;
		this.health = MAX_HEALTH;
		this.currentHeightOffset = PLAYER_HEIGHT_OFFSET;
		this.currentMovementSpeed = MOVEMENT_SPEED;
		this.speedScalar = 1f; // 1 = default movespeed
		this.isPlayerTargeting = false;
		this.abilities = new Array<Ability>();
		//this.setModel(model);
	}
	
	public void initAbilities() {
		abilities.add(new Blizzard(10, false, true, new Vector3(0, 0, 0)));
		abilities.add(new PoisonCloud(11, false, true, new Vector3(0, 0, 0), new Decal().newDecal(Assets.aoeTextureRegion, true)));
	}

	@Override
	public void update(float delta, World world) {
		handleInput(delta);
		fireDelayTimer += delta;
		
	    //TiledMapTileLayer layer = (TiledMapTileLayer)world.getMeshLevel().getTiledMap().getLayers().get(0);//for width
		GridPoint2 playerPosition = new GridPoint2((int)world.getPlayer().camera.position.x, (int)world.getPlayer().camera.position.z);
		if (newPos != oldPos && clipping && GameScreen.mode == State.Offline) {
            distanceMap = world.getDistanceMap();
            distanceMap.resetDistances();
            if (camera.position.y >= 6)
            	distanceMap.addDistances((playerPosition.x + world.getMeshLevel().getMapXDimension() * playerPosition.y) + world.getMeshLevel().getMapXDimension() * world.getMeshLevel().getMapXDimension());
            else
            	distanceMap.addDistances((playerPosition.x + world.getMeshLevel().getMapXDimension() * playerPosition.y));
            // distanceMap.addDistances(( playerPosition.x + world.getMeshLevel().getMapXDimension() * playerPosition.y));
            world.setDistanceMap(distanceMap);
        }
		
		float heightValueLvl1 = currentHeightOffset // has to do with crouching, name change may be in order
				+ world.getMeshLevel().mapHeight(this.camera.position.x, this.camera.position.z, 1);
		float heightValueLvl2 = 6
				+ currentHeightOffset // has to do with crouching, name change may be in order
				+ world.getMeshLevel().mapHeight(this.camera.position.x, this.camera.position.z, 2);
		
		if(clipping){
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
		
		float movAmt = (currentMovementSpeed *speedScalar) * delta;
		if(clipping){
			getMovementVector().y = 0;	// jumping is done separately from the getMovementVector()
		}
		getMovementVector().nor();
		
		oldPos.set(this.camera.position);
		newPos.set(oldPos.x + getMovementVector().x * movAmt, oldPos.y + getMovementVector().y * movAmt, oldPos.z + getMovementVector().z * movAmt);
		
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

		// calculate collision vector (x, y, z) where 0 is collision, and 1 is no collision. This vector is then multiplied by the getMovementVector().
		if (clipping){
			collisionVector = world.getMeshLevel().checkCollision(oldPos, newPos, PLAYER_SIZE, PLAYER_HEIGHT, PLAYER_SIZE);
			getMovementVector().set(getMovementVector().x * collisionVector.x,
					getMovementVector().y * collisionVector.y,
					getMovementVector().z * collisionVector.z);
		}
		
       for(Enemy enemy:World.enemyInstances)
       {
       	
       	if(oldPos.dst(enemy.getPosition()) > 4)
       			continue;

       	if(oldPos.dst(enemy.getPosition()) < 1)
       	{
       		if(enemy.getPosition().dst(newPos) < enemy.getPosition().dst(oldPos))
       		{
       			getMovementVector().set(0,0,0);
       			break;
       		}

       	}
       	if(oldPos.dst(enemy.getPosition()) < 2)
       	{
           	if(isJumping & jumpVelocity < 0)
           		jumpVelocity = 0;
       	
       	}

       }

		this.camera.position.mulAdd(getMovementVector(), movAmt);
		
		//world.getMeshLevel().updateHeightOffset(this.camera.position.y - currentHeightOffset);
		
		this.camera.update();
//		if (this.getModel() != null)
//			this.getModel().transform.translate(this.camera.position.x, this.camera.position.y, this.camera.position.z);
		this.getPosition().set(this.camera.position.x, this.camera.position.y, this.camera.position.z);
		this.updatePosition(delta);
		//this.updateInstanceTransform();
		
		if (this.health <= MIN_HEALTH) {
			if (world.getClient() != null) {
				world.getClient().sendKillUpdate();
			}
			setRespawning(true);
			respawnPlayer(this);
		}
	}
	
	public void handleInput(float delta) {
		//Lock the cursor with right mouse button
		if (Gdx.input.isButtonPressed(Buttons.RIGHT) && !this.isPlayerTargeting) {
			Gdx.input.setCursorCatched(true);
		}
		
		else if (ESCAPE || Gdx.input.isButtonPressed(Buttons.RIGHT) 
				  && this.isPlayerTargeting) {
			catchCursor();
		}
		
		else if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			mouseLeft = true;
		}
		else
			mouseLeft = false;
		
		if (Gdx.app.getType() == ApplicationType.Desktop)
			getMovementVector().set(0,0,0);
		
		//Keyboard input
		if (moveForward)
			moveForward();
		else if (moveBackward)
			moveBackwards();
		if (strafeLeft)
			strafeLeft();
		else if (strafeRight)
			strafeRight();
		if (jump)
			jump();
		if (crouch)
			crouch();
		if (ability1)
			activateAbility1();
		if (ability2)
			activateAbility2();
		
		// camera rotation based on mouse looking
		if (Gdx.input.isCursorCatched()) {
			//ESC cancels cursor lock
			if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
				Gdx.input.setCursorCatched(false);
			}
			
			//Fire players weapon
			if (!this.isPlayerTargeting && this.isMouseLeft() && this.getWeapon() != null && 
				fireDelayTimer >= this.getWeapon().getFiringDelay()) {
				//ray = player.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
				fireDelayTimer = 0;
				fireWeapon();
			}
			
			if (Gdx.input.getDeltaX() != 0)
				setRotating(true);
			else
				setRotating(false);
			
			// rotate xz plane
			camera.direction.rotate(camera.up, -Gdx.input.getDeltaX() * ROTATION_SPEED);
			
			// calculates up and down rotation vector
			// weapon recoil added here
			if (isFiring) {
				camera.direction.y += world.getPlayer().getWeapon().getRecoil();
				temp.set(camera.direction).crs(camera.up).nor();
				isFiring = false;
			}
			else
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
		
		else
			setRotating(false);
	}
	
	public void catchCursor() {
		Gdx.input.setCursorCatched(true);
		world.getPlayer().setPlayerTargeting(false);
		abilities.get(1).setIsActive(false);
	}
	
	public void moveForward() {
		getMovementVector().add(camera.direction);
	}
	
	public void moveBackwards() {
		getMovementVector().sub(camera.direction);
	}
	
	public void strafeRight() {
		temp.set(camera.direction).crs(camera.up);
		getMovementVector().add(temp);
	}
	
	public void strafeLeft() {
		temp.set(camera.up).crs(camera.direction);
		getMovementVector().add(temp);
	}
	
	public void jump() {
		if(!isJumping){
			isJumping = true;
			jumpVelocity = JUMP_SPEED;
		}
	}
	
	public void activateAbility1() {
		if (!abilities.get(0).isCoolingDown() && !this.abilities.get(0).isActive()) {
			abilities.set(0, new Blizzard(10, false, true, new Vector3(0, 0, 0)));
			abilities.get(0).initAbility();
		}
	}
	
	public void activateAbility2() {
		if (!abilities.get(1).isCoolingDown() && !this.abilities.get(1).isActive()) {
			abilities.set(1, new PoisonCloud(11, false, true, new Vector3(0, 0, 0), new Decal().newDecal(Assets.aoeTextureRegion, true)));
			abilities.get(1).initTargeting();
		}
	}
	
	public void crouch() {
		isCrouching = true;
		currentHeightOffset = CROUCH_HEIGHT;
		currentMovementSpeed = CROUCH_SPEED * speedScalar;
	}
	
	public void stopCrouching() {
		isCrouching = false;
		currentHeightOffset = PLAYER_HEIGHT_OFFSET;
		currentMovementSpeed = MOVEMENT_SPEED;
	}
	
	public void unstickPlayer() {
		Vector2 tileCenter = world.getMeshLevel().getTileCenter(camera.position.x, camera.position.z);
		Vector2 camPosition = new Vector2(camera.position.x, camera.position.z);
		Vector2 movVect = new Vector2(0,0);
		movVect = tileCenter.sub(camPosition);
		camera.position.add(movVect.x * Gdx.graphics.getDeltaTime(), 0, movVect.y * Gdx.graphics.getDeltaTime());
	}

	public void fireWeapon() {
		this.isFiring = true;
		this.getWeapon().fireWeapon(world);
	}
	
	public void takeDamage(int damage) {
		this.health -= damage;
		
		world.setFilterEffect(new com.gdx.FilterEffects.RedFade());
	}
	
	public void respawnPlayer(Player player) {
		player.camera.position.set(new Vector3(2f, 1.5f, 2f));
		player.setWeapon(new Weapon());
		player.setHealth(MAX_HEALTH);
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
	
	public void exit() {
		Gdx.app.exit();
	}

	public boolean isPlayerTargeting() {
		return isPlayerTargeting;
	}

	public void setPlayerTargeting(boolean isPlayerTargeting) {
		this.isPlayerTargeting = isPlayerTargeting;
	}
	
	public float getCurrentHeightOffset() {
		return currentHeightOffset;
	}

	public void setCurrentHeightOffset(float currentHeightOffset) {
		this.currentHeightOffset = currentHeightOffset;
	}

	@Override
	public BoundingBox getTransformedBoundingBox() {
		return this.getBoundingBox().set(new Vector3(this.getPosition().x - 0.5f, this.getPosition().y - 0f, this.getPosition().z - 0.5f),
			    						 new Vector3(this.getPosition().x + 0.5f, this.getPosition().y + 1f, this.getPosition().z + 0.5f));
	}

	public boolean isCooldownActive() {
		return isCooldownActive;
	}

	public boolean isMoveForward() {
		return moveForward;
	}

	public boolean isMoveBackward() {
		return moveBackward;
	}

	public boolean isStrafeLeft() {
		return strafeLeft;
	}

	public boolean isStrafeRight() {
		return strafeRight;
	}

	public void setMoveForward(boolean moveForward) {
		this.moveForward = moveForward;
	}

	public void setMoveBackward(boolean moveBackward) {
		this.moveBackward = moveBackward;
	}

	public void setStrafeLeft(boolean strafeLeft) {
		this.strafeLeft = strafeLeft;
	}

	public void setStrafeRight(boolean strafeRight) {
		this.strafeRight = strafeRight;
	}

	public void setCooldownActive(boolean isCooldownActive) {
		this.isCooldownActive = isCooldownActive;
	}
	
	public float getCurrentMovementSpeed() {
		return currentMovementSpeed;
	}

	public void setCurrentMovementSpeed(float currentMovementSpeed) {
		this.currentMovementSpeed = currentMovementSpeed;
	}

	public boolean isClipping() {
		return clipping;
	}

	public void setClipping(boolean clipping) {
		this.clipping = clipping;
	}

	public float getSpeedScalar() {
		return speedScalar;
	}

	public void setSpeedScalar(float speedScalar) {
		this.speedScalar = speedScalar;
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

	public boolean isJump() {
		return jump;
	}

	public boolean isCrouch() {
		return crouch;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void setCrouch(boolean crouch) {
		this.crouch = crouch;
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

	public void setclipping(boolean clipping) {
		this.clipping = clipping;
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

	public boolean isRespawning() {
		return respawning;
	}

	public void setRespawning(boolean respawning) {
		this.respawning = respawning;
	}

	public String getNetName() {
		return netName;
	}

	public void setNetName(String netName) {
		this.netName = netName;
	}

	public boolean isRotating() {
		return isRotating;
	}

	public void setRotating(boolean isRotating) {
		this.isRotating = isRotating;
	}
}