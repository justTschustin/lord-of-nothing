package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistence-oriented game-state snapshot.
 */
public class GameState {
	private Map<ResourceType, Integer> resources = new HashMap<>();
	private GridState grid = new GridState();
	private int currentIngameDay = 1;
	private Integer nextRaidScheduledDay;
	private Integer nextRaidDeterminationDay;

	public Map<ResourceType, Integer> getResources() {
		return resources;
	}

	public void setResources(Map<ResourceType, Integer> resources) {
		this.resources = new HashMap<>();
		if (resources == null) {
			return;
		}

		Map<?, ?> rawResources = resources;
		for (Map.Entry<?, ?> entry : rawResources.entrySet()) {
			ResourceType type = toResourceType(entry.getKey());
			if (type == null) {
				continue;
			}

			int amount = toInt(entry.getValue());
			this.resources.put(type, Math.max(0, amount));
		}
	}

	private ResourceType toResourceType(Object key) {
		if (key instanceof ResourceType) {
			return (ResourceType) key;
		}
		if (key instanceof String) {
			try {
				return ResourceType.valueOf((String) key);
			} catch (IllegalArgumentException ignored) {
				return null;
			}
		}
		return null;
	}

	private int toInt(Object value) {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException ignored) {
				return 0;
			}
		}
		return 0;
	}

	public GridState getGrid() {
		return grid;
	}

	public void setGrid(GridState grid) {
		this.grid = grid == null ? new GridState() : grid;
	}

	public int getCurrentIngameDay() {
		return currentIngameDay;
	}

	public void setCurrentIngameDay(int currentIngameDay) {
		this.currentIngameDay = Math.max(1, currentIngameDay);
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
	}

	public static class GridState {
		private int width;
		private int height;
		private List<TileState> tiles = new ArrayList<>();
		private List<BuildingPlacementState> placements = new ArrayList<>();

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public List<BuildingPlacementState> getPlacements() {
			return placements;
		}

		public void setPlacements(List<BuildingPlacementState> placements) {
			this.placements = placements == null ? new ArrayList<>() : placements;
		}

		public List<TileState> getTiles() {
			return tiles;
		}

		public void setTiles(List<TileState> tiles) {
			this.tiles = tiles == null ? new ArrayList<>() : tiles;
		}
	}

	public static class BuildingPlacementState {
		private String buildingType;
		private int x;
		private int y;
		private int currentWorkers;

		public String getBuildingType() {
			return buildingType;
		}

		public void setBuildingType(String buildingType) {
			this.buildingType = buildingType;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getCurrentWorkers() {
			return currentWorkers;
		}

		public void setCurrentWorkers(int currentWorkers) {
			this.currentWorkers = Math.max(0, currentWorkers);
		}
	}

	public static class TileState {
		private int x;
		private int y;
		private String tileType;
		private String buildingType;
		private Integer assignedVillagers;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public String getTileType() {
			return tileType;
		}

		public void setTileType(String tileType) {
			this.tileType = tileType;
		}

		public String getBuildingType() {
			return buildingType;
		}

		public void setBuildingType(String buildingType) {
			this.buildingType = buildingType;
		}

		public Integer getAssignedVillagers() {
			return assignedVillagers;
		}

		public void setAssignedVillagers(Integer assignedVillagers) {
			if (assignedVillagers == null) {
				this.assignedVillagers = null;
				return;
			}
			this.assignedVillagers = Math.max(0, assignedVillagers);
		}
	}

}
