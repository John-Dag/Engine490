package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Array;
import com.gdx.engine.Render;

public class UIOverlay extends UIBase {
	private Array<ProgressBar> bars;
	private BitmapFont bitmapFont;
	private SpriteBatch batch;
	
	public UIOverlay(Stage stage, SpriteBatch batch, BitmapFont bitmapFont) {
		super(stage);
		this.bars = new Array<ProgressBar>();
		this.bitmapFont = bitmapFont;
		this.batch = batch;
	}
	
	/***
	 * Adds a screen centered crosshair
	 * @param crosshairTexture Texture to be displayed
	 * @param center Center of the screen coords
	 */
	
	public void addCrosshair(Texture crosshairTexture, Vector2 center) {
		try {
			Image crosshair = new Image(crosshairTexture);
			crosshair.setPosition(center.x - (crosshair.getWidth() / 2), center.y - (crosshair.getHeight() / 2));
			crosshair.setTouchable(Touchable.disabled);
			this.getStage().addActor(crosshair);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	/***
	 * Changes the cursors texture
	 * @param file Filename of the image in assets
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
	
	/***
	 * Adds a progress bar with optional font display
	 * @param bar Progress bar to be displayed
	 * @param knobBefore Fills the bar with its texture
	 * @param value The value to update the bars progress
	 */
	
	public void addProgressBarWidget(ProgressBar bar, boolean knobBefore, int value) {
		try {
			if (knobBefore)
				bar.getStyle().knobBefore = bar.getStyle().knob;
		}
		catch(Exception e) {
			System.err.println(e);
		}
		
		bar.setValue(value);
		this.getStage().addActor(bar);
		bars.add(bar);
	}
	
	public void updateWidgets(float delta, int value1) {
		for (ProgressBar bar : bars)
			bar.setValue(value1);
	}
	
	public void renderFPS(float delta, float posX, float posY) {
		bitmapFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), posX, posY);
	}
	
	public void renderPosition(Vector3 position, float delta, float posX, float posY) {
		bitmapFont.draw(batch,  "Position: " + position.x + ", " + position.y + ", " + position.z, posX, posY);
	}
	
	public void renderTilePosition(GridPoint2 tileIndex, float delta, float posX, float posY) {
		bitmapFont.draw(batch,  "Tile (" + tileIndex.x + ", " + tileIndex.y +")", posX, posY);
	}
	
	public void renderModelCount(float posX, float posY) {
		bitmapFont.draw(batch, "Rendered Models: " + Render.renderCount, posX, posY);
	}
}
