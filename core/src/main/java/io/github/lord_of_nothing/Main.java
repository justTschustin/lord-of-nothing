package io.github.lord_of_nothing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.grid.GridRenderer;
import io.github.lord_of_nothing.hud.TopBarRenderer;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Grid grid;
    private GridRenderer gridRenderer;
    private GridInputHandler gridInputHandler;
    private GameWindow gameWindow;
    private ResourceManager resourceManager;
    private TopBarRenderer topBarRenderer;
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();

        grid = new Grid();
        gridRenderer = new GridRenderer();
        gridInputHandler = new GridInputHandler(camera, grid);
        gameWindow = new GameWindow(camera, grid);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(gridInputHandler);
        resourceManager = new ResourceManager();
        topBarRenderer = new TopBarRenderer();

        // Startressourcen zum Testen
        resourceManager.add(ResourceType.WOOD, 100);
    }
    @Override
    public void resize(int width, int height) {
        gameWindow.resize(width, height);
        gridInputHandler.updateLayout(gameWindow);
    }

    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        gridRenderer.render(shapeRenderer, grid, gameWindow);
        topBarRenderer.render(shapeRenderer, batch, gameWindow, resourceManager);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        topBarRenderer.dispose();
    }
}
