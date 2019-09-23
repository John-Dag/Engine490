package com.gdx.engine.desktop;

import java.lang.Thread.UncaughtExceptionHandler;

import lightning3d.Engine.Engine;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Lightning3D Demo";
		//config.width = 800;
		//config.height = 600;
		config.width = 960;
		config.height = 540;
		config.vSyncEnabled = true;
		config.foregroundFPS = 0; 
		config.backgroundFPS = 0;
		config.resizable = false;
		config.fullscreen = false;
		
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
