package com.gdx.Inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SlotActor extends Image implements SlotListener{
	
	private Slot slot;
	
	public SlotActor(Texture texture, Slot slot) {
		super(texture);
		this.slot = slot;
		
		slot.addListener(this);
	}
	
	@Override
	public void hasChanged(Slot slot) {
		if(slot.getItem() != null) {
			TextureRegion region = new TextureRegion(slot.getItem().getTexture());
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);
		}
	}
	
	public Slot getSlot() {
		return slot;
	}
}
