package com.gdx.engine;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.gdx.DynamicEntities.DynamicEntity;
import com.gdx.DynamicEntities.Projectile;

public class BulletContactListener extends ContactListener {	
	public BulletContactListener() {
		super();
	}
	
	@Override
	public boolean onContactAdded(int userValue0, int partId0, int index0, boolean match0, 
            					  int userValue1, int partId1, int index1, boolean match1) {
		if (match1) {
			if (userValue1 < Entity.entityInstances.size && Entity.entityInstances.get(userValue1) instanceof Projectile) {
				Projectile projectile = (Projectile) Entity.entityInstances.get(userValue1);
				projectile.getBulletObject().setContactCallbackFilter(0);
			}
			
			ClientEvent.ProjectileCollision event = new ClientEvent.ProjectileCollision(userValue0, userValue1);
			World.eventManager.addEvent(event);
		}

		return true;
	}
}
