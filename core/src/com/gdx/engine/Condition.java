package com.gdx.engine;

import com.gdx.DynamicEntities.Enemy;
import com.gdx.DynamicEntities.Player;

public abstract class Condition {
	abstract public boolean IsSatisfied(Enemy enemy);
}
