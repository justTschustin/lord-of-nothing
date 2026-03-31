package io.github.lord_of_nothing.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.GameWindow;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.lord_of_nothing.resources.ResourceManager;

import static java.awt.SystemColor.window;

public class GridRenderer {

    /**
     * <summary>Main entry point for rendering the grid, sidebar, and all placed buildings.</summary>
     * @param shapeRenderer The renderer used for geometric UI and grid shapes.
     * @param batch The sprite batch used for drawing textures.
     * @param grid The data model containing tiles and building information.
     * @param window The window context for coordinate calculations.
     * @param houseTex The primary texture used for rendering buildings.
     * @param isSelected Flag indicating if a building is currently selected in the UI.
     * @param resourceManager The manager used to validate costs for UI feedback.
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, Grid grid, GameWindow window, Texture houseTex, Texture grassTex, boolean isSelected, ResourceManager rm) {
        renderBackground(batch, grid, window, grassTex);
        renderGridShapes(shapeRenderer, grid, window);
        renderBuildings(batch, grid, window, houseTex);
        renderSidebar(shapeRenderer, batch, houseTex, isSelected, rm);
    }


    /**
     * <summary>Draws the sidebar background and the interactive building selection button.</summary>
     * @param shapeRenderer Renderer for the sidebar and selection highlight shapes.
     * @param batch SpriteBatch for drawing the building icons.
     * @param houseTex Texture to be displayed as a selectable icon.
     * @param isSelected Current selection state to determine highlight rendering.
     * @param rm Manager to check if the player can afford the building (visual feedback).
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
    /**
     * <summary>Renders the grid terrain background and the mouse hover highlight effect.</summary>
     * @param shapeRenderer Renderer for the tile shapes and hover rectangles.
     * @param grid Grid model to retrieve hovered coordinates.
     * @param window Context for tile size and screen offsets.
     */
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
    /**
     * <summary>Iterates through the grid to draw all placed buildings using the SpriteBatch.</summary>
     * @param batch SpriteBatch used to render building textures.
     * @param grid The game grid containing tile building data.
     * @param window Context providing tile size and positioning offsets.
     * @param houseTex The texture to draw for each building found on a tile.
     */
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
    /**
     * Renders the hover overlay and grid lines using the ShapeRenderer.
     * Removed the olive background rectangle to allow the terrain sprites to be visible.
     */
    private void renderGridShapes(ShapeRenderer shapeRenderer, Grid grid, GameWindow window) {
        int tileSize = window.getTileSize();
        int offsetX = window.getOffsetX();
        int offsetY = window.getOffsetY();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (grid.getHoveredX() != -1) {
            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 0.4f);
            shapeRenderer.rect(offsetX + grid.getHoveredX() * tileSize, offsetY + grid.getHoveredY() * tileSize, tileSize, tileSize);
        }
        shapeRenderer.end();
    }
}
