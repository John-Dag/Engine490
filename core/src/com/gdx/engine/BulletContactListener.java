package com.gdx.engine;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;

public class BulletContactListener extends ContactListener {	
	public BulletContactListener() {
		super();
	}
	
	@Override
	public void onContactStarted(int userValue0, boolean match0, int userValue1, boolean match1) {
		if (userValue0 < Entity.entityInstances.size && userValue0 > -1 && userValue1 < Entity.entityInstances.size && userValue1 > -1) {
			if (match0) {
				ClientEvent.ProjectileCollision event = new ClientEvent.ProjectileCollision(userValue0, userValue1);
				World.eventManager.addEvent(event);
			}
			
			if (match1) {
				ClientEvent.ProjectileCollision event = new ClientEvent.ProjectileCollision(userValue0, userValue1);
				World.eventManager.addEvent(event);
			}
		}
	}
}
