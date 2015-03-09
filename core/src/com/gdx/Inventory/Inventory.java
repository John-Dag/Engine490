package com.gdx.Inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.gdx.engine.Assets;

public class Inventory {
	private Array<Slot> slots;
	private Array<Image> images;
	private int size = 25;

    public Inventory() {
        slots = new Array<Slot>(size);
        images = new Array<Image>(size);
        for (int i = 0; i < size; i++) {
            slots.add(new Slot(null, 0));
            images.add(new Image(Assets.gridslot));
           
        }
    }
    
    public boolean store(Item item, int amount) {
        Slot itemSlot = firstSlotWithItem(item);
        if (itemSlot != null) {
            itemSlot.add(item, amount);      
            return true;
        } else {
            Slot emptySlot = firstSlotWithItem(null);
            if (emptySlot != null) {
                emptySlot.add(item, amount);
                updateImage(emptySlot);
                return true;
            }
        }
        // no slot to add
        return false;
    }
    
    private Slot firstSlotWithItem(Item item) {
        for (Slot slot : slots) {
            if (slot.getItem() == item) {
                return slot;
            }
        }

        return null;
    }
    
    public Array<Image> getImages() {
        return images;
    }
    
   public void updateImage(Slot slot) {
        for (int i = 0; i < size; i++) {
        	if (slots.get(i).equals(slot)) {
        		images.set(i, slot.getItem().getImage());
        		System.out.println("TEST");
        	}
        }
    }
    
    public Array<Slot> getSlots() {
    	return slots;
    }
    
    public int getSize() {
    	return size;
    }
    
    public void setSize(int size) {
    	this.size = size;
    }
}