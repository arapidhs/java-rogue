package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Armor extends Item {

    public Armor(@Nonnull final Enum<ArmorType> armorType) {
        super(ObjectType.ARMOR, armorType, 1);
        Objects.requireNonNull(armorType);
    }

    @Override
    public ArmorType getItemSubType() {
        return (ArmorType) super.getItemSubType();
    }

}
