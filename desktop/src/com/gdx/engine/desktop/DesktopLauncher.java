package com.gdx.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.engine.Engine;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Engine 490";
		config.width = 960;
		config.height = 540;
		//config.width = 1920;
		//config.height = 1080;
		//config.vSyncEnabled = false;
		//config.foregroundFPS = 0; 
		//config.backgroundFPS = 0;
		//config.resizable = false;
		//config.fullscreen = true;
		new LwjglApplication(new Engine(), config);
	}
}
