package com.dungeoncode.javarogue.entity.item.potion;

import com.dungeoncode.javarogue.entity.item.ItemSubtype;

/**
 * Specific types of potions in the game, derived from original Rogue potion definitions.
 */
public enum PotionType implements ItemSubtype {
    CONFUSION,
    HALLUCINATION,
    POISON,
    GAIN_STRENGTH,
    SEE_INVISIBLE,
    HEALING,
    MONSTER_DETECTION,
    MAGIC_DETECTION,
    RAISE_LEVEL,
    EXTRA_HEALING,
    HASTE_SELF,
    RESTORE_STRENGTH,
    BLINDNESS,
    LEVITATION
}
