package com.gdx.Inventory;

import com.badlogic.gdx.utils.Array;

public class Slot {
	private Item item;
	private int amount;
	private Array<SlotListener> slotListeners = new Array<SlotListener>();
	
	public Slot (Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public void add(Item item, int amount) {
		if (this.item == item || this.item == null) {
            this.item = item;
            this.amount += amount;
            notifyListeners();
		}
	}
	
	public void remove(int amount) {
		if (this.amount >= amount) {
            this.amount -= amount;
            if (this.amount == 0) {
                item = null;
            }
            notifyListeners();
		}
	}
	
	public Item getItem() {
		return item;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void addListener(SlotListener slotListener) {
        slotListeners.add(slotListener);
    }

    public void removeListener(SlotListener slotListener) {
        slotListeners.removeValue(slotListener, true);
    }

    private void notifyListeners() {
        for (SlotListener slotListener : slotListeners) {
            slotListener.hasChanged(this);
        }
    }
	
	@Override
    public String toString() {
        return "Slot[" + item + ":" + amount + "]";
    }
}