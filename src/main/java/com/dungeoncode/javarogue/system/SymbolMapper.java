package com.dungeoncode.javarogue.system;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.entity.item.Amulet;
import com.dungeoncode.javarogue.system.entity.item.Armor;
import com.dungeoncode.javarogue.system.entity.item.Food;
import com.dungeoncode.javarogue.system.entity.item.Gold;
import com.dungeoncode.javarogue.system.entity.item.Potion;
import com.dungeoncode.javarogue.system.entity.item.Ring;
import com.dungeoncode.javarogue.system.entity.item.Rod;
import com.dungeoncode.javarogue.system.entity.item.Scroll;
import com.dungeoncode.javarogue.system.entity.item.Weapon;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

/**
 * Maps game elements (classes and enums) to their display character symbols using static methods.
 */
public class SymbolMapper {

    private static final Map<SymbolType, Character> SYMBOL_REGISTRY;
    private static final Map<Class<?>, SymbolType> CLASS_TO_SYMBOL_TYPE;

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
                Map.entry(SymbolType.GOLD, '*'),

                Map.entry(SymbolType.PLAYER, '@'),

                // Inventory key symbols
                Map.entry(SymbolType.KEY_A, 'a'),
                Map.entry(SymbolType.KEY_B, 'b'),
                Map.entry(SymbolType.KEY_C, 'c'),
                Map.entry(SymbolType.KEY_D, 'd'),
                Map.entry(SymbolType.KEY_E, 'e'),
                Map.entry(SymbolType.KEY_F, 'f'),
                Map.entry(SymbolType.KEY_G, 'g'),
                Map.entry(SymbolType.KEY_H, 'h'),
                Map.entry(SymbolType.KEY_I, 'i'),
                Map.entry(SymbolType.KEY_J, 'j'),
                Map.entry(SymbolType.KEY_K, 'k'),
                Map.entry(SymbolType.KEY_L, 'l'),
                Map.entry(SymbolType.KEY_M, 'm'),
                Map.entry(SymbolType.KEY_N, 'n'),
                Map.entry(SymbolType.KEY_O, 'o'),
                Map.entry(SymbolType.KEY_P, 'p'),
                Map.entry(SymbolType.KEY_Q, 'q'),
                Map.entry(SymbolType.KEY_R, 'r'),
                Map.entry(SymbolType.KEY_S, 's'),
                Map.entry(SymbolType.KEY_T, 't'),
                Map.entry(SymbolType.KEY_U, 'u'),
                Map.entry(SymbolType.KEY_V, 'v'),
                Map.entry(SymbolType.KEY_W, 'w'),
                Map.entry(SymbolType.KEY_X, 'x'),
                Map.entry(SymbolType.KEY_Y, 'y'),
                Map.entry(SymbolType.KEY_Z, 'z')
        );

        CLASS_TO_SYMBOL_TYPE = Map.ofEntries(
                Map.entry(Player.class, SymbolType.PLAYER),
                Map.entry(Armor.class, SymbolType.ARMOR),
                Map.entry(Potion.class, SymbolType.POTION),
                Map.entry(Scroll.class, SymbolType.SCROLL),
                Map.entry(Food.class, SymbolType.FOOD),
                Map.entry(Ring.class, SymbolType.RING),
                Map.entry(Weapon.class, SymbolType.WEAPON),
                Map.entry(Rod.class, SymbolType.ROD),
                Map.entry(Amulet.class, SymbolType.AMULET),
                Map.entry(Gold.class, SymbolType.GOLD)
        );
    }

    /**
     * Returns the character symbol for a given game element class.
     * If the class is {@code SymbolType.class}, redirects to {@link #getSymbol(SymbolType)}
     * assuming the input is a {@code SymbolType} instance. Otherwise, maps the class to a
     * {@code SymbolType} and retrieves the corresponding symbol.
     *
     * @param clazz The class to get the character symbol for.
     * @return The display character.
     * @throws IllegalArgumentException If the class is not mapped or the input is invalid.
     */
    public static char getSymbol(@Nonnull final Class<?> clazz) {
        Objects.requireNonNull(clazz);
        final SymbolType symbolType = CLASS_TO_SYMBOL_TYPE.get(clazz);
        if (symbolType == null) {
            throw new IllegalArgumentException(Messages.ERROR_NO_SYMBOL_FOR_CLASS + clazz.getSimpleName());
        }
        return getSymbol(symbolType);
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