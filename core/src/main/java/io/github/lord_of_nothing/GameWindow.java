package io.github.lord_of_nothing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.Tile;

/**
 * Computes viewport-dependent layout values for grid and HUD placement.
 */
public class GameWindow {

    private static final int HUD_BOTTOM_HEIGHT = 120;
    private static final int HUD_TOP_HEIGHT = 40;
    private static final int HUD_SIDE_MARGIN = 20;
    public static final int TOP_BAR_HEIGHT = 40;

    public static final int SIDEBAR_WIDTH = 200;
    private final OrthographicCamera camera;
    private final Grid grid;
    public static final int CLOSE_BUTTON_SIZE = 40;
    public static final int CLOSE_BUTTON_MARGIN = 10;
    private float tileSize;
    private float gridPixelWidth;
    private float gridPixelHeight;
    private float offsetX;
    private float offsetY;

    /**
     * Creates a game window layout helper.
     *
     * @param camera camera updated during resize
     * @param grid grid used to compute pixel dimensions
     */
    public GameWindow(OrthographicCamera camera, Grid grid) {
        this.camera = camera;
        this.grid = grid;
    }

    /**
     * Recomputes tile scale and grid offsets after a window-size change.
     *
     * @param width window width in pixels
     * @param height window height in pixels
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();

        /**
         * Reserve space for rightSidebar based on current window width to ensure rightSidebar isn't cut off.
         */
        float rightSidebarWidth = width * 0.14f; // match getRightSidebarWidth behavior but use current resize width
        float availableWidth = width - SIDEBAR_WIDTH - rightSidebarWidth;
        float availableHeight = height - TOP_BAR_HEIGHT;

        /**
         * Fallback for availableWidth to prevent computational error.
         */
        if (availableWidth < Tile.BASE_TILE_SIZE) {
            availableWidth = Math.max(width - SIDEBAR_WIDTH - 20, Tile.BASE_TILE_SIZE);
        }

        float scaleX = availableWidth / (grid.getWidth() * (float) Tile.BASE_TILE_SIZE);
        float scaleY = availableHeight / (grid.getHeight() * (float) Tile.BASE_TILE_SIZE);
        float scale = Math.min(scaleX, scaleY);

        tileSize = Tile.BASE_TILE_SIZE * scale;
        gridPixelWidth = grid.getWidth() * tileSize;
        gridPixelHeight = grid.getHeight() * tileSize;

        offsetX = SIDEBAR_WIDTH;
        offsetY = availableHeight - gridPixelHeight;
    }

    /**
     * Returns the rendered tile size in pixels.
     *
     * @return tile size in pixels
     */
    public float getTileSize() {
        return tileSize;
    }

    /**
     * Returns the total grid width in pixels.
     *
     * @return grid width in pixels
     */
    public float getGridPixelWidth() {
        return gridPixelWidth;
    }

    /**
     * Returns the total grid height in pixels.
     *
     * @return grid height in pixels
     */
    public float getGridPixelHeight() {
        return gridPixelHeight;
    }

    /**
     * Returns the x offset where the grid starts.
     *
     * @return grid start x in pixels
     */
    public float getOffsetX() {
        return offsetX;
    }

    /**
     * Returns the y offset where the grid starts.
     *
     * @return grid start y in pixels
     */
    public float getOffsetY() {
        return offsetY;
    }

    /**
     * Returns the y coordinate where the top bar begins.
     *
     * @return top bar y coordinate
     */
    public int getTopBarY() { return Gdx.graphics.getHeight() - TOP_BAR_HEIGHT; }

    /**
     * Calculates dimensions for the right info panel using relative screen percentages
     */
    public float getRightSidebarWidth() { return Gdx.graphics.getWidth() * 0.20f; }

    /**
     * Delivers the X-Position of the close button.
     */
    public int getCloseButtonX() { return Gdx.graphics.getWidth() - CLOSE_BUTTON_SIZE - CLOSE_BUTTON_MARGIN; }

    /**
     * Delivers the Y-Position of the close button.
     */
    public float getInfoPanelHeight() {
        return getTopBarY() - getInfoPanelY();
    }
    public int getCloseButtonY() {
        return getTopBarY() + (TOP_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
    }

    /**
     * Calculates dimensions for the right info panel using relative screen percentages
     */
    public float getRightSidebarWidth() { return Gdx.graphics.getWidth() * 0.20f; }

    /**
     * Calculates the height required for the info panel to span from the screen center to the bottom of the top bar.
     * @return The calculated height preventing overlap with the top bar.
     */
    public float getInfoPanelHeight() {
        return getTopBarY() - getInfoPanelY();
    }

    public float getRightSidebarX() { return Gdx.graphics.getWidth() - getRightSidebarWidth(); }

    /**
     * Calculates the horizontal start and width of the right margin area to prevent grid overlap
     */
    public float getRightMarginX() { return offsetX + gridPixelWidth; }
    public float getRightMarginWidth() { return Math.max(0, Gdx.graphics.getWidth() - getRightMarginX()); }


    /**
     * Calculates the info panel's vertical start position at the exact center of the screen.
     *
     * @return The Y-coordinate representing the screen's vertical midpoint.
     */
    public float getInfoPanelY() {
        return Gdx.graphics.getHeight() / 2.0f;
    }
}