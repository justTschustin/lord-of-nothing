package io.github.lord_of_nothing.flow;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.menu.MainMenu;

public class MenuFlowCoordinator {
    private final EventBus eventBus;
    private final GridInputHandler gridInputHandler;
    private final FlowState flowState;
    private MainMenu mainMenu;

    public MenuFlowCoordinator(
        EventBus eventBus,
        GridInputHandler gridInputHandler,
        FlowState flowState,
        MainMenu mainMenu
    ) {
        this.eventBus = eventBus;
        this.gridInputHandler = gridInputHandler;
        this.flowState = flowState;
        this.mainMenu = mainMenu;
    }

    public void returnToMainMenu() {
        flowState.setGameStarted(false);
        flowState.setPaused(false);
        flowState.setScreenState(ScreenState.MAIN_MENU);

        gridInputHandler.clearUiElements();
        gridInputHandler.setGameplayEnabled(false);

        mainMenu.dispose();
        mainMenu = new MainMenu(eventBus);
    }

    public void registerUiElements() {
        mainMenu.registerUiElements(eventBus);
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        mainMenu.render(shapeRenderer, batch);
    }

    public void dispose() {
        mainMenu.dispose();
    }
}

