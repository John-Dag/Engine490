package com.gdx.Inventory;

import com.badlogic.gdx.utils.Array;

public class Inventory {
	private Array<Slot> slots;
	private int size = 25;

    public Inventory() {
        slots = new Array<Slot>(size);
        for (int i = 0; i < size; i++) {
            slots.add(new Slot(null, 0));
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