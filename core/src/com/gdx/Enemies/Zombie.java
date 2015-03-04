package com.gdx.Enemies;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gdx.DynamicEntities.Enemy;
import com.gdx.Shaders.ColorMultiplierEntityShader;
import com.gdx.engine.Assets;

public class Zombie extends Enemy {
	public static final int MAX_HEALTH = 100;
	public static final int DAMAGE = 10;
	
	public Zombie() {
		super();
	}
	
	public Zombie(int id, boolean isActive, boolean isRenderable, Vector3 position, Vector3 rotation,
				 Vector3 scale, Vector3 velocity, Vector3 acceleration, ModelInstance model) {
		super(id, isActive, isRenderable, position, rotation,
			  scale, velocity, acceleration, model);
		this.setHealth(MAX_HEALTH);
		this.setDamage(DAMAGE);
		BoundingBox box = new BoundingBox();
		box.set(new Vector3(this.getPosition().x - 1, this.getPosition().y - 1, this.getPosition().z - 1),
			    new Vector3(this.getPosition().x + 1, this.getPosition().y + 1, this.getPosition().z + 1));
		this.getBoundingBox().set(box);
	}
	
	@Override
	public Enemy spawn() {
		Zombie zombie = new Zombie(9, false, true, spawnPos, new Vector3(0, 0, 0), 
					  	new Vector3(0.8f, 0.8f, 0.8f), new Vector3(0, 0, 0), new Vector3(0, 0, 0), 
					  	new ModelInstance(Assets.manager.get("zombie_fast.g3db", Model.class)));
		zombie.initializeEnemy();
		
		ColorMultiplierEntityShader es=new ColorMultiplierEntityShader();
		es.multiplier.y=(float)Math.random();
		es.multiplier.x=(float)Math.random();
		es.multiplier.z=(float)Math.random();
		zombie.setShader(es);
		return zombie;
	}
	
	@Override
	public void takeDamage(int damage) {
		// TODO Auto-generated method stub
		super.takeDamage(damage);
		
		if(this.shader instanceof ColorMultiplierEntityShader&&this.getHealth()<=10)
		{
			ColorMultiplierEntityShader multiplier=(ColorMultiplierEntityShader)shader;
			multiplier.multiplier.set(1,0,0);
		}
	}
}
