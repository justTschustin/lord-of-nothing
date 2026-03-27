package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static java.awt.SystemColor.window;

public class GridRenderer {

    /**
     * Visualisiert den aktuellen Zustand des Grids, inklusive Hover-Effekt, Gebäuden und UI-Elementen.
     * Kombiniert ShapeRendering für Geometrie und SpriteBatch für Texturen in einer koordinierten Render-Sequenz.
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Grid grid, GameWindow window, Texture houseTex) {
        int tileSize = window.getTileSize();
        int offsetX = window.getOffsetX();
        int offsetY = window.getOffsetY();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                shapeRenderer.setColor(Color.OLIVE);
                shapeRenderer.rect(offsetX + x * tileSize, offsetY + y * tileSize, tileSize, tileSize);

                if (x == grid.getHoveredX() && y == grid.getHoveredY()) {
                    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.4f);
                    shapeRenderer.rect(offsetX + x * tileSize, offsetY + y * tileSize, tileSize, tileSize);
                }
            }
        }
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 50, 40, 40);
        shapeRenderer.end();

        // Linien zeichnen (optional für Grid-Optik)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 0.2f);
        for (int x = 0; x <= grid.getWidth(); x++)
            shapeRenderer.line(offsetX + x * tileSize, offsetY, offsetX + x * tileSize, offsetY + window.getGridPixelHeight());
        for (int y = 0; y <= grid.getHeight(); y++)
            shapeRenderer.line(offsetX, offsetY + y * tileSize, offsetX + window.getGridPixelWidth(), offsetY + y * tileSize);
        shapeRenderer.end();

        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile.hasBuilding()) {
                    batch.draw(houseTex, offsetX + x * tileSize, offsetY + y * tileSize, tileSize, tileSize);
                }
            }
        }
        batch.end();
    }
}
