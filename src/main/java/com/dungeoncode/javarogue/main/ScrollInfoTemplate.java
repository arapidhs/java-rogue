package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing scroll information, based on the original Rogue scr_info table.
 */
public class ScrollInfoTemplate extends ObjectInfoTemplate {

    private final ScrollType scrollType;

    @JsonCreator
    public ScrollInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("scrollType") @Nonnull final ScrollType scrollType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.SCROLL, name, probability, worth);

        Objects.requireNonNull(scrollType);

        this.scrollType = scrollType;
    }

    @Override
    public String getTemplateName() {
        return "scrolls";
    }

    public ScrollType getScrollType() {
        return scrollType;
    }
}
