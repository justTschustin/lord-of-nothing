package io.github.lord_of_nothing.buildings;

/**
 * Abstrakte Basisklasse für alle Gebäudetypen im Spiel.
 * Erlaubt die generische Behandlung verschiedener Bauwerke auf dem Grid.
 */
public abstract class Building {
    public abstract String getTexturePath();
    /**
     * Definiert die Ressourcenanforderungen für den Bau eines Gebäudes.
     * Gibt standardmäßig 0 zurück, sofern nicht in Unterklassen überschrieben.
     */
    public int getCost(io.github.lord_of_nothing.resources.ResourceType type) {
        return 0;
    }
}
