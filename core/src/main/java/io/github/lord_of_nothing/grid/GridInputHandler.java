package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.lord_of_nothing.GameWindow;
import io.github.lord_of_nothing.buildings.Building;
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
    public GridInputHandler(OrthographicCamera camera, Grid grid, GameWindow window, ResourceManager resourceManager) {
        this.camera = camera;
        this.grid = grid;
        this.window = window;
        this.resourceManager = resourceManager;
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
     * Handles every click on the screen.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        if (handleCloseButton(touchPos.x, touchPos.y)) {return true;}
        if (handleSidebarInteraction(touchPos.x, touchPos.y)) {return true;}
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
     * <summary>Updates sidebar interaction to handle selection for buildings based on their slot coordinates.</summary>
     * @param x Touch X-coordinate. @param y Touch Y-coordinate.
     */
    private boolean handleSidebarInteraction(float x, float y) {
        if (x < GameWindow.SIDEBAR_WIDTH) {
            int sidebarHeight = Gdx.graphics.getHeight() - GameWindow.TOP_BAR_HEIGHT;

            // Slot 1: House (centered around sidebarHeight - 100)
            if (y > sidebarHeight - 110 && y < sidebarHeight - 40) {
                pendingBuilding = (pendingBuilding instanceof io.github.lord_of_nothing.buildings.House) ? null : new io.github.lord_of_nothing.buildings.House();
                return true;
            }

            // Slot 2: Sawmill (centered around sidebarHeight - 210)
            if (y > sidebarHeight - 210 && y < sidebarHeight - 140) {
                pendingBuilding = (pendingBuilding instanceof io.github.lord_of_nothing.buildings.Sawmill) ? null : new io.github.lord_of_nothing.buildings.Sawmill();
                return true;
            }
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
                pendingBuilding = null;
                return true;
            }
        }
        return false;
    }

    /**
    Checks if the player can afford the building costs.
     */
    private boolean canAfford(Building building) {
        for (java.util.Map.Entry<ResourceType, Integer> entry : building.getCosts().entrySet()) {
            if (!resourceManager.hasEnough(entry.getKey(), entry.getValue())){return true;}
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
     * <summary>Returns the building currently selected for placement.</summary>
     * @return The pending building instance or null if none is selected.
     */
    public Building getPendingBuilding() {
        return pendingBuilding;
    }
}

