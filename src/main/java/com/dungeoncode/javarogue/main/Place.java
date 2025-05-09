package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Represents a single tile on the level map, storing its display symbol type,
 * flags, position, and occupying monster.
 */
public class Place extends Entity {

    private final EnumSet<PlaceFlag> placeFlags;
    private PlaceType placeType;
    private SymbolType symbolType;
    private Monster monster;
    private Integer passageNumber;

    public Place(@Nonnull final SymbolType symbolType) {
        super();
        Objects.requireNonNull(symbolType);
        this.symbolType=symbolType;
        this.placeFlags = EnumSet.noneOf(PlaceFlag.class);
        this.monster = null;
        this.addFlag(PlaceFlag.REAL);
    }

    public boolean isReal() {
        return placeFlags.contains(PlaceFlag.REAL);
    }

    public boolean isType(final PlaceType placeType) {
        return Objects.equals(this.placeType,placeType);
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

    @Nullable
    public Monster getMonster() {
        return monster;
    }

    public void setMonster(@Nullable final Monster monster) {
        this.monster = monster;
    }

    public Integer getPassageNumber() {
        return passageNumber;
    }

    public void setPassageNumber(Integer passageNumber) {
        this.passageNumber = passageNumber;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(final PlaceType placeType) {
        this.placeType = placeType;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(final SymbolType symbolType) {
        this.symbolType = symbolType;
    }
}