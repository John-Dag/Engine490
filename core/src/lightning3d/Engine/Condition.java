package lightning3d.Engine;

import lightning3d.DynamicEntities.Enemy;

public abstract class Condition {
	abstract public boolean IsSatisfied(Enemy enemy);
}
