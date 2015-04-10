package com.gdx.Commands;

import com.gdx.UI.UIConsoleCommand;
import com.gdx.engine.World;

public class FireCommand extends UIConsoleCommand {
	public FireCommand(String command, World world) {
		super(command, world);
	}
	
	@Override
	public void triggerCommand(String value) {
		if (this.getWorld().getPlayer().getWeapon() != null)
			this.getWorld().getPlayer().getWeapon().fireWeapon(this.getWorld());
	}
}
