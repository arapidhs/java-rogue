package com.dungeoncode.javarogue.system.entity.item;

import com.dungeoncode.javarogue.system.SymbolType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Scroll extends Item {

    public Scroll(@Nonnull Enum<? extends ItemSubtype> scrollType) {
        super(ObjectType.SCROLL, scrollType, 1);
        Objects.requireNonNull(scrollType);
        setSymbolType(SymbolType.SCROLL);
    }

    @Override
    public ScrollType getItemSubType() {
        return (ScrollType) super.getItemSubType();
    }

}
