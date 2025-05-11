package com.dungeoncode.javarogue.entity.item.scroll;

import com.dungeoncode.javarogue.entity.item.Item;
import com.dungeoncode.javarogue.entity.item.ItemSubtype;
import com.dungeoncode.javarogue.entity.item.ObjectType;

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
