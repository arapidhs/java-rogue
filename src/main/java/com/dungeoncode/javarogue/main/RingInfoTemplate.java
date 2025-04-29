package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing ring information, based on the original Rogue ring_info table.
 */
public class RingInfoTemplate extends ObjectInfoTemplate {

    private final RingType ringType;

    @JsonCreator
    public RingInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("ringType") @Nonnull final RingType ringType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.RING, name, probability, worth);

        Objects.requireNonNull(ringType);

        this.ringType = ringType;
    }

    @Override
    public String getTemplateName() {
        return "rings";
    }

    public RingType getRingType() {
        return ringType;
    }
}
