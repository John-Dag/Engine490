package lightning3d.Network;

import java.io.IOException;

import lightning3d.DynamicEntities.Player;
import lightning3d.Engine.World;
import lightning3d.Matches.DeathMatch;
import lightning3d.Network.Net.PlayerPacket;
import lightning3d.Spawners.WeaponSpawn;
import lightning3d.StaticEntities.PowerUp;

import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class NetServer {
	private Server server;
	private NetStatManager netStatManager;
	private World world;
	private NetMatch activeMatch;
	
	public NetServer(World world) throws IOException {
		try {
			this.world = world;
			netStatManager = new NetStatManager();
			server = new Server(Net.writeBuffer, Net.objectBuffer);
			server.start();
			Log.set(Log.LEVEL_INFO);
			Net.register(server);
			server.bind(Net.tcpPort, Net.udpPort);
			Log.info("Server successfully created. Listening on TCP port: " + Net.tcpPort + ", UDP port: " + Net.udpPort);
			server.addListener(createDefaultListeners());
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private Listener createDefaultListeners() {
		Listener listener = new Listener() {
			@Override
			public void connected(Connection connection) {
				clientConnect(connection);
				sendNetStatUpdate();
			}
			
			@Override
			public void disconnected(Connection connection) {
				clientDisconnect(connection);
			}
			
			@Override
			public void received(Connection connection, Object object) {
				packetReceived(connection, object);
			}
		};
		
		return listener;
	}
	
	private void clientConnect(Connection connection) {
		Log.info("Client " + connection + " connected to the server.");
	}
	
	private void clientDisconnect(Connection connection) {
		Log.info("Client " + connection + " disconnected from the server.");
		removePlayer(connection);
	}
	
	
	//Handles received packets
	private void packetReceived(Connection connection, Object object) {
	   	if (object instanceof Net.PlayerPacket) {
    		Net.PlayerPacket packet = (Net.PlayerPacket)object;
    		NetServerEvent.PlayerUpdate event = new NetServerEvent.PlayerUpdate(packet);
    		event.handleEvent(world);
    		//world.getServerEventManager().addNetEvent(event);
    	}
    	
    	else if (object instanceof Net.ProjectilePacket) {
    		//Net.projectile packet = (Net.projectile)object;
    		//updateProjectiles(packet);
    		//server.sendToAllExceptTCP(connection.getID(), packet);
    	}
    	
    	//Handles a packet from a new player. Sends the packet to all players 
    	//except the new one.
    	else if (object instanceof Net.NewPlayer) {
    		Net.NewPlayer playerNew = (Net.NewPlayer)object;
    		Net.NewPlayer packet = new Net.NewPlayer();
    		packet.position = playerNew.position;
    		packet.id = server.getConnections().length;
    		packet.name = playerNew.name;
    		NetServerEvent.NewPlayer event = new NetServerEvent.NewPlayer(packet);
    		world.getServerEventManager().addNetEvent(event);
    		NetServerEvent.NewNetStat eventB = new NetServerEvent.NewNetStat(packet);
    		world.getServerEventManager().addNetEvent(eventB);
    	}
    	
    	else if (object instanceof Net.NewProjectile) {
    		Net.NewProjectile packet = (Net.NewProjectile)object;
    		NetServerEvent.NewProjectile event = new NetServerEvent.NewProjectile(packet);
    		event.handleEvent(world);
    		//world.getServerEventManager().addNetEvent(event);
    	}
        
        else if (object instanceof Net.ChatMessagePacket) {
        	Net.ChatMessagePacket packet = (Net.ChatMessagePacket)object;
        	packet.id = connection.getID();
        	NetServerEvent.ChatMessage event = new NetServerEvent.ChatMessage(packet);
        	world.getServerEventManager().addNetEvent(event);
        }
	   	
        else if (object instanceof Net.KillPacket) {
        	Net.KillPacket packet = (Net.KillPacket)object;
        	NetServerEvent.KillEvent event = new NetServerEvent.KillEvent(packet);
        	world.getServerEventManager().addNetEvent(event);
        }
	   	
        else if (object instanceof Net.DeathPacket) {
        	Net.DeathPacket packet = (Net.DeathPacket)object;
        	NetServerEvent.DeathEvent event = new NetServerEvent.DeathEvent(packet);
        	world.getServerEventManager().addNetEvent(event);
        }
	   	
        else if (object instanceof Net.PowerUpConsumedPacket) {
        	System.out.println("Server received powerUpConsumedPacket");
        	Net.PowerUpConsumedPacket packet = (Net.PowerUpConsumedPacket)object;
        	NetServerEvent.PowerUpConsumed event = new NetServerEvent.PowerUpConsumed(packet);
        	world.getServerEventManager().addNetEvent(event);
        }
	   	
        else if (object instanceof Net.WeaponPickedUpPacket) {
        	System.out.println("Server received weaponPickedUpPacket");
        	Net.WeaponPickedUpPacket packet = (Net.WeaponPickedUpPacket)object;
        	NetServerEvent.WeaponPickedUp event = new NetServerEvent.WeaponPickedUp(packet);
        	world.getServerEventManager().addNetEvent(event);
        }
	}
	
	public void startMatch(NetMatch match) {
		match.broadcastStartMessage();
	}
	
	public void resetSpawns() {
		for(int i = 0; i < world.getMeshLevel().getPowerUpInstances().size; i++) {
			Net.PowerUpRespawnPacket powerUpPacket = new Net.PowerUpRespawnPacket();
			powerUpPacket.powerUpEntityId = i;
			respawnPowerUp(powerUpPacket);
		}
		
		for(int i = 0; i < world.getMeshLevel().getWeaponInstances().size; i++) {
			Net.WeaponRespawnPacket weaponPacket = new Net.WeaponRespawnPacket();
			weaponPacket.weaponEntityId = i;
			respawnWeapon(weaponPacket);
		}
	}
	
	public void sendNetStatUpdate() {
		for (int i = 0; i < netStatManager.getStats().size; i++) {
			Net.StatPacket packet = new Net.StatPacket();
			packet.kills = netStatManager.getStats().get(i).getKills();
			packet.deaths = netStatManager.getStats().get(i).getDeaths();
			packet.name = netStatManager.getStats().get(i).getName();
			packet.playerID = netStatManager.getStats().get(i).getId();
			server.sendToAllTCP(packet);
		}
	}
	
	public void updateKillNetStat(int id) {
		for (int i = 0; i < netStatManager.getStats().size; i++) {
			if (id == netStatManager.getStats().get(i).getId()) {
				netStatManager.getStats().get(i).setKills(netStatManager.getStats().get(i).getKills() + 1);
			}
		}
	}
	
	public void updateDeathNetStat(int id) {
		for (int i = 0; i < netStatManager.getStats().size; i++) {
			if (id == netStatManager.getStats().get(i).getId()) {
				netStatManager.getStats().get(i).setDeaths(netStatManager.getStats().get(i).getDeaths() + 1);
			}
		}
	}
	
	public void addNetStat(Net.NewPlayer packet) {
		NetStat stat = new NetStat(packet.id, packet.name);
		netStatManager.getStats().add(stat);
	}
	
	public void addNewProjectile(Net.NewProjectile packet) {
		world.addProjectile(packet);
		server.sendToAllExceptTCP(packet.originID, packet);
	}
	
	public void addNewPowerUp(Net.NewPowerUpPacket packet) {
		
	}
	
	public void respawnPowerUp(Net.PowerUpRespawnPacket packet) {
		PowerUp powerUp = world.getMeshLevel().getPowerUpInstances().get(packet.powerUpEntityId);
		powerUp.setIsRenderable(true);
		server.sendToAllTCP(packet);
	}
	
	public void consumePowerUp(Net.PowerUpConsumedPacket packet) {
		PowerUp powerUp = world.getMeshLevel().getPowerUpInstances().get(packet.powerUpEntityId);
		// if powerUp is renderable
		if(powerUp.isRenderable()){
			// set to not renderable, set event to broadcast confirmation packet
			powerUp.setIsRenderable(false);
			server.sendToAllTCP(packet);
			// set respawn timer
			powerUp.getSpawner().startTimer();
		} else {
			System.out.println("POWER UP NOT RENDERABLE?!?");
		}

		// else (powerUp inactive)
			// do nothing
	}
	
	public void respawnWeapon(Net.WeaponRespawnPacket packet) {
		WeaponSpawn weaponSpawn = world.getMeshLevel().getWeaponInstances().get(packet.weaponEntityId);
		weaponSpawn.setIsRenderable(true);
		server.sendToAllTCP(packet);
	}
	
	public void weaponPickedUp(Net.WeaponPickedUpPacket packet) {
		WeaponSpawn weaponSpawn = world.getMeshLevel().getWeaponInstances().get(packet.weaponEntityId);
		// if weaponSpawn is renderable
		if(weaponSpawn.isRenderable()){
			// set to not renderable, set event to broadcast confirmation packet
			weaponSpawn.setIsRenderable(false);
			server.sendToAllTCP(packet);
			// set respawn timer
			weaponSpawn.getSpawner().startTimer();
		} else {
			System.out.println("WEAPONSPAWN NOT RENDERABLE?!?");
		}
	}
	
	//Updates all clients with the player that disconnected
	public void removePlayer(Connection connection) {
		String name = "";
		boolean removed = false;
		
		for (int i = 0; i < world.playerInstances.size; i++) {
			Player player = world.playerInstances.get(i);
			
			if (removed) {
				world.playerInstances.get(i).setNetId(world.playerInstances.get(i).getNetId() - 1);
			}
			
			if (player.getNetId() == connection.getID()) {
				name = player.getNetName();
				world.playerInstances.removeIndex(i);
				removed = true;
			}
		}
		
		Net.PlayerDisconnect disconnect = new Net.PlayerDisconnect();
		disconnect.id = connection.getID();
		server.sendToAllTCP(disconnect);
		String message = name + " disconnected.";
		sendGlobalServerMessage(message);
	}
	
	//Updates a new player with all the players on the server
	public void sendAllPlayers(int id) {
		for (int i = 0; i < world.getPlayerInstances().size; i++) {
			//System.out.println(world.getPlayerInstances().size);
			Net.NewPlayer packet = new Net.NewPlayer();
			packet.id = world.getPlayerInstances().get(i).getNetId();
			packet.position = world.getPlayerInstances().get(i).camera.position.cpy();
			packet.name = world.getPlayerInstances().get(i).getNetName();
			
			if (id != packet.id)
				server.sendToTCP(id, packet);
		}
	}
	
	public void sendCollisionPacket(Net.CollisionPacket packet) {
		server.sendToAllTCP(packet);
	}
	
	public void broadcastNewMatch(Net.NewMatch packet) {
		server.sendToAllTCP(packet);
	}
	
	public void addNewPlayer(Net.NewPlayer packet) {
		String message = Net.serverMessage + " " + Net.serverIP + "\nActive connections: " + server.getConnections().length;
		
		world.addPlayer(packet);
		server.sendToAllTCP(packet);
		sendAllPlayers(packet.id);
		sendServerMessage(message, packet.id);
		String joinedMessage = packet.name + " joined.";
		sendGlobalServerMessageExcept(joinedMessage, packet.id);
		addNetStat(packet);
		
		// also want to send to this player the renderable powerUps
		Net.PowerUpRespawnPacket powerUpPacket = new Net.PowerUpRespawnPacket();
		for(int i = 0; i < world.getMeshLevel().getPowerUpInstances().size; i++) {
			if(world.getMeshLevel().getPowerUpInstances().get(i).isRenderable()) {
				powerUpPacket.powerUpEntityId = i;
				server.sendToTCP(packet.id, powerUpPacket);
				System.out.println("Server: PowerUp packet sent.");
			}
		}
		
		// also want to send to this player the renderable weaponSpawns
		Net.WeaponRespawnPacket weaponPacket = new Net.WeaponRespawnPacket();
		for(int i = 0; i < world.getMeshLevel().getWeaponInstances().size; i++) {
			if(world.getMeshLevel().getWeaponInstances().get(i).isRenderable()){
				weaponPacket.weaponEntityId = i;
				server.sendToTCP(packet.id, weaponPacket);
				System.out.println("Server: Weapon packet sent.");
			}
		}
	}
	
	public void updatePlayers(PlayerPacket packet) {
		world.updatePlayers(packet);
		server.sendToAllExceptUDP(packet.id, packet);
	}
	
	public void updatePlayer(PlayerPacket packet, int id) {
		server.sendToTCP(id, packet);
	}
	
	public void updateProjectiles(Net.ProjectilePacket packet) {
		server.sendToAllUDP(packet);
	}
	
	public void sendChatMessage(Net.ChatMessagePacket packet) {
		server.sendToAllExceptTCP(packet.id, packet);
	}
	
	public void sendServerMessage(String message, int id) {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = Net.name + ": " + message;
		server.sendToTCP(id, packet);
	}
	
	public void sendGlobalServerMessage(String message) {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = Net.name + ": " + message;
		server.sendToAllTCP(packet);
	}
	
	public void sendGlobalServerMessageExcept(String message, int id) {
		Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
		packet.message = Net.name + ": " + message;
		server.sendToAllExceptTCP(id, packet);
	}
	
	public void serverUpdate() {
		world.getServerEventManager().processEvents();
		if (this.getActiveMatch() != null)
			this.getActiveMatch().update();
	}

	public NetMatch getActiveMatch() {
		return activeMatch;
	}

	public void setActiveMatch(NetMatch match) {
		this.activeMatch = match;
	}

	public NetStatManager getNetStatManager() {
		return netStatManager;
	}

	public void setNetStatManager(NetStatManager netStatManager) {
		this.netStatManager = netStatManager;
	}
}
