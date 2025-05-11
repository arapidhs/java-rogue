package com.dungeoncode.javarogue.world;

import com.dungeoncode.javarogue.entity.Position;

/**
 * Represents a passage in the level, extending Room to store exits and flags.
 */
public class Passage extends Room {

    private int passageNumber;

    public Passage() {
        super();
        addFlag(RoomFlag.GONE); // Default for passages
        addFlag(RoomFlag.DARK);
    }

    public void addExit(final int x, final int y) {
        getExits().add(new Position(x, y));
    }

    public void setPassageNumber(final int passageNumber) {
        this.passageNumber = passageNumber;
    }

    public int getPassageNumber() {
        return passageNumber;
    }
}