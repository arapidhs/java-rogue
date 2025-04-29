package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing rod (wand/staff) information, based on the original Rogue ws_info table.
 */
public class RodInfoTemplate extends ObjectInfoTemplate {

    private final RodType rodType;

    @JsonCreator
    public RodInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("rodType") @Nonnull final RodType rodType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.ROD, name, probability, worth);

        Objects.requireNonNull(rodType);

        this.rodType = rodType;
    }

    @Override
    public String getTemplateName() {
        return "sticks";
    }

    public RodType getRodType() {
        return rodType;
    }
}
