package com.gdx.Inventory;

import com.badlogic.gdx.utils.Array;

public class Inventory {
	private Array<Slot> slots;

    public Inventory() {
        slots = new Array<Slot>(16);
        for (int i = 0; i < 16; i++) {
            slots.add(new Slot(null, 0));
        }
    }
    
    public boolean store(Item item, int amount) {
        // first check for a slot with the same item type
        Slot itemSlot = firstSlotWithItem(item);
        if (itemSlot != null) {
            itemSlot.add(item, amount);
            return true;
        } else {
            // now check for an available empty slot
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
}