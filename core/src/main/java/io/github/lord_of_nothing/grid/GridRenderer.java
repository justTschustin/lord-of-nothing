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
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Grid grid, GameWindow window, Texture houseTex, Texture grassTex, boolean isSelected, io.github.lord_of_nothing.resources.ResourceManager rm) {
        renderBackground(batch, grid, window, grassTex);
        renderSidebar(shapeRenderer, batch, houseTex, isSelected, rm);
        renderGrid(shapeRenderer, grid, window);
        renderBuildings(batch, grid, window, houseTex);
    }


    /**
     * Zeichnet die Sidebar so, dass sie unterhalb der Topbar endet.
     * Verwendet die TOP_BAR_HEIGHT zur dynamischen Berechnung der verbleibenden vertikalen Fläche.
     * Muss noch aufgeteilt werden.
     */
    private void renderSidebar(ShapeRenderer shapeRenderer, SpriteBatch batch, Texture houseTex, boolean isSelected, io.github.lord_of_nothing.resources.ResourceManager rm) {
        int sidebarHeight = com.badlogic.gdx.Gdx.graphics.getHeight() - io.github.lord_of_nothing.GameWindow.TOP_BAR_HEIGHT;

        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, io.github.lord_of_nothing.GameWindow.SIDEBAR_WIDTH, sidebarHeight);
        if (isSelected) {
            shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.GOLD);
            shapeRenderer.rect(10, sidebarHeight - 90, 60, 60);
        }
        shapeRenderer.end();

        boolean canAfford = rm.hasEnough(io.github.lord_of_nothing.resources.ResourceType.WOOD, 10);

        batch.begin();
        if (!canAfford) {
            batch.setColor(com.badlogic.gdx.graphics.Color.RED);
        }
        batch.draw(houseTex, 20, sidebarHeight - 80, 40, 40);
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        batch.end();
    }

    private void renderGrid(ShapeRenderer shapeRenderer, Grid grid, GameWindow window) {
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
        shapeRenderer.end();
    }

    private void renderBuildings(SpriteBatch batch, Grid grid, GameWindow window, Texture houseTex) {
        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                if (grid.getTile(x, y).hasBuilding()) {
                    batch.draw(houseTex, window.getOffsetX() + x * window.getTileSize(),
                        window.getOffsetY() + y * window.getTileSize(),
                        window.getTileSize(), window.getTileSize());
                }
            }
        }
        batch.end();
    }

    /**
     * Paints the background of the grid using sprites.
     * Iterates over each tile in the grid and draws a texture according to its position.
     * Currently uses a single grass texture for all tiles.
     */
    private void renderBackground(SpriteBatch batch, Grid grid, GameWindow window, Texture grassTex) {
        batch.begin();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
//add getTile(x, y).getType() when further terrain types are added
                batch.draw(grassTex,
                    window.getOffsetX() + x * window.getTileSize(),
                    window.getOffsetY() + y * window.getTileSize(),
                    window.getTileSize(), window.getTileSize());
            }
        }
        batch.end();
    }
}
