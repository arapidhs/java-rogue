package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Armor extends Item {

    public Armor(@Nonnull final Enum<ArmorType> armorType) {
        super(ObjectType.ARMOR, armorType, 1);
        Objects.requireNonNull(armorType);

        final ArmorInfoTemplate armorInfoTemplate = (ArmorInfoTemplate) Templates.findTemplateBySubType(armorType);
        setArmorClass(armorInfoTemplate.getArmorClass());
    }

    /**
     * Formats the armor class bonus, e.g., "+1" or "-2".
     *
     * @return A string representing the armor class bonus relative to the base class.
     */
    public String num() {
        final ArmorInfoTemplate armorTemplate = (ArmorInfoTemplate) Templates.findTemplateBySubType(getItemSubType());
        final int baseClass=armorTemplate.getArmorClass();
        final int bonus = baseClass - getArmorClass();
        return bonus < 0 ? Integer.toString(bonus) : "+" + bonus;
    }

    @Override
    public ArmorType getItemSubType() {
        return (ArmorType) super.getItemSubType();
    }

}
