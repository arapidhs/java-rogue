package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Maps game elements (classes and enums) to their display character symbols using static methods.
 */
public class SymbolMapper {

    private static final Map<Class<?>, Function<Object, Character>> SYMBOL_REGISTRY = new HashMap<>();

    static {
        final Map<ObjectType, Character> objectTypeSymbols = Map.of(
                ObjectType.ARMOR, ']',
                ObjectType.POTION, '!',
                ObjectType.SCROLL, '?',
                ObjectType.FOOD, ':',
                ObjectType.RING, '=',
                ObjectType.WEAPON, ')',
                ObjectType.ROD, '/'
        );
        SYMBOL_REGISTRY.put(ObjectType.class, obj -> objectTypeSymbols.get((ObjectType) obj));

        final Map<RoomType, Character> roomTypeSymbols = Map.of(
                RoomType.PASSAGE, '#',
                RoomType.FLOOR, '.',
                RoomType.EMPTY, ' '
        );
        SYMBOL_REGISTRY.put(RoomType.class, obj -> roomTypeSymbols.get((RoomType) obj));
    }

    /**
     * Returns the character symbol for a given game element.
     *
     * @param element The game element (e.g., ObjectType).
     * @return The display character.
     * @throws IllegalArgumentException If the element type is not mapped.
     */
    public static char getSymbol(@Nonnull final Object element) {
        Objects.requireNonNull(element);
        final Function<Object, Character> symbolFunc = SYMBOL_REGISTRY.get(element.getClass());
        if (symbolFunc == null) {
            throw new IllegalArgumentException(Messages.ERROR_NO_SYMBOL_FOR_TYPE + element.getClass().getName());
        }
        final Character symbol = symbolFunc.apply(element);
        if (symbol == null) {
            throw new IllegalArgumentException(Messages.ERROR_INVALID_SYMBOL_FOR_ELEMENT + element);
        }
        return symbol;
    }
}
