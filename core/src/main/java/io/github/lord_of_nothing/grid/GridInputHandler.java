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
import io.github.lord_of_nothing.hud.InfoSidebar;
import io.github.lord_of_nothing.hud.Sidebar;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

public class GridInputHandler extends InputAdapter {
    private final GameWindow window;
    private final OrthographicCamera camera;
    private final Grid grid;
    private final Vector3 touchPos = new Vector3();

    private int tileSize;
    private int offsetX;
    private int offsetY;
    private int gridPixelWidth;
    private int gridPixelHeight;
    private Building pendingBuilding = null;
    private final ResourceManager resourceManager;
    private final Sidebar sidebar;
    private final InfoSidebar infoSidebar;
    public GridInputHandler(OrthographicCamera camera, Grid grid, GameWindow window, ResourceManager resourceManager, Sidebar sidebar, InfoSidebar infoSidebar) {
        this.camera = camera;
        this.grid = grid;
        this.window = window;
        this.resourceManager = resourceManager;
        this.sidebar = sidebar;
        this.infoSidebar = infoSidebar;
    }


    public void updateLayout(GameWindow window) {
        this.tileSize = window.getTileSize();
        this.offsetX = window.getOffsetX();
        this.offsetY = window.getOffsetY();
        this.gridPixelWidth = window.getGridPixelWidth();
        this.gridPixelHeight = window.getGridPixelHeight();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
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
     * Handles touch input by distinguishing between UI interactions within the info panel and world interactions.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        // 1. Check InfoSidebar Interaction
        if (infoSidebar.isOpen()) {
            // Click INSIDE the sidebar: Handle Add/Remove buttons
            if (touchPos.x >= window.getRightMarginX()) {
                if (handleInfoSidebarButtons(touchPos.x, touchPos.y)) return true;
            }
            // Click OUTSIDE the sidebar: Close it
            else {
                infoSidebar.close();
                // Continue to check if another building was clicked
            }
        }

        if (handleCloseButton(touchPos.x, touchPos.y)) return true;
        if (handleSidebarInteraction(touchPos.x, touchPos.y)) return true;

        // 2. Check World/Grid Interaction
        int tileX = (int) ((touchPos.x - offsetX) / tileSize);
        int tileY = (int) ((touchPos.y - offsetY) / tileSize);

        if (grid.isInside(tileX, tileY) && getPendingBuilding() == null) {
            io.github.lord_of_nothing.grid.Tile tile = grid.getTile(tileX, tileY);
            if (tile.hasBuilding()) {
                infoSidebar.select(tile.getBuilding());
                return true;
            }
        }

        return handleGridPlacement(touchPos.x, touchPos.y);
    }


    /**
    Handles interaction with the close button.
     */
    private boolean handleCloseButton(float x, float y) {
        if (x >= window.getCloseButtonX() && x <= window.getCloseButtonX() + GameWindow.CLOSE_BUTTON_SIZE &&
            y >= window.getCloseButtonY() && y <= window.getCloseButtonY() + GameWindow.CLOSE_BUTTON_SIZE) {
            com.badlogic.gdx.Gdx.app.exit();
            return true;
        }
        return false;
    }

    /**
     Handles the selection logic by either deselecting the current building or instantiating a new one based on the sidebar click.
     The else block facilitates both the initial selection and the switching between different building types.
     */
    private boolean handleSidebarInteraction(float x, float y) {
        Building clicked = sidebar.getBuildingAt(x, y);
        if (clicked != null) {
            // Toggle the building selection
            if (pendingBuilding != null && pendingBuilding.getBuildingTypeKey().equals(clicked.getBuildingTypeKey())) {
                pendingBuilding = null;
            } else {
                if (clicked instanceof House) {pendingBuilding = new io.github.lord_of_nothing.buildings.House();}
                else if (clicked instanceof Sawmill) {pendingBuilding = new io.github.lord_of_nothing.buildings.Sawmill();}
                else if (clicked instanceof Quarry) {pendingBuilding = new io.github.lord_of_nothing.buildings.Quarry();}
                else if (clicked instanceof Field) {pendingBuilding = new io.github.lord_of_nothing.buildings.Field();}
            }
            {return true;}
        }
        return x < GameWindow.SIDEBAR_WIDTH;
    }

    /**
     * Processes button clicks specifically for worker assignment within the InfoSidebar bounds.
     * @param x Unprojected X coordinate. @param y Unprojected Y coordinate.
     */
    private boolean handleInfoSidebarButtons(float x, float y) {
        Building b = infoSidebar.getSelected();
        if (b.getMaxWorkers() <= 0) return false;

        // Check vertical button row
        if (y < window.getInfoPanelY() + 80 && y > window.getInfoPanelY() + 60) {
            // Add Worker: Left side of the info panel
            if (x < window.getRightMarginX() + 100) {
                if (resourceManager.getAmount(ResourceType.CITIZENS_AVAILABLE) > 0 && b.getCurrentWorkers() < b.getMaxWorkers()) {
                    b.addWorker();
                    resourceManager.add(ResourceType.CITIZENS_AVAILABLE, -1);
                }
            }
            // Remove Worker: Right side of the info panel
            else {
                if (b.getCurrentWorkers() > 0) {
                    b.removeWorker();
                    resourceManager.add(ResourceType.CITIZENS_AVAILABLE, 1);
                }
            }
            return true;
        }
        return false;
    }

    /**
    Handles interaction with the grid for placing buildings.
     */
    private boolean handleGridPlacement(float x, float y) {
        int tileX = (int) ((x - offsetX) / tileSize);
        int tileY = (int) ((y - offsetY) / tileSize);

        if (pendingBuilding != null && grid.canPlace(tileX, tileY, pendingBuilding.getWidth(), pendingBuilding.getHeight())) {
            if (canAfford(pendingBuilding)) {
                consumeCosts(pendingBuilding);
                grid.placeBuilding(tileX, tileY, pendingBuilding);
                if (pendingBuilding.getCitizenCapacity() > 0) {
                    resourceManager.add(ResourceType.CITIZENS_CAPACITY, pendingBuilding.getCitizenCapacity());
                    resourceManager.add(ResourceType.CITIZENS_TOTAL, pendingBuilding.getCitizenCapacity());
                    resourceManager.add(ResourceType.CITIZENS_AVAILABLE, pendingBuilding.getCitizenCapacity());
                }
                pendingBuilding = null;
                return true;
            }
        }
        return false;
    }
    /**
     * <summary>Validates that the player has sufficient amounts of all required resources to place the building.</summary>
     * @param building The building instance containing the cost map to be checked.
     */
    private boolean canAfford(Building building) {
        for (java.util.Map.Entry<ResourceType, Integer> entry : building.getCosts().entrySet()) {
            if (!resourceManager.hasEnough(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Subtracts the costs of the building from the ResourceManager.
     */
    private void consumeCosts(Building building) {
        for (java.util.Map.Entry<ResourceType, Integer> entry : building.getCosts().entrySet()) {
            resourceManager.tryConsume(entry.getKey(), entry.getValue());
        }
    }
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
            resourceManager.add(ResourceType.CITIZENS_CAPACITY, b.getCitizenCapacity());
            resourceManager.add(ResourceType.CITIZENS_AVAILABLE, b.getCitizenCapacity());
        }
    }
}
