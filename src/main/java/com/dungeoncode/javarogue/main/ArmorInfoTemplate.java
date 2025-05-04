package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing armor information, based on the original Rogue arm_info table.
 */
public class ArmorInfoTemplate extends ObjectInfoTemplate {

    private final ArmorType armorType;

    @JsonCreator
    public ArmorInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("armorType") @Nonnull final ArmorType armorType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.ARMOR, name, probability, worth, null);

        Objects.requireNonNull(armorType);

        this.armorType = armorType;
    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_ARMOR;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

}
