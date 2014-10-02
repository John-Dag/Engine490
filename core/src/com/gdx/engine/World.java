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
	//private Enemy enemy;
	//private Level level;
	private MeshLevel meshLevel;
	private Ray ray;
	private Array<Decal> decalInstances;
	private Array<BoundingBox> boxes;
	private float timer;
	private Vector3 position;
	private Vector3 out;
    //private TiledMapTileLayer layer = (TiledMapTileLayer)Assets.level2.getLayers().get(0);
	
	public World() {
		player = new Player(this, new Vector3(2f, 1.5f, 2f), true, BuildModel.buildBoxColorModel(1f, 1f, 1f, Color.BLUE));
		meshLevel = new MeshLevel(Assets.level2, true);
		//meshLevel.getInstances().add(player.model);
		decalInstances = new Array<Decal>();
		boxes = new Array<BoundingBox>();
		position = new Vector3();
		out = new Vector3();
	}
	
	public void update(float delta) {
		rayPick();
		player.input(delta);
		player.update(delta);
		updateEntities(delta);
		timer += delta;
	}
	
	private void updateEntities(float delta) {
		int size = meshLevel.getEntityInstances().size;
		
		for (int i = 0; i < size; i++) {
			Entity entity = meshLevel.getEntityInstances().get(i);
			
			if (entity.active) {
				entity.UpdateInstanceTransform();
			}
			
			else {
				meshLevel.getEntityInstances().removeIndex(i);
			}
		}
	}
	
	public void createBoundingBoxes() {
		int size = meshLevel.getInstances().size;
		int length = meshLevel.getEntityInstances().size;
		
		for (int i = 0; i < size; i++) {
			BoundingBox box = new BoundingBox();
			meshLevel.getInstances().get(i).calculateBoundingBox(box);
			boxes.add(box);
		}
		
		for (int j = 0; j < length; j++) {
			Entity entity = meshLevel.getEntityInstances().get(j);
			entity.model.calculateBoundingBox(entity.boundingBox).mul(entity.model.transform);
		}
	}
	
	public void setObjectDecals() {
		int size = meshLevel.getObjectInstances().size;
		
		for (int i = 0; i < size; i++) {
			Decal decal = meshLevel.getObjectInstances().get(i).decal;
			decalInstances.add(decal);
		}
	}
	
	public void rayPick() {
		if (player.mouseLeft == true && timer >= 0.1f) {
			ray = player.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			timer = 0;
			rayPickLevel();
			rayPickEntities();
			setDecals();
		}
	}
	
	public void rayPickLevel() {
		int size = meshLevel.getInstances().size;
		int result = -1;
		float distance = -1;
		
		if (ray != null) {
			for (int i = 0; i < size; i++) {
				ModelInstance model = meshLevel.getInstances().get(i);
				
				model.transform.getTranslation(position);
				position.add(boxes.get(i).getCenter());
				float dist2 = ray.origin.dst2(position);
				
				if (distance >= 0f && dist2 > distance)
					continue;
		
				if (Intersector.intersectRayBoundsFast(ray, boxes.get(i))) {
					result = i;
					distance = dist2;
					
					if (result > -1) {
						Intersector.intersectRayBounds(ray, boxes.get(i), out);
					}
				}
			}
		}
	}
	
	public void rayPickEntities() {
		int size = meshLevel.getEntityInstances().size;
		int result = -1;
		float distance = -1;
		
		if (ray != null) {
			for (int i = 0; i < size; i++) {
				Entity entity = meshLevel.getEntityInstances().get(i);
				
				entity.model.transform.getTranslation(position);
				position.add(entity.boundingBox.getCenter());
				float dist2 = ray.origin.dst2(position);
				
				if (distance >= 0f && dist2 > distance)
					continue;
		
				if (Intersector.intersectRayBoundsFast(ray, entity.boundingBox)) {
					result = i;
					distance = dist2;
					
					if (result > -1) {
						Intersector.intersectRayBounds(ray, entity.boundingBox, out);
					}
				}
			}
		}
	}
	
	public void setDecals() {
		Decal decal = Decal.newDecal(Assets.test1, true);
		decal.setPosition(out);
		decal.lookAt(player.camera.position, player.camera.position.cpy().nor());
		decal.setScale(0.001f);
		decalInstances.add(decal);
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
	
	public Array<BoundingBox> getBoundingBoxes() {
		return boxes;
	}
	
	public Array<ModelInstance> getLevelMesh() {
		return meshLevel.generateLevel();
	}
	
	public MeshLevel getMeshLevel() {
		return meshLevel;
	}
}
