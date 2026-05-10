package io.github.lord_of_nothing;

import java.util.ArrayList;
import java.util.List;
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
import io.github.lord_of_nothing.hud.GameOverOverlay;
import io.github.lord_of_nothing.hud.RaidBanner;
import io.github.lord_of_nothing.hud.TileInspectorBar;
import io.github.lord_of_nothing.hud.TileInspectorRenderer;
import io.github.lord_of_nothing.menu.MainMenu;
import io.github.lord_of_nothing.menu.SettingsMenu;
import io.github.lord_of_nothing.persistence.UserConfigPaths;
import io.github.lord_of_nothing.settings.GameSettings;
import io.github.lord_of_nothing.settings.ResolutionSettings;
import io.github.lord_of_nothing.settings.SettingsStore;

/**
 * Main LibGDX application entry point for core game logic and rendering.
 */
public class Main extends ApplicationAdapter {
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
    private RaidBanner raidBanner;
    private GameOverOverlay gameOverOverlay;

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
    private volatile boolean autoSaveEnabled = true;
    private boolean timeProgressionPaused;

    private float gameOverCountdown = -1f; // -1 = not scheduled
    private static final float NO_COUNTDOWN = -1f;

    /**
     * Initializes the core components, loads graphics resources, and configures input processing
     * Ensures that all rendering systems and game logic are ready when the application starts
     */
    @Override
    public void create() {
        final String saveFilePath = UserConfigPaths.resolveSavePath();
        final String settingsFilePath = UserConfigPaths.resolveSettingsPath();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        grassTexture = new Texture("tiles/Floor_Grass.png");

        gameStateHandler = new GameStateHandler();
        gameStateStore = new GameStateStore(saveFilePath);
        gameStateHandler.resetNewGame();

        gridRenderer = new GridRenderer();
        gameWindow = new GameWindow(camera, gameStateHandler.getCurrentGrid());
        buildingTextures = new HashMap<>();
        buildingTextures.put("house", new Texture("buildings/House.png"));
        buildingTextures.put("sawmill", new Texture("buildings/Sawmill.png"));
        buildingTextures.put("quarry", new Texture("buildings/Quarry.png"));
        buildingTextures.put("field", new Texture("buildings/Field.png"));
        buildingTextures.put("barrack", new Texture("buildings/Barracks.png"));
        sidebar = new Sidebar();
        sidebarRenderer = new SidebarRenderer();
        tileInspectorBar = new TileInspectorBar();
        tileInspectorRenderer = new TileInspectorRenderer();
        raidBanner = new RaidBanner();

        topBarRenderer = new TopBarRenderer();
        eventBus = new EventBus();
        gameOverOverlay = new GameOverOverlay(eventBus);
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
        gridInputHandler.setRaidBanner(raidBanner);
        gridInputHandler.setGameplayEnabled(false);

        // Build available resolutions before constructing settings UI dropdown options.
        Graphics.Monitor currentMonitor = Gdx.graphics.getMonitor();
        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes(currentMonitor);
        ResolutionSettings.initialize(displayModes);

        MainMenu mainMenu = new MainMenu(eventBus, gameStateStore.exists());
        SettingsMenu settingsMenu = new SettingsMenu(eventBus);
        settingsStore = new SettingsStore(settingsFilePath);
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

        // Ensure persistent fullscreen/windowed state
        settingsFlowCoordinator.initializeDisplaySettings(() -> {});

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
                if (speed == 0) {
                    timeProgressionPaused = true;
                } else {
                    tickHandler.setGameSpeed(speed);
                    timeProgressionPaused = false;
                    gameSettings.gameSpeed = tickHandler.getGameSpeed();
                    settingsStore.save(gameSettings);
                }
            }
            if (event instanceof BackToMainMenuEvent) {
                gameOverOverlay.hide();
                gridInputHandler.setExclusiveUiElement(null);
                gameStateHandler.resetRaidTimeline();
                menuFlowCoordinator.returnToMainMenu();
                timeProgressionPaused = false;
                autoSaveEnabled = true;
                gameOverCountdown = NO_COUNTDOWN;
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
     * Main render loop. Draws the active screen each frame
     * Handles simulation ticks, raid sequencing, and game-over scheduling
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

        if (flowState.getScreenState() == ScreenState.GAMEPLAY && !timeProgressionPaused) {
            int completedDays = tickHandler.update(
                Gdx.graphics.getDeltaTime(),
                gameStateHandler.getCurrentIngameDay(),
                gameStateHandler
            );
            boolean raidDefeatDetected = tickHandler.consumePendingRaidDefeat();
            for (int i = 0; i < completedDays; i++) {
                gameStateHandler.advanceIngameDay();
                if (!raidDefeatDetected) {
                    triggerAutoSave();
                }
            }

            // Collect all raid messages emitted this day
            List<String> raidMessages = new ArrayList<>();

            // Tick game-over countdown if scheduled
            if (gameOverCountdown > NO_COUNTDOWN) {
                gameOverCountdown -= Gdx.graphics.getDeltaTime();
                if (gameOverCountdown <= 0f) {
                    gameOverCountdown = NO_COUNTDOWN;
                    enterGameOver();
                }
            }

            String raidMsg;
            while ((raidMsg = tickHandler.pollNextRaidPopupMessage()) != null) {
                raidMessages.add(raidMsg);
            }

            // Show messages as banners in sequence.
            // If two messages arrived (attack + outcome), chain them:
            // the outcome banner shows automatically when the attack banner finishes.
            if (!raidMessages.isEmpty() && !raidBanner.isActive()) {
                String firstMsg = raidMessages.get(0);
                boolean isAttack = firstMsg.contains("Bandits are upon us");
                RaidBanner.BannerType firstType = isAttack
                    ? RaidBanner.BannerType.ATTACK
                    : RaidBanner.BannerType.WARNING;

                if (raidMessages.size() > 1) {
                    final String outcomeMsg = raidMessages.get(1);

                    if (raidDefeatDetected) {
                        // Defeat: show outcome text, then trigger game-over after it fades
                        raidBanner.show(firstType, firstMsg, () -> {
                            raidBanner.showFloatingText(outcomeMsg);
                            scheduleGameOver(RaidBanner.FLOATING_DURATION + 0.2f); // Delay game-over by floating text duration + 1 second
                        });
                    } else {
                        // Victory: just show the outcome floating text
                        raidBanner.show(firstType, firstMsg, () ->
                            raidBanner.showFloatingText(outcomeMsg));
                    }

                } else {
                    if (raidDefeatDetected) {
                        raidBanner.show(firstType, firstMsg, () ->
                            scheduleGameOver(1f));
                    } else {
                        raidBanner.show(firstType, firstMsg, () -> {});
                    }
                }
            } else if (raidDefeatDetected && !raidBanner.isActive()) {
                enterGameOver(); // Edge case: defeat with no messages queued
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
            tickHandler.getGameSpeed(),
            timeProgressionPaused,
            savingInProgress,
            flowState.getScreenState() == ScreenState.GAME_OVER
        );
        tileInspectorRenderer.render(
            shapeRenderer, batch, gameWindow, tileInspectorBar, buildingTextures, eventBus,
            gameStateHandler,
            () -> gridInputHandler.deleteSelectedBuilding()
        );


        if (flowState.getScreenState() == ScreenState.GAME_OVER) {
            gameOverOverlay.render(shapeRenderer, batch);
        }

        // Raid banner renders on top of everything, every frame
        raidBanner.update(Gdx.graphics.getDeltaTime());
        if (raidBanner.isActive() || raidBanner.isFloatingTextActive()) {
            raidBanner.render(batch);
        }

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
        raidBanner.dispose();
        gameOverOverlay.dispose();
        settingsFlowCoordinator.dispose();
        menuFlowCoordinator.dispose();
    }

    private void startNewGame() {
        gameStateHandler.resetNewGame();
        tickHandler.resetTimeline();
        gameOverCountdown = NO_COUNTDOWN;
        timeProgressionPaused = false;
        autoSaveEnabled = true;
        gridInputHandler.resetTransientState();
        gameOverOverlay.hide();
        gridInputHandler.setExclusiveUiElement(null);
        gameStateHandler.resetRaidTimeline();
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

        tickHandler.resetTimeline();
        timeProgressionPaused = false;
        autoSaveEnabled = true;
        gridInputHandler.resetTransientState();
        gameOverOverlay.hide();
        gridInputHandler.setExclusiveUiElement(null);
        gameplayFlowCoordinator.startGame();
        gameOverCountdown = NO_COUNTDOWN;
    }

    private void enterGameOver() {
        if (flowState.getScreenState() == ScreenState.GAME_OVER) {
            return;
        }

        flowState.setPaused(true);
        flowState.setScreenState(ScreenState.GAME_OVER);
        timeProgressionPaused = true;
        autoSaveEnabled = false;
        gameOverOverlay.show();
        gridInputHandler.setExclusiveUiElement(gameOverOverlay.getBackToMenuButton());
        gameStateStore.delete();
    }


    /**
     * Schedules game over to trigger after a delay in seconds
     * Used to let the raid outcome floating text finish before the game over overlay appears
     */
    private void scheduleGameOver(float delaySeconds) {
        gameOverCountdown = delaySeconds;
    }


    private void triggerAutoSave() {
        if (!autoSaveEnabled) {
            return;
        }

        final GameState snapshot = gameStateHandler.getSnapshot();
        pendingSaveTasks.incrementAndGet();
        savingInProgress = true;

        saveExecutor.submit(() -> {
            try {
                if (autoSaveEnabled) {
                    gameStateStore.save(snapshot);
                }
            } finally {
                if (pendingSaveTasks.decrementAndGet() <= 0) {
                    pendingSaveTasks.set(0);
                    savingInProgress = false;
                }
            }
        });
    }
}
