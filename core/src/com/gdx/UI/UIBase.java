package com.gdx.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class UIBase implements Screen {
	private Stage stage;
	
	public UIBase(Stage stage) {
		this.stage = stage;
	    Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
	    stage.act(delta);
	    stage.draw();
	}
	
	/***
	 * Adds a keyDown input listener to show/hide an actor
	 * @param key Specified key value
	 * @param actorNumber Index of the actor that is attached to the stage
	 */
	
	public void addInputListener(final int key, final int actorIndex) {
		final Stage stage = this.getStage();
		if (actorIndex > stage.getActors().size) {
			System.err.println("addInputListener(): Actor index value out of range.");
			return;
		}
		
		this.getStage().addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keyCode) {
				if (keyCode == key && stage.getActors().get(actorIndex).isVisible() && !stage.getActors().get(0).isVisible())
					stage.getActors().get(actorIndex).setVisible(false);
				else if (keyCode == key && !stage.getActors().get(actorIndex).isVisible() && !stage.getActors().get(0).isVisible()) {
					stage.getActors().get(actorIndex).setVisible(true);
				}
				
				return true;
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
