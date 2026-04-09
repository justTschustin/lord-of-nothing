package io.github.lord_of_nothing.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.ResolutionChangedEvent;
import io.github.lord_of_nothing.events.ToggleFullscreenEvent;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.hud.TopBarRenderer;
import io.github.lord_of_nothing.menu.SettingsMenu;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.settings.ResolutionSettings;
import io.github.lord_of_nothing.settings.SettingsStore;

import java.util.Map;

/**
 * Coordinates entering, leaving, rendering, and persisting settings flow.
 */
public class SettingsFlowCoordinator {
    private final FlowState flowState;
    private final GridInputHandler gridInputHandler;
    private final TopBarRenderer topBarRenderer;
    private final GameWindow gameWindow;
    private final SettingsMenu settingsMenu;
    private final EventBus eventBus;
    private final SettingsStore settingsStore;
    private final GameSettings gameSettings;
    private final Runnable registerMainMenuUiElements;

    /**
     * Creates a settings-flow coordinator.
     *
     * @param flowState mutable flow state
     * @param gridInputHandler input handler to reconfigure during transitions
     * @param topBarRenderer top bar renderer used when returning to paused gameplay
     * @param gameWindow game window layout context
     * @param settingsMenu settings menu renderer
     * @param eventBus event bus used for UI registration
     * @param settingsStore persistence for display settings
     * @param gameSettings loaded settings model
     * @param registerMainMenuUiElements callback to rebuild main-menu UI elements
     */
    public SettingsFlowCoordinator(
        FlowState flowState,
        GridInputHandler gridInputHandler,
        TopBarRenderer topBarRenderer,
        GameWindow gameWindow,
        SettingsMenu settingsMenu,
        EventBus eventBus,
        SettingsStore settingsStore,
        GameSettings gameSettings,
        Runnable registerMainMenuUiElements
    ) {
        this.flowState = flowState;
        this.gridInputHandler = gridInputHandler;
        this.topBarRenderer = topBarRenderer;
        this.gameWindow = gameWindow;
        this.settingsMenu = settingsMenu;
        this.eventBus = eventBus;
        this.settingsStore = settingsStore;
        this.gameSettings = gameSettings;
        this.registerMainMenuUiElements = registerMainMenuUiElements;

        applyDisplaySettings();
        settingsMenu.syncDisplaySettings(gameSettings);

        eventBus.subscribe(event -> {
            if (event instanceof ResolutionChangedEvent) {
                Map.Entry<Integer, Integer> resolution = ResolutionSettings.getResolutionFromWidth(Integer.parseInt(((ResolutionChangedEvent) event).getResolution()));
                gameSettings.windowedWidth = resolution.getKey();
                gameSettings.windowedHeight = resolution.getValue();
                saveDisplaySettings();
                applyDisplaySettings();
            }
            if (event instanceof ToggleFullscreenEvent) {
                toggleFullscreenMode();
            }
        });
    }

    /**
     * Opens the settings screen from either menu or pause flow.
     */
    public void openSettingsMenu() {
        flowState.setSettingsOpenedFromPause(flowState.getScreenState() == ScreenState.PAUSED);
        flowState.setScreenState(ScreenState.SETTINGS);

        gridInputHandler.clearUiElements();
        gridInputHandler.setGameplayEnabled(false);
        settingsMenu.syncDisplaySettings(gameSettings);
        settingsMenu.registerUiElements(eventBus);
    }

    /**
     * Closes settings and returns to the previous flow context.
     */
    public void closeSettingsMenu() {
        boolean reopenPaused = flowState.isSettingsOpenedFromPause();

        flowState.setScreenState(reopenPaused ? ScreenState.PAUSED : ScreenState.MAIN_MENU);
        flowState.setGameStarted(reopenPaused);
        flowState.setPaused(reopenPaused);
        gridInputHandler.clearUiElements();

        if (reopenPaused) {
            gridInputHandler.setGameplayEnabled(true);
            topBarRenderer.registerUiElements(gameWindow, eventBus);
            topBarRenderer.invalidatePauseOverlayUiElements();
            return;
        }

        gridInputHandler.setGameplayEnabled(false);
        registerMainMenuUiElements.run();
    }

    /**
     * Toggles between fullscreen and windowed display modes and persists settings.
     */
    public void toggleFullscreenMode() {
        gameSettings.fullscreen = !gameSettings.fullscreen;
        applyDisplaySettings();
    }

    /**
     * Persists the current display settings to storage.
     */
    public void saveDisplaySettings() {
        settingsStore.save(gameSettings);
    }

    public void applyDisplaySettings() {
        if (gameSettings.fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(gameSettings.windowedWidth, gameSettings.windowedHeight);
        }
    }

    /**
     * Renders the settings menu.
     *
     * @param shapeRenderer shape renderer used for background
     * @param batch sprite batch used for text and buttons
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        settingsMenu.render(shapeRenderer, batch);
    }

    /**
     * Disposes settings-menu resources.
     */
    public void dispose() {
        settingsMenu.dispose();
    }
}

