package io.github.lord_of_nothing.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Loads and saves {@link GameSettings} from a local JSON file.
 */
public class SettingsStore {
    private final Json json = new Json();
    private final String localPath;

    /**
     * Creates a settings store for a local file path.
     *
     * @param localPath local file path used for settings persistence
     */
    public SettingsStore(String localPath) {
        this.localPath = localPath;
    }

    /**
     * Loads settings from disk, falling back to defaults on missing or invalid data.
     *
     * @return loaded settings or defaults when load fails
     */
    public GameSettings load() {
        FileHandle file = Gdx.files.local(localPath);
        if (!file.exists()) {
            return new GameSettings();
        }

        try {
            GameSettings loaded = json.fromJson(GameSettings.class, file.readString("UTF-8"));
            return loaded == null ? new GameSettings() : loaded;
        } catch (Exception ignored) {
            // Fall back to defaults if the settings file is malformed.
            return new GameSettings();
        }
    }

    /**
     * Saves settings to disk as pretty-printed JSON.
     *
     * @param settings settings object to persist
     */
    public void save(GameSettings settings) {
        FileHandle file = Gdx.files.local(localPath);
        file.writeString(json.toJson(settings), false, "UTF-8");
    }
}

