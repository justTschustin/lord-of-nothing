package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Owns raid timeline scheduling and popup message generation.
 */
public final class RaidMechanic {
    public static final int FIRST_RAID_DETERMINATION_DAY = 10;
    public static final int RAID_WARNING_MIN_DAYS = 5;
    public static final int RAID_WARNING_MAX_DAYS = 20;
    public static final int RAID_REST_MIN_DAYS = 10;
    public static final int RAID_REST_MAX_DAYS = 20;

    private RaidMechanic() {}

    /**
     * Resets raid state for a fresh run.
     *
     * @param state mutable game state handler
     */
    public static void resetTimeline(GameStateHandler state) {
        if (state == null) {
            return;
        }
        state.setNextRaidScheduledDay(null);
        state.setNextRaidDeterminationDay(FIRST_RAID_DETERMINATION_DAY);
    }

    /**
     * Applies one in-game day of raid progression and emits raid popup text when needed.
     *
     * @param state mutable game state handler
     * @param currentDay newly started in-game day
     * @param popupConsumer sink for popup messages
     * @return {@code true} when the raid defeats the village on this day
     */
    public static boolean processDay(GameStateHandler state, int currentDay, Consumer<String> popupConsumer) {
        if (state == null || currentDay < 1) {
            return false;
        }

        Integer scheduledRaidDay = state.getNextRaidScheduledDay();
        if (scheduledRaidDay == null) {
            Integer determinationDay = state.getNextRaidDeterminationDay();
            if (determinationDay != null && currentDay >= determinationDay) {
                scheduleAndAnnounceRaid(state, currentDay, popupConsumer);
                scheduledRaidDay = state.getNextRaidScheduledDay();
            }
        }

        if (scheduledRaidDay != null && currentDay >= scheduledRaidDay) {
            boolean defeated = announceRaidArrival(state, currentDay, popupConsumer);
            state.setNextRaidScheduledDay(null);
            state.setNextRaidDeterminationDay(currentDay + randomIntInclusive(RAID_REST_MIN_DAYS, RAID_REST_MAX_DAYS));
            return defeated;
        }

        return false;
    }

    private static void scheduleAndAnnounceRaid(
        GameStateHandler state,
        int currentDay,
        Consumer<String> popupConsumer
    ) {
        int warningMin = randomIntInclusive(RAID_WARNING_MIN_DAYS, RAID_WARNING_MAX_DAYS - 3);
        int warningMax = randomIntInclusive(warningMin + 3, RAID_WARNING_MAX_DAYS);

        int daysUntilRaid = randomIntInclusive(warningMin, warningMax);
        state.setNextRaidScheduledDay(currentDay + daysUntilRaid);
        state.setNextRaidDeterminationDay(null);

        emitPopup(
            popupConsumer,
            "Scouts have spotted bandits massing nearby... brace yourself for a raid, they strike in "
                 + warningMin + " to " + warningMax + " days!"
        );

        System.out.println("Raid warned to be in " + warningMin + " to " + warningMax + " days");
        System.out.println("Raid scheduled for " + state.getNextRaidScheduledDay());
    }

    private static boolean announceRaidArrival(GameStateHandler state, int currentDay, Consumer<String> popupConsumer) {
        int banditMin = 15 + (currentDay / 2);
        int banditMax = 15 + currentDay;
        int bandits = randomIntInclusive(banditMin, banditMax);
        int soldiersBeforeRaid = state.getResourceAmount(ResourceType.SOLDIERS);
        int villagersBeforeRaid = Math.max(0, state.getResourceAmount(ResourceType.CITIZENS_TOTAL) - soldiersBeforeRaid);
        state.setLastRaidSummary(bandits, soldiersBeforeRaid);

        int requestedSoldierDeaths = sampleGaussianDeaths(
            computeSoldierDeathMean(bandits, soldiersBeforeRaid),
            computeSoldierDeathDeviation(bandits, soldiersBeforeRaid),
            soldiersBeforeRaid
        );
        int requestedVillagerDeaths = sampleGaussianDeaths(
            computeVillagerDeathMean(bandits, soldiersBeforeRaid),
            computeVillagerDeathDeviation(bandits, soldiersBeforeRaid),
            villagersBeforeRaid
        );

        int actualSoldierDeaths = state.applySoldierCasualties(requestedSoldierDeaths);
        int actualVillagerDeaths = state.applyVillagerCasualties(requestedVillagerDeaths);
        boolean defeated = state.getResourceAmount(ResourceType.CITIZENS_TOTAL) <= 0;

        emitPopup(
            popupConsumer,
            "Bandits are upon us! " + bandits + " bandits assault the village! "
                + soldiersBeforeRaid + " soldiers stand between them and ruin."
        );

        if (defeated) {
            emitPopup(
                popupConsumer,
                "Your soldiers were overwhelmed. " + soldiersBeforeRaid + " soldiers failed fending off " + bandits + " bandits. The village has fallen!"
            );
        } else {
            emitPopup(
                popupConsumer,
                "Your soldiers fought bravely and repelled the bandits. The village is safe... for now. During the raid, " + actualSoldierDeaths + " soldiers and " + actualVillagerDeaths + " villagers lost their lives"
            );
        }

        return defeated;
    }

    private static double computeSoldierDeathMean(int bandits, int soldiers) {
        int banditAdvantage = Math.max(0, bandits - soldiers);
        return (bandits * 0.70d) + (banditAdvantage * 0.30d);
    }

    private static double computeSoldierDeathDeviation(int bandits, int soldiers) {
        return Math.max(1.0d, computeSoldierDeathMean(bandits, soldiers) * 0.25d);
    }

    private static double computeVillagerDeathMean(int bandits, int soldiers) {
        int banditAdvantage = Math.max(0, bandits - soldiers);
        return (bandits * 0.35d) + (banditAdvantage * 0.90d);
    }

    private static double computeVillagerDeathDeviation(int bandits, int soldiers) {
        return Math.max(1.0d, computeVillagerDeathMean(bandits, soldiers) * 0.30d);
    }

    private static int sampleGaussianDeaths(double mean, double deviation, int maxDeaths) {
        if (maxDeaths <= 0) {
            return 0;
        }

        double sample = sampleGaussian(mean, deviation);
        int rounded = (int) Math.round(sample);
        return Math.max(0, Math.min(maxDeaths, rounded));
    }

    private static double sampleGaussian(double mean, double deviation) {
        if (deviation <= 0d) {
            return mean;
        }

        double u1 = Math.max(Double.MIN_VALUE, ThreadLocalRandom.current().nextDouble());
        double u2 = ThreadLocalRandom.current().nextDouble();
        double gaussian = Math.sqrt(-2.0d * Math.log(u1)) * Math.cos(2.0d * Math.PI * u2);
        return mean + gaussian * deviation;
    }

    private static void emitPopup(Consumer<String> popupConsumer, String message) {
        if (popupConsumer == null || message == null || message.isEmpty()) {
            return;
        }
        popupConsumer.accept(message);
    }

    private static int randomIntInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}

