package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.buildings.Barrack;
import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.buildings.BuildingFactory;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.Tile;
import io.github.lord_of_nothing.grid.TileType;
import io.github.lord_of_nothing.hud.EventLog;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns and exposes game-state lifecycle responsibilities.
 */
public class GameStateHandler implements ResourceStateMutator {
    public static final int DEFAULT_STARTING_WOOD = 200;
    public static final int DEFAULT_STARTING_FOOD = 250;

    private final ResourceManager resourceManager;
    private final Grid grid;
    private final HungerMechanic hungerMechanic = new HungerMechanic();
    private int currentIngameDay;
    private Integer nextRaidScheduledDay;
    private Integer nextRaidDeterminationDay;
    private Integer lastRaidBanditCount;
    private Integer lastRaidDefenderCount;

    public GameStateHandler() {
        this(new ResourceManager(), new Grid(), 1);
    }

    public GameStateHandler(ResourceManager currentResources, Grid currentGrid, int currentIngameDay) {
        this.resourceManager = currentResources == null ? new ResourceManager() : currentResources;
        this.grid = currentGrid == null ? new Grid() : currentGrid;
        this.currentIngameDay = Math.max(1, currentIngameDay);
        resetRaidTimeline();
    }

    public GameState getState() {
        return getSnapshot();
    }

    @Override
    public int getResourceAmount(ResourceType type) {
        return resourceManager.getAmount(type);
    }

    @Override
    public void addResource(ResourceType type, int amount) {
        resourceManager.add(type, amount);
    }

    @Override
    public boolean hasEnoughResources(ResourceType type, int amount) {
        return resourceManager.hasEnough(type, amount);
    }

    @Override
    public boolean tryConsumeResource(ResourceType type, int amount) {
        return resourceManager.tryConsume(type, amount);
    }

    public Grid getCurrentGrid() {
        return grid;
    }

    public int getCurrentIngameDay() {
        return currentIngameDay;
    }

    public void setCurrentIngameDay(int currentIngameDay) {
        this.currentIngameDay = Math.max(1, currentIngameDay);
    }

    public void advanceIngameDay() {
        this.currentIngameDay++;
    }

    /** Returns the hunger mechanic instance for read/write access and rendering. */
    public HungerMechanic getHungerMechanic() { return hungerMechanic; }

    /**
     * Applies the hunger tick: consumes food, adjusts the hunger bar, and flags
     * a game-over condition when the bar reaches zero.
     *
     * @param log event log used for starvation messages
     */
    public void applyHungerTick(EventLog log) {
        hungerMechanic.update(this, log);
    }

    /**
     * Returns {@code true} once after a hunger-caused game over is detected, then resets the flag.
     */
    public boolean consumePendingHungerGameOver() {
        return hungerMechanic.consumePendingGameOver();
    }

    /**
     * Resets the current runtime state to a fresh new-game setup.
     */
    public void resetNewGame() {
        resetResources();
        clearGrid();
        setCurrentIngameDay(1);
        addResource(ResourceType.WOOD, DEFAULT_STARTING_WOOD);
        addResource(ResourceType.FOOD, DEFAULT_STARTING_FOOD);
        clearLastRaidSummary();
        hungerMechanic.reset();
    }

    /**
     * Applies a persisted game snapshot to the current runtime state.
     *
     * @param state loaded snapshot
     * @return {@code true} if the snapshot was applied
     */
    public boolean applyState(GameState state) {
        if (state == null) {
            return false;
        }

        resetResources();
        clearGrid();

        Map<ResourceType, Integer> resources = state.getResources();
        if (resources != null) {
            for (ResourceType type : ResourceType.values()) {
                resourceManager.setAmount(type, Math.max(0, resources.getOrDefault(type, 0)));
            }
        }

        setCurrentIngameDay(state.getCurrentIngameDay());
        setNextRaidScheduledDay(state.getNextRaidScheduledDay());
        setNextRaidDeterminationDay(state.getNextRaidDeterminationDay());
        clearLastRaidSummary();
        hungerMechanic.reset();

        GameState.GridState gridState = state.getGrid();
        if (gridState == null) {
            return true;
        }

        applyTileStates(gridState.getTiles());
        applyPlacements(gridState.getPlacements());
        return true;
    }

    public Integer getNextRaidScheduledDay() {
        return nextRaidScheduledDay;
    }

    public void setNextRaidScheduledDay(Integer nextRaidScheduledDay) {
        if (nextRaidScheduledDay == null) {
            this.nextRaidScheduledDay = null;
            return;
        }
        this.nextRaidScheduledDay = Math.max(1, nextRaidScheduledDay);
    }

    public Integer getNextRaidDeterminationDay() {
        return nextRaidDeterminationDay;
    }

    public void setNextRaidDeterminationDay(Integer nextRaidDeterminationDay) {
        if (nextRaidDeterminationDay == null) {
            this.nextRaidDeterminationDay = null;
            return;
        }
        this.nextRaidDeterminationDay = Math.max(1, nextRaidDeterminationDay);
        System.out.println("Next raid will be determined on day " + nextRaidDeterminationDay);
    }

