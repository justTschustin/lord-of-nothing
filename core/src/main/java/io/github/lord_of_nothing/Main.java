package io.github.lord_of_nothing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lord_of_nothing.events.BackToMainMenuEvent;
import io.github.lord_of_nothing.events.CloseSettingsMenuEvent;
import io.github.lord_of_nothing.events.EventBus;
import io.github.lord_of_nothing.events.GameSpeedChangedEvent;
import io.github.lord_of_nothing.events.LoadGameEvent;
import io.github.lord_of_nothing.events.NewGameEvent;
import io.github.lord_of_nothing.events.OpenSettingsMenuEvent;
import io.github.lord_of_nothing.events.PauseGameEvent;
import io.github.lord_of_nothing.events.ResumeGameEvent;
import io.github.lord_of_nothing.events.StartGameEvent;
import io.github.lord_of_nothing.flow.FlowState;
import io.github.lord_of_nothing.flow.GameplayFlowCoordinator;
import io.github.lord_of_nothing.flow.MenuFlowCoordinator;
import io.github.lord_of_nothing.flow.ScreenState;
import io.github.lord_of_nothing.flow.SettingsFlowCoordinator;
import io.github.lord_of_nothing.game.GameState;
import io.github.lord_of_nothing.game.GameStateHandler;
import io.github.lord_of_nothing.game.GameStateStore;
import io.github.lord_of_nothing.game.TickHandler;
import io.github.lord_of_nothing.grid.GridInputHandler;
import io.github.lord_of_nothing.grid.GridRenderer;
import io.github.lord_of_nothing.hud.Sidebar;
import io.github.lord_of_nothing.hud.SidebarRenderer;
import io.github.lord_of_nothing.hud.TopBarRenderer;
import io.github.lord_of_nothing.hud.TileInspectorBar;
import io.github.lord_of_nothing.hud.TileInspectorRenderer;
import io.github.lord_of_nothing.menu.MainMenu;
import io.github.lord_of_nothing.menu.SettingsMenu;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.settings.ResolutionSettings;
import io.github.lord_of_nothing.settings.SettingsStore;

/**
 * Main LibGDX application entry point for core game logic and rendering.
 */
public class Main extends ApplicationAdapter {
    private static final String SAVE_FILE_PATH = "../config/savegame.json";

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Map<String, Texture> buildingTextures;

    private GameStateHandler gameStateHandler;
    private GridRenderer gridRenderer;
    private GridInputHandler gridInputHandler;
    private GameWindow gameWindow;
    private TopBarRenderer topBarRenderer;
    private Texture grassTexture;
    private Sidebar sidebar;
    private SidebarRenderer sidebarRenderer;
    private TileInspectorBar tileInspectorBar;
    private TileInspectorRenderer tileInspectorRenderer;

    private EventBus eventBus;
    private FlowState flowState;
    private TickHandler tickHandler;
    private MenuFlowCoordinator menuFlowCoordinator;
    private GameplayFlowCoordinator gameplayFlowCoordinator;
    private SettingsFlowCoordinator settingsFlowCoordinator;
    private GameSettings gameSettings;
    private SettingsStore settingsStore;
    private GameStateStore gameStateStore;
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingSaveTasks = new AtomicInteger(0);
    private volatile boolean savingInProgress;

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

        gameStateHandler = new GameStateHandler();
        gameStateStore = new GameStateStore(SAVE_FILE_PATH);
        gameStateHandler.resetNewGame();

        gridRenderer = new GridRenderer();
        gameWindow = new GameWindow(camera, gameStateHandler.getCurrentGrid());
        buildingTextures = new HashMap<>();
        buildingTextures.put("house", new Texture("buildings/House.png"));
        buildingTextures.put("sawmill", new Texture("buildings/Sawmill.png"));
        buildingTextures.put("quarry", new Texture("buildings/Quarry.png"));
        buildingTextures.put("field", new Texture("buildings/Field.png"));
        buildingTextures.put("barrack", new Texture("buildings/Placeholder_2x2_1.png"));
        sidebar = new Sidebar();
        sidebarRenderer = new SidebarRenderer();
        tileInspectorBar = new TileInspectorBar();
        tileInspectorRenderer = new TileInspectorRenderer();

        topBarRenderer = new TopBarRenderer();
        eventBus = new EventBus();
        flowState = new FlowState();
        tickHandler = new TickHandler();

