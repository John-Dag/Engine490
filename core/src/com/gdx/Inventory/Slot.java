package com.gdx.Inventory;

public class Slot {
	private Item item;
	private int amount;
	
	public Slot (Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public void add(Item item, int amount) {
		if (this.item == item || this.item == null) {
            this.item = item;
            this.amount += amount;
		}
	}
	
	public void remove(int amount) {
		if (this.amount >= amount) {
            this.amount -= amount;
            if (this.amount == 0) {
                item = null;
            }
		}
	}
	
	public Item getItem() {
		return item;
	}
	
	public int getAmount() {
		return amount;
	}
	
	@Override
    public String toString() {
        return "Slot[" + item + ":" + amount + "]";
    }
}