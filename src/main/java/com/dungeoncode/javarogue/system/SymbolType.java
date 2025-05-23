package com.dungeoncode.javarogue.system;

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
    STAIRS,

    // Inventory key symbols
    KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I,
    KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R,
    KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z,

    // Monster symbols
    MONSTER_AQUATOR,
    MONSTER_BAT,
    MONSTER_CENTAUR,
    MONSTER_DRAGON,
    MONSTER_EMU,
    MONSTER_VENUS_FLYTRAP,
    MONSTER_GRIFFIN,
    MONSTER_HOBGOBLIN,
    MONSTER_ICE_MONSTER,
    MONSTER_JABBERWOCK,
    MONSTER_KESTREL,
    MONSTER_LEPRECHAUN,
    MONSTER_MEDUSA,
    MONSTER_NYMPH,
    MONSTER_ORC,
    MONSTER_PHANTOM,
    MONSTER_QUAGGA,
    MONSTER_RATTLESNAKE,
    MONSTER_SNAKE,
    MONSTER_TROLL,
    MONSTER_BLACK_UNICORN,
    MONSTER_VAMPIRE,
    MONSTER_WRAITH,
    MONSTER_XEROC,
    MONSTER_YETI,
    MONSTER_ZOMBIE;

    // EnumSet containing all inventory keys
    public static final EnumSet<SymbolType> INVENTORY_KEYS = EnumSet.of(
            KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I,
            KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R,
            KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z
    );

    // EnumSet containing all monster symbols
    public static final EnumSet<SymbolType> MONSTER_SYMBOLS = EnumSet.of(
            MONSTER_AQUATOR, MONSTER_BAT, MONSTER_CENTAUR, MONSTER_DRAGON, MONSTER_EMU,
            MONSTER_VENUS_FLYTRAP, MONSTER_GRIFFIN, MONSTER_HOBGOBLIN, MONSTER_ICE_MONSTER,
            MONSTER_JABBERWOCK, MONSTER_KESTREL, MONSTER_LEPRECHAUN, MONSTER_MEDUSA,
            MONSTER_NYMPH, MONSTER_ORC, MONSTER_PHANTOM, MONSTER_QUAGGA, MONSTER_RATTLESNAKE,
            MONSTER_SNAKE, MONSTER_TROLL, MONSTER_BLACK_UNICORN, MONSTER_VAMPIRE,
            MONSTER_WRAITH, MONSTER_XEROC, MONSTER_YETI, MONSTER_ZOMBIE
    );

    /**
     * Checks if this symbol type represents a monster.
     *
     * @return {@code true} if this symbol type is a monster symbol, {@code false} otherwise.
     */
    public boolean isMonsterSymbol() {
        return MONSTER_SYMBOLS.contains(this);
    }

}
