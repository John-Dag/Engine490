package lightning3d.Weapons;

import lightning3d.DynamicEntities.Projectile;
import lightning3d.DynamicEntities.Weapon;
import lightning3d.Engine.Assets;
import lightning3d.Engine.ClientEvent;
import lightning3d.Engine.GameScreen;
import lightning3d.Engine.World;
import lightning3d.Network.Net;
import lightning3d.Network.NetClientEvent;
import lightning3d.Network.NetWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class RocketLauncher extends Weapon {
	public static final float FIRING_DELAY = 0.1f;
	public static final float PROJECTILE_SPEED = 15f;
	public static final float PROJECTILE_SCALAR = 500f;
	private final float RECOIL = 0.05f;
	public static final int DAMAGE = 25;
	private Vector3 startY = new Vector3(), camDirXZ = new Vector3(), startXZ = new Vector3(-1, 0, 0);
	
	public RocketLauncher() {
		super();
	}
	
	public RocketLauncher(boolean isParticleWeapon, int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
			  			  Vector3 scale, Vector3 velocity, Vector3 acceleration, Model model) {
		super(isParticleWeapon, id, isActive, isRenderable, position, rotation, scale, velocity, acceleration, model);
		this.firingDelay = FIRING_DELAY;
		this.projectileSpeed = PROJECTILE_SPEED;
		this.recoil = RECOIL;
		this.damage = DAMAGE;
	}
	
	@Override
	public void fireWeapon(World world) {
		try {
			if (world.getClient() != null) {
				NetClientEvent.CreatePlayerProjectile event = new NetClientEvent.CreatePlayerProjectile();
				event.position.set(world.getPlayer().camera.position.cpy());
				world.getNetEventManager().addNetEvent(event);
			}
			
			else {
				Projectile projectile = NetWorld.entityManager.projectilePool.obtain();
				projectile.reset();
				projectile.setDamage(DAMAGE);
				projectile.setPlayerProjectile(true);
				projectile.getBulletBody().setContactCallbackFilter(World.ENEMY_FLAG);
				projectile.getBulletBody().activate();
				Ray ray = world.getPlayer().camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
				projectile.getBulletBody().applyCentralImpulse(ray.direction.scl(PROJECTILE_SCALAR));
				ClientEvent.CreateEntity event = new ClientEvent.CreateEntity(projectile);
				world.getClientEventManager().addEvent(event);
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	@Override
	public Weapon spawn(Vector3 spawnPos) {
		RocketLauncher launcher = new RocketLauncher(true, 1, true, true, new Vector3(-1, 0, 0), 
		   	       new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0),
		   	       Assets.manager.get("GUNFBX.g3db", Model.class));
		BoundingBox temp = new BoundingBox();
		launcher.getModel().calculateBoundingBox(temp);
		launcher.setBoundingBox(temp);
		launcher.getModel().transform.setToTranslation(spawnPos);
		launcher.getModel().transform.scale(0.005f, 0.005f, 0.005f);
		
		return launcher;
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.isPickedup()) {
			this.getModel().transform.setToTranslation(world.getPlayer().camera.position.x, 
					   								   world.getPlayer().camera.position.y - 0.1f, 
					   								   world.getPlayer().camera.position.z);
			startY.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
			camDirXZ.set(world.getPlayer().camera.direction.x, 0, world.getPlayer().camera.direction.z);
	
			this.getModel().transform.rotate(startY, world.getPlayer().camera.direction.nor());
			this.getModel().transform.rotate(startXZ, camDirXZ.nor());
			this.getModel().transform.scale(0.005f, 0.005f, 0.005f);
		}
		
		else if (!this.isPickedup() && this.getTransformedBoundingBox().intersects(World.player.getTransformedBoundingBox()) && this.getWeapon().getWeaponType() != this.getWeaponType().rocketLauncher) {
			if (GameScreen.mode == GameScreen.Mode.Offline){
				world.getPlayer().setWeapon(this);
			} else {
//				this.setPickedup(true);
//				this.setIsRenderable(false);
				System.out.println("Send rocketObtain message to server");
				if (world.getClient() != null) {
					Net.WeaponPickedUpPacket packet = new Net.WeaponPickedUpPacket();
					packet.playerId = world.player.getNetId();
					packet.weaponEntityId = this.getUniqueId();
					NetClientEvent.WeaponPickedUp event = new NetClientEvent.WeaponPickedUp(packet);
					world.getNetEventManager().addNetEvent(event);
				}
			}
		}
		
//		else {
//			this.getModel().transform.rotate(rotationVec, 180f * delta);
//		}
	}
}

