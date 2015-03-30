package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.Inventory.Inventory;
import com.gdx.Inventory.InventoryActor;

public class UIInventory extends UIBase{
	
	private InventoryActor invActor;
	
	public UIInventory(Stage stage, Inventory inventory, Skin skin) {
		super(stage);
		invActor = new InventoryActor(inventory, skin);
		
		//Prevents mouse catching when right clicking on the inventory window.
		invActor.addListener(new ClickListener(Input.Buttons.RIGHT) {}); 
		
		stage.addActor(invActor);
	}
	
	@Override
	public void show() {
		if (!this.invActor.isVisible()) {
			this.invActor.setVisible(true);
			Gdx.input.setCursorCatched(false);
		}
		else {
			this.invActor.setVisible(false);
			Gdx.input.setCursorCatched(true);
		}
	}
	
	public InventoryActor getInventoryActor() {
		return invActor;
	}
}
