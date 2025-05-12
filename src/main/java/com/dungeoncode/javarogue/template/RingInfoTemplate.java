package com.dungeoncode.javarogue.template;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.system.entity.item.ObjectType;
import com.dungeoncode.javarogue.system.entity.item.RingType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

/**
 * Template representing ring information, based on the original Rogue ring_info table.
 */
public class RingInfoTemplate extends ObjectInfoTemplate {

    @JsonCreator
    public RingInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("ringType") @Nonnull final RingType ringType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.RING, ringType, name, probability, worth, null, null, null);

    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_RING;
    }

    public RingType getRingType() {
        return (RingType) super.getItemSubType();
    }

}
