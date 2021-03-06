package lightning3d.DynamicEntities;

import lightning3d.Engine.BulletMotionState;
import lightning3d.Engine.Entity;
import lightning3d.Engine.Render;
import lightning3d.Engine.World;
import lightning3d.Shaders.EntityShader;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;

public class DynamicEntity extends Entity {
	private Weapon weapon;
	private Ability ability;
	private Vector3 position, rotation, scale, velocity, acceleration, angVelocity, angAccel;
	private ModelInstance model;
	private Decal decal;
	private ParticleEffect particleEffect;
	private boolean inCollision, isRendered, isAnimating;
	private Quaternion rotationQuaternion;
	private AnimationController animation;
	private BoundingBox boundingBox;
	private BoundingBox detectionBox;
	private int NetId, originID;
	private Vector3 newPosition = new Vector3();
	private Vector3 newVelocity = new Vector3();
	private Vector3 newRotation = new Vector3();
	private Vector3 newAngVelocity = new Vector3();
	private Vector3 movementVector = new Vector3();
	private btCollisionShape bulletShape = null;
	private btCollisionObject bulletObject;
	private btRigidBody bulletBody;
	private btRigidBodyConstructionInfo constructionInfo = null;
	private BulletMotionState motionState;
	private Matrix4 target;
	private Vector3 inertia;
	
