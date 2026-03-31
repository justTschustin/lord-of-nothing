package io.github.lord_of_nothing.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.lord_of_nothing.Main;

/**
 * LAUNCHCLASS:
 * This class is responsible for launching the desktop application.
 */
public class DesktopLauncher {
    public static void main(String[] arg) {
        new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Lord of Nothing");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

        return configuration;
    }
}
