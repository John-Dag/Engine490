package com.gdx.Inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.engine.Assets;

// Extend this class to make your own items
public class Item {
	protected Texture texture;
	protected String name;
	
	public Item() {
		this.texture =  Assets.gridslot;
		this.name = "base item";
	}
	
	//Override this to give items effects.
	public void effect(){
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}