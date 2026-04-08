package io.github.lord_of_nothing.flow;

import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.hud.TopBarRenderer;

public class GameplayFlowCoordinator {
    private final FlowState flowState;
    private final GridInputHandler gridInputHandler;
    private final TopBarRenderer topBarRenderer;
    private final GameWindow gameWindow;
    private final EventBus eventBus;

    public GameplayFlowCoordinator(
        FlowState flowState,
        GridInputHandler gridInputHandler,
        TopBarRenderer topBarRenderer,
        GameWindow gameWindow,
        EventBus eventBus
    ) {
        this.flowState = flowState;
        this.gridInputHandler = gridInputHandler;
        this.topBarRenderer = topBarRenderer;
        this.gameWindow = gameWindow;
        this.eventBus = eventBus;
    }

    public void startGame() {
        flowState.setGameStarted(true);
        flowState.setPaused(false);
        flowState.setScreenState(ScreenState.GAMEPLAY);

        gridInputHandler.clearUiElements();
        gridInputHandler.setGameplayEnabled(true);
        topBarRenderer.registerUiElements(gameWindow, eventBus);
    }

    public void pauseGame() {
        flowState.setPaused(true);
        flowState.setScreenState(ScreenState.PAUSED);
    }

    public void resumeGame() {
        flowState.setPaused(false);
        flowState.setScreenState(ScreenState.GAMEPLAY);
    }
}

