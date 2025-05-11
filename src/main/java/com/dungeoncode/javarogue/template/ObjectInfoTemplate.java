package com.dungeoncode.javarogue.template;

import com.dungeoncode.javarogue.config.Messages;
import com.dungeoncode.javarogue.entity.item.ItemFlag;
import com.dungeoncode.javarogue.entity.item.ItemSubtype;
import com.dungeoncode.javarogue.entity.item.ObjectType;
import com.dungeoncode.javarogue.ui.SymbolType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
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
 *     <li><b>itemFlags</b> - Miscellaneous flags for the object</li>
 * </ul>
 * </p>
 *
 * @see ObjectType
 */
public class ObjectInfoTemplate extends AbstractTemplate {

    private final long id;
    private final ObjectType objectType;
    private final Enum<? extends ItemSubtype> itemSubType;
    private final String name;
    private final double probability;
    private final int worth;
    private final Boolean stackable;
    private final EnumSet<ItemFlag> itemFlags;
    private final SymbolType symbolType;
    private double cumulativeProbability;

    @JsonCreator
    public ObjectInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("objectType") @Nonnull final ObjectType objectType,
            @JsonProperty("itemSubType") @Nullable final Enum<? extends ItemSubtype> itemSubType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth,
            @JsonProperty("stackable") final Boolean stackable,
            @JsonProperty("itemFlags") @Nullable final EnumSet<ItemFlag> itemFlags,
            @JsonProperty("symbolType") @Nullable final SymbolType symbolType) {

        super(id);

        Objects.requireNonNull(objectType);
        Objects.requireNonNull(name);

        this.id = id;
        this.objectType = objectType;
        this.itemSubType = itemSubType;
        this.name = name;
        this.probability = probability;
        this.worth = worth;
        this.stackable = stackable;
        this.itemFlags = itemFlags != null ? EnumSet.copyOf(itemFlags) : EnumSet.noneOf(ItemFlag.class);
        this.symbolType = symbolType;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_THINGS;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Enum<? extends ItemSubtype> getItemSubType() {
        return itemSubType;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the cumulative probability of this object appearing.
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Returns the monetary worth of the object for scoring and item valuation.
     */
    public int getWorth() {
        return worth;
    }

    public boolean isStackable() {
        return stackable;
    }

    /**
     * Returns the set of flags associated with the object.
     *
     * @return A copy of the item's flags.
     */
    @Nonnull
    public EnumSet<ItemFlag> getItemFlags() {
        return EnumSet.copyOf(itemFlags);
    }

    public double getCumulativeProbability() {
        return cumulativeProbability;
    }

    public void setCumulativeProbability(double cumulativeProbability) {
        this.cumulativeProbability = cumulativeProbability;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }
}