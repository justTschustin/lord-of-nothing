package io.github.lord_of_nothing.lwjgl3;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.Json;
import io.github.lord_of_nothing.Main;
import io.github.lord_of_nothing.persistence.UserConfigPaths;
import io.github.lord_of_nothing.settings.ResolutionDto;
import io.github.lord_of_nothing.settings.ResolutionSettings;
import io.github.lord_of_nothing.settings.GameSettings;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static final Json json = new Json();
    private static final String settingsFilePath = UserConfigPaths.resolveSettingsPath();

    /**
     * Desktop JVM entry point.
     *
     * @param args startup arguments
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    /**
     * Creates and starts the LWJGL3 application instance.
     *
     * @return created application instance
     */
    private static void createApplication() {
        new Lwjgl3Application(new Main(), getApplicationConfig());
    }

    private static Lwjgl3ApplicationConfiguration getApplicationConfig() {
        Lwjgl3ApplicationConfiguration config = getDefaultConfiguration();
        Path settingsPath = Paths.get(settingsFilePath);
        if (!Files.exists(settingsPath)) {
            return config;
        }

        try {
            String settingsJson = new String(Files.readAllBytes(settingsPath), StandardCharsets.UTF_8);
            GameSettings loaded = json.fromJson(GameSettings.class, settingsJson);

            try {
                ResolutionDto windowedResolution = ResolutionSettings.clampToWindowedBounds(
                    loaded.windowedWidth,
                    loaded.windowedHeight,
                    Lwjgl3ApplicationConfiguration.getDisplayMode()
                );
                config.setWindowedMode(windowedResolution.width, windowedResolution.height);
            } catch (Exception ignored) {
                // if settings file is missing or the windowedWidth or windowedHeight is missing,
                // this throws and we ignore it as we just use the default values in that case
            }

            if (loaded.fullscreen) {
                config.setFullscreenMode(resolveDisplayMode(loaded.windowedWidth, loaded.windowedHeight));
            }
        } catch (Exception ignored) {
            // Fall back to defaults if the settings file is malformed.
        }
        return config;
    }

    /**
     * Builds the default desktop window configuration.
     *
     * @return configured LWJGL3 application configuration
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Lord of Nothing");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);

        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        //// This could improve compatibility with Windows machines with buggy OpenGL drivers, Macs
        //// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
        //// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
        //// You would need to add this line to lwjgl3/build.gradle , below the dependency on `gdx-backend-lwjgl3`:
        ////     implementation "com.badlogicgames.gdx:gdx-lwjgl3-angle:$gdxVersion"
        //// You can choose to add the following line and the mentioned dependency if you want; they
        //// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
        //// Know that it might not work well in some cases.
//        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        configuration.setForegroundFPS(30);
        // Keep fullscreen Alt-Tab behavior working on Windows by iconifying on focus loss.
        configuration.setAutoIconify(true);

        // Neutral default: start windowed using the current display's safe size unless persisted settings request fullscreen.
        Graphics.DisplayMode desktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        int defaultWidth = desktopMode == null ? 1080 : desktopMode.width;
        int defaultHeight = desktopMode == null ? 720 : desktopMode.height;
        ResolutionDto startupResolution = ResolutionSettings.clampToWindowedBounds(
            defaultWidth,
            defaultHeight,
            desktopMode
        );
        configuration.setWindowedMode(startupResolution.width, startupResolution.height);

        configuration.setWindowSizeLimits(1080, 720, 9999, 9999);
        configuration.setResizable(false);

        configuration.setPauseWhenMinimized(true);
        configuration.setPauseWhenLostFocus(false);


        return configuration;
    }

    private static Graphics.DisplayMode resolveDisplayMode(int width, int height) {
        Graphics.DisplayMode[] modes = Lwjgl3ApplicationConfiguration.getDisplayModes();
        Graphics.DisplayMode closest = null;
        long closestDistance = Long.MAX_VALUE;

        if (modes != null) {
            for (Graphics.DisplayMode mode : modes) {
                if (mode.width == width && mode.height == height) {
                    return mode;
                }

                long dw = (long) mode.width - width;
                long dh = (long) mode.height - height;
                long distance = (dw * dw) + (dh * dh);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = mode;
                }
            }
        }
        if (closest != null) {
            return closest;
        }
        return Lwjgl3ApplicationConfiguration.getDisplayMode();
    }
}
