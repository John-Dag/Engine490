package com.gdx.Matches;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gdx.DynamicEntities.Player;
import com.gdx.Network.Net;
import com.gdx.Network.NetMatch;
import com.gdx.Network.NetStat;
import com.gdx.engine.World;

public class DeathMatch extends NetMatch {
	private boolean matchActive;
	private int killsToWin;
	private Timer timer;
	
	public DeathMatch(World world, Array<Player> players, Array<NetStat> stats, long startDelay, int killsToWin) {
		super(world, players, stats, startDelay);
		matchActive = false;
		this.killsToWin = killsToWin;
		this.timer = new Timer();
	}
	
	@Override
	public void broadcastStartMessage() {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = "Death Match starting in " + this.getStartDelay() / 1000 + " seconds...";
		this.getWorld().getServer().sendChatMessage(packet);
		
		timer.schedule(new TimerTask() {
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
			if (matchActive && stat.getKills() >= this.killsToWin) {
				Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
				packet.message = "" + stat.getName() + " wins the match with " + stat.getKills() + " kills" + " and " + stat.getDeaths() + " deaths!";
				this.getWorld().getServer().sendChatMessage(packet);
				this.getWorld().getServer().setActiveMatch(null);
				matchActive = false;
			}
		}
	}
}
