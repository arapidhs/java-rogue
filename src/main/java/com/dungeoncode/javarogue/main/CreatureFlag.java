package com.dungeoncode.javarogue.main;

/**
 * Flags representing properties or states of creatures in the game.
 */
public enum CreatureFlag {
    /**
     * Creature can confuse
     */
    CANHUH,
    /**
     * Creature can see invisible creatures
     */
    CANSEE,
    /**
     * Creature is blind
     */
    ISBLIND,
    /**
     * Creature has special qualities cancelled
     */
    ISCANC,
    /**
     * Creature has been seen (used for objects)
     */
    ISFOUND,
    /**
     * Creature runs to protect gold
     */
    ISGREED,
    /**
     * Creature has been hastened
     */
    ISHASTE,
    /**
     * Creature is the target of an 'f' command
     */
    ISTARGET,
    /**
     * Creature has been held
     */
    ISHELD,
    /**
     * Creature is confused
     */
    ISHUH,
    /**
     * Creature is invisible
     */
    ISINVIS,
    /**
     * Creature can wake when player enters room
     */
    ISMEAN,
    /**
     * Creature can regenerate
     */
    ISREGEN,
    /**
     * Creature is running at the player
     */
    ISRUN,
    /**
     * Creature can fly
     */
    ISFLY,
    /**
     * Creature has been slowed
     */
    ISSLOW
}