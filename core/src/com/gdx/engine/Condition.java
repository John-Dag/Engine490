package com.gdx.engine;

import com.gdx.DynamicEntities.Enemy;

public abstract class Condition {
	abstract public boolean IsSatisfied(Enemy enemy);
}
