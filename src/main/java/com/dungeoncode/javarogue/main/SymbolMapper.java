package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

/**
 * Maps game elements (classes and enums) to their display character symbols using static methods.
 */
public class SymbolMapper {

    private static final Map<SymbolType, Character> SYMBOL_REGISTRY;

    static {
        SYMBOL_REGISTRY = Map.ofEntries(
                Map.entry(SymbolType.EMPTY, ' '),
                Map.entry(SymbolType.PASSAGE, '#'),
                Map.entry(SymbolType.FLOOR, '.'),
                Map.entry(SymbolType.WALL_VERTICAL, '|'),
                Map.entry(SymbolType.WALL_HORIZONTAL, '-'),
                Map.entry(SymbolType.DOOR, '+'),
                Map.entry(SymbolType.ARMOR, ']'),
                Map.entry(SymbolType.POTION, '!'),
                Map.entry(SymbolType.SCROLL, '?'),
                Map.entry(SymbolType.FOOD, ':'),
                Map.entry(SymbolType.RING, '='),
                Map.entry(SymbolType.WEAPON, ')'),
                Map.entry(SymbolType.ROD, '/'),
                Map.entry(SymbolType.AMULET, ','),
                Map.entry(SymbolType.GOLD, '*')
        );
    }

    /**
     * Returns the character symbol for a given game element.
     *
     * @param symbolType The SymbolType to get the character for.
     * @return The display character.
     * @throws IllegalArgumentException If the element type is not mapped.
     */
    public static char getSymbol(@Nonnull final SymbolType symbolType) {
        Objects.requireNonNull(symbolType);
        final Character character = SYMBOL_REGISTRY.get(symbolType);
        if (character == null) {
            throw new IllegalArgumentException(Messages.ERROR_NO_SYMBOL_FOR_TYPE + symbolType);
        }
        return character;
    }
}
