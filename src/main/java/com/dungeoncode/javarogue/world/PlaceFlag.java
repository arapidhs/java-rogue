package com.dungeoncode.javarogue.world;

/**
 * Flags representing properties of a level map tile.
 */
public enum PlaceFlag {
    SEEN,      // Tile has been seen by the player
    DROPPED,   // An object was dropped here (shared with LOCKED)
    LOCKED,    // Door is locked (shared with DROPPED)
    REAL,      // Tile’s appearance is accurate (what you see is what you get)
    TMASK,     // Trap number (mask, typically combined with value)
}