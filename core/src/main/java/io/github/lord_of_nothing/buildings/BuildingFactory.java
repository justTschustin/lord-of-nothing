package io.github.lord_of_nothing.buildings;

/**
 * Creates building instances from persisted building type keys.
 */
public final class BuildingFactory {
    private BuildingFactory() {}

    /**
     * Creates a building by its type key.
     *
     * @param buildingTypeKey persisted building key
     * @return matching building instance or {@code null} when unsupported
     */
    public static Building create(String buildingTypeKey) {
        if (buildingTypeKey == null) {
            return null;
        }

        switch (buildingTypeKey) {
            case "house":
                return new House();
            case "sawmill":
                return new Sawmill();
            case "quarry":
                return new Quarry();
            case "field":
                return new Field();
            case "barrack":
            case "barracks":
                return new Barrack();
            default:
                return null;
        }
    }
}

