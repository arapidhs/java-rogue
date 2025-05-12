package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.system.entity.creature.MonsterType;
import com.dungeoncode.javarogue.system.entity.item.*;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.dungeoncode.javarogue.template.Template;
import com.dungeoncode.javarogue.template.Templates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A factory for creating random game objects, providing utilities for generating items
 * and entities in the Rogue game.
 * <p>
 * Inspired by utility functions in the C Rogue source, such as object creation and
 * random selection logic (e.g., rnd_thing in things.c).
 * </p>
 */
public class RogueFactory {

    /**
     * List of possible object types that can be randomly selected, mirroring the
     * thing_list in the C Rogue source.
     */
    private static final ObjectType[] THING_LIST = {
            ObjectType.POTION,
            ObjectType.SCROLL,
            ObjectType.RING,
            ObjectType.ROD,
            ObjectType.FOOD,
            ObjectType.WEAPON,
            ObjectType.ARMOR,
            ObjectType.STAIRS,
            ObjectType.GOLD,
            ObjectType.AMULET
    };

    /**
     * An ordered list of {@link MonsterType} values representing monsters in approximate order of
     * increasing difficulty for dungeon levels. Used by {@link #randMonster(boolean,int)} to select
     * monsters for standard level generation, favoring meaner monsters at higher levels.
     * Mirrors the <code>lvl_mons</code> array in the C Rogue source.
     */
    public static final List<MonsterType> LVL_MONS = List.of(
            MonsterType.KESTREL,
            MonsterType.EMU,
            MonsterType.BAT,
            MonsterType.SNAKE,
            MonsterType.HOBGOBLIN,
            MonsterType.ICE_MONSTER,
            MonsterType.RATTLESNAKE,
            MonsterType.ORC,
            MonsterType.ZOMBIE,
            MonsterType.LEPRECHAUN,
            MonsterType.CENTAUR,
            MonsterType.QUAGGA,
            MonsterType.AQUATOR,
            MonsterType.NYMPH,
            MonsterType.YETI,
            MonsterType.VENUS_FLYTRAP,
            MonsterType.TROLL,
            MonsterType.WRAITH,
            MonsterType.PHANTOM,
            MonsterType.XEROC,
            MonsterType.BLACK_UNICORN,
            MonsterType.MEDUSA,
            MonsterType.VAMPIRE,
            MonsterType.GRIFFIN,
            MonsterType.JABBERWOCK,
            MonsterType.DRAGON
    );

    /**
     * An ordered list of {@link MonsterType} values representing monsters available for wandering
     * encounters, with gaps for excluded types. Used by {@link #randMonster(boolean,int)} to select
     * monsters for wandering spawns, favoring a subset of monsters. Mirrors the
     * <code>wand_mons</code> array in the C Rogue source.
     */
    public static final List<MonsterType> WAND_MONS = List.of(
            MonsterType.KESTREL,
            MonsterType.EMU,
            MonsterType.BAT,
            MonsterType.SNAKE,
            MonsterType.HOBGOBLIN,
            MonsterType.RATTLESNAKE,
            MonsterType.ORC,
            MonsterType.ZOMBIE,
            MonsterType.CENTAUR,
            MonsterType.QUAGGA,
            MonsterType.AQUATOR,
            MonsterType.YETI,
            MonsterType.TROLL,
            MonsterType.WRAITH,
            MonsterType.PHANTOM,
            MonsterType.BLACK_UNICORN,
            MonsterType.MEDUSA,
            MonsterType.VAMPIRE,
            MonsterType.GRIFFIN,
            MonsterType.JABBERWOCK
    );

    private final Config config;
    private final RogueRandom rogueRandom;

    /**
     * A counter for assigning unique group IDs to stackable weapons (e.g., daggers, arrows),
     * starting at 2 to distinguish from non-grouped items. Incremented for each new group
     * created by {@link #weapon(WeaponType)}.
     */
    private int weaponsGroup = 2;

    /**
     * Constructs a factory with the specified configuration and random number generator.
     *
     * @param config      The game configuration, providing settings like amulet level.
     * @param rogueRandom The random number generator for object selection.
     * @throws NullPointerException if config or rogueRandom is null.
     */
    public RogueFactory(@Nonnull final Config config, @Nonnull final RogueRandom rogueRandom) {
        Objects.requireNonNull(rogueRandom);
        Objects.requireNonNull(config);
        this.config = config;
        this.rogueRandom = rogueRandom;
    }

    /**
     * Selects a random object type appropriate for the given level, excluding the amulet
     * unless the level is at or above the configured amulet level.
     * <p>
     * Equivalent to the <code>rnd_thing</code> function in the C Rogue source (things.c).
     * </p>
     *
     * @param level The current dungeon level.
     * @return A randomly selected {@link ObjectType}.
     */
    public ObjectType rndThing(final int level) {
        final int i;
        if (level >= config.getAmuletLevel()) {
            i = rogueRandom.rnd(THING_LIST.length);
        } else {
            i = rogueRandom.rnd(THING_LIST.length - 1);
        }
        return THING_LIST[i];
    }

