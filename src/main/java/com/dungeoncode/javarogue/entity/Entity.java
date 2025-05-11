package com.dungeoncode.javarogue.entity;

import com.dungeoncode.javarogue.ui.SymbolType;

/**
 * Represents an entity in the game, such as the player, a monster, or an object.
 * Based on the original Rogue "thing" union structure.
 */
public abstract class Entity {

    private Position position;
    private SymbolType symbolType;

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
        if (position == null) {
            position = new Position(x, y);
        } else {
            this.position.setX(x);
            this.position.setY(y);
        }
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(SymbolType symbolType) {
        this.symbolType = symbolType;
    }

}
