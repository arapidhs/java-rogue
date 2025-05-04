package com.dungeoncode.javarogue.main;

import javax.annotation.Nullable;

public class Ring extends Item {

    public Ring(@Nullable Enum<RingType> ringType) {
        super(ObjectType.RING, ringType, 1);
    }

    @Override
    public RingType getItemSubType() {
        return (RingType) super.getItemSubType();
    }

}
