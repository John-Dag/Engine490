package lightning3d.Inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class SlotToolTip extends Window{

	private Skin skin;
	private Slot slot;
	
	public SlotToolTip(Slot slot, Skin skin) {
		super("ToolTip", skin);
		this.skin = skin;
		this.slot = slot;
		
		setTitle(slot.getAmount() + "x " + slot.getItem());
        clear();
        Label label = new Label("Super awesome description of " + slot.getItem(), skin);
        add(label);
        pack();
	}

}
