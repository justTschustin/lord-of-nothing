package io.github.lord_of_nothing.game;

/**
 * Defines the four hunger tiers with their thresholds, display names,
 * visible effect descriptions, production multiplier, and citizen arrival multiplier.
 */
public enum HungerLevel {

    STARVING(0.00f, 0.25f, "STARVING",
        new String[]{
            "Production: -50%",
            "Settler arrival: -90%",
            "Morale at rock bottom"
        },
        0.50f, 0.1f),

    HUNGRY(0.25f, 0.60f, "HUNGRY",
        new String[]{
            "Production: -20%",
            "Settler arrival: -30%"
        },
        0.80f, 0.70f),

    SATISFIED(0.60f, 0.80f, "SATISFIED",
        new String[]{
            "No modifiers"
        },
        1.00f, 1.00f),

    WELL_FED(0.80f, 1.01f, "WELL-FED",
        new String[]{
            "Production: +15%",
            "Settler arrival: +10%",
            "High work morale"
        },
        1.15f, 1.10f);

    private final float minLevel;
    private final float maxLevel;
    private final String displayName;
    private final String[] effects;
    /** Multiplier applied to all building production per tick (1.0 = no change). */
    private final float productionMultiplier;
    /** Multiplier applied to the daily citizen arrival count (0.0 = no arrivals). */
    private final float arrivalMultiplier;

    HungerLevel(float minLevel, float maxLevel, String displayName,
                String[] effects, float productionMultiplier, float arrivalMultiplier) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.displayName = displayName;
        this.effects = effects;
        this.productionMultiplier = productionMultiplier;
        this.arrivalMultiplier = arrivalMultiplier;
    }

    /** Returns the hunger bar fraction at which this tier begins (inclusive). */
    public float getMinLevel() { return minLevel; }

    /** Returns the hunger bar fraction at which this tier ends (exclusive). */
    public float getMaxLevel() { return maxLevel; }

    /** Display label shown below the hunger bar. */
    public String getDisplayName() { return displayName; }

    /** Short effect descriptions rendered below the status label. */
    public String[] getEffects() { return effects; }

    /** Building production scaling factor for this tier. */
    public float getProductionMultiplier() { return productionMultiplier; }

    /** Citizen arrival scaling factor for this tier (0 = blocked). */
    public float getArrivalMultiplier() { return arrivalMultiplier; }

    /**
     * Returns the hunger tier that corresponds to {@code level}.
     *
     * @param level value in [0, 1]
     * @return matching tier, defaulting to {@link #STARVING} for edge cases
     */
    public static HungerLevel forLevel(float level) {
        for (HungerLevel h : values()) {
            if (level >= h.minLevel && level < h.maxLevel) {
                return h;
            }
        }
        return STARVING;
    }
}
