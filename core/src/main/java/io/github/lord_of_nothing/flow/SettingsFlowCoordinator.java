package io.github.lord_of_nothing.flow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.hud.TopBarRenderer;
import io.github.lord_of_nothing.menu.SettingsMenu;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.settings.SettingsStore;

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

    private int windowedWidth;
    private int windowedHeight;

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
        this.windowedWidth = gameSettings.windowedWidth;
        this.windowedHeight = gameSettings.windowedHeight;
    }

    public void applySavedDisplayMode() {
        if (gameSettings.fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            return;
        }

        Gdx.graphics.setWindowedMode(gameSettings.windowedWidth, gameSettings.windowedHeight);
    }

    public void onResize(int width, int height) {
        if (!Gdx.graphics.isFullscreen()) {
            windowedWidth = width;
            windowedHeight = height;
            gameSettings.windowedWidth = width;
            gameSettings.windowedHeight = height;
        }
    }

    public void openSettingsMenu() {
        flowState.setSettingsOpenedFromPause(flowState.getScreenState() == ScreenState.PAUSED);
        flowState.setScreenState(ScreenState.SETTINGS);

        gridInputHandler.clearUiElements();
        gridInputHandler.setGameplayEnabled(false);
        settingsMenu.registerUiElements(eventBus);
    }

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

    public void toggleFullscreenMode() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(windowedWidth, windowedHeight);
        } else {
            windowedWidth = Gdx.graphics.getWidth();
            windowedHeight = Gdx.graphics.getHeight();
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        syncDisplaySettings();
        settingsStore.save(gameSettings);
    }

    public void syncDisplaySettings() {
        gameSettings.fullscreen = Gdx.graphics.isFullscreen();
        if (!gameSettings.fullscreen) {
            gameSettings.windowedWidth = windowedWidth;
            gameSettings.windowedHeight = windowedHeight;
        }
    }

    public void saveDisplaySettings() {
        syncDisplaySettings();
        settingsStore.save(gameSettings);
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        settingsMenu.render(shapeRenderer, batch);
    }

    public void dispose() {
        settingsMenu.dispose();
    }
}

