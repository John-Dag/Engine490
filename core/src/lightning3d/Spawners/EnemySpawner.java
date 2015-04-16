package lightning3d.Spawners;

import lightning3d.DynamicEntities.Enemy;
import lightning3d.Engine.ClientEvent;
import lightning3d.Engine.World;
import lightning3d.StaticEntities.StaticEntity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class EnemySpawner extends StaticEntity {
	Enemy enemyRef = new Enemy();
	
	public EnemySpawner() {
		super();
	}
	
	public EnemySpawner(Vector3 position, int id, boolean isActive, boolean isRenderable, boolean isDecalFacing, float initialSpawnTime, float delayedSpawnTime, Enemy enemy) {
		super(position, id, isActive, isRenderable, isDecalFacing);	
		enemyRef = (Enemy)enemy;
		
		Timer.schedule(new Task() {
			@Override
			public void run() {
				if (!enemyRef.isActive()) {
					enemyRef = (Enemy)enemyRef.spawn();
					//World.enemyInstances.add(enemyRef);
					ClientEvent.CreateEntity event = new ClientEvent.CreateEntity(enemyRef);
					World.eventManager.addEvent(event);
				}
			}
		}, initialSpawnTime, delayedSpawnTime);
	}
}