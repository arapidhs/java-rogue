package com.dungeoncode.javarogue.ui;

import java.util.EnumSet;

/**
 * Represents symbol types used in the game.
 */
public enum SymbolType {
    EMPTY,
    FLOOR,
    PASSAGE,
    DOOR,
    WALL_VERTICAL,
    WALL_HORIZONTAL,

    ARMOR,
    POTION,
    SCROLL,
    FOOD,
    RING,
    WEAPON,
    ROD,
    GOLD,
    AMULET,

    PLAYER,

    // Inventory key symbols
    KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I,
    KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R,
    KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z;

    // EnumSet containing all inventory keys
    public static final EnumSet<SymbolType> INVENTORY_KEYS = EnumSet.of(
            KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I,
            KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R,
            KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z
    );

}
