package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.buildings.Building;
import io.github.lord_of_nothing.grid.Grid;
import io.github.lord_of_nothing.grid.Tile;
import io.github.lord_of_nothing.resources.ResourceManager;
import io.github.lord_of_nothing.resources.ResourceType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Owns and exposes game-state lifecycle responsibilities.
 */
public class GameStateHandler implements ResourceStateMutator {
    private final ResourceManager resourceManager;
    private final Grid grid;
    private int currentIngameDay;

    public GameStateHandler() {
        this(new ResourceManager(), new Grid(), 1);
    }

    public GameStateHandler(ResourceManager currentResources, Grid currentGrid, int currentIngameDay) {
        this.resourceManager = currentResources == null ? new ResourceManager() : currentResources;
        this.grid = currentGrid == null ? new Grid() : currentGrid;
        this.currentIngameDay = Math.max(1, currentIngameDay);
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

    public GameState getSnapshot() {
        GameState snapshot = new GameState();
        snapshot.setCurrentIngameDay(currentIngameDay);
        snapshot.setResources(createResourcesSnapshot());
        snapshot.setGrid(createGridSnapshot());
        return snapshot;
    }

    private Map<ResourceType, Integer> createResourcesSnapshot() {
        Map<ResourceType, Integer> snapshot = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            snapshot.put(type, resourceManager.getAmount(type));
        }
        return snapshot;
    }

    private GameState.GridState createGridSnapshot() {
        GameState.GridState gridSnapshot = new GameState.GridState();
        gridSnapshot.setWidth(grid.getWidth());
        gridSnapshot.setHeight(grid.getHeight());
        gridSnapshot.setPlacements(createBuildingPlacementsSnapshot());
        return gridSnapshot;
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
                placements.add(placement);
            }
        }
        return placements;
    }
}
