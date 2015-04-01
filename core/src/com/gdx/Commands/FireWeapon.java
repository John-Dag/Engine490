package com.gdx.Commands;

import com.gdx.UI.UIConsoleCommand;
import com.gdx.engine.World;

public class FireWeapon extends UIConsoleCommand {
	public FireWeapon(String command, World world) {
		super(command, world);
	}
	
	@Override
	public void triggerCommand() {
		if (this.getWorld().getPlayer().getWeapon() != null)
			this.getWorld().getPlayer().getWeapon().fireWeapon(this.getWorld());
	}
}
