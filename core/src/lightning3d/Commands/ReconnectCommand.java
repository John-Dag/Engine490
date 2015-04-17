package lightning3d.Commands;

import java.io.IOException;

import lightning3d.DynamicEntities.Player;
import lightning3d.Engine.ClientEvent;
import lightning3d.Engine.Entity;
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
				for (int i = 0; i < this.getWorld().playerInstances.size; i++) {
					this.getWorld().playerInstances.get(i).dispose();
				}
				
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
