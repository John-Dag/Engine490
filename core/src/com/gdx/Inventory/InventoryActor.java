package com.gdx.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gdx.engine.Assets;

public class InventoryActor extends Window{
	
	public InventoryActor(Inventory inventory, Skin skin) {
		super("Inventory", skin);
		setVisible(false);
		
		//Layout
		setPosition(Gdx.graphics.getWidth() - 230, 30);
        defaults().space(3);
        row().fill().expandX();
		
		
		// Adds a slotActor for every slot.
		int i = 0;
		for (Slot slot : inventory.getSlots()) {
			final SlotActor slotActor;
			if (slot.getItem() != null) 
				slotActor = new SlotActor(slot.getItem().getTexture(), slot);
			else 
				slotActor = new SlotActor(Assets.gridslot, slot); // No item to show.
				
				// Removes item and triggers its effect when clicked on.
				slotActor.addListener(new ClickListener(Input.Buttons.RIGHT) {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Slot slot = slotActor.getSlot();
						System.out.println(slotActor.getSlot().getItem());
						if (slot.getItem() != null)
							slot.getItem().effect(); // Use item
						slot.remove(1); // Consume item
					}
				});
            
            add(slotActor).pad(3).size(30, 30);
            
            // New row every 5 slots
            i++;
            if (i % 5 == 0) {
                row();
            }
		}
		
		//Fits actors to window.
		pack();
	}
}