    public void resetRaidTimeline() {
        RaidMechanic.resetTimeline(this);
    }

    public Integer getLastRaidBanditCount() {
        return lastRaidBanditCount;
    }

    public Integer getLastRaidDefenderCount() {
        return lastRaidDefenderCount;
    }

    public void setLastRaidSummary(Integer banditCount, Integer defenderCount) {
        this.lastRaidBanditCount = banditCount == null ? null : Math.max(0, banditCount);
        this.lastRaidDefenderCount = defenderCount == null ? null : Math.max(0, defenderCount);
    }

    public void clearLastRaidSummary() {
        this.lastRaidBanditCount = null;
        this.lastRaidDefenderCount = null;
    }

    /**
     * Applies raid casualties to soldiers and removes the same number of assigned soldiers from barracks.
     *
     * @param amount requested soldier deaths
     * @return actual soldier deaths applied
     */
    public int applySoldierCasualties(int amount) {
        int requestedDeaths = Math.max(0, amount);
        if (requestedDeaths == 0) {
            return 0;
        }

        int actualDeaths = Math.min(requestedDeaths, resourceManager.getAmount(ResourceType.SOLDIERS));
        actualDeaths = Math.min(actualDeaths, resourceManager.getAmount(ResourceType.CITIZENS_TOTAL));
        actualDeaths = Math.min(actualDeaths, countAssignedWorkers(true));
        if (actualDeaths <= 0) {
            return 0;
        }

        removeAssignedWorkers(actualDeaths, true);
        resourceManager.add(ResourceType.SOLDIERS, -actualDeaths);
        resourceManager.add(ResourceType.CITIZENS_TOTAL, -actualDeaths);
        return actualDeaths;
    }

    /**
     * Applies raid casualties to civilian villagers and removes workers from non-barrack buildings first.
     *
     * @param amount requested civilian deaths
     * @return actual civilian deaths applied
     */
    public int applyVillagerCasualties(int amount) {
        int requestedDeaths = Math.max(0, amount);
        if (requestedDeaths == 0) {
            return 0;
        }

        int availableVillagers = resourceManager.getAmount(ResourceType.CITIZENS_AVAILABLE);
        int workerVillagers = countAssignedWorkers(false);
        int actualDeaths = Math.min(requestedDeaths, availableVillagers + workerVillagers);
        actualDeaths = Math.min(actualDeaths, resourceManager.getAmount(ResourceType.CITIZENS_TOTAL));
        if (actualDeaths <= 0) {
            return 0;
        }

        int lostAvailable = Math.min(actualDeaths, availableVillagers);
        if (lostAvailable > 0) {
            resourceManager.add(ResourceType.CITIZENS_AVAILABLE, -lostAvailable);
        }

        int remainingDeaths = actualDeaths - lostAvailable;
        int lostWorkers = removeAssignedWorkers(remainingDeaths, false);
        int totalLost = lostAvailable + lostWorkers;

        resourceManager.add(ResourceType.CITIZENS_TOTAL, -totalLost);
        return totalLost;
    }

    public GameState getSnapshot() {
        GameState snapshot = new GameState();
        snapshot.setCurrentIngameDay(currentIngameDay);
        snapshot.setNextRaidScheduledDay(nextRaidScheduledDay);
        snapshot.setNextRaidDeterminationDay(nextRaidDeterminationDay);
        snapshot.setResources(createResourcesSnapshot());
        snapshot.setGrid(createGridSnapshot());
        return snapshot;
    }

    private Map<ResourceType, Integer> createResourcesSnapshot() {
        Map<ResourceType, Integer> snapshot = new HashMap<>();
        for (ResourceType type : ResourceType.values()) {
            snapshot.put(type, resourceManager.getAmount(type));
        }
        return snapshot;
    }

    private GameState.GridState createGridSnapshot() {
        GameState.GridState gridSnapshot = new GameState.GridState();
        gridSnapshot.setWidth(grid.getWidth());
        gridSnapshot.setHeight(grid.getHeight());
        gridSnapshot.setTiles(createTileSnapshot());
        gridSnapshot.setPlacements(createBuildingPlacementsSnapshot());
        return gridSnapshot;
    }

    private List<GameState.TileState> createTileSnapshot() {
        List<GameState.TileState> tiles = new ArrayList<>();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile == null) {
                    continue;
                }

