package com.boids.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.boids.Simulation;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 700;
		config.height = 700;
		config.x = (1920 - config.width)/2;
		config.y = (1080 - config.height)/2;
		config.resizable = false;
		config.undecorated = true;
		config.forceExit = true;
		config.foregroundFPS = 60;
		new LwjglApplication(new Simulation(), config);
	}
}
