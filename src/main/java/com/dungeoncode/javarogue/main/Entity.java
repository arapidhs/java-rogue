package com.dungeoncode.javarogue.main;

/**
 * Represents an entity in the game, such as the player, a monster, or an object.
 * Based on the original Rogue "thing" union structure.
 */
public class Entity {

    private Position position;

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(final int x, final int y) {
        this.position = new Position(x, y);
    }

}
