package com.dungeoncode.javarogue.main;

/**
 * Represents a passage in the level, extending Room to store exits and flags.
 */
public class Passage extends Room {
    public Passage() {
        super();
        addFlag(RoomFlag.GONE); // Default for passages
        addFlag(RoomFlag.DARK);
    }

    public void addExit(final int x, final int y) {
        getExits().add(new Position(x, y));
    }

    // TODO move passage number here ?
}