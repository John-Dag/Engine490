package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.engine.Assets;
import com.gdx.engine.Entity;

public class UIVirtualJoystick extends UIBase {
	private Touchpad touchpad;
	private Skin touchpadSkin;
	private TouchpadStyle touchpadStyle;
	private Drawable background;
	private Drawable knob;
	private DynamicEntity entity;
	private Vector3 position;
	private PerspectiveCamera camera;
	private float rotationSpeed;
	
	public UIVirtualJoystick(Stage stage, Texture joystickBackground, Texture joystickKnob, float xPos, float yPos, float width, float height) {
		super(stage);
		touchpadSkin = new Skin();
		try {
			touchpadSkin.add("background", joystickBackground);
			touchpadSkin.add("knob", joystickKnob);
		}
		catch (Exception e) {
			System.err.println(e);
		}
		background = touchpadSkin.getDrawable("background");
		knob = touchpadSkin.getDrawable("knob");
		touchpadStyle = new TouchpadStyle();
		touchpadStyle.background = background;
		touchpadStyle.knob = knob;
		touchpad = new Touchpad(0, touchpadStyle);
		touchpad.setBounds(xPos,  yPos,  width, height);
		position = new Vector3();
		camera = new PerspectiveCamera();
	}

	public void addVirtualJoystick(DynamicEntity entity, PerspectiveCamera camera, float rotationSpeed) {
		try {
			this.getStage().addActor(touchpad);
			this.entity = entity;
			this.camera = camera;
			this.rotationSpeed = rotationSpeed;
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	@Override
	public void render(float delta) {

	}
}
