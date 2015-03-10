package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gdx.Inventory.Inventory;
import com.gdx.Inventory.Slot;
import com.gdx.Inventory.TestInventory;

public class UIGrid extends UIBase {
	private Table table;
	private Window window;
	private Array<Image> images;
	private Array<Slot> slots;
	private Texture slotTexture;
	private TestInventory testInv = new TestInventory();
	
	public UIGrid(Stage stage, Skin skin, String name, Texture slotTexture) {
		super(stage);
		table = new Table();
		table.setFillParent(true);
		window = new Window(name, skin);
		window.addActor(table);
		window.setVisible(false);
		window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.slotTexture = slotTexture;
		images = testInv.getImages();
		slots = testInv.getSlots();
	}
	
	public UIGrid(Stage stage, Skin skin, Color color, String name, Texture slotTexture) {
		super(stage);
		table = new Table();
		table.setFillParent(true);
		window = new Window(name, skin);
		window.addActor(table);
		window.setColor(color);
		window.setVisible(false);
		window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.slotTexture = slotTexture;
		images = testInv.getImages();
		slots = testInv.getSlots();
	}
	
	/***
	 * Generates a grid in a scene2d window based on specified number of slots
	 * and slot size.
	 * @param align Alignment of the grid within a window
	 * @param slotWidth Width of each slot
	 * @param slotHeight Height of each slot
	 * @param numSlotsX Number of slots to fill each row
	 * @param numSlotsY Number of slots to fill each column
	 * @param padding Amount of padding between each slot
	 */
	
	public void generateGrid(int align, float slotWidth, float slotHeight, int numSlotsX, int numSlotsY, int padding) {
		window.setSize(slotWidth * numSlotsX, slotHeight * numSlotsY);
		table.align(align);
		
		//Add click listeners to each slot
		for (int a = 0; a < numSlotsX * numSlotsY; a++) {
			final int A = a;
			images.add(new Image(slotTexture));
			images.get(a).addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(slots.get(A).getItem());
					slots.get(A).getItem().effect();
					//slots.get(A).remove(1);
					//System.out.println(slots);
					//testInv.getInventory().updateImage(slots.get(A));
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

		this.getStage().addActor(window);
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
	}
}
