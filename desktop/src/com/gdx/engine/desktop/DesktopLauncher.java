package com.gdx.engine.desktop;

import java.lang.Thread.UncaughtExceptionHandler;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.engine.Engine;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Engine 490";
		//config.width = 800;
		//config.height = 600;
		config.width = 1920/2;
		config.height = 1080/2;
		//config.vSyncEnabled = false;
		//config.foregroundFPS = 0; 
		//config.backgroundFPS = 0;
		config.resizable = false;
		//config.fullscreen = true;
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				System.err.println("Kryonet: Exception has occurred. Shutting down.");
				ex.printStackTrace();
				System.exit(0);
			}
		});
		
		new LwjglApplication(new Engine(), config);
	}
}
