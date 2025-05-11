package com.dungeoncode.javarogue.world;

import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.entity.Entity;
import com.dungeoncode.javarogue.entity.Position;
import com.dungeoncode.javarogue.ui.SymbolType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a room in the game, storing its position, size, gold, exits, and flags.
 */
public class Room extends Entity {

    private final EnumSet<RoomFlag> roomFlags;
    private final List<Position> exits;
    private Position size;
    private Position goldPosition;
    private int goldValue;

    public Room() {
        super();
        this.exits = new ArrayList<>();
        this.roomFlags = EnumSet.noneOf(RoomFlag.class);
    }

    /**
     * Generates a random position within the room, excluding the 1-tile border.
     * Based on C function rnd_pos() in Rogue source.
     *
     * @param rogueRandom The random number generator.
     * @return A random Position inside the room's inner area.
     * @throws NullPointerException if rogueRandom, position, or size is null.
     */
    @Nonnull
    public Position rndPos(@Nonnull RogueRandom rogueRandom) {
        Objects.requireNonNull(rogueRandom);

        int x = getX() + rogueRandom.rnd(size.getX() - 2) + 1;
        int y = getY() + rogueRandom.rnd(size.getY() - 2) + 1;
        return new Position(x, y);
    }

    public boolean hasFlag(@Nonnull final RoomFlag roomFlag) {
        return roomFlags.contains(roomFlag);
    }

    public void addFlag(@Nonnull final RoomFlag roomFlag) {
        roomFlags.add(roomFlag);
    }

    public void removeFlag(@Nonnull final RoomFlag roomFlag) {
        roomFlags.remove(roomFlag);
    }

    public void setGoldPosition(final int x, final int y) {
        if(goldPosition==null){
            goldPosition=new Position(x,y);
        }else {
            goldPosition.setX(x);
            goldPosition.setY(y);
        }
    }

    /**
     * Returns the dimensions of the room.
     *
     * @return The room's size.
     */
    @Nonnull
    public Position getSize() {
        return size;
    }

    public SymbolType getSymbolType() {
        if (roomFlags.contains(RoomFlag.GONE))
            return SymbolType.PASSAGE;
        else {
            return SymbolType.FLOOR;
        }
    }

    /**
     * Returns the position where the gold is located.
     *
     * @return The gold's position.
     */
    @Nonnull
    public Position getGoldPosition() {
        return goldPosition;
    }

    /**
     * Returns the worth of the gold in the room.
     *
     * @return The gold value.
     */
    public int getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(int goldValue) {
        this.goldValue = goldValue;
    }

    /**
     * Returns the properties of the room.
     *
     * @return The room's flags.
     */
    @Nonnull
    public EnumSet<RoomFlag> getRoomFlags() {
        return roomFlags;
    }

    /**
     * Returns the list of exit locations from the room.
     *
     * @return The exits list.
     */
    @Nonnull
    public List<Position> getExits() {
        return exits;
    }

    public void setSize(int roomSizeX, int roomSizeY) {
        if (this.size == null) {
            this.size = new Position(roomSizeX, roomSizeY);
        } else {
            this.size.setX(roomSizeX);
            this.size.setY(roomSizeY);
        }
    }

}
