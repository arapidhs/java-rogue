package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a room in the game, storing its position, size, gold, exits, and flags.
 */
public class Room {

    private final Position position;
    private final Position size;
    private final Position goldPosition;
    private final int goldValue;
    private final EnumSet<RoomFlag> roomFlags;
    private final List<Position> exits;

    /**
     * Constructs a new Room with the specified properties.
     *
     * @param position     The upper left corner of the room.
     * @param size         The dimensions of the room (width and height).
     * @param goldPosition The position where the gold is located.
     * @param goldValue    The worth of the gold in the room.
     * @param roomFlags    The properties of the room (e.g., dark, corridor).
     * @param exits        The list of exit locations from the room.
     */
    public Room(@Nonnull final Position position,
                @Nonnull final Position size,
                @Nonnull final Position goldPosition,
                final int goldValue,
                @Nullable final EnumSet<RoomFlag> roomFlags,
                @Nonnull final List<Position> exits) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(size);
        Objects.requireNonNull(goldPosition);
        Objects.requireNonNull(exits);
        this.position = position;
        this.size = size;
        this.goldPosition = goldPosition;
        this.goldValue = goldValue;
        if (roomFlags != null) {
            this.roomFlags = EnumSet.copyOf(roomFlags);
        } else {
            this.roomFlags = EnumSet.noneOf(RoomFlag.class);
        }
        this.exits = new ArrayList<>(exits);
    }

    public boolean hasFlag(@Nonnull final RoomFlag roomFlag) {
        return roomFlags.contains(roomFlag);
    }

    /**
     * Returns the upper left corner position of the room.
     *
     * @return The room's position.
     */
    @Nonnull
    public Position getPosition() {
        return position;
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

    public char getChar() {
        if (roomFlags.contains(RoomFlag.CORRIDOR))
            return SymbolMapper.getSymbol(RoomType.PASSAGE);
        else {
            return SymbolMapper.getSymbol(RoomType.FLOOR);
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

}
