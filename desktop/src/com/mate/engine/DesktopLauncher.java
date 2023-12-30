package com.mate.engine;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mate.engine.MateEngine;

public class DesktopLauncher {
    public static String GLOBAL_VERSION = "0.1.0";

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setMaximized(true);
        config.setIdleFPS(10);
        config.setForegroundFPS(75);
        config.useVsync(false);
        config.setTitle("Mate-" + GLOBAL_VERSION);
        //config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 8);
        new Lwjgl3Application(new MateEngine(), config);
    }
}
