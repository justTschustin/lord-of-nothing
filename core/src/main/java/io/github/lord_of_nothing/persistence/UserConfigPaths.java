package io.github.lord_of_nothing.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Resolves per-user config locations for desktop platforms.
 */
public final class UserConfigPaths {
    private static final String WINDOWS_MAC_DIR = "Lord of Nothing";
    private static final String LINUX_DIR = "lord-of-nothing";

    private UserConfigPaths() {
        // Utility class
    }

    /**
     * Resolves the base config directory for the current desktop OS.
     *
     * @return absolute path to the app config directory
     */
    public static Path resolveBaseDir() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String home = System.getProperty("user.home", ".");

        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            String base = isBlank(appData) ? home : appData;
            return Paths.get(base, WINDOWS_MAC_DIR);
        }

        if (osName.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", WINDOWS_MAC_DIR);
        }

        String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
        if (!isBlank(xdgConfigHome)) {
            return Paths.get(xdgConfigHome, LINUX_DIR);
        }
        return Paths.get(home, ".config", LINUX_DIR);
    }

    /**
     * Resolves the absolute path for settings persistence.
     *
     * @return absolute settings file path
     */
    public static String resolveSettingsPath() {
        return resolveBaseDir().resolve("settings.json").toString();
    }

    /**
     * Resolves the absolute path for game-state persistence.
     *
     * @return absolute savegame file path
     */
    public static String resolveSavePath() {
        return resolveBaseDir().resolve("savegame.json").toString();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

