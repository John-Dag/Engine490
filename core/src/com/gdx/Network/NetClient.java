package com.gdx.Network;

import java.io.IOException;

import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.DynamicEntities.Weapon;
import com.gdx.Network.Net.PlayerPacket;
import com.gdx.StaticEntities.PowerUp;
import com.gdx.engine.Entity;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;

public class NetClient {
	private Client client;
	private World world;
	private GameScreen screen;
	private int id;
	
	public NetClient() {
		super();
	}
	
	public NetClient(World world, GameScreen screen) throws IOException {
		try {
			this.world = world;
			this.screen = screen;
			client = new Client();
			Log.set(Log.LEVEL_INFO);
			client.start();
		    Net.register(client);
		    connectClientToServer();
		    createPlayerOnServer();
		    setId(client.getID());
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private void connectClientToServer() {
		try {
			client.connect(5000, Net.serverIP, Net.tcpPort, Net.udpPort);
			client.addListener(createDefaultListeners());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createPlayerOnServer() {
		Net.NewPlayer packet = new Net.NewPlayer();
		packet.position = world.getPlayer().getPosition();
		packet.id = client.getID();
		packet.name = Net.name;
		world.getPlayer().setNetId(client.getID());
		client.sendTCP(packet);
	}
	
	private Listener createDefaultListeners() {
		Listener listener = new Listener() {
			@Override
			public void connected(Connection connection) {
				serverConnect(connection);
			}
			
			@Override
			public void disconnected(Connection connection) {
				serverDisconnect(connection);
			}
			
			@Override
			public void received(Connection connection, Object object) {
				packetReceived(connection, object);
			}
		};
		
		return listener;
	}
	
	private void serverConnect(Connection connection) {
		Log.info("Client " + connection + " connected to " + Net.serverIP);
	}
	
	private void serverDisconnect(Connection connection) {
		Log.info("Client " + connection + " disconnected from " + Net.serverIP);
	}
	
	//Handles received packets
	private void packetReceived(Connection connection, Object object) {
        if (object instanceof Net.PlayerPacket) {
     	   Net.PlayerPacket packet = (Net.PlayerPacket)object;
     	   updatePlayers(packet, connection);
        }
        
        else if (object instanceof Net.NewPlayer) {
       	   Net.NewPlayer packet = (Net.NewPlayer)object;
       	   createPlayerStatField(packet);
       	   world.getClientEventManager().addNetEvent(new NetClientEvent.CreatePlayer(packet));
        }
        
        else if (object instanceof Net.PlayerDisconnect) {
     	   Net.PlayerDisconnect packet = (Net.PlayerDisconnect)object;
     	   world.getClientEventManager().addNetEvent(new NetClientEvent.RemovePlayer(packet));
        }
        
        else if (object instanceof Net.ProjectilePacket) {
     	   Net.ProjectilePacket packet = (Net.ProjectilePacket)object;
     	   updateProjectiles(packet);
        }
        
        else if (object instanceof Net.NewProjectile) {
     	   Net.NewProjectile packet = (Net.NewProjectile)object;
     	   world.getClientEventManager().addNetEvent(new NetClientEvent.CreateProjectile(packet));
     	}
        
        else if (object instanceof Net.ChatMessagePacket) {
        	Net.ChatMessagePacket packet = (Net.ChatMessagePacket)object;
        	world.getClientEventManager().addNetEvent(new NetClientEvent.ChatMessage(packet));
        }
        
        else if (object instanceof Net.CollisionPacket) {
        	Net.CollisionPacket packet = (Net.CollisionPacket)object;
        	world.getClientEventManager().addNetEvent(new NetClientEvent.ProjectileCollision(packet));
        }
        
        else if (object instanceof Net.StatPacket) {
        	Net.StatPacket packet = (Net.StatPacket)object;
        	updateNetStats(packet);
        }
        
        else if (object instanceof Net.PowerUpConsumedPacket) {
        	System.out.println("Client received powerUpConsumed confirmation packet");
        	Net.PowerUpConsumedPacket packet = (Net.PowerUpConsumedPacket)object;
        	handlePowerUpConsumedPacket(packet);
        }
        
        else if (object instanceof Net.PowerUpRespawnPacket) {
        	System.out.println("Client received powerUpRespawn packet");
        	Net.PowerUpRespawnPacket packet = (Net.PowerUpRespawnPacket)object;
        	handlePowerUpRespawnPacket(packet);
        }
        
        else if (object instanceof Net.WeaponPickedUpPacket) {
        	System.out.println("Client received powerUpConsumed confirmation packet");
        	Net.WeaponPickedUpPacket packet = (Net.WeaponPickedUpPacket)object;
        	handleWeaponPickedUpPacket(packet);
        }
        
        else if (object instanceof Net.WeaponRespawnPacket) {
        	System.out.println("Client received powerUpRespawn packet");
        	Net.WeaponRespawnPacket packet = (Net.WeaponRespawnPacket)object;
        	handleWeaponRespawnPacket(packet);
        }
	}
	
	private void handlePowerUpConsumedPacket(Net.PowerUpConsumedPacket packet) {
		// this makes the power up disappear for players who did not consume it but witnessed the event
		PowerUp powerUp = world.getMeshLevel().getPowerUpInstances().get(packet.powerUpEntityId);
		powerUp.setIsRenderable(false);
		if(packet.playerId == world.getPlayer().getNetId()) {
			powerUp.effect();
		}
	}
	
	private void handlePowerUpRespawnPacket(Net.PowerUpRespawnPacket packet) {
		PowerUp powerUp = world.getMeshLevel().getPowerUpInstances().get(packet.powerUpEntityId);
		powerUp.setIsRenderable(true);
	}
	
	private void handleWeaponPickedUpPacket(Net.WeaponPickedUpPacket packet) {
		// this makes the weapon disappear for players who did not consume it but witnessed the event
		Weapon weapon = world.getMeshLevel().getWeaponInstances().get(packet.weaponEntityId);
		if(packet.playerId == world.getPlayer().getNetId()) {
			world.getPlayer().setWeapon(weapon);
		} else {
			weapon.setIsRenderable(false);
		}
	}
	
	private void handleWeaponRespawnPacket(Net.WeaponRespawnPacket packet) {
		Weapon weapon = world.getMeshLevel().getWeaponInstances().get(packet.weaponEntityId);
		weapon.setIsRenderable(true);
	}
	
	public void createPlayerStatField(Net.NewPlayer packet) {
		NetStatField field = new NetStatField("", GameScreen.skin);
		field.setText(packet.name + "                  " + 
	              		     0 + "                  " + 0);
		field.setPlayerID(packet.id);
		screen.getStatForm().addNetStatField(field, 0, 20f * screen.getStatForm().getFields().size, 300, 20);
	}
	
	public void updateNetStats(Net.StatPacket packet) {
		for (int i = 0; i < screen.getStatForm().getFields().size; i++) {
			NetStatField form = (NetStatField) screen.getStatForm().getFields().get(i);
			if (form.getPlayerID() == packet.playerID) {
				screen.getStatForm().getFields().get(i).setText(packet.name + "         " + 
																			  packet.kills + "                " + packet.deaths);
			}
		}
	}
	
	public void sendKillUpdate(int playerID) {
		Net.KillPacket packet = new Net.KillPacket();
		packet.name = Net.name;
		client.sendTCP(packet);
	}
	
	public void sendDeathUpdate() {
		Net.deathPacket packet = new Net.deathPacket();
		client.sendTCP(packet);
	}
	
	public void sendPowerUpConsumedUpdate(Net.PowerUpConsumedPacket packet) {
		client.sendTCP(packet);
	}
	
	public void sendWeaponPickedUpUpdate(Net.WeaponPickedUpPacket packet) {
		client.sendTCP(packet);
	}
	
	//Remove the player that disconnected from the world
	//Check both the player instances, and entity instances for the player
	public void removePlayer(Net.PlayerDisconnect disconnect) {
		try {
			for (int i = 0; i < world.playerInstances.size; i++) {
				Player player = world.playerInstances.get(i);
				if (player.getNetId() == disconnect.id) {
					world.getPlayerInstances().removeIndex(i);
					screen.getStatForm().getFields().get(i).setVisible(false);
					screen.getStatForm().getFields().removeIndex(i);
				}
			}
			
			for (int i = 0; i < Entity.entityInstances.size; i++) {
				Entity entity = Entity.entityInstances.get(i);
				if (entity instanceof Player) {
					Player player = (Player)Entity.entityInstances.get(i);
					if (player.getNetId() == disconnect.id)
						Entity.entityInstances.removeIndex(i);
				}
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void addChatMessage(Net.ChatMessagePacket packet) {
		screen.getChat().addMessage(packet);
	}
	
	public void sendChatMessage(Net.ChatMessagePacket packet) {
		client.sendTCP(packet);
	}
	
	public void sendProjectile(Projectile projectile, int id) {
		Net.NewProjectile packet = new Net.NewProjectile();
		packet.id = id;
		packet.position = projectile.getPosition();
		packet.cameraPos = projectile.getVelocity();
		packet.originID = this.getId();
		client.sendTCP(packet);
	}
	
	public void sendPlayerUpdate() {
    	Net.PlayerPacket packet = new Net.PlayerPacket();
    	packet.position = world.getPlayer().camera.position.cpy();
    	packet.position.y = packet.position.y - .5f;
    	packet.id = client.getID();
    	packet.direction = world.getPlayer().camera.direction.cpy();
		client.sendUDP(packet);
		world.getPlayer().setRespawning(false);
	}
	
	//Adds a new player to the client representing a player on the server
	public void addPlayer(Net.NewPlayer packet) {
		world.addPlayer(packet);
	}
	
	public void addServerProjectile(Net.NewProjectile packet) {
		world.addProjectile(packet);
	}
	
	//Updates the clients players with player positions from the server
	public void updatePlayers(PlayerPacket packet, Connection connection) {
		world.updatePlayers(packet);
	}
	
	public void updateProjectiles(Net.ProjectilePacket packet) {
		world.updateProjectiles(packet);
	}
	
	//Send updates to the server if the player is moving, jumping, or respawning.
	public void clientUpdate() {
		world.getClientEventManager().processEvents();
		if (!world.getPlayer().getMovementVector().isZero() || world.getPlayer().isJumping() || 
		    world.getPlayer().isRespawning() || world.getPlayer().isRotating()) {
			sendPlayerUpdate();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
