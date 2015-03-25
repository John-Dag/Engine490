package com.gdx.UI;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gdx.Network.NetClient;
import com.gdx.Network.NetWorld;
import com.gdx.engine.World;

public class UIBase implements Screen {
	public static boolean uiSelected = false;
	private Stage stage;
	private World world;
	private Window window;

	public UIBase(Stage stage) {
		this.stage = stage;
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
	
	public void addVisibleInputListener(final int key, final int actorIndex) {
		final Stage stage = this.getStage();
		
		if (actorIndex > stage.getActors().size) {
			System.err.println("addInputListener(): Actor index value out of range.");
			return;
		}
		
		this.getStage().addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keyCode) {
				if (keyCode == key && stage.getActors().get(actorIndex).isVisible() && 
				    !stage.getActors().get(0).isVisible())
					stage.getActors().get(actorIndex).setVisible(false);
				else if (keyCode == key && !stage.getActors().get(actorIndex).isVisible() && 
						 !stage.getActors().get(0).isVisible()) {
					stage.getActors().get(actorIndex).setVisible(true);
				}
				
				return true;
			}
		});
	}
	
	public void setOpacity(float alpha) {
		if (this.getWindow() == null)
			return;
		this.getWindow().setColor(this.getWindow().getColor().r,
								  this.getWindow().getColor().g,
								  this.getWindow().getColor().b,
								  alpha);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		if (this.getWindow() == null)
			return;
		if (this.getWindow().isVisible())
			this.getWindow().setVisible(false);
		else if (!this.getWindow().isVisible() && !uiSelected)
			this.getWindow().setVisible(true);
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
		stage.dispose();
	}
	
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public void setWindowSize(int width, int height) {
		this.window.setWidth(width);
		this.window.setHeight(height);
	}
}
