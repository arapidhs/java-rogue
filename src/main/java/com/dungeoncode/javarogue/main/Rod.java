package com.dungeoncode.javarogue.main;

import javax.annotation.Nullable;

public class Rod extends Item {

    public Rod(@Nullable Enum<RingType> itemSubType) {
        super(ObjectType.ROD, itemSubType, 1);
    }

    @Override
    public RodType getItemSubType() {
        return (RodType) super.getItemSubType();
    }

}
