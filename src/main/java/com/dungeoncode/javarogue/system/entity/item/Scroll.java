package com.dungeoncode.javarogue.system.entity.item;

import javax.annotation.Nullable;

public class Scroll extends Item {

    public Scroll(@Nullable Enum<? extends ItemSubtype> scrollType) {
        super(ObjectType.SCROLL, scrollType, 1);
    }

    @Override
    public ScrollType getItemSubType() {
        return (ScrollType) super.getItemSubType();
    }

}
