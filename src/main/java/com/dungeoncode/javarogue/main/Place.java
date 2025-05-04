package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Represents a single tile on the level map, storing its display character,
 * flags, position, and occupying monster.
 */
public class Place {

    private final EnumSet<PlaceFlag> flags;
    private final Position position;
    private char symbol;
    private Monster monster;

    public Place(char symbol, int x, int y) {
        this.symbol = symbol;
        this.flags = EnumSet.noneOf(PlaceFlag.class);
        this.monster = null;
        this.position = new Position(x, y);
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(final char symbol) {
        this.symbol = symbol;
    }

    @Nonnull
    public EnumSet<PlaceFlag> getFlags() {
        return flags;
    }

    @Nullable
    public Monster getMonster() {
        return monster;
    }

    public void setMonster(@Nullable final Monster monster) {
        this.monster = monster;
    }

    @Nonnull
    public Position getPosition() {
        return position;
    }
}