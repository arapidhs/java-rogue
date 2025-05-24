package com.dungeoncode.javarogue.system.entity.creature;

/**
 * Enum representing save types against various effects in the Rogue game, equivalent to save definitions
 * in <code>rogue.h</code> from the original C source. Each save type has a base saving throw modifier,
 * where a higher value indicates a harder save.
 */
public enum SaveType {
    VS_POISON(0),       // Save against poison
    VS_PARALYZATION(0), // Save against paralyzation
    VS_DEATH(0),        // Save against death
    VS_BREATH(2),       // Save against breath attacks
    VS_MAGIC(3);        // Save against magic

    private final int baseValue;

    /**
     * Constructs a SaveType with the specified base saving throw modifier.
     *
     * @param baseValue The base value of the saving throw modifier (higher is harder).
     */
    SaveType(final int baseValue) {
        this.baseValue = baseValue;
    }

    /**
     * Returns the base saving throw modifier for this save type.
     *
     * @return The base value, where higher values indicate a harder save.
     */
    public int getBaseValue() {
        return baseValue;
    }
}