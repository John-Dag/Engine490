package com.gdx.Matches;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.gdx.DynamicEntities.Player;
import com.gdx.Network.Net;
import com.gdx.Network.NetMatch;
import com.gdx.Network.NetStat;
import com.gdx.engine.World;

public class DeathMatch extends NetMatch {
	private boolean matchActive;
	private int killsToWin;
	
	public DeathMatch(World world, Array<Player> players, Array<NetStat> stats, float startDelay, int killsToWin) {
		super(world, players, stats, startDelay);
		matchActive = false;
		this.killsToWin = killsToWin;
	}
	
	@Override
	public void broadcastStartMessage() {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = "Death Match starting in " + this.getStartDelay() + " seconds...";
		this.getWorld().getServer().sendChatMessage(packet);
		
		Timer.schedule(new Task(){
		    @Override
		    public void run() {
		    	startMatch();
		    }
		}, this.getStartDelay());
	}
	
	@Override
	public void startMatch() {
		Net.ChatMessagePacket chatPacket = new Net.ChatMessagePacket();
		Net.NewMatch matchPacket = new Net.NewMatch();
		matchPacket.startPos = new Vector3(2.0f, 1.5f, 2.0f);
		chatPacket.message = "Match started";
		this.getWorld().getServer().sendChatMessage(chatPacket);
		this.getWorld().getServer().getNetStatManager().resetStats();
		this.getWorld().getServer().sendNetStatUpdate();
		this.getWorld().getServer().broadcastNewMatch(matchPacket);
		this.getWorld().getServer().setActiveMatch(this);
		this.getWorld().getServer().resetSpawns();
		matchActive = true;
	}

	@Override
	public void update() {
		for (NetStat stat : this.getStats()) {
			if (matchActive && stat.getKills() == this.killsToWin) {
				Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
				packet.message = "" + stat.getName() + " wins the match with " + stat.getKills() + " kills" + " and " + stat.getDeaths() + " deaths!";
				this.getWorld().getServer().sendChatMessage(packet);
				this.getWorld().getServer().setActiveMatch(null);
				matchActive = false;
			}
		}
	}
}
