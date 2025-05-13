package com.dungeoncode.javarogue.system.entity.creature;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.Entity;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.entity.item.Inventory;
import com.dungeoncode.javarogue.system.world.Room;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Represents a living creature in the game, such as a player or monster.
 * Extends Entity by adding Stats.
 */
public abstract class Creature extends Entity {

    private EnumSet<CreatureFlag> creatureFlags;
    private Stats stats;
    private Position destination;
    private Room room;
    private Inventory inventory;

    /**
     * Equivalent to tp -> t_oldch.
     */
    private SymbolType oldSymbolType;

    /**
     * If slowed, is it a turn to move.
     * <p>
     * Equivalent of bool _t_turn;.
     */
    private boolean turn;

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

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setCreatureFlags(EnumSet<CreatureFlag> creatureFlags) {
        this.creatureFlags = creatureFlags;
    }

    public EnumSet<CreatureFlag> getCreatureFlags() {
        return creatureFlags;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public SymbolType getOldSymbolType() {
        return oldSymbolType;
    }

    public void setOldSymbolType(SymbolType oldSymbolType) {
        this.oldSymbolType = oldSymbolType;
    }
}
