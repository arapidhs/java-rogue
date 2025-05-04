package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Represents a living creature in the game, such as a player or monster.
 * Extends Entity by adding Stats.
 */
public abstract class Creature extends Entity {

    private final EnumSet<CreatureFlag> creatureFlags;
    private Stats stats;
    private Position destination;

    protected Creature() {
        super();
        this.creatureFlags = EnumSet.noneOf(CreatureFlag.class);
    }

    public boolean hasFlag(@Nonnull final CreatureFlag creatureFlag) {
        return creatureFlags.contains(creatureFlag);
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(final Position destination) {
        this.destination = destination;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

}
