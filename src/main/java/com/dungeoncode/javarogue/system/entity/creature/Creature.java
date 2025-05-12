package com.dungeoncode.javarogue.system.entity.creature;

import com.dungeoncode.javarogue.system.entity.Entity;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.world.Room;

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
    private Room room;

    protected Creature() {
        super();
        this.creatureFlags = EnumSet.noneOf(CreatureFlag.class);
    }

    public void addFlag(@Nonnull final CreatureFlag creatureFlag) {
        creatureFlags.add(creatureFlag);
    }

    public void removeFlag(@Nonnull final CreatureFlag creatureFlag) {
        creatureFlags.remove(creatureFlag);
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

    public void setDestination(final int x, final int y) {
        this.destination = new Position(x, y);
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
