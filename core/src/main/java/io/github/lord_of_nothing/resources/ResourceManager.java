package io.github.lord_of_nothing.resources;

import java.util.EnumMap;
import java.util.Map;

/**
 * Verwaltet die Bestände aller Ressourcen und bietet Methoden für Transaktionen an.
 * Nutzt eine EnumMap für performanten, typsicheren Zugriff auf die Ressourcenwerte.
 */
public class ResourceManager {
    private final Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);

    public ResourceManager() {
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    public int getAmount(ResourceType type) { return resources.getOrDefault(type, 0); }

    public void add(ResourceType type, int amount) {
        resources.put(type, getAmount(type) + amount);
    }

    public boolean hasEnough(ResourceType type, int amount) {
        return getAmount(type) >= amount;
    }

    /**
     * Reduziert den Bestand einer Ressource, sofern genügend Einheiten vorhanden sind.
     * @return true, wenn die Transaktion erfolgreich war, andernfalls false.
     */
    public boolean tryConsume(ResourceType type, int amount) {
        if (hasEnough(type, amount)) {
            add(type, -amount);
            return true;
        }
        return false;
    }
}
