package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Objects;

public class MonsterTemplate extends AbstractTemplate {

    private final long id;
    private final String name;
    private final int dropProbability;
    private final EnumSet<StatusEffect> statusEffects;
    private final Stats stats;

    @JsonCreator
    public MonsterTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("dropProbability") final int dropProbability,
            @JsonProperty("statusEffects") final EnumSet<StatusEffect> statusEffects,
            @JsonProperty("stats") @Nonnull final Stats stats) {

        super(id);

        Objects.requireNonNull(name);
        Objects.requireNonNull(stats);

        this.id = id;
        this.name = name;
        this.dropProbability = dropProbability;
        this.statusEffects = statusEffects == null
                ? EnumSet.noneOf(StatusEffect.class)
                : EnumSet.copyOf(statusEffects);
        this.stats = stats;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EnumSet<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    public Stats getStats() {
        return stats;
    }
}