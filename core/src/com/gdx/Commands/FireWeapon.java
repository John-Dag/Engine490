package com.gdx.Commands;

import com.gdx.UI.UIConsoleCommand;
import com.gdx.engine.World;

public class FireWeapon extends UIConsoleCommand {
	public FireWeapon(String command, World world) {
		super(command, world);
	}
	
	@Override
	public void triggerCommand() {
		this.getWorld().getPlayer().getWeapon().fireWeapon(this.getWorld());
	}
}
