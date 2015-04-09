package com.gdx.engine;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.gdx.UI.UIBase;

public class WorldInputProcessor implements InputProcessor {
	private World world;
	
	public WorldInputProcessor(World world) {
		this.world = world;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (!UIBase.uiSelected) {
			switch (keycode) {
				case Keys.W:
					world.getPlayer().setMoveForward(true);
					break;
				case Keys.S:
					world.getPlayer().setMoveBackward(true);
					break;
				case Keys.A:
					world.getPlayer().setStrafeLeft(true);
					break;
				case Keys.D:
					world.getPlayer().setStrafeRight(true);
					break;
				case Keys.SPACE:
					world.getPlayer().setJump(true);
					break;
				case Keys.SHIFT_LEFT:
					world.getPlayer().setCrouch(true);
					break;
				case Keys.NUM_1:
					world.getPlayer().activateAbility1();
					break;
				case Keys.NUM_2:
					world.getPlayer().activateAbility2();
					break;
				case Keys.O:
					world.getPlayer().unstickPlayer();
					break;
				case Keys.P:
					world.getPlayer().exit();
					break;
				case Keys.ESCAPE:
					world.getPlayer().catchCursor();
					break;
			}
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Keys.W:
				world.getPlayer().setMoveForward(false);
				break;
			case Keys.S:
				world.getPlayer().setMoveBackward(false);
				break;
			case Keys.A:
				world.getPlayer().setStrafeLeft(false);
				break;
			case Keys.D:
				world.getPlayer().setStrafeRight(false);
				break;
			case Keys.SPACE:
				world.getPlayer().setJump(false);
				break;
			case Keys.SHIFT_LEFT:
				world.getPlayer().setCrouch(false);
				world.getPlayer().stopCrouching();
				break;
		}
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//		if(button == Input.Buttons.RIGHT) {
//			world.getPlayer().catchCursor();
//			return true;
//		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
