package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.Gdx;
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        if (touchPos.x >= Gdx.graphics.getWidth() - 50 && touchPos.y >= Gdx.graphics.getHeight() - 50) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }
}
