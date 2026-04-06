package io.github.lord_of_nothing.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SettingsStore {
    private final Json json = new Json();
    private final String localPath;

    public SettingsStore(String localPath) {
        this.localPath = localPath;
    }

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

    public void save(GameSettings settings) {
        FileHandle file = Gdx.files.local(localPath);
        file.writeString(json.prettyPrint(settings), false, "UTF-8");
    }
}

