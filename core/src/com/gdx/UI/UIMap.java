package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.gdx.engine.Assets;
import com.gdx.engine.GameScreen;
import com.gdx.engine.MeshLevel;

public class UIMap extends UIBase {
	private OrthographicCamera mapCamera;
	private SpriteBatch batch;
	private Sprite[] levelSprites;
	private float x, y;
	
	public UIMap(Stage stage, SpriteBatch batch) {
		super(stage);
		mapCamera = new OrthographicCamera(Gdx.graphics.getWidth(), 
				                           Gdx.graphics.getHeight());
		mapCamera.zoom = 1;
		this.batch = batch;
		levelSprites = new Sprite[9999];
		x = 0;
		y = 0;
	}
	
	public void generateMap(Array<ModelInstance> instances) {
		Texture texture = null;
		Material material = null;
		
		if (instances == null) {
			System.err.println("generateMap(): MeshLevel null");
		}
		

			for (int i = 0; i <= instances.size; i++) {
				ModelInstance model = instances.get(i);
				for (int j = 0; j < model.materials.size; j++) {
					material = model.materials.get(j);
				
					if (material.has(TextureAttribute.Diffuse)) {
						System.out.println("texture");
						TextureAttribute attribute = (TextureAttribute)material.get(TextureAttribute.Diffuse);
						if (attribute != null)
							texture = attribute.textureDescription.texture;
						Sprite sprite = new Sprite(texture);
						levelSprites[i] = sprite;
						i++;
						System.out.println(i);
					}
				}
				
				if (i == instances.size - 1)
					break;
			}
			
		
	}
	
	@Override
	public void render(float delta) {
		mapCamera.update();
		batch.setProjectionMatrix(mapCamera.combined);
		for (int i = 1; i < 1000; i++) {
			batch.draw(levelSprites[i], (i % 31) * 3, (i / 31) * 3, 5f, 5f);
			System.out.println("X: " + i % 32 + "Y: " + i / 32);
			i++;
		}
	}
}