	public DynamicEntity() {
		super(0, false, false);
		weapon = null;
		position = new Vector3(0, 0, 0);
		rotation = new Vector3(0, 0, 0);
		scale = new Vector3(0, 0, 0);
		acceleration = new Vector3(0, 0, 0);
		model = null;
	}
	
	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.boundingBox = new BoundingBox();
	}

	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position,
			 	   	     Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, ParticleEffect effect) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.particleEffect = effect;
		this.boundingBox = new BoundingBox();
	}
	
	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position,
		       	   		 Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.boundingBox = new BoundingBox();
	}

	public DynamicEntity(int id, boolean isActive, boolean isRenderable, Vector3 position,
			       		 Vector3 rotation, Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable);
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.velocity = velocity;
		this.model = model;
		this.isRendered = false;
		this.isAnimating = false;
		this.rotationQuaternion = new Quaternion();
		this.boundingBox = new BoundingBox();
		this.detectionBox = new BoundingBox();
		//this.model.calculateBoundingBox(this.boundingBox);
		this.detectionBox.set(this.boundingBox);
		this.isRendered = false;
		this.acceleration = acceleration;
		this.angAccel = new Vector3(0, 0, 0);
		this.angVelocity = new Vector3(0, 0, 0);
	}
	
	public void initializeBulletObject(Vector3 boxVector, short callBackFlag) {
		initializeBulletObject(new btBoxShape(boxVector),callBackFlag);
	}
	
	
	public void initializeBulletObject(btCollisionShape shape, short callBackFlag) {
		cleanUpBulletObjects();
		
		try {
			this.setBulletShape(shape);
			this.setBulletObject(new btCollisionObject());
			this.getBulletObject().setCollisionShape(this.getBulletShape());
			this.getBulletObject().setWorldTransform(this.getTarget().translate(this.getPosition()));
			this.getBulletObject().setContactCallbackFlag(callBackFlag);
			this.getBulletObject().setCollisionFlags(this.getBulletObject().getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void initializeBulletBody(Vector3 boxVector, float mass, short callBackFlag) {
		initializeBulletBody(new btBoxShape(boxVector),mass, callBackFlag);
	}
	
	public void initializeBulletBody(btCollisionShape shape, float mass, short callBackFlag) {
		cleanUpBulletObjects();
		
		Vector3 localInertia = new Vector3();
		
		try {
			this.setBulletShape(shape);
			this.setBulletObject(new btCollisionObject());
			this.getBulletObject().setCollisionShape(this.getBulletShape());
			this.setTarget(new Matrix4());
			this.getBulletShape().calculateLocalInertia(mass, localInertia);
			
			this.setMotionState(new BulletMotionState());
			this.getMotionState().transform = this.calculateTarget(this.getPosition());
			this.setConstructionInfo(new btRigidBody.btRigidBodyConstructionInfo(mass, null, this.getBulletShape(), localInertia));
			this.setBulletBody(new btRigidBody(this.getConstructionInfo()));
			this.getBulletBody().setMotionState(this.getMotionState());
			this.getBulletBody().setCollisionFlags(this.getBulletBody().getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			this.getBulletBody().activate();
			this.getConstructionInfo().dispose();
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private void cleanUpBulletObjects() {
		if (this.getMotionState() != null) {
			motionState.dispose();
		}
		
		if (this.getBulletBody() != null) {
			World.dynamicsWorld.removeRigidBody(this.getBulletBody());
			bulletBody.dispose();
		}
		
		if (this.getBulletObject() != null) {
			World.dynamicsWorld.removeCollisionObject(this.getBulletObject());
			bulletObject.dispose();
		}
		
		if (this.constructionInfo != null) {
			constructionInfo.dispose();
		}
		
		if (this.bulletShape != null) {
			bulletShape.dispose();
		}
		
		bulletBody = null;
		bulletObject = null;
		motionState = null;
		constructionInfo = null;
		bulletShape = null;
	}
	
	public void addBulletObject() {
		if (this.getBulletObject() == null)
			System.err.println("addBulletObject(): Bullet collision object hasn't been initialized");
		else
			World.dynamicsWorld.addCollisionObject(this.getBulletObject());
	}
	
	public void addBulletBody() {
		if (this.getBulletBody() == null)
			System.err.println("addBulletBody(): Bullet body hasn't been initialized");
		else
			World.dynamicsWorld.addRigidBody(this.getBulletBody());
	}
	
	@Override
	public void update(float delta, World world) {
		
	}
	
	@Override
	public void render(ModelBatch modelBatch, DecalBatch decalBatch, ModelBatch shadowBatch) {
		if (this.model != null) {
			shadowBatch.render(this.model);
			modelBatch.render(this.model, Render.environment);
		}
		
		if (this.decal != null) {
			decalBatch.add(this.getDecal());
		}
	}
	
	@Override
	public void initialize(World world) {
		super.initialize(world);
		
		if (this.getModel() != null) {
			world.getBoundingBoxes().add(this.getBoundingBox());
			//world.getMeshLevel().getInstances().add(this.getModel());
		}
	}

	public void updatePosition(float time)
	{
		//Vector3 timeV=new Vector3(time,time,time);

		//position.add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));
		//rotation.add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
		velocity.x+=acceleration.x*time;
		velocity.y+=acceleration.y*time;
		velocity.z+=acceleration.z*time;
		position.x+=time*velocity.x;
		position.y+=time*velocity.y;
		position.z+=time*velocity.z;
	}
	
	//Updates the animation with given time
	public void updateAnimation(float time)
	{
		if(this.isAnimating && this.animation != null)
		{
			this.animation.update(time);
		}
	}
	
	//Get new position without updating current position
	public Vector3 getNewPosition(float time)
	{
		//Vector3 timeV=new Vector3(time,time,time);
		//Vector3 newPosition=new Vector3(position).add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));
		newPosition.set(position);
		newVelocity.set(velocity);
		newVelocity.x+=acceleration.x*time;
		newVelocity.y+=acceleration.y*time;
		newVelocity.z+=acceleration.z*time;
		newPosition.x+=time*newVelocity.x;
		newPosition.y+=time*newVelocity.y;
		newPosition.z+=time*newVelocity.z;
		
		return newPosition;
	}
	
	//Get new rotation without updating current rotation
	public Vector3 getNewRotation(float time)
	{
		//Vector3 timeV=new Vector3(time,time,time);
		//Vector3 newRotation=new Vector3(rotation).add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
		newRotation.set(rotation);
		newAngVelocity.set(angVelocity);
		newAngVelocity.x+=angAccel.x*time;
		newAngVelocity.y+=angAccel.y*time;
		newAngVelocity.z+=angAccel.z*time;
		newRotation.x+=time*newAngVelocity.x;
		newRotation.y+=time*newAngVelocity.y;
		newRotation.z+=time*newAngVelocity.z;
		
		return newRotation;
	}
	
	public void updateInstanceTransform(){
		if(model==null)
			return;
		this.model.transform.idt();
		
		this.model.transform.translate(this.position);

		this.model.transform.rotate(rotationQuaternion.setEulerAngles(this.rotation.x, this.rotation.y, this.rotation.z));
		this.model.transform.scale(scale.x,scale.y,scale.z);
		this.model.calculateTransforms();
	}
	
	@Override
	public void dispose() {
		//Removes all bullet references. Important to call this dispose method before removing an entity.
		//The gc will cause stuttering if they aren't removed. 
		cleanUpBulletObjects();
	}
	
	@Override
	public void decrementBulletIndex() {
		if (this.getBulletBody() != null)
			this.bulletBody.setUserValue(this.bulletBody.getUserValue() - 1);
		else if (this.getBulletObject() != null)
			this.bulletObject.setUserValue(this.bulletObject.getUserValue() - 1);
	}
	
	public Matrix4 calculateTarget(Vector3 vector) {
		this.getTarget().idt();
		return this.getTarget().translate(vector);
	}
	
	public int getNetId() {
		return NetId;
	}

	public void setNetId(int netId) {
		NetId = netId;
	}

	public Decal getDecal() {
		return decal;
	}

	public void setDecal(Decal decal) {
		this.decal = decal;
	}
	
	public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	public BoundingBox getDetectionBox() {
		return detectionBox;
	}

	public void setDetectionBox(BoundingBox detectionBox) {
		this.detectionBox = detectionBox;
	}

	public BoundingBox getTransformedBoundingBox() {
		return new BoundingBox(this.boundingBox).mul(this.model.transform);
	}
	
	public BoundingBox getTransformedDetectionBoundingBox() {
		return new BoundingBox(this.detectionBox).mul(this.model.transform);
	}
	
	public Vector3 getMovementVector() {
		return movementVector;
	}

	public void setMovementVector(Vector3 movementVector) {
		this.movementVector = movementVector;
	}

	public Vector3 getAngVelocity() {
		return angVelocity;
	}

	public Vector3 getAngAccel() {
		return angAccel;
	}

	public void setAngVel(Vector3 angVel) {
		this.angVelocity = angVel;
	}

	public void setAngAccel(Vector3 angAccel) {
		this.angAccel = angAccel;
	}
	
	public boolean isAnimating() {
		return isAnimating;
	}

	public Quaternion getRotationQuaternion() {
		return rotationQuaternion;
	}

	public AnimationController getAnimation() {
		return animation;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setAnimating(boolean isAnimating) {
		this.isAnimating = isAnimating;
	}

	public void setRotationQuaternion(Quaternion rotationQuaternion) {
		this.rotationQuaternion = rotationQuaternion;
	}

	public void setAnimation(AnimationController animation) {
		this.animation = animation;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public boolean isRendered() {
		return isRendered;
	}

	public void setRendered(boolean isRendered) {
		this.isRendered = isRendered;
	}

	public boolean isInCollision() {
		return inCollision;
	}

	public void setInCollision(boolean inCollision) {
		this.inCollision = inCollision;
	}
	
	public ParticleEffect getParticleEffect() {
		return particleEffect;
	}

	public void setParticleEffect(ParticleEffect particleEffect) {
		this.particleEffect = particleEffect;
	}

	public Weapon getWeapon() {
		if (weapon == null)
			return null;
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		if (this.weapon == null) {
			this.weapon = weapon;
			this.weapon.setPickedup(true);
			Entity.entityInstances.add(this.weapon);
		}
		
		else if (this.getWeapon().getId() != weapon.getId()) {
			//this.weapon.setIsActive(false);
			this.weapon.setIsRenderable(false);
			this.weapon = weapon;
			this.weapon.setPickedup(true);
			Entity.entityInstances.add(this.weapon);
		}
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public Vector3 getScale() {
		return scale;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public Vector3 getAcceleration() {
		return acceleration;
	}

	public ModelInstance getModel() {
		return model;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setRotation(Vector3 rotation) {
		this.rotation = rotation;
	}

	public void setScale(Vector3 scale) {
		this.scale = scale;
	}

	public void setVelocity(Vector3 velocity) {
		this.velocity = velocity;
	}

	public void setAcceleration(Vector3 acceleration) {
		this.acceleration = acceleration;
	}

	public void setModel(ModelInstance model) {
		this.model = model;
	}

	public int getOriginID() {
		return originID;
	}

	public void setOriginID(int originID) {
		this.originID = originID;
	}

	public btCollisionShape getBulletShape() {
		return bulletShape;
	}

	public void setBulletShape(btCollisionShape entityShape) {
		this.bulletShape = entityShape;
	}

	public Matrix4 getTarget() {
		return target;
	}

	public void setTarget(Matrix4 target) {
		this.target = target;
	}

	public btCollisionObject getBulletObject() {
		return bulletObject;
	}

	public void setBulletObject(btCollisionObject bulletObject) {
		this.bulletObject = bulletObject;
	}

	public btRigidBody getBulletBody() {
		return bulletBody;
	}

	public void setBulletBody(btRigidBody bulletBody) {
		this.bulletBody = bulletBody;
	}

	public btRigidBody.btRigidBodyConstructionInfo getConstructionInfo() {
		return constructionInfo;
	}

	public void setConstructionInfo(btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
		this.constructionInfo = constructionInfo;
	}

	public Vector3 getInertia() {
		return inertia;
	}

	public void setInertia(Vector3 intertia) {
		this.inertia = intertia;
	}

	public BulletMotionState getMotionState() {
		return motionState;
	}

	public void setMotionState(BulletMotionState motionState) {
		this.motionState = motionState;
	}
	
	public void setShader(EntityShader shader) {
		super.setShader(shader);
		if (this.model != null)
		{
			this.model.userData = shader;
		}
	}
}
