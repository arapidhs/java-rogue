package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.system.entity.item.ObjectType;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.dungeoncode.javarogue.template.Templates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A factory for creating random game objects, providing utilities for generating items
 * and entities in the Rogue game.
 * <p>
 * Inspired by utility functions in the C Rogue source, such as object creation and
 * random selection logic (e.g., rnd_thing in things.c).
 * </p>
 */
public class RogueFactory {

    /**
     * List of possible object types that can be randomly selected, mirroring the
     * thing_list in the C Rogue source.
     */
    private static final ObjectType[] THING_LIST = {
            ObjectType.POTION,
            ObjectType.SCROLL,
            ObjectType.RING,
            ObjectType.ROD,
            ObjectType.FOOD,
            ObjectType.WEAPON,
            ObjectType.ARMOR,
            ObjectType.STAIRS,
            ObjectType.GOLD,
            ObjectType.AMULET
    };

    private final Config config;
    private final RogueRandom rogueRandom;

    /**
     * Constructs a factory with the specified configuration and random number generator.
     *
     * @param config      The game configuration, providing settings like amulet level.
     * @param rogueRandom The random number generator for object selection.
     * @throws NullPointerException if config or rogueRandom is null.
     */
    public RogueFactory(@Nonnull final Config config, @Nonnull final RogueRandom rogueRandom) {
        Objects.requireNonNull(rogueRandom);
        Objects.requireNonNull(config);
        this.config = config;
        this.rogueRandom = rogueRandom;
    }

    /**
     * Selects a random object type appropriate for the given level, excluding the amulet
     * unless the level is at or above the configured amulet level.
     * <p>
     * Equivalent to the <code>rnd_thing</code> function in the C Rogue source (things.c).
     * </p>
     *
     * @param level The current dungeon level.
     * @return A randomly selected {@link ObjectType}.
     */
    public ObjectType rndThing(final int level) {
        final int i;
        if (level >= config.getAmuletLevel()) {
            i = rogueRandom.rnd(THING_LIST.length);
        } else {
            i = rogueRandom.rnd(THING_LIST.length - 1);
        }
        return THING_LIST[i];
    }

    /**
     * Selects a random {@link ObjectType} from templates with positive probability,
     * using weighted random selection based on cumulative probabilities. Returns a
     * {@link PickResult} containing the selected type, bad pick status, a formatted
     * message for bad picks, and the checked templates for bad picks.
     * <p>
     * Equivalent to the <code>pick_one</code> function in the C Rogue source (things.c).
     * </p>
     *
     * @return A {@link PickResult} with the selected {@link ObjectType}, bad pick status,
     *         formatted message, and checked templates (non-empty only for bad picks).
     * @throws IllegalStateException if no templates with positive probability exist.
     */
    @Nonnull
    public PickResult pickOne() {
        // Filter templates with probability > 0 and sort by ID
        final List<ObjectInfoTemplate> validTemplates = getObjectInfoProbabilityTemplates();

        if (validTemplates.isEmpty()) {
            throw new IllegalStateException("No ObjectInfoTemplates with positive probability found");
        }

        // Pick a random number between 0 and 99
        int random = rogueRandom.rnd(100);

        // Select the first template where random < cumulativeProbability
        for (ObjectInfoTemplate template : validTemplates) {
            if (random < template.getCumulativeProbability()) {
                return new PickResult(template.getObjectType(), false, null, null);
            }
        }

        // Bad pick: no template matched, prepare formatted message
        String badPickMessage = String.format("bad pick_one: %d from %d items", random, validTemplates.size());

        // Default to first template, include checked templates
        return new PickResult(
                validTemplates.get(0).getObjectType(),
                true,
                badPickMessage,
                List.copyOf(validTemplates)
        );
    }

    public List<ObjectInfoTemplate> getObjectInfoProbabilityTemplates() {
        return Templates.getTemplates(ObjectInfoTemplate.class)
                .stream()
                .filter(template -> template.getProbability() > 0)
                .sorted(Comparator.comparingLong(ObjectInfoTemplate::getId))
                .toList();
    }

    /**
     * A record containing the result of a random {@link ObjectType} selection, including
     * whether the pick was bad, a formatted message, and the templates checked for bad picks.
     *
     * @param objectType The selected {@link ObjectType}.
     * @param isBadPick True if no template matched the random number, false otherwise.
     * @param badPickMessage The formatted message for bad picks, null if no issue.
     * @param checkedTemplates The templates checked for bad picks, null for successful picks.
     */
    public record PickResult(
            @Nonnull ObjectType objectType,
            boolean isBadPick,
            @Nullable String badPickMessage,
            @Nullable List<ObjectInfoTemplate> checkedTemplates
    ) {
        public PickResult {
            Objects.requireNonNull(objectType);
        }
    }

}
