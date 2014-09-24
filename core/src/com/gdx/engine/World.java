package com.gdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class World {
	private Player player;
	//private Level level;
	private MeshLevel meshLevel;
	private Ray ray;
	private Array<Decal> decalInstances = new Array<Decal>();
	private float timer;
	private Vector3 position = new Vector3();
	private Vector3 out = new Vector3();
	
	public World() {
		player = new Player(new Vector3(2f, 1.5f, 2f), true, BuildModel.buildBoxColorModel(1f, 1f, 1f, Color.BLUE));
		//level = new Level(Assets.level, 1f, 1f, 1f, true, Assets.floorMat, Assets.wallMat);
		//level.getInstances().add(player);
		meshLevel = new MeshLevel(Assets.level, true);
	}
	
	public Array<ModelInstance> getLevelMesh() {
		return meshLevel.generateLevel();
	}
	
	public void update(float delta) {
		rayPick();
		player.input(delta);
		player.update(delta);
		timer += delta;
	}
	
	//Temporary
	public void rayPick() {
		ray = null;
		int size = meshLevel.getInstances().size;
		int result = -1;
		float distance = -1;
		
		if (player.mouseLeft == true && timer >= 0.1f) {
			ray = player.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			timer = 0;
		}
		
		if (ray != null) {
			for (int i = 0; i < size; i++) {
				ModelInstance model = meshLevel.getInstances().get(i);
				
				model.transform.getTranslation(position);
				BoundingBox box = new BoundingBox();
				model.calculateBoundingBox(box);
				position.add(box.getCenter());
				float dist2 = ray.origin.dst2(position);
				
				if (distance >= 0f && dist2 > distance)
					continue;
		
				if (Intersector.intersectRayBoundsFast(ray, box)) {
					result = i;
					distance = dist2;
					
					if (result > -1) {
						Intersector.intersectRayBounds(ray, box, out);
						Decal test = Decal.newDecal(Assets.test1, true);
						test.setPosition(out);
						test.lookAt(player.camera.position, player.camera.position.cpy().nor());
						test.setScale(0.001f);
						decalInstances.add(test);
						//System.out.println("Fired");
						//System.out.println(result);
					}
				}
			}
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
//	public Level getLevel() {
//		return level;
//	}
	
	public Array<Decal> getDecals() {
		return decalInstances;
	}
}
