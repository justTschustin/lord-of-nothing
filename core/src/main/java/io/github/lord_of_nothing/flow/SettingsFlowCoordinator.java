package io.github.lord_of_nothing.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
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
import io.github.lord_of_nothing.settings.ResolutionDto;
import io.github.lord_of_nothing.settings.ResolutionSettings;
import io.github.lord_of_nothing.settings.SettingsStore;

/**
 * Coordinates entering, leaving, rendering, and persisting settings flow.
 */
public class SettingsFlowCoordinator {
    // Caps how many frames we wait for backend-reported size to settle after a mode switch.
    // This avoids long UI stalls while still giving fullscreen changes time to apply.
    private static final int DISPLAY_MODE_APPLY_MAX_RETRIES = 6;

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

        settingsMenu.syncDisplaySettings(gameSettings);

        eventBus.subscribe(event -> {
            if (event instanceof ResolutionChangedEvent) {
                ResolutionDto resolution = ((ResolutionChangedEvent) event).getResolution();
                if (resolution == null) {
                    return;
                }

                gameSettings.windowedWidth = resolution.width;
                gameSettings.windowedHeight = resolution.height;
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
        saveDisplaySettings();
        applyDisplaySettings();
    }

    /**
     * Persists the current display settings to storage.
     */
    public void saveDisplaySettings() {
        settingsStore.save(gameSettings);
    }

    /**
     * Apply display settings and call onApplied once the backend has applied the mode
     * and the GameWindow has been resized accordingly.
     */
    public void applyDisplaySettings() {
        applyDisplaySettings(null);
    }

    private void applyDisplaySettings(Runnable onApplied) {
        if (gameSettings.fullscreen) {
            Graphics.DisplayMode preferredMode = resolveFullscreenMode(
                gameSettings.windowedWidth,
                gameSettings.windowedHeight
            );

            if (isCurrentFullscreenMode(preferredMode)) {
                applyLayoutFromCurrentSize();
                if (onApplied != null) {onApplied.run();}
                return;
            }

            final Graphics.DisplayMode appliedMode;
            if (!Gdx.graphics.setFullscreenMode(preferredMode)) {
                // Fall back to the desktop mode if the selected mode cannot be applied.
                appliedMode = Gdx.graphics.getDisplayMode();
                if (!isCurrentFullscreenMode(appliedMode)) {
                    Gdx.graphics.setFullscreenMode(appliedMode);
                }
            } else {
                appliedMode = preferredMode;
            }

            // Wait until the backend reports the new size (may take a few frames).
            waitForDisplaySize(appliedMode.width, appliedMode.height, DISPLAY_MODE_APPLY_MAX_RETRIES, () -> {
                applyLayoutFromCurrentSize();
                if (onApplied != null) {onApplied.run();}
            });
        } else {
            ResolutionDto clampedWindowedResolution = ResolutionSettings.clampToWindowedBounds(
                gameSettings.windowedWidth,
                gameSettings.windowedHeight,
                Gdx.graphics.getDisplayMode()
            );
            boolean windowedResolutionAdjusted =
                clampedWindowedResolution.width != gameSettings.windowedWidth ||
                    clampedWindowedResolution.height != gameSettings.windowedHeight;
            gameSettings.windowedWidth = clampedWindowedResolution.width;
            gameSettings.windowedHeight = clampedWindowedResolution.height;

            if (windowedResolutionAdjusted) {
                saveDisplaySettings();
            }

            final int w = gameSettings.windowedWidth;
            final int h = gameSettings.windowedHeight;

            if (!Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() == w && Gdx.graphics.getHeight() == h) {
                applyLayoutFromCurrentSize();
                if (onApplied != null) {onApplied.run();}
                return;
            }

            Gdx.graphics.setWindowedMode(w, h);
            // Windowed mode usually takes effect immediately
            applyLayoutFromCurrentSize();
            if (onApplied != null) {onApplied.run();}
        }
    }

    /**
     * Checks if we are already in fullscreen with the requested mode dimensions.
     * This lets us skip redundant mode switches, which can cause visible stalls.
     */
    private boolean isCurrentFullscreenMode(Graphics.DisplayMode mode) {
        if (!Gdx.graphics.isFullscreen() || mode == null) {
            return false;
        }

        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        return currentMode != null && currentMode.width == mode.width && currentMode.height == mode.height;
    }

    /**
     * Applies layout updates using the size currently reported by the backend.
     * We use runtime values instead of requested values because platforms can clamp/scale dimensions.
     */
    private void applyLayoutFromCurrentSize() {
        int width = resolveLayoutWidth();
        int height = resolveLayoutHeight();
        gameWindow.resize(width, height);
        gridInputHandler.updateLayout(gameWindow);
    }

    private int resolveLayoutWidth() {
        if (gameSettings.fullscreen) {
            return Math.max(1, gameSettings.windowedWidth);
        }
        return Math.max(1, Gdx.graphics.getWidth());
    }

    private int resolveLayoutHeight() {
        if (gameSettings.fullscreen) {
            return Math.max(1, gameSettings.windowedHeight);
        }
        return Math.max(1, Gdx.graphics.getHeight());
    }

    /**
     * Resolves the fullscreen mode for a requested width/height on the current monitor.
     * We match only by resolution (width/height).
     * If no match exists, we fall back to the monitor's current desktop mode.
     */
    private Graphics.DisplayMode resolveFullscreenMode(int targetWidth, int targetHeight) {
        Graphics.Monitor monitor = Gdx.graphics.getMonitor();
        Graphics.DisplayMode[] modes = Gdx.graphics.getDisplayModes(monitor);

        Graphics.DisplayMode closest = null;
        long closestDistance = Long.MAX_VALUE;

        for (Graphics.DisplayMode mode : modes) {
            if (mode.width == targetWidth && mode.height == targetHeight) {
                return mode;
            }

            long dw = (long) mode.width - targetWidth;
            long dh = (long) mode.height - targetHeight;
            long distance = (dw * dw) + (dh * dh);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = mode;
            }
        }

        if (closest != null) {
            return closest;
        }

        return Gdx.graphics.getDisplayMode(monitor);
    }

    /**
     * Polls for the expected display size across frames using postRunnable.
     * Mode changes can apply asynchronously, so we retry briefly before continuing with current size.
     */
    private void waitForDisplaySize(int expectedW, int expectedH, int retriesLeft, Runnable onReady) {
        Gdx.app.postRunnable(() -> {
            int cw = Gdx.graphics.getWidth();
            int ch = Gdx.graphics.getHeight();
            if (cw == expectedW && ch == expectedH) {
                onReady.run();
            } else if (retriesLeft > 0) {
                // try again next frame
                waitForDisplaySize(expectedW, expectedH, retriesLeft - 1, onReady);
            } else {
                // give up and continue with currently reported window size
                onReady.run();
            }
        });
    }

    /**
     * Initializes display settings at startup by triggering the toggle mechanism.
     * This forces LibGDX to properly recalculate window dimensions by simulating user toggle actions.
     */
    public void initializeDisplaySettings(Runnable onComplete) {
        // Run the display mode application on the main/render thread in the next loop.
        // This lets the LWJGL backend finish initial window setup before we change modes
        // and ensures the subsequent resize uses the correct, final dimensions.
        Gdx.app.postRunnable(() -> {
            // Apply requested display settings (will call onComplete when finished)
            applyDisplaySettings(onComplete);
        });
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
