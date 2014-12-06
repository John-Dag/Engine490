package com.gdx.Inventory;

public class Item {
	private String textureRegion;
	
	public Item(String textureRegion) {
		this.setTextureRegion(textureRegion);
	}

	public String getTextureRegion() {
		return textureRegion;
	}

	public void setTextureRegion(String textureRegion) {
		this.textureRegion = textureRegion;
	}
}