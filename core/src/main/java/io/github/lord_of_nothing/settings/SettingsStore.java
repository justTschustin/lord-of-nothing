package io.github.lord_of_nothing.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.nio.file.Paths;

/**
 * Loads and saves {@link GameSettings} from a local JSON file.
 */
public class SettingsStore {
    private final Json json = new Json();
    private final String filePath;

    /**
     * Creates a settings store for a local file path.
     *
     * @param filePath local file path used for settings persistence
     */
    public SettingsStore(String filePath) {
        this.filePath = filePath;
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        System.out.println(java.nio.file.Paths.get(filePath).toAbsolutePath());
    }

    /**
     * Loads settings from disk, falling back to defaults on missing or invalid data.
     *
     * @return loaded settings or defaults when load fails
     */
    public GameSettings load() {
        GameSettings defaults = createDefaultSettings();
        FileHandle file = resolveFileHandle();
        if (!file.exists()) {
            save(defaults);
            return defaults;
        }

        try {
            GameSettings loaded = json.fromJson(GameSettings.class, file.readString("UTF-8"));
            GameSettings normalized = normalize(loaded, defaults);
            save(normalized);
            return normalized;
        } catch (Exception ignored) {
            // Fall back to defaults if the settings file is malformed.
            save(defaults);
            return defaults;
        }
    }

    /**
     * Saves settings to disk as pretty-printed JSON.
     *
     * @param settings settings object to persist
     */
    public void save(GameSettings settings) {
        GameSettings normalized = normalize(settings, createDefaultSettings());
        FileHandle file = resolveFileHandle();
        FileHandle parent = file.parent();
        if (parent != null) {
            parent.mkdirs();
        }
        file.writeString(json.prettyPrint(normalized), false, "UTF-8");
    }

    private FileHandle resolveFileHandle() {
        if (Paths.get(filePath).isAbsolute()) {
            return Gdx.files.absolute(filePath);
        }
        return Gdx.files.local(filePath);
    }

    private GameSettings normalize(GameSettings loaded, GameSettings defaults) {
        if (loaded == null) {
            return defaults;
        }

        GameSettings normalized = new GameSettings();
        normalized.fullscreen = loaded.fullscreen;
        normalized.windowedWidth = loaded.windowedWidth > 0 ? loaded.windowedWidth : defaults.windowedWidth;
        normalized.windowedHeight = loaded.windowedHeight > 0 ? loaded.windowedHeight : defaults.windowedHeight;
        normalized.gameSpeed = normalizeGameSpeed(loaded.gameSpeed, defaults.gameSpeed);
        return normalized;
    }

    private GameSettings createDefaultSettings() {
        GameSettings defaults = new GameSettings();
        ResolutionDto defaultResolution = ResolutionSettings.getDefaultResolution();
        defaults.windowedWidth = defaultResolution.width;
        defaults.windowedHeight = defaultResolution.height;
        return defaults;
    }

    private int normalizeGameSpeed(int requested, int fallback) {
        if (requested >= 1) {
            return requested;
        }
        return fallback;
    }
}

