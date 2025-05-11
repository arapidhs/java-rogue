package com.dungeoncode.javarogue.entity.item.armor;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.entity.item.ObjectType;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

/**
 * Template representing armor information, based on the original Rogue arm_info table.
 */
public class ArmorInfoTemplate extends ObjectInfoTemplate {

    private final int armorClass;

    @JsonCreator
    public ArmorInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("armorType") @Nonnull final ArmorType armorType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth,
            @JsonProperty("armorClass") final int armorClass) {

        super(id, ObjectType.ARMOR, armorType, name, probability, worth, null, null, null);
        this.armorClass = armorClass;
    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_ARMOR;
    }

    /**
     * Returns the type of armor.
     *
     * @return The ArmorType.
     */
    public ArmorType getArmorType() {
        return (ArmorType) super.getItemSubType();
    }

    /**
     * Returns the armor class value for this armor type.
     *
     * @return The armor class (lower is better protection).
     */
    public int getArmorClass() {
        return armorClass;
    }

}