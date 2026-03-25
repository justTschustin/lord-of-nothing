package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GridRenderer {

    public void render(ShapeRenderer shapeRenderer,
                       Grid grid,
                       int tileSize,
                       int offsetX,
                       int offsetY,
                       int gridPixelWidth,
                       int gridPixelHeight) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile != null && tile.isClicked()) {
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        for (int x = 0; x <= grid.getWidth(); x++) {
            shapeRenderer.line(
                offsetX + x * tileSize, offsetY,
                offsetX + x * tileSize, offsetY + gridPixelHeight
            );
        }

        for (int y = 0; y <= grid.getHeight(); y++) {
            shapeRenderer.line(
                offsetX, offsetY + y * tileSize,
                offsetX + gridPixelWidth, offsetY + y * tileSize
            );
        }

        shapeRenderer.end();
    }
}
