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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.zip.Deflater;
import io.github.lord_of_nothing.audio.AudioManager;
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
import io.github.lord_of_nothing.hud.EventLog;
import io.github.lord_of_nothing.hud.EventLogRenderer;
import io.github.lord_of_nothing.hud.TutorialOverlay;
import io.github.lord_of_nothing.menu.MainMenu;
import io.github.lord_of_nothing.menu.SettingsMenu;
import io.github.lord_of_nothing.persistence.UserConfigPaths;
import io.github.lord_of_nothing.resources.ResourceType;
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
    private Texture hudBackgroundTexture;
    private Texture hudCornerTexture;
    private Texture hudEdgeTexture;
    private Sidebar sidebar;
    private SidebarRenderer sidebarRenderer;
    private TileInspectorBar tileInspectorBar;
    private TileInspectorRenderer tileInspectorRenderer;
    private RaidBanner raidBanner;
    private GameOverOverlay gameOverOverlay;
    private TutorialOverlay tutorialOverlay;

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
    private AudioManager audioManager;
    private EventLog eventLog;
    private EventLogRenderer eventLogRenderer;

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
        audioManager = new AudioManager();
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

        audioManager.startPlaylist();

        raidBanner = new RaidBanner();
        tickHandler = new TickHandler();
        eventLog = new EventLog(
            gameStateHandler::getCurrentIngameDay,
            tickHandler::getCurrentIngameHour
        );
        eventLogRenderer = new EventLogRenderer();
        topBarRenderer = new TopBarRenderer();
        hudBackgroundTexture = new Texture("hud/HUD_Wood.png");
        hudCornerTexture = new Texture("hud/HUD_Corner_Overlay.png");
        hudEdgeTexture = new Texture("hud/HUD_Border_Overlay.png");
        initializeHudRenderers(hudBackgroundTexture, hudCornerTexture, hudEdgeTexture);
        eventBus = new EventBus();
        gameOverOverlay = new GameOverOverlay(eventBus);
        tutorialOverlay = new TutorialOverlay();
        // Load HUD frame assets into the tutorial overlay so it matches other panels
        tutorialOverlay.loadAssets(hudBackgroundTexture, hudCornerTexture, hudEdgeTexture);

        flowState = new FlowState();


        gridInputHandler = new GridInputHandler(
            camera,
            gameStateHandler.getCurrentGrid(),
            gameWindow,
            sidebar,
            gameStateHandler,
            eventBus,
            tileInspectorBar,
            eventLog,
            audioManager
        );
        gridInputHandler.setRaidBanner(raidBanner);
        gridInputHandler.setGameplayEnabled(false);

        // Build available resolutions before constructing settings UI dropdown options.
        Graphics.Monitor currentMonitor = Gdx.graphics.getMonitor();
        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes(currentMonitor);
        ResolutionSettings.initialize(displayModes);

        MainMenu mainMenu = new MainMenu(eventBus, gameStateStore.exists());
        SettingsMenu settingsMenu = new SettingsMenu(eventBus, mainMenu.getBackgroundManager());
        settingsStore = new SettingsStore(settingsFilePath);
        gameSettings = settingsStore.load();
        tickHandler.setGameSpeed(gameSettings.gameSpeed);
        audioManager.setMasterVolume(gameSettings.masterVolume);
        audioManager.setMusicVolume(gameSettings.musicVolume);
        audioManager.setSoundVolume(gameSettings.soundVolume);

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

        // Ensure a persistent fullscreen/windowed state
        settingsFlowCoordinator.initializeDisplaySettings(() -> {});
        menuFlowCoordinator.registerUiElements();

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
            if (event instanceof io.github.lord_of_nothing.events.MusicVolumeChangedEvent) {
                float vol = ((io.github.lord_of_nothing.events.MusicVolumeChangedEvent) event).getVolume();
                audioManager.setMusicVolume(vol);
                gameSettings.musicVolume = vol;
                settingsStore.save(gameSettings);
            }
            if (event instanceof io.github.lord_of_nothing.events.SoundVolumeChangedEvent) {
                float vol = ((io.github.lord_of_nothing.events.SoundVolumeChangedEvent) event).getVolume();
                audioManager.setSoundVolume(vol);
                gameSettings.soundVolume = vol;
                settingsStore.save(gameSettings);
            }
            if (event instanceof BackToMainMenuEvent) {
                gameOverOverlay.hide();
                tutorialOverlay.hide();
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
            if (event instanceof io.github.lord_of_nothing.events.OpenTutorialEvent) {
                tutorialOverlay.show(eventBus);
                gridInputHandler.setExclusiveUiElement(tutorialOverlay.getCloseButton());
            }
            if (event instanceof io.github.lord_of_nothing.events.CloseTutorialEvent) {
                tutorialOverlay.hide();
                gridInputHandler.setExclusiveUiElement(null);
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
            if (tutorialOverlay != null && tutorialOverlay.isVisible()) {
                renderTutorialOverlay();
            }
            return;
        }

        if (!flowState.isGameStarted()) {
            menuFlowCoordinator.render(batch);
            if (tutorialOverlay != null && tutorialOverlay.isVisible()) {
                renderTutorialOverlay();
            }
            return;
        }

        if (flowState.getScreenState() == ScreenState.GAMEPLAY && !timeProgressionPaused && !raidBanner.isActive()) {
            int completedDays = tickHandler.update(
                Gdx.graphics.getDeltaTime(),
                gameStateHandler.getCurrentIngameDay(),
                gameStateHandler,
                eventLog
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
                eventLog.addMessage(raidMsg, true);
            }

            // Show messages as banners in sequence.
            // If two messages arrive (attack and outcome), chain them:
            // the outcome banner shows automatically when the attack banner finishes.
            if (!raidMessages.isEmpty() && !raidBanner.isActive()) {
                String firstMsg = raidMessages.get(0);
                boolean isAttack = firstMsg.contains("Bandits are upon us");
                if (isAttack) {
                    audioManager.playKampf();
                } else {
                    audioManager.playRaidAnnounce();
                }
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
        eventLogRenderer.render(
            shapeRenderer, batch, gameWindow, eventLog,
            flowState.getScreenState() == ScreenState.PAUSED
        );
        tileInspectorRenderer.render(
            shapeRenderer, batch, gameWindow, tileInspectorBar, buildingTextures, eventBus,
            gameStateHandler,
            () -> gridInputHandler.deleteSelectedBuilding(),
            flowState.getScreenState() == ScreenState.PAUSED,
            gameStateHandler.getHungerMechanic()
        );
        gameOverOverlay.render(shapeRenderer, batch);

        // Tutorial overlay (dim + panel) when active
        // Draw dim first so the panel appears above everything else
        // The overlay's own render() will draw the decorative frame and content
        if (tutorialOverlay != null && tutorialOverlay.isVisible()) {
            renderTutorialOverlay();
        }

        // Render and overlay raid banner
        raidBanner.update(Gdx.graphics.getDeltaTime());
        if (raidBanner.isActive()) {
            raidBanner.renderDimOverlay(shapeRenderer);
        }
        if (raidBanner.isActive() || raidBanner.isFloatingTextActive()) {
            raidBanner.render(batch);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            takeScreenshot();
        }
    }

    private void takeScreenshot() {
        int w = Gdx.graphics.getBackBufferWidth();
        int h = Gdx.graphics.getBackBufferHeight();
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, w, h);
        String filename = "Screenshots/screenshot_" + System.currentTimeMillis() + ".png";
        PixmapIO.writePNG(Gdx.files.local(filename), pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
        Gdx.app.log("Screenshot", "Screenshot saved: " + Gdx.files.local(filename).file().getAbsolutePath());
    }

    private void renderTutorialOverlay() {
        // ShapeRenderer-based dim overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        shapeRenderer.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        tutorialOverlay.render(batch);
    }

    /**
     * Centralizes UI asset loading and distributes shared textures to the HUD renderers.
     * Reuses texture instances for the background, borders, and icons to optimize memory and simplify resource disposal.
     */
    private void initializeHudRenderers(Texture uiBg, Texture uiCorner, Texture uiEdge) {
        // Shared Icon Map to avoid loading the same files multiple times
        Map<ResourceType, Texture> icons = new HashMap<>();
        icons.put(ResourceType.WOOD, new Texture("icons/Wood.png"));
        icons.put(ResourceType.STONE, new Texture("icons/Stone.png"));
        icons.put(ResourceType.FOOD, new Texture("icons/Food.png"));
        icons.put(ResourceType.CITIZENS_TOTAL, new Texture("icons/Citizen.png"));
        icons.put(ResourceType.SOLDIERS, new Texture("icons/Soldier.png"));
        icons.put(ResourceType.CITIZENS_CAPACITY, new Texture("icons/Capacity.png"));

        Texture dayIcon = new Texture("icons/Day.png");
        Texture timeIcon = new Texture("icons/Time.png");

        // Initialize and configure TopBar
        topBarRenderer = new TopBarRenderer();
        topBarRenderer.loadAssets(uiBg, uiCorner, uiEdge,
            icons.get(ResourceType.WOOD), icons.get(ResourceType.STONE), icons.get(ResourceType.FOOD),
            icons.get(ResourceType.CITIZENS_TOTAL), icons.get(ResourceType.SOLDIERS),
            icons.get(ResourceType.CITIZENS_CAPACITY), dayIcon, timeIcon);

        // Map icons for tooltip/resource groups inside TopBar
        icons.forEach(topBarRenderer::setResourceIcon);
        topBarRenderer.setTimeIcons(dayIcon, timeIcon);

        // Initialize remaining HUD components with shared frames
        sidebarRenderer = new SidebarRenderer();
        sidebarRenderer.loadAssets(uiBg, uiCorner, uiEdge);
        eventLogRenderer.loadAssets(uiBg, uiCorner, uiEdge);

        tileInspectorRenderer = new TileInspectorRenderer();
        tileInspectorRenderer.loadAssets(uiBg, uiCorner, uiEdge);
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
     * Save settings and disposes of rendering resources.
     */
    @Override
    public void dispose() {
        saveCurrentGameStateIfAllowed();

        saveExecutor.shutdownNow();
        audioManager.stopPlaylist();
        shapeRenderer.dispose();
        batch.dispose();
        grassTexture.dispose();
        hudBackgroundTexture.dispose();
        hudCornerTexture.dispose();
        hudEdgeTexture.dispose();
        topBarRenderer.dispose();
        raidBanner.dispose();
        gameOverOverlay.dispose();
        tutorialOverlay.dispose();
        settingsFlowCoordinator.dispose();
        menuFlowCoordinator.dispose();
        audioManager.dispose();
        tileInspectorRenderer.dispose();
        gridRenderer.dispose();
    }

    private void startNewGame() {
        eventLog.clear();
        eventLog.addMessage("Welcome, Lord of Nothing!", false);
        eventLog.addMessage("Your settlement starts small, but with wise planning it can survive.", false);
        eventLog.addMessage("Select buildings from the left sidebar and place them on free tiles.", false);
        eventLog.addMessage("Gather wood, stone, and food to keep expanding your village.", false);
        eventLog.addMessage("Build houses for more citizens and barracks to prepare for raids.", false);
        gameStateHandler.resetNewGame();
        tickHandler.resetTimeline();
        gameOverCountdown = NO_COUNTDOWN;
        timeProgressionPaused = false;
        autoSaveEnabled = true;
        gridInputHandler.resetTransientState();
        gameOverOverlay.hide();
        tutorialOverlay.hide();
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

        eventLog.setMessages(loadedState.getEventLogMessages());
        tickHandler.resetTimeline();
        timeProgressionPaused = false;
        autoSaveEnabled = true;
        gridInputHandler.resetTransientState();
        gameOverOverlay.hide();
        tutorialOverlay.hide();
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
        Integer bandits = gameStateHandler.getLastRaidBanditCount();
        Integer defenders = gameStateHandler.getLastRaidDefenderCount();
        if (bandits != null && defenders != null) {
            gameOverOverlay.show(bandits, defenders);
        } else {
            gameOverOverlay.show();
        }
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

        final GameState snapshot = createSaveSnapshot();
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

    private GameState createSaveSnapshot() {
        GameState snapshot = gameStateHandler.getSnapshot();
        snapshot.setEventLogMessages(eventLog.getMessagesSnapshot());
        return snapshot;
    }

    private void saveCurrentGameStateIfAllowed() {
        if (!autoSaveEnabled || gameStateStore == null || gameStateHandler == null || eventLog == null) {
            return;
        }

        gameStateStore.save(createSaveSnapshot());
    }
}
