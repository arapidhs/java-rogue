package com.dungeoncode.javarogue.system.entity.creature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Stats {

    private final int strength;
    private final int experience;
    private final int level;
    private final int armor;
    private final int hitPoints;
    private final String damage; // e.g., "1x4"
    private final int maxHitPoints;

    @JsonCreator
    public Stats(
            @JsonProperty("strength") final int strength,
            @JsonProperty("experience") final int experience,
            @JsonProperty("level") final int level,
            @JsonProperty("armor") final int armor,
            @JsonProperty("hitPoints") final int hitPoints,
            @JsonProperty("damage") final String damage,
            @JsonProperty("maxHitPoints") final int maxHitPoints) {
        this.strength = strength;
        this.experience = experience;
        this.level = level;
        this.armor = armor;
        this.hitPoints = hitPoints;
        this.damage = damage;
        this.maxHitPoints = maxHitPoints;
    }

    public Stats(@Nonnull final Stats stats) {
        Objects.requireNonNull(stats);
        this.strength = stats.strength;
        this.experience = stats.experience;
        this.level = stats.level;
        this.armor = stats.armor;
        this.hitPoints = stats.hitPoints;
        this.damage = stats.damage;
        this.maxHitPoints = stats.maxHitPoints;
    }

    public int getStrength() {
        return strength;
    }

    public int getLevel() {
        return level;
    }

    public int getArmor() {
        return armor;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public String getDamage() {
        return damage;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public int getExperience() {
        return experience;
    }

}