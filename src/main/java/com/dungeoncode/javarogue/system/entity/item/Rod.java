package com.dungeoncode.javarogue.system.entity.item;

import com.dungeoncode.javarogue.system.SymbolType;

import javax.annotation.Nullable;

public class Rod extends Item {

    private int charges;

    public Rod(@Nullable Enum<RodType> itemSubType) {
        super(ObjectType.ROD, itemSubType, 1);
        setSymbolType(SymbolType.ROD);
    }

    @Override
    public RodType getItemSubType() {
        return (RodType) super.getItemSubType();
    }

    /**
     * Formats the charge count for the rod, e.g., "[3]" or "[3 charges]".
     *
     * @param terse If true, uses short format; if false, uses verbose format.
     * @return A string representing the charge count, or empty if not known.
     */
    public String chargeStr(boolean terse) {
        if (!hasFlag(ItemFlag.ISKNOW)) {
            return "";
        }
        return terse ? String.format(" [%d]", charges) :
                String.format(" [%d charges]", charges);
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }

    public int getCharges() {
        return charges;
    }
}
