package lightning3d.Inventory;

import lightning3d.Items.ItemBase;

import com.badlogic.gdx.utils.Array;

public class Slot {
	private ItemBase item;
	private int amount;
	private Array<SlotListener> slotListeners = new Array<SlotListener>();
	
	public Slot (ItemBase item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public void add(ItemBase item, int amount) {
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
	
	public ItemBase getItem() {
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