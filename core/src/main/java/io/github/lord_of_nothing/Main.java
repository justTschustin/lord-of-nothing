package io.github.lord_of_nothing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Main extends ApplicationAdapter {

    // Fixed number of visible tiles
    private static final int GRID_WIDTH = 16;
    private static final int GRID_HEIGHT = 9;

    // Base tile size
    private static final int BASE_TILE_SIZE = 32;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Vector3 touchPos;

    private boolean[][] clickedTiles;

    // Actual tile size on screen = BASE_TILE_SIZE * scale
    private int tileSize;
    private int gridPixelWidth;
    private int gridPixelHeight;
    private int offsetX;
    private int offsetY;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        touchPos = new Vector3();

        clickedTiles = new boolean[GRID_WIDTH][GRID_HEIGHT];

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.input.setInputProcessor(new InputAdapter() {
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

                if (tileX >= 0 && tileX < GRID_WIDTH && tileY >= 0 && tileY < GRID_HEIGHT) {
                    clickedTiles[tileX][tileY] = !clickedTiles[tileX][tileY];
                    System.out.println("Clicked tile: (" + tileX + ", " + tileY + ")");
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();

        int scaleX = width / (GRID_WIDTH * BASE_TILE_SIZE);
        int scaleY = height / (GRID_HEIGHT * BASE_TILE_SIZE);

        int scale = Math.max(1, Math.min(scaleX, scaleY));

        tileSize = BASE_TILE_SIZE * scale;
        gridPixelWidth = GRID_WIDTH * tileSize;
        gridPixelHeight = GRID_HEIGHT * tileSize;

        offsetX = (width - gridPixelWidth) / 2;
        offsetY = (height - gridPixelHeight) / 2;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);

        // Filled clicked tiles
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (clickedTiles[x][y]) {
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.rect(
                        offsetX + x * tileSize,
                        offsetY + y * tileSize,
                        tileSize,
                        tileSize
                    );
                }
            }
        }
        shapeRenderer.end();

        // Grid lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        for (int x = 0; x <= GRID_WIDTH; x++) {
            shapeRenderer.line(
                offsetX + x * tileSize, offsetY,
                offsetX + x * tileSize, offsetY + gridPixelHeight
            );
        }

        for (int y = 0; y <= GRID_HEIGHT; y++) {
            shapeRenderer.line(
                offsetX, offsetY + y * tileSize,
                offsetX + gridPixelWidth, offsetY + y * tileSize
            );
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
