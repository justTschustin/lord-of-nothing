package io.github.lord_of_nothing.buildings;

/**
 * Repräsentiert ein einfaches Wohnhaus.
 * Verweist auf den spezifischen Pfad der Bildressource in den Assets.
 */
public class House extends Building {
    public House() {
        costs.put(io.github.lord_of_nothing.resources.ResourceType.WOOD, 10);
    }

    @Override
    public String getTexturePath() {
        return "buildings/House1.png";
    }
}
