package com.gdx.UI;

import com.gdx.engine.World;

public abstract class UIConsoleCommand {
	private String command;
	private World world;
	
	public UIConsoleCommand(String command, World world) {
		this.command = new String();
		this.command = command;
		this.setWorld(world);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public abstract void triggerCommand();
}
