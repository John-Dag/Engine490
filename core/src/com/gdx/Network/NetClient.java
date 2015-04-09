package com.gdx.Network;

import java.io.IOException;

import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.gdx.DynamicEntities.Player;
import com.gdx.DynamicEntities.Projectile;
import com.gdx.Network.Net.PlayerPacket;
import com.gdx.StaticEntities.PowerUp;
import com.gdx.StaticEntities.WeaponSpawn;
import com.gdx.engine.Entity;
import com.gdx.engine.GameScreen;
import com.gdx.engine.World;
import com.badlogic.gdx.utils.Sort;

public class NetClient {
	private Client client;
	private World world;
	private GameScreen screen;
	private int id;
	private NetStatComparator comparator;
	
	public NetClient() {
		super();
	}
	
	public NetClient(World world, GameScreen screen) throws IOException {
		try {
			this.world = world;
			this.screen = screen;
			comparator = new NetStatComparator();
			client = new Client();
			Log.set(Log.LEVEL_INFO);
			client.start();
		    Net.register(client);
		    connectClientToServer();
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
			    createPlayerOnServer();
			}
			
			@Override
			public void disconnected(Connection connection) {
				Net.ChatMessagePacket packet = new Net.ChatMessagePacket();
				packet.message = "Connection to server lost";
				world.getClient().addChatMessage(packet);
				serverDisconnect(connection);
				removeAllStatFields();
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
       	   world.getNetEventManager().addNetEvent(new NetClientEvent.CreatePlayer(packet));
        }
        
        else if (object instanceof Net.PlayerDisconnect) {
     	   Net.PlayerDisconnect packet = (Net.PlayerDisconnect)object;
     	   world.getNetEventManager().addNetEvent(new NetClientEvent.RemovePlayer(packet));
        }
        
        else if (object instanceof Net.ProjectilePacket) {
     	   Net.ProjectilePacket packet = (Net.ProjectilePacket)object;
     	   updateProjectiles(packet);
        }
        
        else if (object instanceof Net.NewProjectile) {
     	   Net.NewProjectile packet = (Net.NewProjectile)object;
     	   world.getNetEventManager().addNetEvent(new NetClientEvent.CreateProjectile(packet));
     	}
        
        else if (object instanceof Net.ChatMessagePacket) {
        	Net.ChatMessagePacket packet = (Net.ChatMessagePacket)object;
        	world.getNetEventManager().addNetEvent(new NetClientEvent.ChatMessage(packet));
        }
        
        else if (object instanceof Net.CollisionPacket) {
        	Net.CollisionPacket packet = (Net.CollisionPacket)object;
        	world.getNetEventManager().addNetEvent(new NetClientEvent.ProjectileCollision(packet));
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
        	System.out.println("Client received weaponPickedUp confirmation packet");
        	Net.WeaponPickedUpPacket packet = (Net.WeaponPickedUpPacket)object;
        	handleWeaponPickedUpPacket(packet);
        }
        
        else if (object instanceof Net.WeaponRespawnPacket) {
        	System.out.println("Client received weaponRespawn packet");
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
		WeaponSpawn weaponSpawn = world.getMeshLevel().getWeaponInstances().get(packet.weaponEntityId);
		weaponSpawn.setIsRenderable(false);
		if(packet.playerId == world.getPlayer().getNetId()) {
			weaponSpawn.effect();
		} 
	}
	
	private void handleWeaponRespawnPacket(Net.WeaponRespawnPacket packet) {
		WeaponSpawn weaponSpawn = world.getMeshLevel().getWeaponInstances().get(packet.weaponEntityId);
		weaponSpawn.setIsRenderable(true);
	}
	
	public void createPlayerStatField(Net.NewPlayer packet) {
		NetStatField field = new NetStatField("", GameScreen.skin);
		field.setPlayerName(packet.name);
		field.setStats(field.getPlayerName() + "                 K: " + 0 + "                 D: " + 0);
		field.setText(field.getStats());
		field.setPlayerID(packet.id);
		screen.getStatForm().addNetStatField(field, 0, (screen.getStatForm().getWindow().getHeight() - 40f) - 
				                                       (screen.getStatForm().getStatFields().size * 20f), 300, 20);
		screen.getStatForm().getWindow().setHeight(screen.getStatForm().getWindow().getHeight() + 20f);
	}
	
	public void updateNetStats(Net.StatPacket packet) {
		for (int i = 0; i < screen.getStatForm().getStatFields().size; i++) {
			NetStatField field = screen.getStatForm().getStatFields().get(i);

			if (field.getPlayerID() == packet.playerID) {
				field.setPlayerName(packet.name);
				field.setKills(packet.kills);
				field.setDeaths(packet.deaths);
				field.setStats(field.getPlayerName() + "                 K: " + packet.kills + "                 D: " + packet.deaths);
				field.setText(field.getStats());
			}
		}
	
		Array<NetStatField> fields = new Array<NetStatField>();
		fields.addAll(sortPlayerStatFields());
		
		for (int i = 0; i < fields.size; i++) {
			screen.getStatForm().addNetStatField(fields.get(i), 0, (screen.getStatForm().getWindow().getHeight() - 40f) - 
												(screen.getStatForm().getStatFields().size * 20f), 300, 20);
		}
	}
	
	public void sendKillUpdate(int playerID) {
		Net.KillPacket packet = new Net.KillPacket();
		packet.id = playerID;
		client.sendTCP(packet);
	}
	
	public void sendDeathUpdate(int playerID) {
		Net.DeathPacket packet = new Net.DeathPacket();
		packet.id = playerID;
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
	
	public void removePlayerStatField(Net.PlayerDisconnect disconnect) {
		boolean removed = false;
		
		for (int i = 0; i < screen.getStatForm().getStatFields().size; i++) {
			NetStatField field = screen.getStatForm().getStatFields().get(i);
			
			//A players stat field was removed shift all fields down
			if (removed) {
				field.setPosition(field.getX(), field.getY() + 20f);
			}
			
			if (disconnect.id == field.getPlayerID()) {
				field.setVisible(false);
				screen.getStatForm().getStatFields().removeIndex(i);
				removed = true;
				i--;
			}
		}
	}
	
	public void removeAllStatFields() {
		for (int i = 0; i < screen.getStatForm().getStatFields().size; i++) {
			NetStatField field = screen.getStatForm().getStatFields().get(i);
			
			if (field.getPlayerID() == world.getPlayer().getNetId()) {
				field.setPlayerName(world.getPlayer().getNetName());
				field.setKills(0);
				field.setDeaths(0);
				field.setStats(field.getPlayerName() + "                 K: " + 0 + "                 D: " + 0);
				field.setText(field.getStats());
			}
			else {
				field.setVisible(false);
				screen.getStatForm().getStatFields().removeIndex(i);
			}
		}
	}
	
	public Array<NetStatField> sortPlayerStatFields() {
		Array<NetStatField> fields = new Array<NetStatField>(); 
		Sort.instance().sort(screen.getStatForm().getStatFields(), comparator);
		fields.addAll(screen.getStatForm().getStatFields());
		screen.getStatForm().getStatFields().clear();
		screen.getStatForm().getWindow().clear();
		
		return fields;
	}
	
	public void addChatMessage(Net.ChatMessagePacket packet) {
		screen.getChat().addMessage(packet);
	}
	
	public void sendChatMessage(Net.ChatMessagePacket packet) {
		client.sendTCP(packet);
	}
	
	public void sendProjectile(Projectile projectile, int id, Ray ray) {
		Net.NewProjectile packet = new Net.NewProjectile();
		packet.id = id;
		packet.position = projectile.getPosition();
		packet.rayDirection = ray.direction;
		packet.rayOrigin = ray.origin;
		packet.cameraPos = projectile.getPosition();
		packet.originID = this.getClient().getID();
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
		world.getNetEventManager().processEvents();

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
