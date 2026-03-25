package io.github.lord_of_nothing;

import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.Tile;

public class GameWindow {

    private final OrthographicCamera camera;
    private final Grid grid;

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

        int scaleX = width / (grid.getWidth() * Tile.BASE_TILE_SIZE);
        int scaleY = height / (grid.getHeight() * Tile.BASE_TILE_SIZE);
        int scale = Math.max(1, Math.min(scaleX, scaleY));

        tileSize = Tile.BASE_TILE_SIZE * scale;
        gridPixelWidth = grid.getWidth() * tileSize;
        gridPixelHeight = grid.getHeight() * tileSize;

        offsetX = (width - gridPixelWidth) / 2;
        offsetY = (height - gridPixelHeight) / 2;
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
}
