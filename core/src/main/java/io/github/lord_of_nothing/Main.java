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
import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.CloseSettingsMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.OpenSettingsMenuEvent;
import io.github.lord_of_nothing.events.PauseGameEvent;
import io.github.lord_of_nothing.events.ResumeGameEvent;
import io.github.lord_of_nothing.events.StartGameEvent;
import io.github.lord_of_nothing.events.ToggleFullscreenEvent;
import io.github.lord_of_nothing.flow.FlowState;
import io.github.lord_of_nothing.flow.GameplayFlowCoordinator;
import io.github.lord_of_nothing.flow.MenuFlowCoordinator;
import io.github.lord_of_nothing.flow.ScreenState;
import io.github.lord_of_nothing.flow.SettingsFlowCoordinator;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.grid.GridRenderer;
import io.github.lord_of_nothing.hud.Sidebar;
import io.github.lord_of_nothing.hud.SidebarRenderer;
import io.github.lord_of_nothing.hud.TopBarRenderer;
import io.github.lord_of_nothing.menu.MainMenu;
import io.github.lord_of_nothing.menu.SettingsMenu;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.settings.SettingsStore;

/**
 * Main LibGDX application entry point for core game logic and rendering.
 */
public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Map<String, Texture> buildingTextures;

    private Grid grid;
    private GridRenderer gridRenderer;
    private GridInputHandler gridInputHandler;
    private GameWindow gameWindow;
    private ResourceManager resourceManager;
    private TopBarRenderer topBarRenderer;
    private Texture grassTexture;
    private Sidebar sidebar;
    private SidebarRenderer sidebarRenderer;


    private EventBus eventBus;
    private FlowState flowState;
    private MenuFlowCoordinator menuFlowCoordinator;
    private GameplayFlowCoordinator gameplayFlowCoordinator;
    private SettingsFlowCoordinator settingsFlowCoordinator;

    /**
     * Initialisiert die Kernkomponenten, lädt Grafikressourcen und konfiguriert die Eingabeverarbeitung.
     * Stellt sicher, dass alle Render-Systeme und die Spiellogik beim Anwendungsstart bereitstehen.
     */
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        grassTexture = new Texture("tiles/Floor_Grass.png");

        resourceManager = new ResourceManager();
        resourceManager.add(ResourceType.WOOD, 1000);

        grid = new Grid();
        gridRenderer = new GridRenderer();
        gameWindow = new GameWindow(camera, grid);
        buildingTextures = new HashMap<>();
        buildingTextures.put("house", new Texture("buildings/House1.png"));
        buildingTextures.put("sawmill", new Texture("buildings/Placeholder_2x1.png"));
        buildingTextures.put("quarry", new Texture("buildings/Placeholder_2x2_1.png"));
        sidebar = new Sidebar();
        sidebarRenderer = new SidebarRenderer();

        topBarRenderer = new TopBarRenderer();
        eventBus = new EventBus();
        flowState = new FlowState();

        gridInputHandler = new GridInputHandler(camera, grid, resourceManager, sidebar, eventBus);
        gridInputHandler.setGameplayEnabled(false);

        MainMenu mainMenu = new MainMenu(eventBus);
        SettingsMenu settingsMenu = new SettingsMenu(eventBus);
        SettingsStore settingsStore = new SettingsStore("settings.json");
        GameSettings gameSettings = settingsStore.load();

        menuFlowCoordinator = new MenuFlowCoordinator(eventBus, gridInputHandler, flowState, mainMenu);
        gameplayFlowCoordinator = new GameplayFlowCoordinator(
            flowState,
            gridInputHandler,
            topBarRenderer,
            gameWindow,
            eventBus
        );
        settingsFlowCoordinator = new SettingsFlowCoordinator(
            flowState,
            gridInputHandler,
            topBarRenderer,
            gameWindow,
            settingsMenu,
            eventBus,
            settingsStore,
            gameSettings,
            () -> menuFlowCoordinator.registerUiElements()
        );

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(gridInputHandler);

        eventBus.subscribe(event -> {
            if (event instanceof StartGameEvent) {
                gameplayFlowCoordinator.startGame();
            }
            if (event instanceof BackToMainMenuEvent) {
                menuFlowCoordinator.returnToMainMenu();
            }
            if (event instanceof PauseGameEvent) {
                gameplayFlowCoordinator.pauseGame();
            }
            if (event instanceof ResumeGameEvent) {
                gameplayFlowCoordinator.resumeGame();
            }
            if (event instanceof OpenSettingsMenuEvent) {
                settingsFlowCoordinator.openSettingsMenu();
            }
            if (event instanceof CloseSettingsMenuEvent) {
                settingsFlowCoordinator.closeSettingsMenu();
            }
        });
    }

    /**
     * Updates layout-dependent systems after window resize.
     *
     * @param width new window width
     * @param height new window height
     */
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

        if (flowState.getScreenState() == ScreenState.SETTINGS) {
            settingsFlowCoordinator.render(shapeRenderer, batch);
            return;
        }

        if (!flowState.isGameStarted()) {
            menuFlowCoordinator.render(shapeRenderer, batch);
            return;
        }
        gridRenderer.render(shapeRenderer, batch, grid, gameWindow, buildingTextures, grassTexture, gridInputHandler.getPendingBuilding(), resourceManager);
        sidebarRenderer.render(shapeRenderer, batch, sidebar, buildingTextures, gridInputHandler.getPendingBuilding(), resourceManager);
        topBarRenderer.render(
            shapeRenderer,
            batch,
            gameWindow,
            resourceManager,
            eventBus,
            flowState.getScreenState() == ScreenState.PAUSED
        );
    }

    /**
     * Resumes gameplay when the application regains focus while paused.
     */
    @Override
    public void resume() {
        if (flowState.getScreenState() == ScreenState.PAUSED) {
            gameplayFlowCoordinator.resumeGame();
        }
    }

    /**
     * Saves settings and disposes rendering resources.
     */
    @Override
    public void dispose() {
        settingsFlowCoordinator.saveDisplaySettings();

        shapeRenderer.dispose();
        batch.dispose();
        grassTexture.dispose();
        topBarRenderer.dispose();
        settingsFlowCoordinator.dispose();
        menuFlowCoordinator.dispose();
    }
}
