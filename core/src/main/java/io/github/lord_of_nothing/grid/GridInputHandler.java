package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.lord_of_nothing.GameWindow;

public class GridInputHandler extends InputAdapter {

    private final OrthographicCamera camera;
    private final Grid grid;
    private final Vector3 touchPos = new Vector3();

    private int tileSize;
    private int offsetX;
    private int offsetY;
    private int gridPixelWidth;
    private int gridPixelHeight;

    public GridInputHandler(OrthographicCamera camera, Grid grid) {
        this.camera = camera;
        this.grid = grid;
    }

    public void updateLayout(GameWindow window) {
        this.tileSize = window.getTileSize();
        this.offsetX = window.getOffsetX();
        this.offsetY = window.getOffsetY();
        this.gridPixelWidth = window.getGridPixelWidth();
        this.gridPixelHeight = window.getGridPixelHeight();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        float worldX = touchPos.x;
        float worldY = touchPos.y;

        if (worldX < offsetX || worldX >= offsetX + gridPixelWidth ||
            worldY < offsetY || worldY >= offsetY + gridPixelHeight) {
            return false;
        }

        int tileX = (int) ((worldX - offsetX) / tileSize);
        int tileY = (int) ((worldY - offsetY) / tileSize);

        grid.toggleTile(tileX, tileY);
        return true;
    }
}