        gridInputHandler = new GridInputHandler(
            camera,
            gameStateHandler.getCurrentGrid(),
            gameWindow,
            sidebar,
            gameStateHandler,
            eventBus,
            tileInspectorBar
        );
        gridInputHandler.setGameplayEnabled(false);

        // Build available resolutions before constructing settings UI dropdown options.
        Graphics.Monitor currentMonitor = Gdx.graphics.getMonitor();
        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes(currentMonitor);
        ResolutionSettings.initialize(displayModes);

        MainMenu mainMenu = new MainMenu(eventBus, gameStateStore.exists());
        SettingsMenu settingsMenu = new SettingsMenu(eventBus);
        settingsStore = new SettingsStore("../config/settings.json");
        gameSettings = settingsStore.load();
        tickHandler.setGameSpeed(gameSettings.gameSpeed);

        menuFlowCoordinator = new MenuFlowCoordinator(
            eventBus,
            gridInputHandler,
            flowState,
            mainMenu,
            () -> gameStateStore.exists()
        );
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

        // Ensure persisted fullscreen/windowed choice is applied after core systems are wired.
        settingsFlowCoordinator.initializeDisplaySettings(() -> {});

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(gridInputHandler);

        eventBus.subscribe(event -> {
            if (event instanceof StartGameEvent || event instanceof NewGameEvent) {
                startNewGame();
            }
            if (event instanceof LoadGameEvent) {
                loadExistingGame();
            }
            if (event instanceof GameSpeedChangedEvent) {
                int speed = ((GameSpeedChangedEvent) event).getGameSpeed();
                tickHandler.setGameSpeed(speed);
                gameSettings.gameSpeed = tickHandler.getGameSpeed();
                settingsStore.save(gameSettings);
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
        gameWindow.resize(Math.max(1, width), Math.max(1, height));
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

        if (flowState.getScreenState() == ScreenState.GAMEPLAY) {
            int completedDays = tickHandler.update(
                Gdx.graphics.getDeltaTime(),
                gameStateHandler.getCurrentIngameDay(),
                gameStateHandler
            );
            for (int i = 0; i < completedDays; i++) {
                gameStateHandler.advanceIngameDay();
                triggerAutoSave();
            }
        }

        gridRenderer.render(
            shapeRenderer,
            batch,
            gameStateHandler.getCurrentGrid(),
            gameWindow,
            buildingTextures,
            grassTexture,
            gridInputHandler.getPendingBuilding()
        );
        sidebarRenderer.render(
            shapeRenderer,
            batch,
            sidebar,
            buildingTextures,
            gridInputHandler.getPendingBuilding(),
            gameStateHandler
        );
        topBarRenderer.render(
            shapeRenderer,
            batch,
            gameWindow,
            gameStateHandler,
            gameStateHandler.getCurrentIngameDay(),
            tickHandler.getCurrentIngameHour(),
            eventBus,
            flowState.getScreenState() == ScreenState.PAUSED,
            savingInProgress
        );
        tileInspectorRenderer.render(
            shapeRenderer, batch, gameWindow, tileInspectorBar, buildingTextures, eventBus,
            gameStateHandler,
            () -> gridInputHandler.deleteSelectedBuilding()
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
        saveExecutor.shutdownNow();
        shapeRenderer.dispose();
        batch.dispose();
        grassTexture.dispose();
        topBarRenderer.dispose();
        settingsFlowCoordinator.dispose();
        menuFlowCoordinator.dispose();
    }

    private void startNewGame() {
        gameStateHandler.resetNewGame();
        gameplayFlowCoordinator.startGame();
    }

    private void loadExistingGame() {
        if (!gameStateStore.exists()) {
            return;
        }

        GameState loadedState = gameStateStore.load();
        if (!gameStateHandler.applyState(loadedState)) {
            return;
        }

        gameplayFlowCoordinator.startGame();
    }

    private void triggerAutoSave() {
        final GameState snapshot = gameStateHandler.getSnapshot();
        pendingSaveTasks.incrementAndGet();
        savingInProgress = true;

        saveExecutor.submit(() -> {
            try {
                gameStateStore.save(snapshot);
            } finally {
                if (pendingSaveTasks.decrementAndGet() <= 0) {
                    pendingSaveTasks.set(0);
                    savingInProgress = false;
                }
            }
        });
    }
}
