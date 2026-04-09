package io.github.lord_of_nothing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.Tile;

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

    public GameWindow(OrthographicCamera camera, Grid grid) {
        this.camera = camera;
        this.grid = grid;
    }
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

    public int getTileSize() {
        return tileSize;
    }

    public int getGridPixelWidth() {
        return gridPixelWidth;
    }

    public int getGridPixelHeight() {
        return gridPixelHeight;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Delivers the Y-Position of the top bar.
     */
    public int getTopBarY() { return Gdx.graphics.getHeight() - TOP_BAR_HEIGHT; }

    /**
     * Delivers the X-Position of the close button.
     */

    public int getCloseButtonX() { return Gdx.graphics.getWidth() - CLOSE_BUTTON_SIZE - CLOSE_BUTTON_MARGIN; }

    /**
     * Delivers the Y-Position of the close button.
     */
    public int getCloseButtonY() {
        return getTopBarY() + (TOP_BAR_HEIGHT - CLOSE_BUTTON_SIZE) / 2;
    }

    /**
     * Calculates dimensions for the right info panel using relative screen percentages.
     */
    public float getRightSidebarWidth() { return Gdx.graphics.getWidth() * 0.20f; }

    /**
     Calculates the height required for the info panel to span from the screen center to the bottom of the top bar.
     * @return The calculated height preventing overlap with the top bar.
     */
    public float getInfoPanelHeight() {
        return getTopBarY() - getInfoPanelY();
    }    public float getRightSidebarX() { return Gdx.graphics.getWidth() - getRightSidebarWidth(); }

    /**
     * Calculates the horizontal start and width of the right margin area to prevent grid overlap.
     */
    public int getRightMarginX() { return offsetX + gridPixelWidth; }
    public int getRightMarginWidth() { return Gdx.graphics.getWidth() - getRightMarginX(); }

    /**
     * Calculates the info panel's vertical start position at the exact center of the screen.
     * @return The Y-coordinate representing the screen's vertical midpoint.
     */
    public float getInfoPanelY() {
        return Gdx.graphics.getHeight() / 2.0f;
    }

}
