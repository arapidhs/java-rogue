package com.dungeoncode.javarogue.entity.item.potion;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.entity.item.ObjectType;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

/**
 * Template representing potion information, based on the original Rogue pot_info table.
 */
public class PotionInfoTemplate extends ObjectInfoTemplate {

    @JsonCreator
    public PotionInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("potionType") @Nonnull final PotionType potionType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {
        super(id, ObjectType.POTION, potionType, name, probability, worth, null, null, null);
    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_POTION;
    }

    public PotionType getPotionType() {
        return (PotionType) super.getItemSubType();
    }

}
