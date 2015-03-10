package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.engine.Assets;

public class ItemList {
	public ItemList(){
	}
	
	//private Image iEmpty = new Image(Assets.gridslot);
	//public Item empty = new Item(iEmpty, "Empty");
	
	private Image iHealthPotion = new Image(Assets.healthPotion);
	public HealthPotion healthPotion = new HealthPotion(iHealthPotion, "Health Potion");
	
	private Image iManaPotion = new Image(Assets.manaPotion);
	public Item manaPotion = new Item(iManaPotion, "Mana Potion");
}
