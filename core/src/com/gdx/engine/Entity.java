package com.gdx.engine;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Entity {
	public Vector3 position;
	public Vector3 rotation;
	public Vector3 scale;
	public int index;
	public boolean active, collision;
	
	public Vector3 velocity;     
	public Vector3 acceleration;
	
	public Vector3 angVelocity;
	public Vector3 angAccel;
	
	public ModelInstance model;		//Model is a class, so this is will not use extra memory, unless used wrong.
	public btCollisionObject collisionShape;
	public btRigidBody rigidBody;
	public boolean isRendered;
	public int id;
	public  BoundingBox boundingBox=new BoundingBox();
	
	public Entity(Vector3 position, Vector3 rotation, Vector3 scale, boolean active, 
			      int index, boolean collision) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.active = active;
		this.index = index;
		this.collision = collision;
		
		velocity=new Vector3(0,0,0);
		acceleration=new Vector3(0,0,0);
		angAccel=new Vector3(0,0,0);
		angVelocity=new Vector3(0,0,0);
	}
	
	public Entity(Vector3 position, boolean active, int id, ModelInstance model) {
		this.model = model;
		this.position = position;
		this.active = active;
		this.id = id;
		this.isRendered = false;
	}
	
	public Entity(Vector3 position, boolean active, int id, BoundingBox boundingBox) {
		this.position = position;
		this.active = active;
		this.isRendered = false;
		this.id = id;
		this.boundingBox = boundingBox;
	}
	
	//Default constructor
	public Entity(){
		position=new Vector3(0,0,0);
		rotation=new Vector3(0,0,0);
		scale=new Vector3(1,1,1);
		velocity=new Vector3(0,0,0);
		acceleration=new Vector3(0,0,0);
		angAccel=new Vector3(0,0,0);
		angVelocity=new Vector3(0,0,0);
	}
	
	public BoundingBox getBoundingBox(){
		//TODO 
		return null;
	}
	
	public void UpdatePosition(float time)
	{
		//TODO Just an idea how to process basic physics until we start using a proper physics library.
		//Same can be done with angular velocity and acceleration
		Vector3 timeV=new Vector3(time,time,time);

		position.add(new Vector3(velocity.add(new Vector3(acceleration).scl(timeV))).scl(timeV));
		rotation.add(new Vector3(angVelocity.add(new Vector3(angAccel).scl(timeV))).scl(timeV));
	}
	
	public void UpdateAnimation()
	{
		//TODO Animate meshes if they have animation
		//This method should probably also take some kind of time elapsed parameter to know which animation frame should be set
	}
	
	public void SetPosition(Vector3 position)
	{
		this.position.set(position);
	}
	
	public void SetRotation(Vector3 rotation)
	{
		this.rotation.set(rotation);
	}
	
	public void SetScale(Vector3 scale)
	{
		this.scale.set(scale);
	}
	
	public void UpdateInstanceTransform(){
		if(model==null)
			return;
		model.transform.idt();
		model.transform.setToScaling(scale);
		
		model.transform.translate(position);
		model.transform.rotate(Vector3.X, rotation.x);

		model.transform.rotate(Vector3.Y, rotation.y);
		model.transform.rotate(Vector3.Z, rotation.z);
		
		model.calculateTransforms();
	}
	
}
