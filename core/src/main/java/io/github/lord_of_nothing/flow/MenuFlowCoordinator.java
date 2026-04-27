package io.github.lord_of_nothing.flow;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.menu.MainMenu;

import java.util.function.BooleanSupplier;

/**
 * Coordinates transitions and rendering for the main-menu flow.
 */
public class MenuFlowCoordinator {
    private final EventBus eventBus;
    private final GridInputHandler gridInputHandler;
    private final FlowState flowState;
    private final BooleanSupplier hasSaveFile;
    private MainMenu mainMenu;

    /**
     * Creates a coordinator for main-menu behavior.
     *
     * @param eventBus event bus used by menu elements
     * @param gridInputHandler input handler to reconfigure during transitions
     * @param flowState mutable flow state
     * @param mainMenu menu view instance
     * @param hasSaveFile callback that reports whether a save file exists
     */
    public MenuFlowCoordinator(
        EventBus eventBus,
        GridInputHandler gridInputHandler,
        FlowState flowState,
        MainMenu mainMenu,
        BooleanSupplier hasSaveFile
    ) {
        this.eventBus = eventBus;
        this.gridInputHandler = gridInputHandler;
        this.flowState = flowState;
        this.mainMenu = mainMenu;
        this.hasSaveFile = hasSaveFile;
    }

    /**
     * Resets flow state and input handling back to the main menu.
     */
    public void returnToMainMenu() {
        flowState.setGameStarted(false);
        flowState.setPaused(false);
        flowState.setScreenState(ScreenState.MAIN_MENU);

        gridInputHandler.clearUiElements();
        gridInputHandler.setGameplayEnabled(false);

        mainMenu.dispose();
        mainMenu = new MainMenu(eventBus, hasSaveFile.getAsBoolean());
    }

    /**
     * Registers the menu's interactive UI elements.
     */
    public void registerUiElements() {
        mainMenu.registerUiElements(eventBus);
    }

    /**
     * Renders the main menu.
     *
     * @param shapeRenderer shape renderer used for background
     * @param batch sprite batch used for text and buttons
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        mainMenu.render(shapeRenderer, batch);
    }

    /**
     * Disposes menu-owned resources.
     */
    public void dispose() {
        mainMenu.dispose();
    }
}

