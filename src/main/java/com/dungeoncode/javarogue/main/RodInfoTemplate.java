package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

/**
 * Template representing rod (wand/staff) information, based on the original Rogue ws_info table.
 */
public class RodInfoTemplate extends ObjectInfoTemplate {

    @JsonCreator
    public RodInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("rodType") @Nonnull final RodType rodType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.ROD, rodType, name, probability, worth, null, null,null);

    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_ROD;
    }

    public RodType getRodType() {
        return (RodType) super.getItemSubType();
    }
}
