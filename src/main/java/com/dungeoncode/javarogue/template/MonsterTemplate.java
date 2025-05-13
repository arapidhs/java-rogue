package com.dungeoncode.javarogue.template;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.system.entity.creature.MonsterType;
import com.dungeoncode.javarogue.system.entity.creature.Stats;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Objects;

public class MonsterTemplate extends AbstractTemplate {

    private final long id;
    private final MonsterType monsterType;
    private final String name;
    private final int carryProbability;
    private final EnumSet<CreatureFlag> creatureFlags;
    private final Stats stats;
    private final SymbolType symbolType;

    @JsonCreator
    public MonsterTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("monsterType") @Nonnull final MonsterType monsterType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("carryProbability") final int carryProbability,
            @JsonProperty("creatureFlags") final EnumSet<CreatureFlag> creatureFlags,
            @JsonProperty("stats") @Nonnull final Stats stats,
            @JsonProperty("symbolType") @Nonnull final SymbolType symbolType) {

        super(id);

        Objects.requireNonNull(monsterType);
        Objects.requireNonNull(name);
        Objects.requireNonNull(stats);

        this.id = id;
        this.monsterType=monsterType;
        this.name = name;
        this.carryProbability = carryProbability;
        this.creatureFlags = creatureFlags == null
                ? EnumSet.noneOf(CreatureFlag.class)
                : EnumSet.copyOf(creatureFlags);
        this.stats = stats;
        this.symbolType=symbolType;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EnumSet<CreatureFlag> getCreatureFlags() {
        return creatureFlags;
    }

    public Stats getStats() {
        return stats;
    }

    public int getCarryProbability() {
        return carryProbability;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }
}