package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Armor extends Item {

    private int armorClass;

    public Armor(@Nonnull final Enum<ArmorType> armorType) {
        super(ObjectType.ARMOR, armorType, 1);
        Objects.requireNonNull(armorType);

        final ArmorInfoTemplate armorInfoTemplate = Templates.findTemplateBySubType(ArmorInfoTemplate.class, armorType);
        this.armorClass = armorInfoTemplate.getArmorClass();
    }

    @Override
    public ArmorType getItemSubType() {
        return (ArmorType) super.getItemSubType();
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(final int armorClass) {
        this.armorClass = armorClass;
    }

}
