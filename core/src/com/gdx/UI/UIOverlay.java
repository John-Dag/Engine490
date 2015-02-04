package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class UIOverlay extends UIBase {
	private BitmapFont font;
	
	public UIOverlay(Stage stage, BitmapFont font) {
		super(stage);
		this.font = font;
	}
	
	/***
	 * Adds a screen centered crosshair
	 * @param crosshairTexture Texture to be displayed
	 * @param center Center of the screen coords
	 */
	
	public void addCrosshair(Texture crosshairTexture, Vector2 center) {
		try {
			Image crosshair = new Image(crosshairTexture);
			crosshair.setPosition(center.x - crosshair.getCenterX(), center.y - crosshair.getCenterY());
			crosshair.setTouchable(Touchable.disabled);
			this.getStage().addActor(crosshair);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	/***
	 * Changes the image of the cursor
	 * @param file Filename of the image
	 * @param xCursorHotspot The active portion of the cursor (x-coord)
	 * @param yCursorHotspot The active portion of the cursor (y-coord)
	 */
	
	public void setCursorImage(String filename, int xCursorHotspot, int yCursorHotspot) {
		try {
			Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
			Gdx.input.setCursorImage(pixmap, xCursorHotspot, yCursorHotspot);
			pixmap.dispose();
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}
