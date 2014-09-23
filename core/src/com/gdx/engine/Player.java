package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class Player extends Entity {
	public final float ROTATION_SPEED = 0.2f;
	public final float MOVEMENT_SPEED = 5.0f;
	public PerspectiveCamera camera;
	public boolean mouseLocked, mouseLeft;
	public Vector2 center;
	public Vector3 temp;
	public Ray ray;
	
	public Player(Vector3 position, boolean active, ModelInstance model) {
		super(position, true, 1, model);
		this.camera = new PerspectiveCamera();
		this.center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
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
			Vector2 deltaPos = new Vector2(Gdx.input.getX() - center.x, Gdx.input.getY() - center.y);
			
			boolean rotX = deltaPos.x != 0;
			boolean rotY = deltaPos.y != 0;
			
			//Testing purposes
			//System.out.println("Pointer Position: " + (int)deltaPos.x);
			
			if (rotX || rotY) {
				Gdx.input.setCursorPosition((int)center.x, (int)center.y);
				camera.direction.rotate(camera.up, -Gdx.input.getDeltaX() * ROTATION_SPEED);
				temp.set(camera.direction).crs(camera.up).nor();
				camera.direction.rotate(temp, -Gdx.input.getDeltaY() * ROTATION_SPEED);
			}
		}
		
		//Keyboard input
		if (Gdx.input.isKeyPressed(Keys.D)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(delta * MOVEMENT_SPEED);
			this.camera.position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			temp.set(camera.direction).crs(camera.up).nor().scl(-delta * MOVEMENT_SPEED);
			this.camera.position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			temp.set(camera.direction).nor().scl(delta * MOVEMENT_SPEED);
			this.camera.position.add(temp.x, 0, temp.z);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			temp.set(camera.direction).nor().scl(-delta * MOVEMENT_SPEED);
			this.camera.position.add(temp.x, 0, temp.z);
		}
	}
}
