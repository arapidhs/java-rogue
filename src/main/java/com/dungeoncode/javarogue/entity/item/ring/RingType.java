package com.dungeoncode.javarogue.entity.item.ring;

import com.dungeoncode.javarogue.entity.item.ItemSubtype;

/**
 * Specific types of rings in the game, derived from original Rogue ring definitions.
 */
public enum RingType implements ItemSubtype {
    PROTECTION,
    ADD_STRENGTH,
    SUSTAIN_STRENGTH,
    SEARCHING,
    SEE_INVISIBLE,
    ADORNMENT,
    AGGRAVATE_MONSTER,
    DEXTERITY,
    INCREASE_DAMAGE,
    REGENERATION,
    SLOW_DIGESTION,
    TELEPORTATION,
    STEALTH,
    MAINTAIN_ARMOR
}
