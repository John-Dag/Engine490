package com.gdx.Inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gdx.engine.Assets;

public class SlotActor extends Image implements SlotListener{
	
	private Slot slot;
	
	public SlotActor(Texture texture, Slot slot) {
		super(texture);
		this.slot = slot;
		
		slot.addListener(this);
	}
	
	@Override
	public void hasChanged(Slot slot) {
		// Show new item
		if(slot.getItem() != null) {
			TextureRegion region = new TextureRegion(slot.getItem().getTexture());
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);
		}
		// Show an empty slot
		else {
			TextureRegion region = new TextureRegion(Assets.gridslot);
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);
		}
		
	}
	
	public Slot getSlot() {
		return slot;
	}
}
