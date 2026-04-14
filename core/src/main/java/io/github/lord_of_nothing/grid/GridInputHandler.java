package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.buildings.House;
import io.github.lord_of_nothing.buildings.Field;
import io.github.lord_of_nothing.buildings.Sawmill;
import io.github.lord_of_nothing.buildings.Quarry;
import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.PauseGameEvent;
import io.github.lord_of_nothing.events.ResumeGameEvent;
import io.github.lord_of_nothing.events.StartGameEvent;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.game.ResourceStateMutator;
import io.github.lord_of_nothing.hud.InfoSidebar;
import io.github.lord_of_nothing.hud.Sidebar;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;
import io.github.lord_of_nothing.ui.UiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles mouse input for UI clicks, sidebar selection, and building placement.
 */
public class GridInputHandler extends InputAdapter {
    private final OrthographicCamera camera;
    private final Grid grid;
    private final Vector3 touchPos = new Vector3();

    private float tileSize;
    private float offsetX;
    private float offsetY;
    private float gridPixelWidth;
    private float gridPixelHeight;
    private Building pendingBuilding = null;
    private final ResourceStateMutator resources;
    private final List<UiElement> uiElements = new ArrayList<>();
    private final Sidebar sidebar;
    private boolean paused;
    private boolean gameplayEnabled;
    private final InfoSidebar infoSidebar;
    private final GameWindow window;


    /**
     * Creates the input handler and subscribes to relevant flow/UI events.
     *
     * @param camera world camera used for unprojecting screen coordinates
     * @param grid grid model
     * @param resources resource state mutator used for cost checks
     * @param sidebar sidebar model used for template selection
     * @param eventBus event bus used to track UI creation and pause state
     * @param infoSidebar sidebar model used for displaying tile information
     */
    public GridInputHandler(
        OrthographicCamera camera,
        Grid grid,
        GameWindow window,
        Sidebar sidebar,
        ResourceStateMutator resources,
        EventBus eventBus,
        InfoSidebar infoSidebar
    ) {
        this.camera = camera;
        this.grid = grid;
        this.resources = resources;
        this.sidebar = sidebar;
        this.infoSidebar = infoSidebar;
        this.window = window;

        eventBus.subscribe(event -> {
            if (event instanceof UiElementCreatedEvent) {
                UiElement element = ((UiElementCreatedEvent) event).getElement();
                if (!uiElements.contains(element)) {
                    uiElements.add(element);
                }
            }
            if (event instanceof PauseGameEvent) {
                paused = true;
            }
            if (
                event instanceof ResumeGameEvent
                    || event instanceof StartGameEvent
                    || event instanceof BackToMainMenuEvent
            ) {
                paused = false;
            }
        });
    }


    /**
     * Updates cached layout values after window resize.
     *
     * @param window game window layout context
     */
    public void updateLayout(GameWindow window) {
        this.tileSize = window.getTileSize();
        this.offsetX = window.getOffsetX();
        this.offsetY = window.getOffsetY();
        this.gridPixelWidth = window.getGridPixelWidth();
        this.gridPixelHeight = window.getGridPixelHeight();
    }

    /**
     * Enables or disables gameplay interactions.
     *
     * @param gameplayEnabled whether gameplay interactions are enabled
     */
    public void setGameplayEnabled(boolean gameplayEnabled) {
        this.gameplayEnabled = gameplayEnabled;
    }

    /**
     * Clears tracked UI elements.
     */
    public void clearUiElements() {
        uiElements.clear();
    }

    /**
     * Updates hovered tile while the mouse moves.
     *
     * @param screenX mouse x coordinate in screen space
     * @param screenY mouse y coordinate in screen space
     * @return {@code true} when the event is handled
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (paused || !gameplayEnabled) {
            return false;
        }
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        int tileX = (int) ((touchPos.x - offsetX) / tileSize);
        int tileY = (int) ((touchPos.y - offsetY) / tileSize);

        if (touchPos.x >= offsetX && touchPos.x < offsetX + gridPixelWidth &&
            touchPos.y >= offsetY && touchPos.y < offsetY + gridPixelHeight) {
            grid.setHovered(tileX, tileY);
        } else {
            grid.setHovered(-1, -1);
        }
        return true;
    }
    /**
     * Handles clicks for UI, sidebar, and grid placement.
     *
     * @param screenX click x coordinate in screen space
     * @param screenY click y coordinate in screen space
     * @param pointer pointer index
     * @param button mouse button index
     * @return {@code true} when the click is consumed
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        // Convert Screen Coordinates to World Coordinates
        camera.unproject(touchPos);

        // 1. Check InfoSidebar Interaction
        if (infoSidebar.isOpen()) {
            // Click INSIDE the sidebar: Handle Add/Remove buttons
            if (touchPos.x >= window.getRightMarginX()) {
                if (handleInfoSidebarButtons(touchPos.x, touchPos.y)) {return true;}
            }
            // Click OUTSIDE the sidebar: Close it
            else {
                infoSidebar.close();
                // Continue to check if another building was clicked
            }
        }

        if (handleSidebarInteraction(touchPos.x, touchPos.y)) {return true;}

        // 2. Check World/Grid Interaction
        int tileX = (int) ((touchPos.x - offsetX) / tileSize);
        int tileY = (int) ((touchPos.y - offsetY) / tileSize);

        if (grid.isInside(tileX, tileY) && getPendingBuilding() == null) {
            Tile tile = grid.getTile(tileX, tileY);
            if (tile.hasBuilding()) {
                infoSidebar.select(tile.getBuilding());
                return true;
            }
        }

        return handleUiClicks(touchPos.x, touchPos.y)
            || paused
            || !gameplayEnabled
            || handleSidebarInteraction(touchPos.x, touchPos.y)
            || handleGridPlacement(touchPos.x, touchPos.y);
    }


    /**
     * Dispatches a click to the first matching UI element.
     *
     * @param x click x coordinate in world space
     * @param y click y coordinate in world space
     * @return {@code true} if a UI element handled the click
     */
    private boolean handleUiClicks(float x, float y) {
        return uiElements.stream()
            .filter(element -> element.contains(x, y))
            .findFirst()
            .map(element -> {
                element.onClick();
                return true;
            })
            .orElse(false);
    }

