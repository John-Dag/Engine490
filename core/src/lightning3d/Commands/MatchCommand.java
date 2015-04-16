package lightning3d.Commands;

import lightning3d.Engine.GameScreen;
import lightning3d.Engine.World;
import lightning3d.Matches.DeathMatch;
import lightning3d.UI.UIConsole;
import lightning3d.UI.UIConsoleCommand;

public class MatchCommand extends UIConsoleCommand {
	public MatchCommand(String command, World world) {
		super(command, world);
	}

	@Override
	public void triggerCommand(String value) {
		int killsToWin = UIConsole.parseConsoleValueInt(value);
		
		if (killsToWin == 0)
			killsToWin = 5;
		
		if (GameScreen.mode == GameScreen.Mode.Server && this.getWorld().playerInstances.size > 1) {
			DeathMatch match = new DeathMatch(this.getWorld(), this.getWorld().getPlayerInstances(), this.getWorld().getServer().getNetStatManager().getStats(), 5000, killsToWin);
			this.getWorld().getServer().startMatch(match);
		}
		
		else if (GameScreen.mode != GameScreen.Mode.Server) {
			System.out.println("Only hosts can start matches.");
		}
		
		else {
			System.out.println("Not enough players to start a match.");
		}
	}
}
