package io.github.lord_of_nothing.buildings;

/**
 * Repräsentiert ein einfaches Wohnhaus.
 * Verweist auf den spezifischen Pfad der Bildressource in den Assets.
 */
public class House extends Building {
    @Override
    public String getTexturePath() {
        return "buildings/House1.png";
    }

    /**
     * Legt fest, dass ein Haus spezifisch 10 Einheiten Holz für den Bau benötigt.
     */
    @Override
    public int getCost(io.github.lord_of_nothing.resources.ResourceType type) {
        if (type == io.github.lord_of_nothing.resources.ResourceType.WOOD) {
            return 10;
        }
        return 0;
    }
}
