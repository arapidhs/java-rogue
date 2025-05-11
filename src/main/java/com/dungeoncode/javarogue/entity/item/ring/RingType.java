package com.dungeoncode.javarogue.entity.item.ring;

import com.dungeoncode.javarogue.entity.item.ItemSubtype;

/**
 * Specific types of rings in the game, derived from original Rogue ring definitions.
 */
public enum RingType implements ItemSubtype {
    R_PROTECT,
    R_ADDSTR,
    R_SUSTSTR,
    R_SEARCH,
    R_SEEINVIS,
    R_NOP,
    R_AGGR,
    R_ADDHIT,
    R_ADDDAM,
    R_REGEN,
    R_DIGEST,
    R_TELEPORT,
    R_STEALTH,
    R_SUSTARM
}
