package lightning3d.Commands;

import java.io.IOException;

import lightning3d.Engine.World;
import lightning3d.UI.UIConsoleCommand;

public class ReconnectCommand extends UIConsoleCommand {
	public ReconnectCommand(String command, World world) {
		super(command, world);
	}

	@Override
	public void triggerCommand(String value) {
		if (this.getWorld().getClient() == null) {
			System.out.println("Offline mode");
			return;
		}
	
		try {
			if (!this.getWorld().getClient().getClient().isConnected()) {
				this.getWorld().playerInstances.clear();
				this.getWorld().getClient().getClient().reconnect();
				this.getWorld().getClient().createPlayerOnServer();
			}
			else {
				System.out.println("Already connected to a host");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
