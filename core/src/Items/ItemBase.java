package Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.engine.Assets;

// Extend this class to make your own items.
public class ItemBase implements Item {
	protected Texture texture;
	protected String name;
	
	// Can be used to show an empty or null slot.
	public ItemBase() {
		this.texture =  Assets.gridslot;
		this.name = "no item";
	}
	
	//Override this to give items effects.
	public void effect(){
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	//TODO: equals method
	/*public boolean equals(Item item) {
		if (item != null) {
			if (this.texture == item.getTexture() && this.name == item.getName())
				return true;
		}
		else
			
		return false;
	}*/
	
	@Override
	public String toString() {
		return name;
	}
}