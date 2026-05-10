package io.github.lord_of_nothing.game;

import io.github.lord_of_nothing.resources.ResourceType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Owns raid timeline scheduling, raid resolution, and raid popup message generation.
 * In gameplay terms, this class controls when raids are announced, when they
 * arrive, how many villagers or soldiers are lost, and when the village falls
 * because no citizens remain.
 */
public final class RaidMechanic {
    public static final int FIRST_RAID_DETERMINATION_DAY = 7;
    public static final int RAID_WARNING_MIN_DAYS = 5;
    public static final int RAID_WARNING_MAX_DAYS = 12;
    public static final int RAID_REST_MIN_DAYS = 10;
    public static final int RAID_REST_MAX_DAYS = 15;

    private RaidMechanic() {}

    /**
     * Resets raid state for a fresh run by clearing any pending attack and
     * restoring the first day on which a new raid can be determined. This keeps
     * a new game from inheriting a previous raid schedule and ensures the player
     * starts with the intended early-game grace period before the first bandit
     * attack is announced.
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
     * Advances raid state for the newly started day, schedules a raid when the
     * warning window begins, resolves the attack when its day arrives, emits raid
     * messages, and returns whether the village has fallen. In-game, this is the
     * daily raid check that keeps the tension moving forward without requiring
     * the player to manually trigger anything.
     * @param state mutable game state handler
     * @param currentDay newly started in-game day
     * @param popupConsumer sink for popup messages
     * @return {@code true} when the raid kills all citizens on this day
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

    /**
     * Chooses the raid warning window, schedules the raid, and tells the player
     * that bandits have been spotted on the way. This creates the short-term
     * pressure phase of the raid system: the player gets a warning instead of an
     * immediate attack, which gives time to prepare soldiers or make room for
     * civilians.
     * @param state mutable game state handler
     * @param currentDay current in-game day
     * @param popupConsumer sink for popup messages
     */
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

    /**
     * Resolves the raid encounter, applies soldier and villager losses, records
     * the raid summary, and reports defeat if no citizens remain. This is the
     * moment where the attack turns into real gameplay impact: some defenders may
     * survive, civilians may die, and the player only loses outright if the
     * village is completely emptied out.
     * @param state mutable game state handler
     * @param currentDay current in-game day
     * @param popupConsumer sink for popup messages
     * @return {@code true} when all citizens are dead after the raid
     */
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

    /**
     * Estimates the average number of soldiers the raid is expected to kill.
     * Higher bandit pressure increases the expected losses, which makes well
     * defended villages more likely to survive with manageable losses while weak
     * defenses are punished harder in gameplay.
     */
    private static double computeSoldierDeathMean(int bandits, int soldiers) {
        int banditAdvantage = Math.max(0, bandits - soldiers);
        return (bandits * 0.70d) + (banditAdvantage * 0.30d);
    }

    /**
     * Returns the spread used for soldier casualties. This controls how much the
     * actual raid result can vary around the mean, so the player sees believable
     * but not perfectly predictable raid damage.
     */
    private static double computeSoldierDeathDeviation(int bandits, int soldiers) {
        return Math.max(1.0d, computeSoldierDeathMean(bandits, soldiers) * 0.25d);
    }

    /**
     * Estimates the average number of civilian villagers the raid is expected to
     * kill. If the bandits outnumber the defenders, civilian losses rise sharply,
     * making an under-defended village much more likely to collapse.
     */
    private static double computeVillagerDeathMean(int bandits, int soldiers) {
        int banditAdvantage = Math.max(0, bandits - soldiers);
        return (bandits * 0.35d) + (banditAdvantage * 0.90d);
    }

    /**
     * Returns the spread used for civilian casualties. This adds uncertainty so a
     * raid can occasionally be survived with unexpectedly low losses, or can
     * become catastrophic when the bandits have the upper hand.
     */
    private static double computeVillagerDeathDeviation(int bandits, int soldiers) {
        return Math.max(1.0d, computeVillagerDeathMean(bandits, soldiers) * 0.30d);
    }

    /**
     * Samples a casualty count from a gaussian distribution and clamps it to the
     * number of available victims. This is what turns the raid system from a
     * binary win/lose outcome into a damage model with partial survival, wounded
     * defense, and dramatic losses.
     */
    private static int sampleGaussianDeaths(double mean, double deviation, int maxDeaths) {
        if (maxDeaths <= 0) {
            return 0;
        }

        double sample = sampleGaussian(mean, deviation);
        int rounded = (int) Math.round(sample);
        return Math.max(0, Math.min(maxDeaths, rounded));
    }

    /**
     * Generates a gaussian sample around the requested mean and deviation. The
     * random variation makes raid damage feel less scripted and more like an
     * uncertain battle, which keeps repeated raids from resolving the same way
     * every time.
     */
    private static double sampleGaussian(double mean, double deviation) {
        if (deviation <= 0d) {
            return mean;
        }

        double u1 = Math.max(Double.MIN_VALUE, ThreadLocalRandom.current().nextDouble());
        double u2 = ThreadLocalRandom.current().nextDouble();
        double gaussian = Math.sqrt(-2.0d * Math.log(u1)) * Math.cos(2.0d * Math.PI * u2);
        return mean + gaussian * deviation;
    }

    /**
     * Sends a popup message to the UI if both the consumer and text are valid.
     * This keeps the raid flow from crashing when the UI is not ready, while
     * still showing the player the warning and outcome messages whenever possible.
     */
    private static void emitPopup(Consumer<String> popupConsumer, String message) {
        if (popupConsumer == null || message == null || message.isEmpty()) {
            return;
        }
        popupConsumer.accept(message);
    }

    /**
     * Returns a random integer within the inclusive range. This is used for raid
     * timing so the player cannot perfectly predict when the attack starts or how
     * long the warning window will last.
     */
    private static int randomIntInclusive(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}

