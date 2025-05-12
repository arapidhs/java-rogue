package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.system.entity.item.ObjectType;

import javax.annotation.Nonnull;
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
}
