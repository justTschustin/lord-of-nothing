package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Persistence-oriented game-state snapshot.
 */
public class GameState {
	private Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
	private GridState grid = new GridState();
	private int currentIngameDay = 1;

	public Map<ResourceType, Integer> getResources() {
		return resources;
	}

	public void setResources(Map<ResourceType, Integer> resources) {
		this.resources = resources == null ? new EnumMap<>(ResourceType.class) : resources;
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

	public static class GridState {
		private int width;
		private int height;
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
	}

	public static class BuildingPlacementState {
		private String buildingType;
		private int x;
		private int y;

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
	}

}
