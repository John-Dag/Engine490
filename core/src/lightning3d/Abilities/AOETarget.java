package lightning3d.Abilities;

import lightning3d.DynamicEntities.Ability;
import lightning3d.Engine.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class AOETarget extends Ability {
	private Vector3 min, max;
	private Ray targetRay;
	private Vector3 modelPosition, out;
	private float maxTargetingDistance;
	private boolean outOfRange;

	public AOETarget() {
		super();
	}
	
	public AOETarget(int id, boolean isActive, boolean isRenderable, Vector3 position, float duration, float cooldown, Decal decal) {
		super(id, isActive, isRenderable, position, duration, cooldown);
		this.setTarget(new Matrix4());
		this.setDecal(decal);
		this.setTargeting(true);
		outOfRange = false;
		min = new Vector3();
		max = new Vector3();
		modelPosition = new Vector3();
		out = new Vector3();
	}
	
	@Override
	public void update(float delta, World world) {
		if (this.isTargeting()) {
			world.getPlayer().setPlayerTargeting(true);
			this.getDecal().setScale(0.01f);
			targetRay = world.getPlayer().camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			int size = world.getMeshLevel().getInstances().size;
			int result = -1;
			float distance = -1;
			
			if (targetRay != null) {
				for (int i = 0; i < size; i++) {
					ModelInstance model = world.getMeshLevel().getInstances().get(i);
					
					model.transform.getTranslation(modelPosition);
					modelPosition.add(world.getBoundingBoxes().get(i).getCenter());
					float dist2 = targetRay.origin.dst2(modelPosition);
					
					if (distance >= 0f && dist2 > distance)
						continue;
			
					if (Intersector.intersectRayBoundsFast(targetRay, world.getBoundingBoxes().get(i))) {
						result = i;
						distance = dist2;
						
						if (result > -1) {
							Intersector.intersectRayBounds(targetRay, world.getBoundingBoxes().get(i), out);
						}
					}
				}
			}
			
			if (distance <= this.getMaxTargetingDistance()) {
				outOfRange = false;
				this.getDecal().setColor(Color.GREEN);
			}
			else {
				outOfRange = true;
				this.getDecal().setColor(Color.RED);
			}
			
			this.getDecal().setPosition(out.x, out.y + 0.1f, out.z);
			
			if (world.getPlayer().isMouseLeft() && !outOfRange) {
				Gdx.input.setCursorCatched(true);
				this.setTargeting(false);
				this.getDecal().setColor(0f, 0f, 0f, 0f);
				this.initTargetAbility();
				
				Timer.schedule(new Task() {
					@Override
					public void run() {
						World.player.setPlayerTargeting(false);
					}
				}, 0.2f);
			}
		}
		
		else {
			if (!this.isRendered() && this.getParticleEffect() != null) 
				this.initializeAbilityEffect();
			
			this.getTarget().idt();
			this.getTarget().translate(out);
			this.getParticleEffect().setTransform(this.getTarget());
			this.setPosition(out);
	
			this.getBoundingBox().set(min.set(this.getPosition().x - this.getSize(), this.getPosition().y, this.getPosition().z  - this.getSize()),
					  				  max.set(this.getPosition().x + this.getSize(), this.getPosition().y + this.getSize(), this.getPosition().z + this.getSize()));
			world.checkAbilityCollision(this);
		}
	}
	
	public void initializeAbilityEffect() {
		this.setRendered(true);
		this.getParticleEffect().init();
		this.getParticleEffect().start();
		this.setBoundingBox(this.getParticleEffect().getBoundingBox());
		World.particleManager.system.add(this.getParticleEffect());
	}
	
	public float getMaxTargetingDistance() {
		return maxTargetingDistance;
	}

	public void setMaxTargetingDistance(float maxTargetingDistance) {
		this.maxTargetingDistance = maxTargetingDistance;
	}
}
