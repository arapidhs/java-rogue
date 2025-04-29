package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing potion information, based on the original Rogue pot_info table.
 */
public class PotionInfoTemplate extends ObjectInfoTemplate {

    private final PotionType potionType;

    @JsonCreator
    public PotionInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("potionType") @Nonnull final PotionType potionType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.POTION, name, probability, worth);

        Objects.requireNonNull(potionType);

        this.potionType = potionType;
    }

    @Override
    public String getTemplateName() {
        return "potions";
    }

    public PotionType getPotionType() {
        return potionType;
    }

}
