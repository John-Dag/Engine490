package com.gdx.Commands;

import java.io.IOException;

import com.gdx.UI.UIConsoleCommand;
import com.gdx.engine.World;

public class ReconnectCommand extends UIConsoleCommand {
	public ReconnectCommand(String command, World world) {
		super(command, world);
	}

	@Override
	public void triggerCommand() {
		if (this.getWorld().getClient() == null) {
			System.err.println("Offline mode");
			return;
		}
	
		try {
			if (!this.getWorld().getClient().getClient().isConnected()) {
				this.getWorld().playerInstances.clear();
				this.getWorld().getClient().getClient().reconnect();
				this.getWorld().getClient().createPlayerOnServer();
			}
			else {
				System.err.println("Already connected to a host");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
