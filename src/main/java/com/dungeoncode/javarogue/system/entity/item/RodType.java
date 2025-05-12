package com.dungeoncode.javarogue.system.entity.item;

/**
 * Specific types of rods (wands/staves) in the game, based on original Rogue stick definitions.
 */
public enum RodType implements ItemSubtype {
    WS_LIGHT,
    WS_INVIS,
    WS_ELECT,
    WS_FIRE,
    WS_COLD,
    WS_POLYMORPH,
    WS_MISSILE,
    WS_HASTE_M,
    WS_SLOW_M,
    WS_DRAIN,
    WS_NOP,
    WS_TELAWAY,
    WS_TELTO,
    WS_CANCEL
}
