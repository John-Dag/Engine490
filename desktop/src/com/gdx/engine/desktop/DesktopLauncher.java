package com.gdx.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.engine.Engine;
import com.gdx.engine.MeshCreatorTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Engine(), config);
		//new LwjglApplication(new MeshCreatorTest(), config);
	}
}
