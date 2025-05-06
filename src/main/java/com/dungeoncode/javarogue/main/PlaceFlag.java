package com.dungeoncode.javarogue.main;

/**
 * Flags representing properties of a level map tile.
 */
public enum PlaceFlag {
    PASS,      // Tile is a passageway
    SEEN,      // Tile has been seen by the player
    DROPPED,   // An object was dropped here (shared with LOCKED)
    LOCKED,    // Door is locked (shared with DROPPED)
    REAL,      // Tile’s appearance is accurate (what you see is what you get)
    PNUM,      // Passage number (mask, typically combined with value)
    TMASK,     // Trap number (mask, typically combined with value)
    FLOOR,
    WALL_VERTICAL,
    DOOR, WALL_HORIZONTAL
}