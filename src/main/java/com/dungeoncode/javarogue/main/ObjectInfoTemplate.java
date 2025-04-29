package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing object information in the dungeon.
 * <p>
 * This directly maps the original Rogue <code>obj_info</code> structure:
 * <ul>
 *     <li><b>objectType</b> - Category of the object (armor, potion, scroll, etc.)</li>
 *     <li><b>probability</b> - Probability weight for object appearance</li>
 *     <li><b>worth</b> - Gold value of the object</li>
 *     <li><b>cumulativeProbability</b> - Cumulative Probability</li>
 * </ul>
 * </p>
 *
 * @see ObjectType
 */
public class ObjectInfoTemplate extends AbstractTemplate {

    private final long id;
    private final ObjectType objectType;
    private final String name;
    private final double probability;
    private final int worth;
    private double cumulativeProbability;

    @JsonCreator
    public ObjectInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("objectType") @Nonnull final ObjectType objectType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id);

        Objects.requireNonNull(objectType);
        Objects.requireNonNull(name);

        this.id = id;
        this.objectType = objectType;
        this.name = name;
        this.probability = probability;
        this.worth = worth;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getTemplateName() {
        return "things";
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public String getName() {
        return name;
    }

    /**
     * <p>Cumulative probability of this object appearing.</p>
     */
    public double getProbability() {
        return probability;
    }

    /**
     * <p>Monetary worth of the object for scoring and item valuation.</p>
     */
    public int getWorth() {
        return worth;
    }

    public double getCumulativeProbability() {
        return cumulativeProbability;
    }

    public void setCumulativeProbability(double cumulativeProbability) {
        this.cumulativeProbability = cumulativeProbability;
    }

}
