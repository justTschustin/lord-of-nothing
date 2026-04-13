package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.buildings.House;
import io.github.lord_of_nothing.buildings.Quarry;
import io.github.lord_of_nothing.buildings.Sawmill;
import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.PauseGameEvent;
import io.github.lord_of_nothing.events.ResumeGameEvent;
import io.github.lord_of_nothing.events.StartGameEvent;
import io.github.lord_of_nothing.events.UiElementCreatedEvent;
import io.github.lord_of_nothing.game.ResourceStateMutator;
import io.github.lord_of_nothing.hud.TileInspectorBar;
import io.github.lord_of_nothing.hud.Sidebar;
import io.github.lord_of_nothing.hud.TileInspectorRenderer;
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
    private final TileInspectorBar tileInspectorBar;
    private final TileInspectorRenderer tileInspectorRenderer;

    /**
     * Creates the input handler and subscribes to relevant flow/UI events.
     *
     * @param camera world camera used for unprojecting screen coordinates
     * @param grid grid model
     * @param resources resource state mutator used for cost checks
     * @param sidebar sidebar model used for template selection
     * @param eventBus event bus used to track UI creation and pause state
     * @param tileInspectorBar inspect panel state
     * @param tileInspectorRenderer inspect panel renderer, used to forward clicks to the delete building button
     */
    public GridInputHandler(
        OrthographicCamera camera,
        Grid grid,
        ResourceStateMutator resources,
        Sidebar sidebar,
        EventBus eventBus,
        TileInspectorBar tileInspectorBar,
        TileInspectorRenderer tileInspectorRenderer
    ) {
        this.camera = camera;
        this.grid = grid;
        this.resources = resources;
        this.sidebar = sidebar;
        this.tileInspectorBar = tileInspectorBar;
        this.tileInspectorRenderer = tileInspectorRenderer;

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

        // Close panel if clicking anywhere left of the right margin start
        if (tileInspectorBar.isOpen() && touchPos.x < offsetX + gridPixelWidth) {
            tileInspectorBar.close();
        }

        // If inspector is open, forward click to delete button first
        // If button consumed, stop here; don't process as a grid click
        if (tileInspectorBar.isOpen()) {
            if (tileInspectorRenderer.handleInput(touchPos.x, touchPos.y)) { return true; }
        }

        if (handleSidebarInteraction(touchPos.x, touchPos.y)) { return true; }

        int tileX = (int) ((touchPos.x - offsetX) / tileSize);
        int tileY = (int) ((touchPos.y - offsetY) / tileSize);

        // Open panel if a building is clicked and no new building is being placed
        if (grid.isInside(tileX, tileY) && getPendingBuilding() == null) {
            Tile tile = grid.getTile(tileX, tileY);
            if (tile.hasBuilding()) {
                Building b = tile.getBuilding();
                tileInspectorBar.select(b, b.getAnchorX(), b.getAnchorY());
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
                if (clicked instanceof House) { pendingBuilding = new House(); }
                else if (clicked instanceof Sawmill) { pendingBuilding = new Sawmill(); }
                else if (clicked instanceof Quarry) { pendingBuilding = new Quarry(); }
            }
            return true;
        }
        return x < GameWindow.SIDEBAR_WIDTH;
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

        if (pendingBuilding != null && grid.canPlace(tileX, tileY, pendingBuilding.getWidth(), pendingBuilding.getHeight())) {
            if (canAfford(pendingBuilding)) {
                consumeCosts(pendingBuilding);
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
     * Demolishes the currently inspected building;
     * Removes it from the grid, refunds 50% of its costs, and closes the panel.
     */
    public void deleteSelectedBuilding() {
        if (!tileInspectorBar.isOpen()) return;

        Building b = tileInspectorBar.getSelected();
        int x      = tileInspectorBar.getSelectedGridX();
        int y      = tileInspectorBar.getSelectedGridY();

        grid.removeBuilding(x, y);

        for (Map.Entry<ResourceType, Integer> entry : b.getCosts().entrySet()) {
            resources.addResource(entry.getKey(), entry.getValue() / 2);
        }

        tileInspectorBar.close();
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
    public Building getPendingBuilding() { return pendingBuilding; }
}
