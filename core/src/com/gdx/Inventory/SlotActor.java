package com.gdx.Inventory;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SlotActor extends ImageButton implements SlotListener {
	
	private Slot slot;
    private Skin skin;
    static AssetManager manager;

    public SlotActor(Skin skin, Slot slot) {
        super(createStyle(skin, slot));
        this.slot = slot;
        this.skin = skin;
        manager = new AssetManager();

        // this actor has to be notified when the slot itself changes
        slot.addListener(this);
    }

    private static ImageButtonStyle createStyle(Skin skin, Slot slot) {
        TextureAtlas icons = manager.get("icons.atlas", TextureAtlas.class);
        TextureRegion image;
        if (slot.getItem() != null) {
            image = icons.findRegion(slot.getItem().getTextureRegion());
        } else {
            // we have a special "empty" region in our atlas file, which is just black
            image = icons.findRegion("nothing");
        }
        ImageButtonStyle style = new ImageButtonStyle(skin.get(ButtonStyle.class));
        style.imageUp = new TextureRegionDrawable(image);
        style.imageDown = new TextureRegionDrawable(image);

        return style;
    }
    
    @Override
    public void hasChanged(Slot slot) {
        // when the slot changes, we switch the icon via a new style
        setStyle(createStyle(skin, slot));
    }
    
    public Slot getSlot() {
        return slot;
    }
}