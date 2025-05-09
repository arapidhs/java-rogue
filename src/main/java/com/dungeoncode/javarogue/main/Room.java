package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

    public boolean hasFlag(@Nonnull final RoomFlag roomFlag) {
        return roomFlags.contains(roomFlag);
    }

    public void addFlag(@Nonnull final RoomFlag roomFlag) {
        roomFlags.add(roomFlag);
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
