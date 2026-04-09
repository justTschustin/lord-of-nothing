package io.github.lord_of_nothing.flow;

import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.hud.TopBarRenderer;

/**
 * Coordinates state transitions for active gameplay.
 */
public class GameplayFlowCoordinator {
    private final FlowState flowState;
    private final GridInputHandler gridInputHandler;
    private final TopBarRenderer topBarRenderer;
    private final GameWindow gameWindow;
    private final EventBus eventBus;

    /**
     * Creates a coordinator for gameplay transitions.
     *
     * @param flowState mutable flow state
     * @param gridInputHandler input handler to reconfigure during transitions
     * @param topBarRenderer top bar renderer that owns gameplay UI buttons
     * @param gameWindow game window layout context
     * @param eventBus event bus used for UI registration
     */
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

    /**
     * Enters gameplay mode and enables gameplay input.
     */
    public void startGame() {
        flowState.setGameStarted(true);
        flowState.setPaused(false);
        flowState.setScreenState(ScreenState.GAMEPLAY);

        gridInputHandler.clearUiElements();
        gridInputHandler.setGameplayEnabled(true);
        topBarRenderer.registerUiElements(gameWindow, eventBus);
    }

    /**
     * Switches gameplay to the paused state.
     */
    public void pauseGame() {
        flowState.setPaused(true);
        flowState.setScreenState(ScreenState.PAUSED);
    }

    /**
     * Resumes gameplay from the paused state.
     */
    public void resumeGame() {
        flowState.setPaused(false);
        flowState.setScreenState(ScreenState.GAMEPLAY);
    }
}

