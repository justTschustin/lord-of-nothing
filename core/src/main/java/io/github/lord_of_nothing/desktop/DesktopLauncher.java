package io.github.lord_of_nothing.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.lord_of_nothing.Main;
/*LAUNCHKLASSE
Launchklasse ist hier im Package unter Main, da Main sich um die Initialisierung der Spiellogik kümmert und
DesktopLauncher um das öffnen des Fensters. Ist wohl ein Standart in libGDX.
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
