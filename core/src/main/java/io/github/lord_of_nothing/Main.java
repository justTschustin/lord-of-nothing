package io.github.lord_of_nothing;

import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.grid.GridRenderer;
import io.github.lord_of_nothing.hud.InfoSidebar;
import io.github.lord_of_nothing.hud.InfoSidebarRenderer;
import io.github.lord_of_nothing.hud.Sidebar;
import io.github.lord_of_nothing.hud.SidebarRenderer;
import io.github.lord_of_nothing.hud.CloseButtonRenderer;
import io.github.lord_of_nothing.hud.TopBarRenderer;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private CloseButtonRenderer closeButtonRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Map<String, Texture> buildingTextures;

    private Grid grid;
    private GridRenderer gridRenderer;
    private GridInputHandler gridInputHandler;
    private GameWindow gameWindow;
    private ResourceManager resourceManager;
    private TopBarRenderer topBarRenderer;
    private Texture houseTexture;
    private Texture grassTexture;
    private Sidebar sidebar;
    private SidebarRenderer sidebarRenderer;
    private InfoSidebar infoSidebar;
    private InfoSidebarRenderer infoSidebarRenderer;


    /**
     * Initialisiert die Kernkomponenten, lädt Grafikressourcen und konfiguriert die Eingabeverarbeitung.
     * Stellt sicher, dass alle Render-Systeme und die Spiellogik beim Anwendungsstart bereitstehen.
     */
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        houseTexture = new Texture("buildings/House1.png");
        grassTexture = new Texture("tiles/Floor_Grass.png");

        resourceManager = new ResourceManager();
        resourceManager.add(ResourceType.WOOD, 1000);

        grid = new Grid();
        gridRenderer = new GridRenderer();
        gameWindow = new GameWindow(camera, grid);
        buildingTextures = new HashMap<>();
        buildingTextures.put("house", new Texture("buildings/House1.png"));
        buildingTextures.put("sawmill", new Texture("buildings/Sawmill.png"));
        buildingTextures.put("quarry", new Texture("buildings/Placeholder_2x2_1.png"));
        buildingTextures.put("field", new Texture("buildings/Field.png"));
        sidebar = new Sidebar();
        sidebarRenderer = new SidebarRenderer();
        infoSidebar = new InfoSidebar();
        infoSidebarRenderer = new InfoSidebarRenderer();

        topBarRenderer = new TopBarRenderer();
        closeButtonRenderer = new CloseButtonRenderer();

        gridInputHandler = new GridInputHandler(camera, grid, gameWindow, resourceManager, sidebar, infoSidebar);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(gridInputHandler);
    }

    @Override
    public void resize(int width, int height) {
        gameWindow.resize(width, height);
        gridInputHandler.updateLayout(gameWindow);
    }

    /**
     * Updates the render call to pass the generic texture map and the currently selected building object.
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        gridRenderer.render(shapeRenderer, batch, grid, gameWindow, buildingTextures, grassTexture, gridInputHandler.getPendingBuilding(), resourceManager);
        sidebarRenderer.render(shapeRenderer, batch, sidebar, buildingTextures, gridInputHandler.getPendingBuilding(), resourceManager);
        infoSidebarRenderer.render(shapeRenderer, batch, gameWindow, infoSidebar, buildingTextures, resourceManager);
        topBarRenderer.render(shapeRenderer, batch, gameWindow, resourceManager);
        closeButtonRenderer.render(shapeRenderer, gameWindow);
    }


    @Override
    public void dispose() {
        shapeRenderer.dispose();
        grassTexture.dispose();
        infoSidebarRenderer.dispose();
        topBarRenderer.dispose();
    }
}
