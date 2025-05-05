package com.dungeoncode.javarogue.main;

import javax.annotation.Nullable;

public class Ring extends Item {

    public Ring(@Nullable Enum<RingType> ringType) {
        super(ObjectType.RING, ringType, 1);
    }

    /**
     * Formats the ring bonus for applicable ring types, e.g., "[+1]" or "[-2]".
     *
     * @return A string representing the ring bonus, or empty string if not known or inapplicable.
     */
    public String num() {
        if (!hasFlag(ItemFlag.ISKNOW)) {
            return "";
        }
        final RingType type = getItemSubType();
        if (type == RingType.PROTECTION || type == RingType.ADD_STRENGTH ||
                type == RingType.INCREASE_DAMAGE || type == RingType.DEXTERITY) {
            final int bonus = getArmorClass();
            return String.format(" [%s]", bonus < 0 ? Integer.toString(bonus) : "+" + bonus);
        }
        return "";
    }

    @Override
    public RingType getItemSubType() {
        return (RingType) super.getItemSubType();
    }

}
