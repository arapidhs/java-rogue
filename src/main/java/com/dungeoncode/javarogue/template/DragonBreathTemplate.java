package com.dungeoncode.javarogue.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing dragon breath attack. Based on fake entry in Rogue's weap_info.
 */
public class DragonBreathTemplate extends AbstractTemplate {

    private final DragonBreathType breathType;

    @JsonCreator
    public DragonBreathTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("breathType") @Nonnull final DragonBreathType breathType) {

        super(id);
        Objects.requireNonNull(breathType);
        this.breathType = breathType;
    }

    public DragonBreathType getBreathType() {
        return breathType;
    }

}
