package com.dungeoncode.javarogue.system.world;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.Entity;
import com.dungeoncode.javarogue.system.entity.creature.Monster;

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
    private final EnumSet<TrapFlag> trapFlags;
    private PlaceType placeType;
    private SymbolType symbolType;
    private Monster monster;
    private Integer passageNumber;

    public Place() {
        super();
        this.placeType = PlaceType.EMPTY;
        this.symbolType = SymbolType.EMPTY;
        this.placeFlags = EnumSet.noneOf(PlaceFlag.class);
        this.trapFlags = EnumSet.noneOf(TrapFlag.class);
        this.monster = null;
        this.addFlag(PlaceFlag.REAL);
    }

    public void addFlag(@Nonnull final PlaceFlag placeFlag) {
        placeFlags.add(placeFlag);
    }

    public void addFlag(@Nonnull final TrapFlag trapFlag) {
        trapFlags.add(trapFlag);
    }

    public boolean isReal() {
        return placeFlags.contains(PlaceFlag.REAL);
    }

    public boolean isStepOk() {
        return !isType(PlaceType.EMPTY) && !isType(PlaceType.WALL) && monster == null;
    }

    public boolean isType(final PlaceType placeType) {
        return Objects.equals(this.placeType, placeType);
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