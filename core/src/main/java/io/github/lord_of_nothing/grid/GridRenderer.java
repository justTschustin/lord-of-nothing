package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;

public class GridRenderer {

    public void render(ShapeRenderer shapeRenderer, Grid grid, GameWindow window) {
        int tileSize = window.getTileSize();
        int offsetX = window.getOffsetX();
        int offsetY = window.getOffsetY();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                shapeRenderer.setColor(Color.OLIVE);
                shapeRenderer.rect(offsetX + x * tileSize, offsetY + y * tileSize, tileSize, tileSize);

                // Hover Effekt
                if (x == grid.getHoveredX() && y == grid.getHoveredY()) {
                    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.4f);
                    shapeRenderer.rect(offsetX + x * tileSize, offsetY + y * tileSize, tileSize, tileSize);
                }
            }
        }

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 50, 40, 40);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.end();
    }
}
