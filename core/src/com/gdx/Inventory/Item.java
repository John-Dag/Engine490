package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Item {
	private Image image;
	private String name;
	
	public Item(Image image, String name) {
		this.image = image;
		this.name = name;
	}
	
	//Override this to give items effects.
	public void effect(){
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
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