package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.gdx.UI.UIGrid;

public class TestInventory {
	private Array<Image> images = new Array<Image>();
	private Inventory inventory;
	
	public TestInventory() {
	inventory = new Inventory();
	inventory.store(ItemList.healthPotion, 2);
	inventory.store(ItemList.manaPotion, 1);
	System.out.println(inventory.getSlots());
	images = inventory.getImages();
	System.out.println(images.size);
	}
	
	public Array<Image> getImages() {
		return images;
	}
}
