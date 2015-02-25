package com.gdx.UI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class UIForm extends UIBase {
	private Window window;
	private Skin skin;
	
	public UIForm(Stage stage, Skin skin, String name) {
		super(stage);
		window = new Window(name, skin);
		this.skin = skin;
	}

	public void generateWindow(float x, float y, int width, int height) {
		window.setPosition(x, y);
		window.setSize(width, height);
		this.getStage().addActor(window);
	}
	
	/***
	 * Adds a text field to the form window
	 * @param title
	 * @param x position x
	 * @param y position y
	 * @param width width of window
	 * @param height height of window
	 */
	
	public void addTextField(String title, float x, float y, int width, int height) {
		try {
			TextField field = new TextField(title, skin);
			field.setPosition(x, y);
			field.setSize(width, height);
			window.addActor(field);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}
