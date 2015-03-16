package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.gdx.UI.UIGrid;
import com.gdx.engine.Assets;

public class TestInventory {
	private Array<Image> images = new Array<Image>();
	private Array<Slot> slots = new Array<Slot>();
	private Inventory inventory;
	private ItemList list = new ItemList();
	
	public TestInventory() {
	inventory = new Inventory();
	inventory.store(new HealthPotion(new Image(Assets.healthPotion), "HealthPotion"), 1);
	inventory.store(new HealthPotion(new Image(Assets.healthPotion), "HealthPotion"), 1);
	inventory.store(new HealthPotion(new Image(Assets.healthPotion), "HealthPotion"), 1);
	inventory.store(list.manaPotion, 1);
	System.out.println(inventory.getSlots());
	images = inventory.getImages();
	System.out.println(images);
	slots = inventory.getSlots();
	System.out.println(images.size);
	}
	
	public Array<Image> getImages() {
		return images;
	}
	
	public Array<Slot> getSlots() {
		return slots;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
}
