package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gdx.Inventory.Inventory;
import com.gdx.Inventory.Slot;

public class UIGrid {
	/*private Table table;
	private Window window;
	private Array<Image> images;
	private Array<Slot> slots;
	private Texture slotTexture;
	private Inventory inventory;
	
	public UIGrid(Stage stage, Skin skin, Inventory inventory, String name, Texture slotTexture) {
		super(stage);
		table = new Table();
		table.setFillParent(true);
		window = new Window(name, skin);
		window.addActor(table);
		window.setVisible(false);
		window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.slotTexture = slotTexture;
		this.inventory = inventory;
		initializeGrid(Align.bottom, 30, 30, 5, 5, 3);
		updateGrid(Align.bottom, 30, 30, 5, 5, 3);
	}
	
	public UIGrid(Stage stage, Skin skin, Color color, Inventory inventory, String name, Texture slotTexture) {
		super(stage);
		table = new Table();
		table.setFillParent(true);
		window = new Window(name, skin);
		window.addActor(table);
		window.setColor(color);
		window.setVisible(false);
		window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.slotTexture = slotTexture;
		this.inventory = inventory;
		updateGrid(Align.bottom, 30, 30, 5, 5, 3);
	}
	
	private void updateGrid(int align, float slotWidth, float slotHeight, int numSlotsX, int numSlotsY, int padding) {
		window.setSize(slotWidth * numSlotsX, slotHeight * numSlotsY);
		table.align(align);
		
		//Add click listeners to each slot
		//TODO: Make own method for this
		for (int a = 0; a < numSlotsX * numSlotsY; a++) {
			final int A = a;
			//images.add(new Image(slotTexture));
			images.get(a).addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					itemClicked();
				}
			});
		}
		
		//Add the slot actors to the window in a grid
		for (int i = 0; i < numSlotsX; i++) {
			for (int j = 0; j < numSlotsY; j++) { 
				table.add(images.get((i * numSlotsX) + j)).pad(padding).size(slotWidth, slotHeight);
			}
			table.row();
		}

		//this.getStage().addActor(window);
	}
	
	private void itemClicked() {
		
	}
	
	@Override
	public void show() {
		if (!this.window.isVisible() && uiSelected == false)
			this.window.setVisible(true);
		else
			this.window.setVisible(false);
	}
	
	public Table getTable() {
		return table;
	}

	public Window getWindow() {
		return window;
	}

	public Array<Image> getImages() {
		return images;
	}

	public Texture getSlotTexture() {
		return slotTexture;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public void setImages(Array<Image> images) {
		this.images = images;
	}

	public void setSlotTexture(Texture slotTexture) {
		this.slotTexture = slotTexture;
	}

	public void setWindowSize(int width, int height) {
		this.window.setWidth(width);
		this.window.setHeight(height);
	} */
} 
