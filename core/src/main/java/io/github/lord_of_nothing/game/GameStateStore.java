package io.github.lord_of_nothing.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.nio.file.Paths;

/**
 * Loads and saves {@link GameState} snapshots from a local JSON file.
 */
public class GameStateStore {
    private final Json json = new Json();
    private final String filePath;

    /**
     * Creates a game-state store for a local file path.
     *
     * @param filePath local file path used for game-state persistence
     */
    public GameStateStore(String filePath) {
        this.filePath = filePath;
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
    }

    /**
     * Returns whether a save file currently exists.
     *
     * @return {@code true} when the configured save file exists
     */
    public boolean exists() {
        return resolveFileHandle().exists();
    }

    /**
     * Saves a snapshot to disk as pretty-printed JSON.
     *
     * @param state snapshot to persist
     */
    public void save(GameState state) {
        if (state == null) {
            return;
        }

        FileHandle file = resolveFileHandle();
        FileHandle parent = file.parent();
        if (parent != null) {
            parent.mkdirs();
        }
        file.writeString(json.prettyPrint(state), false, "UTF-8");
    }

    /**
     * Loads and deserializes a snapshot from disk.
     *
     * @return loaded snapshot or {@code null} when missing/invalid
     */
    public GameState load() {
        FileHandle file = resolveFileHandle();
        if (!file.exists()) {
            return null;
        }

        String raw = file.readString("UTF-8");

        try {
            GameState state = json.fromJson(GameState.class, raw);
            return normalizeLoadedState(state, false);
        } catch (Exception ignored) {
            try {
                // Backward compatibility for earlier saves that serialized resources as EnumMap.
                String migrated = raw.replaceAll(
                    "(?m)^\\s*\\\"class\\\"\\s*:\\s*\\\"java\\.util\\.EnumMap\\\"\\s*,\\s*\\n?",
                    ""
                );
                GameState state = json.fromJson(GameState.class, migrated);
                return normalizeLoadedState(state, true);
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
    }

    private GameState normalizeLoadedState(GameState state, boolean alwaysRewrite) {
        if (state == null) {
            return null;
        }

        // Re-run through setter normalization to coerce String keys to ResourceType keys.
        state.setResources(state.getResources());

        if (alwaysRewrite) {
            save(state);
        }
        return state;
    }

    private FileHandle resolveFileHandle() {
        if (Paths.get(filePath).isAbsolute()) {
            return Gdx.files.absolute(filePath);
        }
        return Gdx.files.local(filePath);
    }
}

