package lightning3d.Commands;

import lightning3d.Engine.World;
import lightning3d.UI.UIConsoleCommand;

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