                GameState.TileState tileState = new GameState.TileState();
                tileState.setX(x);
                tileState.setY(y);
                tileState.setTileType(tile.getType().name());
                tileState.setBuildingType(tile.hasBuilding() ? tile.getBuilding().getBuildingTypeKey() : null);
                if (tile.hasBuilding() && tile.getBuilding().getMaxWorkers() > 0) {
                    tileState.setAssignedVillagers(tile.getBuilding().getCurrentWorkers());
                }
                tiles.add(tileState);
            }
        }
        return tiles;
    }

    private List<GameState.BuildingPlacementState> createBuildingPlacementsSnapshot() {
        List<GameState.BuildingPlacementState> placements = new ArrayList<>();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile == null || !tile.hasBuilding()) {
                    continue;
                }

                Building building = tile.getBuilding();
                if (!building.isAnchorPoint(x, y)) {
                    continue;
                }

                GameState.BuildingPlacementState placement = new GameState.BuildingPlacementState();
                placement.setBuildingType(building.getBuildingTypeKey());
                placement.setX(x);
                placement.setY(y);
                placement.setCurrentWorkers(building.getCurrentWorkers());
                placements.add(placement);
            }
        }
        return placements;
    }

    /**
     * Calculates and applies resource production for all buildings on the grid for a single simulation tick.
     * Only processes the root tile of multi-tile buildings to ensure production is only counted once.
     */
    public void applyTickProduction() {
        float productionMult = hungerMechanic.getCurrentTier().getProductionMultiplier();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile != null && tile.hasBuilding() && tile.getBuilding().isAnchorPoint(x, y)) {
                    Building b = tile.getBuilding();
                    if (b.getProductionType() != null && b.getCurrentWorkers() > 0) {
                        int raw = b.getCurrentWorkers() * b.getProductionPerWorker();
                        int amount = Math.round(raw * productionMult);
                        this.addResource(b.getProductionType(), amount);
                    }
                }
            }
        }
    }

    /**
     * Calculates the total hourly production for a specific resource type based on currently assigned workers.
     * @param type The resource type to calculate income for. @return Sum of production from all active buildings.
     */
    public int getHourlyIncome(ResourceType type) {
        int income = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                io.github.lord_of_nothing.grid.Tile tile = grid.getTile(x, y);
                if (tile != null && tile.hasBuilding() && tile.getBuilding().isAnchorPoint(x, y)) {
                    io.github.lord_of_nothing.buildings.Building b = tile.getBuilding();
                    if (b.getProductionType() == type) {
                        income += b.getCurrentWorkers() * b.getProductionPerWorker();
                    }
                }
            }
        }
        return income;
    }

    private void applyTileStates(List<GameState.TileState> tiles) {
        if (tiles == null) {
            return;
        }

        for (GameState.TileState tileState : tiles) {
            if (tileState == null || !grid.isInside(tileState.getX(), tileState.getY())) {
                continue;
            }

            Tile tile = grid.getTile(tileState.getX(), tileState.getY());
            tile.setBuilding(null);
            if (tileState.getTileType() != null) {
                try {
                    tile.setType(TileType.valueOf(tileState.getTileType()));
                } catch (IllegalArgumentException ignored) {
                    tile.setType(TileType.GRASS);
                }
            }
        }
    }

    private void applyPlacements(List<GameState.BuildingPlacementState> placements) {
        if (placements == null) {
            return;
        }

        for (GameState.BuildingPlacementState placement : placements) {
            if (placement == null) {
                continue;
            }

            Building building = BuildingFactory.create(placement.getBuildingType());
            if (building == null) {
                continue;
            }

            int x = placement.getX();
            int y = placement.getY();
            if (!grid.isInside(x, y) || !grid.canPlace(x, y, building.getWidth(), building.getHeight())) {
                continue;
            }

            int workerTarget = Math.min(placement.getCurrentWorkers(), building.getMaxWorkers());
            for (int i = 0; i < workerTarget; i++) {
                building.addWorker();
            }

            grid.placeBuilding(x, y, building);
        }
    }

    private void resetResources() {
        for (ResourceType type : ResourceType.values()) {
            resourceManager.setAmount(type, 0);
        }
    }

    private void clearGrid() {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile == null) {
                    continue;
                }
                tile.setBuilding(null);
                tile.setType(TileType.GRASS);
            }
        }
        grid.setHovered(-1, -1);
    }

    private int countAssignedWorkers(boolean barracksOnly) {
        int assignedWorkers = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Tile tile = grid.getTile(x, y);
                if (tile == null || !tile.hasBuilding()) {
                    continue;
                }

                Building building = tile.getBuilding();
                if (!building.isAnchorPoint(x, y)) {
                    continue;
                }

                boolean isBarrack = building instanceof Barrack;
                if (barracksOnly != isBarrack) {
                    continue;
                }

                assignedWorkers += building.getCurrentWorkers();
            }
        }
        return assignedWorkers;
    }

    private int removeAssignedWorkers(int requestedAmount, boolean barracksOnly) {
        int remaining = Math.max(0, requestedAmount);
        int removed = 0;

        if (remaining == 0) {
            return 0;
        }

        for (int x = 0; x < grid.getWidth() && remaining > 0; x++) {
            for (int y = 0; y < grid.getHeight() && remaining > 0; y++) {
                Tile tile = grid.getTile(x, y);
                if (tile == null || !tile.hasBuilding()) {
                    continue;
                }

                Building building = tile.getBuilding();
                if (!building.isAnchorPoint(x, y)) {
                    continue;
                }

                boolean isBarrack = building instanceof Barrack;
                if (barracksOnly != isBarrack) {
                    continue;
                }

                while (remaining > 0 && building.getCurrentWorkers() > 0) {
                    building.removeWorker();
                    remaining--;
                    removed++;
                }
            }
        }

        return removed;
    }
}
