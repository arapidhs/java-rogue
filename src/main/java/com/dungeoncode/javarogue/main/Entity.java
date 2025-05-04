package com.dungeoncode.javarogue.main;

/**
 * Represents an entity in the game, such as the player, a monster, or an object.
 * Based on the original Rogue "thing" union structure.
 */
public class Entity {

    private final Position position;

    public Entity() {
        position = new Position();
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public Position getPosition() {
        return position;
    }

}
