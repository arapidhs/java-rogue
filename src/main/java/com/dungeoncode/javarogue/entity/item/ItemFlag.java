package com.dungeoncode.javarogue.entity.item;

/**
 * Flags representing properties or states of items in the game.
 */
public enum ItemFlag {
    /**
     * Object is cursed
     */
    ISCURSED,
    /**
     * Player knows details about the object
     */
    ISKNOW,
    /**
     * Object is a missile type
     */
    ISMISL,
    /**
     * Object comes in groups
     */
    ISMANY,
    /**
     * Object has been seen (also used for creatures)
     */
    ISFOUND,
    /**
     * Armor is permanently protected
     */
    ISPROT
}