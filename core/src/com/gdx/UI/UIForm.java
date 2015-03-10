package com.gdx.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.gdx.Network.NetStatField;

public class UIForm extends UIBase {
	private Skin skin;
	private Array<TextField> fields;

	public UIForm(Stage stage, Skin skin, String name) {
		super(stage);
		this.setWindow(new Window(name, skin));
		fields = new Array<TextField>();
		this.skin = skin;
	}

	public void generateWindow(float x, float y, int width, int height) {
		getWindow().setPosition(x, y);
		getWindow().setSize(width, height);
		getWindow().setMovable(false);
		this.getStage().addActor(getWindow());
	}
	
	/***
	 * Adds a text field to the form window
	 * @param title
	 * @param x position x
	 * @param y position y
	 * @param width width of getWindow()
	 * @param height height of getWindow()
	 */
	
	public void addTextField(String title, float x, float y, int width, int height) {
		try {
			TextField field = new TextField(title, skin);
			field.setPosition(x, y);
			field.setSize(width, height);
			fields.add(field);
			getWindow().addActor(field);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void addNetStatField(NetStatField field, float posX, float posY, int width, int height) {
		field.setPosition(posX, posY);
		field.setWidth(width);
		field.setHeight(height);
		fields.add(field);
		getWindow().addActor(field);
	}
	
	public Array<TextField> getFields() {
		return fields;
	}

	public void setFields(Array<TextField> fields) {
		this.fields = fields;
	}
}