    /**
     * Selects a random {@link ObjectType} and optional {@link ItemSubtype} from templates with positive
     * probability for the specified {@link ObjectType}, using weighted random selection based on
     * cumulative probabilities. Returns a {@link PickResult} containing the selected type, subtype,
     * bad pick status, a formatted message for bad picks, and the checked templates for bad picks.
     * <p>
     * Equivalent to the <code>pick_one</code> function in the C Rogue source (things.c).
     * </p>
     *
     * @param objectType The {@link ObjectType} to match, or null to select from all
     *                   {@link ObjectInfoTemplate} instances with null {@link ItemSubtype}.
     * @return A {@link PickResult} with the selected {@link ObjectType}, {@link ItemSubtype},
     *         bad pick status, formatted message, and checked templates (non-empty only for bad picks).
     * @throws IllegalStateException if no templates with positive probability exist.
     */
    @Nonnull
    public PickResult pickOne(@Nullable ObjectType objectType) {
        final Set<Template> templates = Templates.getTemplates(objectType);

        // Filter templates with probability > 0 and sort by ID
        final List<ObjectInfoTemplate> validTemplates = templates.stream()
                .filter(t -> t instanceof ObjectInfoTemplate)
                .map(t -> (ObjectInfoTemplate) t)
                .filter(template -> template.getProbability() > 0)
                .sorted(Comparator.comparingLong(ObjectInfoTemplate::getId))
                .toList();

        // Throw if no valid templates found
        if (validTemplates.isEmpty()) {
            throw new IllegalStateException("No ObjectInfoTemplates with positive probability found");
        }

        // Pick a random number between 0 and 99
        final int random = rogueRandom.rnd(100);

        // Select the first template where random < cumulativeProbability
        for (ObjectInfoTemplate template : validTemplates) {
            if (random < template.getCumulativeProbability()) {
                return new PickResult(template.getObjectType(), template.getItemSubType(), false, null, null);
            }
        }

        // Bad pick: no template matched, prepare formatted message
        final String badPickMessage = String.format("bad pick_one: %d from %d items", random, validTemplates.size());

        // Default to first template, include checked templates
        return new PickResult(
                validTemplates.get(0).getObjectType(),
                validTemplates.get(0).getItemSubType(),
                true,
                badPickMessage,
                List.copyOf(validTemplates)
        );
    }

    /**
     * Selects a random {@link MonsterType} for a dungeon level or wandering encounter.
     * Chooses from {@link #LVL_MONS} for level spawns or {@link #WAND_MONS} for wandering,
     * adjusting the selection index based on the dungeon level to favor meaner monsters at
     * higher levels. Ensures a valid monster is selected by retrying if the index points to a null entry.
     * <p>
     * Equivalent to the <code>randmonster</code> function in the C Rogue source (monsters.c).
     * </p>
     *
     * @param wander True to select from wandering monsters, false for level monsters.
     * @param level The current dungeon level, influencing monster selection.
     * @return A randomly selected {@link MonsterType}.
     */
    @Nonnull
    public MonsterType randMonster(boolean wander, final int level) {
        List<MonsterType> mons = wander ? WAND_MONS : LVL_MONS;
        int d;
        do {
            d = level + (rogueRandom.rnd(10) - 6);
            if (d < 0) {
                d = rogueRandom.rnd(5);
            }
            if (d > 25) {
                d = rogueRandom.rnd(5) + 21;
            }
        } while (d >= mons.size() || mons.get(d) == null);
        return mons.get(d);
    }

    /**
     * Creates and initializes a {@link Weapon} of the specified {@link WeaponType}, setting
     * its count and group based on type and flags. Daggers receive 2-5 units, weapons with
     * {@link ItemFlag#ISMANY} receive 8-15 units with a unique group ID, and others receive
     * 1 unit with no group. Increments the weapons group counter for stackable weapons.
     * <p>
     * Equivalent to weapon initialization logic in the C Rogue source (things.c).
     * </p>
     *
     * @param weaponType The {@link WeaponType} to create.
     * @return The initialized {@link Weapon}.
     * @throws NullPointerException if weaponType is null.
     */
    public Weapon weapon(@Nonnull final WeaponType weaponType) {
        Objects.requireNonNull(weaponType);
        final Weapon weapon = new Weapon(weaponType);
        if (WeaponType.DAGGER.equals(weapon.getWeaponType())) {
            weapon.setCount(rogueRandom.rnd(4) + 2);
            weapon.setGroup(weaponsGroup++);
        } else if (weapon.hasFlag(ItemFlag.ISMANY)) {
            weapon.setCount(rogueRandom.rnd(8) + 8);
            weapon.setGroup(weaponsGroup++);
        } else {
            weapon.setCount(1);
            weapon.setGroup(0);
        }
        return weapon;
    }

    /**
     * A record containing the result of a random {@link ObjectType} selection, including
     * the selected type, optional {@link ItemSubtype}, bad pick status, a formatted message,
     * and the templates checked for bad picks.
     *
     * @param objectType The selected {@link ObjectType}.
     * @param itemSubType The selected {@link ItemSubtype}, or null if not applicable.
     * @param isBadPick True if no template matched the random number, false otherwise.
     * @param badPickMessage The formatted message for bad picks, null if no issue.
     * @param checkedTemplates The templates checked for bad picks, null for successful picks.
     */
    public record PickResult(
            @Nonnull ObjectType objectType,
            @Nullable Enum<? extends ItemSubtype> itemSubType,
            boolean isBadPick,
            @Nullable String badPickMessage,
            @Nullable List<ObjectInfoTemplate> checkedTemplates
    ) {
        /**
         * Constructs a {@link PickResult}, ensuring the {@link ObjectType} is non-null.
         *
         * @throws NullPointerException if objectType is null.
         */
        public PickResult {
            Objects.requireNonNull(objectType);
        }
    }

}
