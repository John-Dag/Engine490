package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.engine.Assets;

public class ItemList {
	private static Image iEmpty = new Image(Assets.gridslot);
	public static Item empty = new Item(iEmpty, "Empty");
	
	private static Image iHealthPotion = new Image(Assets.healthPotion);
	public static Item healthPotion = new Item(iHealthPotion, "Health Potion");
	
	private static Image iManaPotion = new Image(Assets.manaPotion);
	public static Item manaPotion = new Item(iManaPotion, "Mana Potion");
}
