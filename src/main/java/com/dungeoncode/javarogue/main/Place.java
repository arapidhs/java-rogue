package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Represents a single tile on the level map, storing its display character,
 * flags, position, and occupying monster.
 */
public class Place extends Entity {

    private final EnumSet<PlaceFlag> placeFlags;
    private Character symbol;
    private Monster monster;
    private int passageNumber;

    public Place() {
        this(null);
    }

    public Place(final Character symbol) {
        super();
        this.symbol = symbol;
        this.placeFlags = EnumSet.noneOf(PlaceFlag.class);
        this.monster = null;
        this.addFlag(PlaceFlag.REAL);
    }

    public void addFlag(@Nonnull final PlaceFlag placeFlag) {
        placeFlags.add(placeFlag);
    }

    public void removeFlag(@Nonnull final PlaceFlag placeFlag) {
        placeFlags.remove(placeFlag);
    }

    public boolean hasFlag(@Nonnull final PlaceFlag placeFlag) {
        return placeFlags.contains(placeFlag);
    }

    public Character getSymbol() {
        return symbol;
    }

    public void setSymbol(final Character symbol) {
        this.symbol = symbol;
    }

    @Nullable
    public Monster getMonster() {
        return monster;
    }

    public void setMonster(@Nullable final Monster monster) {
        this.monster = monster;
    }

    public int getPassageNumber() {
        return passageNumber;
    }

    public void setPassageNumber(int passageNumber) {
        this.passageNumber = passageNumber;
    }
}