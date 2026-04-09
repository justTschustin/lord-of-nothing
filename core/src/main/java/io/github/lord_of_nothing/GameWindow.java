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
    private int tileSize;
    private int gridPixelWidth;
    private int gridPixelHeight;
    private int offsetX;
    private int offsetY;

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

        int availableWidth = width - (HUD_SIDE_MARGIN * 2);
        int availableHeight = height - HUD_BOTTOM_HEIGHT - HUD_TOP_HEIGHT;

        int scaleX = availableWidth / (grid.getWidth() * Tile.BASE_TILE_SIZE);
        int scaleY = availableHeight / (grid.getHeight() * Tile.BASE_TILE_SIZE);
        int scale = Math.max(1, Math.min(scaleX, scaleY));

        tileSize = Tile.BASE_TILE_SIZE * scale;
        gridPixelWidth = grid.getWidth() * tileSize;
        gridPixelHeight = grid.getHeight() * tileSize;

        offsetX = HUD_SIDE_MARGIN + (availableWidth - gridPixelWidth) / 2;
        offsetY = HUD_BOTTOM_HEIGHT + (availableHeight - gridPixelHeight) / 2;
    }

    /**
     * Returns the rendered tile size in pixels.
     *
     * @return tile size in pixels
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Returns the total grid width in pixels.
     *
     * @return grid width in pixels
     */
    public int getGridPixelWidth() {
        return gridPixelWidth;
    }

    /**
     * Returns the total grid height in pixels.
     *
     * @return grid height in pixels
     */
    public int getGridPixelHeight() {
        return gridPixelHeight;
    }

    /**
     * Returns the x offset where the grid starts.
     *
     * @return grid start x in pixels
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Returns the y offset where the grid starts.
     *
     * @return grid start y in pixels
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Returns the y coordinate where the top bar begins.
     *
     * @return top bar y coordinate
     */
    public int getTopBarY() { return Gdx.graphics.getHeight() - TOP_BAR_HEIGHT; }
}