    /**
     * Handles building-template selection from the sidebar.
     *
     * @param x click x coordinate in world space
     * @param y click y coordinate in world space
     * @return {@code true} if the click was inside the sidebar area
     */
    private boolean handleSidebarInteraction(float x, float y) {
        Building clicked = sidebar.getBuildingAt(x, y);
        if (clicked != null) {
            // Toggle the building selection
            if (pendingBuilding != null && pendingBuilding.getBuildingTypeKey().equals(clicked.getBuildingTypeKey())) {
                pendingBuilding = null;
            } else {
                if (clicked instanceof House) {pendingBuilding = new House();}
                else if (clicked instanceof Sawmill) {pendingBuilding = new Sawmill();}
                else if (clicked instanceof Quarry) {pendingBuilding = new Quarry();}
                else if (clicked instanceof Field) {pendingBuilding = new Field();}
            }
            return true;
        }
        return x < GameWindow.SIDEBAR_WIDTH;
    }

    /**
     * Processes button clicks specifically for worker assignment within the InfoSidebar bounds.
     * @param x Unprojected X coordinate. @param y Unprojected Y coordinate.
     */
    private boolean handleInfoSidebarButtons(float x, float y) {
        Building b = infoSidebar.getSelected();
        if (b == null || b.getMaxWorkers() <= 0) {
            return false;
        }
        if (y < window.getInfoPanelY() + 80 && y > window.getInfoPanelY() + 60) {
            // Add Worker
            if (x < window.getRightMarginX() + 100) {
                // Use 'resources' (Mutator) instead of 'resourceManager'
                int available = resources.getResourceAmount(ResourceType.CITIZENS_AVAILABLE);
                if (available > 0 && b.getCurrentWorkers() < b.getMaxWorkers()) {
                    b.addWorker();
                    resources.addResource(ResourceType.CITIZENS_AVAILABLE, -1);
                }
            }
            // Remove Worker
            else {
                if (b.getCurrentWorkers() > 0) {
                    b.removeWorker();
                    resources.addResource(ResourceType.CITIZENS_AVAILABLE, 1);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Handles placement of the currently selected building on the grid.
     *
     * @param x click x coordinate in world space
     * @param y click y coordinate in world space
     * @return {@code true} if a building was placed
     */
    private boolean handleGridPlacement(float x, float y) {
        int tileX = (int) ((x - offsetX) / tileSize);
        int tileY = (int) ((y - offsetY) / tileSize);

        if (pendingBuilding != null && grid.isInside(tileX, tileY) && grid.canPlace(tileX, tileY, pendingBuilding.getWidth(), pendingBuilding.getHeight())) {
            if (canAfford(pendingBuilding)) {
                consumeCosts(pendingBuilding);
                handleBuildingEffects(pendingBuilding);
                grid.placeBuilding(tileX, tileY, pendingBuilding);
                pendingBuilding = null;
                return true;
            }
        }
        return false;
    }
    /**
     * Validates that the player has sufficient amounts of all required resources to place the building.
     *
     *  @param building The building instance containing the cost map to be checked.
     * @return {@code true} if all required resources are available
     */
    private boolean canAfford(Building building) {
        for (Map.Entry<ResourceType, Integer> entry : building.getCosts().entrySet()) {
            if (!resources.hasEnoughResources(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Subtracts building costs from the current game state.
     *
     * @param building building whose costs should be consumed
     */
    private void consumeCosts(Building building) {
        for (Map.Entry<ResourceType, Integer> entry : building.getCosts().entrySet()) {
            resources.tryConsumeResource(entry.getKey(), entry.getValue());
        }
    }
    /**
     * Returns whether any building is currently selected for placement.
     *
     * @return {@code true} if a building is pending placement
     */
    public boolean isHouseSelected() { return pendingBuilding != null; }

    /**
     * Returns the building currently selected for placement.
     * @return The pending building instance or null if none is selected.
     */
    public Building getPendingBuilding() {
        return pendingBuilding;
    }

    /**
     * Increments both capacity and available worker pool when a residential building is placed.
     */
    private void handleBuildingEffects(Building b) {
        if (b.getCitizenCapacity() > 0) {
            // Use the merged 'resources' mutator to ensure the UI and TickManager receive the update
            resources.addResource(ResourceType.CITIZENS_CAPACITY, b.getCitizenCapacity());
        }
    }
}
