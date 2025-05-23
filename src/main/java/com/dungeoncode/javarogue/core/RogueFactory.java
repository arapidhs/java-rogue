package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.system.SymbolMapper;
import com.dungeoncode.javarogue.system.entity.creature.*;
import com.dungeoncode.javarogue.system.entity.item.*;
import com.dungeoncode.javarogue.template.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;

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
     * An ordered list of {@link MonsterType} values representing monsters in approximate order of
     * increasing difficulty for dungeon levels. Used by {@link #randMonster(boolean, int)} to select
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
     * encounters, with gaps for excluded types. Used by {@link #randMonster(boolean, int)} to select
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RogueFactory.class);
    private static final int DEFAULT_ITEM_ARMOR = 11;
    private static final int DEFAULT_ITEM_COUNT = 1;
    private static final int DEFAULT_ITEM_GROUP = 0;
    private static final String DEFAULT_ITEM_WIELD_DAMAGE = "0x0";
    private static final String DEFAULT_ITEM_THROW_DAMAGE = "0x0";
    private static final String FORM_WAND = "wand";
    private static final String FORM_STAFF = "staff";
    private static final String SYLLABLES_JSON_PATH = "/data/syllables.json";
    private static final String COLORS_JSON_PATH = "/data/colors.json";
    private static final String STONES_JSON_PATH = "/data/stones.json";
    private static final String METALS_JSON_PATH = "/data/metals.json";
    private static final String WOODS_JSON_PATH = "/data/woods.json";
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
    private static final int DEFAULT_WEAPONS_GROUP = 2;

    private final Config config;
    private final RogueRandom rogueRandom;
    private final int maxScrollGeneratedNameLength;
    private final Map<Enum<? extends ItemSubtype>, String> itemSubTypeNames;
    private final Map<RingType, Integer> ringWorthMap;
    private final Map<RodType, RodFormData> rodFormData;
    private final Map<Enum<? extends ItemSubtype>, Boolean> itemSubTypeKnown;
    private final Map<Enum<? extends ItemSubtype>, String> itemSubTypeGuessNames;

    /**
     * A counter for assigning unique group IDs to stackable weapons (e.g., daggers, arrows),
     * starting at 2 to distinguish from non-grouped items. Incremented for each new group
     * created by {@link #initWeapon(WeaponType)}.
     */
    private int weaponsGroup = DEFAULT_WEAPONS_GROUP;

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
        this.maxScrollGeneratedNameLength = config.getMaxScrollItemGeneratedNameLength();
        this.itemSubTypeNames = new HashMap<>();
        this.ringWorthMap = new HashMap<>();
        this.rodFormData = new HashMap<>();
        this.itemSubTypeKnown = new HashMap<>();
        this.itemSubTypeGuessNames = new HashMap<>();
        init();
    }

    public void init() {
        weaponsGroup = DEFAULT_WEAPONS_GROUP;
        itemSubTypeNames.clear();
        ringWorthMap.clear();
        rodFormData.clear();
        itemSubTypeKnown.clear();
        itemSubTypeGuessNames.clear();
        initializeScrollNames();
        initializePotionNames();
        initializeRings();
        initializeRodMaterials();
    }

    /**
     * Initializes random names for each ScrollType by generating combinations of syllables.
     * ItemData are stored in the itemSubTypeNames map for ScrollType subtypes.
     */
    private void initializeScrollNames() {
        try (InputStream in = getClass().getResourceAsStream(SYLLABLES_JSON_PATH)) {
            final ObjectMapper mapper = new ObjectMapper();
            final List<String> syllables = mapper.readValue(
                    in, mapper.getTypeFactory().constructCollectionType(List.class, String.class));

            // Generate a random name for each ScrollType
            for (ScrollType scrollType : ScrollType.values()) {
                StringBuilder nameBuilder = new StringBuilder();
                // 2 to 4 words, per C code
                int numWords = rogueRandom.rnd(3) + 2;

                while (numWords-- > 0) {
                    // 1 to 3 syllables per word
                    int numSyllables = rogueRandom.rnd(3) + 1;
                    while (numSyllables-- > 0) {
                        // Randomly select a syllable
                        String syllable = syllables.get(rogueRandom.rnd(syllables.size()));
                        // Check if adding syllable exceeds max length (including space)
                        if (nameBuilder.length() + syllable.length() + 1 > maxScrollGeneratedNameLength) {
                            break;
                        }
                        nameBuilder.append(syllable);
                    }
                    // Add space between words, except for the last word
                    if (numWords > 0) {
                        nameBuilder.append(' ');
                    }
                }

                // Trim trailing space and ensure name fits within max length
                String name = nameBuilder.toString().trim();
                if (name.length() > maxScrollGeneratedNameLength) {
                    name = name.substring(0, maxScrollGeneratedNameLength).trim();
                }

                // Assign the generated name to the ScrollType
                setName(scrollType, name);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_FAILED_LOAD_SYLLABLES, SYLLABLES_JSON_PATH), e);
        }
    }

    /**
     * Initializes unique random color names for each PotionType, ensuring no color is reused.
     * ItemData are stored in the itemSubTypeNames map for PotionType subtypes.
     */
    private void initializePotionNames() {
        try (InputStream in = getClass().getResourceAsStream(COLORS_JSON_PATH)) {
            final ObjectMapper mapper = new ObjectMapper();
            final List<String> colors = mapper.readValue(
                    in, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            final Set<String> setColors = new HashSet<>(colors);
            final List<String> availableColors = new ArrayList<>(setColors);

            // Assign a unique color to each PotionType
            for (PotionType potionType : PotionType.values()) {
                // Randomly select an available color
                int index = rogueRandom.rnd(availableColors.size());
                final String color = availableColors.remove(index);
                // Assign the color to the PotionType
                setName(potionType, color);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_FAILED_TO_LOAD_DATA, COLORS_JSON_PATH), e);
        }
    }

    /**
     * Initializes random stone names and updated worth for each RingType, ensuring no stone is reused.
     * Stores names in itemSubTypeNames and worth in ringWorth.
     */
    private void initializeRings() {
        try (InputStream in = getClass().getResourceAsStream(STONES_JSON_PATH)) {
            final ObjectMapper mapper = new ObjectMapper();
            final List<Stone> stones = mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, Stone.class));
            final List<Stone> availableStones = new ArrayList<>(stones);

            for (RingType ringType : RingType.values()) {
                int index = rogueRandom.rnd(availableStones.size());
                final Stone stone = availableStones.remove(index);
                setName(ringType, stone.name);
                final RingInfoTemplate template = (RingInfoTemplate) Templates.findTemplateBySubType(ringType);
                ringWorthMap.put(ringType, template.getWorth() + stone.value);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_FAILED_TO_LOAD_DATA, STONES_JSON_PATH), e);
        }
    }

    /**
     * Initializes random forms (wand or staff) and materials (metal or wood) for each RodType, ensuring no material is reused.
     * Stores form and material in rodFormData map.
     */
    private void initializeRodMaterials() {
        try (final InputStream metalIn = getClass().getResourceAsStream(METALS_JSON_PATH);
             final InputStream woodIn = getClass().getResourceAsStream(WOODS_JSON_PATH)) {
            final ObjectMapper mapper = new ObjectMapper();
            final List<String> metals = mapper.readValue(
                    metalIn, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            final List<String> woods = mapper.readValue(
                    woodIn, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            final List<String> availableMetals = new ArrayList<>(metals);
            final List<String> availableWoods = new ArrayList<>(woods);

            for (RodType rodType : RodType.values()) {
                RodForm form;
                String material;
                while (true) {
                    if (rogueRandom.rnd(2) == 0 && !availableMetals.isEmpty()) {
                        // Select wand with random metal
                        int index = rogueRandom.rnd(availableMetals.size());
                        material = availableMetals.remove(index);
                        form = RodForm.WAND;
                        break;
                    } else if (!availableWoods.isEmpty()) {
                        // Select staff with random wood
                        int index = rogueRandom.rnd(availableWoods.size());
                        material = availableWoods.remove(index);
                        form = RodForm.STAFF;
                        break;
                    }
                }
                rodFormData.put(rodType, new RogueFactory.RodFormData(form, material));
            }
        } catch (Exception e) {
            throw new RuntimeException(Messages.ERROR_FAILED_TO_LOAD_DATA_FROM_JSON, e);
        }
    }

    /**
     * Sets a custom name for the specified item subtype.
     *
     * @param itemSubType The item subtype to name (e.g., ScrollType.IDENTIFY_SCROLL).
     * @param name        The custom name to assign.
     * @throws IllegalArgumentException If the name is null or empty.
     */
    private void setName(@Nonnull final Enum<? extends ItemSubtype> itemSubType, @Nonnull final String name) {
        Objects.requireNonNull(itemSubType);
        Objects.requireNonNull(name);
        if (name.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_NAME);
        }
        itemSubTypeNames.put(itemSubType, name);
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
     * bad pick status, formatted message, and checked templates (non-empty only for bad picks).
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
     * @param level  The current dungeon level, influencing monster selection.
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
     * Creates and initializes a new {@link Monster} of the specified {@link MonsterType} for the given
     * dungeon level. Sets stats, flags, and disguise based on the monster's template, adjusting for level
     * relative to the amulet level. Applies haste for levels above 29 and sets a random disguise for Xeroc.
     * <p>
     * Equivalent to the <code>new_monster</code> function in the C Rogue source (monsters.c).
     * </p>
     *
     * @param monsterType The {@link MonsterType} to create.
     * @param level       The current dungeon level, influencing stats and flags.
     * @return The initialized {@link Monster}.
     * @throws NullPointerException if monsterType is null.
     */
    public Monster monster(@Nonnull final MonsterType monsterType, final int level) {
        final Monster monster = new Monster(monsterType);

        final MonsterTemplate template = Templates.getMonsterTemplate(monsterType);
        assert template != null; // Assumes template exists for all MonsterType values

        // Adjust stats based on level relative to amulet level
        int levelAdd = level - config.getAmuletLevel();
        if (levelAdd < 0) {
            levelAdd = 0; // Ensure non-negative adjustment
        }
        final Stats stats = template.getStats();
        final int lvl = stats.getLevel() + levelAdd;
        final int maxHp = rogueRandom.roll(level, 8); // Roll hit points (level * 1d8)
        final int armorClass = stats.getArmor() - levelAdd;
        final String dmg = stats.getDamage();
        final int str = stats.getStrength();
        final int exp = stats.getExperience() + levelAdd * 10 + expAdd(lvl, maxHp);
        final EnumSet<CreatureFlag> flags = EnumSet.copyOf(template.getCreatureFlags());
        monster.setStats(new Stats(str, exp, lvl, armorClass, maxHp, dmg, maxHp));
        monster.setCreatureFlags(flags);

        // Apply haste flag for high levels
        if (level > 29) {
            monster.addFlag(CreatureFlag.ISHASTE);
        }
        monster.setTurn(true); // Monster starts with turn enabled
        monster.setInventory(null); // No initial inventory

        // Set random disguise for Xeroc
        if (monsterType.equals(MonsterType.XEROC)) {
            final ObjectType objectType = rndThing(level);
            monster.setDisguiseSymbolType(SymbolMapper.getSymbolType(objectType));
        }
        return monster;
    }

    /**
     * Calculates additional experience points for a monster based on its level and maximum hit points.
     * Divides max hit points by 8 for level 1, or by 6 for higher levels, then multiplies by 20 for levels
     * above 9, by 4 for levels 7-9, or leaves unchanged for levels 2-6.
     * <p>
     * Equivalent to the <code>exp_add</code> function in the C Rogue source (monsters.c).
     * </p>
     *
     * @param level        The level of the monster.
     * @param maxHitPoints The maximum hit points of the monster.
     * @return The additional experience points.
     */
    public int expAdd(final int level, final int maxHitPoints) {
        int mod;
        if (level == 1) {
            mod = maxHitPoints / 8; // Level 1: divide max HP by 8
        } else {
            mod = maxHitPoints / 6; // Levels 2+: divide max HP by 6
        }
        if (level > 9) {
            mod *= 20; // Levels 10+: multiply by 20
        } else if (level > 6) {
            mod *= 4; // Levels 7-9: multiply by 4
        }
        return mod;
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
     * Creates and initializes a {@link Food} item, randomly setting it as fruit (10% chance)
     * or non-fruit (90% chance) based on a random roll.
     *
     * @return The initialized {@link Food} item.
     */
    public Food food() {
        final Food food = new Food();
        // 10% chance for fruit
        food.setFruit(rogueRandom.rnd(10) == 0); // 90% chance for non-fruit
        return food;
    }

    /**
     * Creates and configures a new Amulet item for the Rogue game.
     * Sets the amulet's throw damage to "0x0" and armor class to 11.
     *
     * @return A configured Amulet instance.
     */
    public Amulet amulet() {
        final Amulet amulet = new Amulet();
        amulet.setThrowDamage("0x0");
        amulet.setWieldDamage("0x0");
        amulet.setArmorClass(11);
        return amulet;
    }

    /**
     * Creates and initializes a {@link Potion} of the specified {@link PotionType}.
     * Applies default item properties via {@link #initItem(Item)}.
     *
     * @param potionType The {@link PotionType} to create.
     * @return The initialized {@link Potion}.
     * @throws NullPointerException if potionType is null.
     */
    public Potion potion(@Nonnull final PotionType potionType) {
        Objects.requireNonNull(potionType);
        final Potion potion = new Potion(potionType);
        initItem(potion); // Set default count, armor, group, and clear flags
        return potion;
    }

    /**
     * Initializes an {@link Item} with default properties: count, armor class, group,
     * wield damage, throw damage, and clears all flags.
     *
     * @param item The {@link Item} to initialize.
     * @throws NullPointerException if item is null.
     */
    private void initItem(@Nonnull final Item item) {
        item.setCount(DEFAULT_ITEM_COUNT); // Set default quantity
        item.setArmorClass(DEFAULT_ITEM_ARMOR); // Set default armor class
        item.setGroup(DEFAULT_ITEM_GROUP); // Set default group ID
        item.setWieldDamage(DEFAULT_ITEM_WIELD_DAMAGE); // Set default wield damage
        item.setThrowDamage(DEFAULT_ITEM_THROW_DAMAGE); // Set default throw damage
        item.getItemFlags().clear(); // Reset all flags
    }

    /**
     * Creates and initializes a {@link Scroll} of the specified {@link ScrollType}.
     * Applies default item properties via {@link #initItem(Item)}.
     *
     * @param scrollType The {@link ScrollType} to create.
     * @return The initialized {@link Scroll}.
     * @throws NullPointerException if scrollType is null.
     */
    public Scroll scroll(@Nonnull final ScrollType scrollType) {
        Objects.requireNonNull(scrollType);
        final Scroll scroll = new Scroll(scrollType);
        initItem(scroll); // Set default count, armor, group, and clear flags
        return scroll;
    }

    /**
     * Creates and initializes a {@link Weapon} of the specified {@link WeaponType}, setting
     * count and group via {@link #initWeapon(WeaponType)}. Applies a 10% chance for a cursed
     * weapon with reduced hit bonus (-1 to -3), a 5% chance for an enhanced hit bonus (+1 to +3),
     * or no bonus change otherwise.
     *
     * @param weaponType The {@link WeaponType} to create.
     * @return The initialized {@link Weapon}.
     * @throws NullPointerException if weaponType is null.
     */
    public Weapon weapon(@Nonnull final WeaponType weaponType) {
        Objects.requireNonNull(weaponType);
        final Weapon weapon = initWeapon(weaponType);
        final int r = rogueRandom.rnd(100);
        int hitPlus = weapon.getHitPlus();
        if (r < 10) {
            weapon.addFlag(ItemFlag.ISCURSED); // 10% chance for cursed
            hitPlus -= rogueRandom.rnd(3) + 1; // Reduce hit bonus by 1-3
            weapon.setHitPlus(hitPlus);
        } else if (r < 15) {
            hitPlus += rogueRandom.rnd(3) + 1; // 5% chance for enhanced, increase by 1-3
            weapon.setHitPlus(hitPlus);
        }
        return weapon;
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
    public Weapon initWeapon(@Nonnull final WeaponType weaponType) {
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
     * Creates and initializes an {@link Armor} of the specified {@link ArmorType}. Applies a
     * 20% chance for a cursed armor with increased armor class (+1 to +3), an 8% chance for
     * reduced armor class (-1 to -3), or no change otherwise.
     *
     * @param armorType The {@link ArmorType} to create.
     * @return The initialized {@link Armor}.
     * @throws NullPointerException if armorType is null.
     */
    public Armor armor(@Nonnull final ArmorType armorType) {
        Objects.requireNonNull(armorType);
        final Armor armor = new Armor(armorType);
        final int r = rogueRandom.rnd(100);
        int armorClass = armor.getArmorClass();
        if (r < 20) {
            armor.addFlag(ItemFlag.ISCURSED); // 20% chance for cursed
            armorClass += rogueRandom.rnd(3) + 1; // Increase armor class by 1-3
            armor.setArmorClass(armorClass);
        } else if (r < 28) {
            armorClass -= rogueRandom.rnd(3) + 1; // 8% chance for reduced, decrease by 1-3
            armor.setArmorClass(armorClass);
        }
        return armor;
    }

    /**
     * Creates and initializes a {@link Ring} of the specified {@link RingType}. Applies default
     * item properties via {@link #initItem(Item)}. For strength, protection, hit, or damage rings,
     * sets a random armor class (0-2), making it cursed with -1 if 0. Aggression and teleport
     * rings are always cursed.
     * <p>
     * Equivalent to ring initialization logic in the C Rogue source (things.c).
     * </p>
     *
     * @param ringType The {@link RingType} to create.
     * @return The initialized {@link Ring}.
     * @throws NullPointerException if ringType is null.
     */
    public Ring ring(@Nonnull final RingType ringType) {
        Objects.requireNonNull(ringType);
        final Ring ring = new Ring(ringType);
        initItem(ring);
        switch (ringType) {
            case R_ADDSTR, R_PROTECT, R_ADDHIT, R_ADDDAM -> {
                ring.setArmorClass(rogueRandom.rnd(3));
                if (ring.getArmorClass() == 0) {
                    ring.setArmorClass(-1);
                    ring.addFlag(ItemFlag.ISCURSED);
                }
            }
            case R_AGGR, R_TELEPORT -> ring.addFlag(ItemFlag.ISCURSED);
        }
        return ring;
    }

    /**
     * Creates and initializes a {@link Rod} of the specified {@link RodType}. Applies default
     * item properties via {@link #initItem(Item)} and configures damage and charges via
     * {@link #fixStick(Rod)}.
     *
     * @param rodType The {@link RodType} to create.
     * @return The initialized {@link Rod}.
     * @throws NullPointerException if rodType is null.
     */
    public Rod rod(@Nonnull final RodType rodType) {
        Objects.requireNonNull(rodType);
        final Rod rod = new Rod(rodType);
        initItem(rod); // Set default count, armor, group, and clear flags
        fixStick(rod); // Configure damage and charges
        return rod;
    }

    /**
     * Configures a rod (wand or staff) by setting its wield damage, throw damage, and charges.
     * Staffs receive stronger wield damage ("2x3"), while wands get weaker ("1x1"). All rods
     * have minimal throw damage ("1x1"). Charges are set to 10-19 for light rods or 3-7 for others.
     * <p>
     * Equivalent to the <code>fix_stick</code> function in the C Rogue source (things.c).
     * </p>
     *
     * @param rod The rod to configure.
     * @throws NullPointerException if rod is null.
     */
    public void fixStick(@Nonnull Rod rod) {
        Objects.requireNonNull(rod);
        if (Objects.equals(getRodForm(rod.getItemSubType()), RodForm.STAFF)) {
            rod.setWieldDamage("2x3");
        } else {
            rod.setWieldDamage("1x1");
        }
        rod.setThrowDamage("1x1");
        if (rod.isType(RodType.WS_LIGHT)) {
            rod.setCharges(rogueRandom.rnd(10) + 10);
        } else {
            rod.setCharges(rogueRandom.rnd(5) + 3);
        }
    }

    /**
     * Returns the RodForm for the specified RodType.
     *
     * @param rodType The RodType to query.
     * @return The RodForm for the corresponding type.
     * @throws IllegalArgumentException if no form for the given type is set.
     */
    @Nonnull
    public RodForm getRodForm(@Nonnull final RodType rodType) {
        Objects.requireNonNull(rodType);
        final RodFormData data = rodFormData.get(rodType);
        if (data == null) throw new IllegalArgumentException();
        return data.form;
    }

    /**
     * Creates a {@link Gold} item with the specified gold value.
     *
     * @param goldValue The amount of gold for the item.
     * @return The initialized {@link Gold} item.
     */
    public Gold gold(final int goldValue) {
        return new Gold(goldValue);
    }

    /**
     * Generates the display name for an inventory item, reflecting its type, count, known status, and usage state.
     * Implements the Rogue C `inv_name` function, producing names like "A blue potion" or "2 staves of lightning (on left hand)".
     */
    @Nonnull
    public String invName(@Nullable Player player, @Nonnull final Item item, boolean dropCapital) {
        // Validate item
        Objects.requireNonNull(item);

        // Retrieve item metadata
        final ObjectType objectType = item.getObjectType();
        Objects.requireNonNull(objectType);

        // Initialize buffer for building the name
        final StringBuilder itemBuf = new StringBuilder();
        final Enum<? extends ItemSubtype> itemSubtype = item.getItemSubType();
        final int count = item.getCount();

        // Generate name based on item type
        switch (objectType) {
            // Delegate naming for potions, rods, and rings to nameIt for consistent formatting
            case POTION, ROD, RING -> itemBuf.append(nameIt(item));
            case SCROLL -> {
                // Format scroll name with count and known/guess status
                if (count == 1) {
                    itemBuf.append("A scroll ");
                } else {
                    itemBuf.append(String.format("%d scrolls ", count));
                }
                if (isKnown(item.getItemSubType())) {
                    final String realName = Templates.findTemplateBySubType(itemSubtype).getName();
                    itemBuf.append(String.format("of %s", realName));
                } else if (getGuessName(itemSubtype) != null) {
                    itemBuf.append(String.format("called %s", getGuessName(itemSubtype)));
                } else {
                    itemBuf.append(String.format("titled '%s'", getName(itemSubtype)));
                }
            }
            case FOOD -> {
                // Differentiate between favorite fruit and standard rations
                if (((Food) item).isFruit()) {
                    final String fruit = config.getFavoriteFruit();
                    if (count == 1) {
                        final String vowelString = RogueUtils.getIndefiniteArticleFor(fruit);
                        final String vowelStringCapitalized = vowelString.substring(0, 1).toUpperCase() + vowelString.substring(1);
                        itemBuf.append(String.format("%s %s", vowelStringCapitalized, fruit));
                    } else {
                        itemBuf.append(String.format("%d %ss", count, fruit));
                    }
                } else if (count == 1) {
                    itemBuf.append("Some food");
                } else {
                    itemBuf.append(String.format("%d rations of food", count));
                }
            }
            case WEAPON -> {
                // Format weapon name with count, bonuses, and optional label
                final String realName = Templates.findTemplateBySubType(itemSubtype).getName();
                if (count > 1) {
                    itemBuf.append(String.format("%d ", count));
                } else {
                    final String vowelString = RogueUtils.getIndefiniteArticleFor(realName);
                    final String vowelStringCapitalized = vowelString.substring(0, 1).toUpperCase() + vowelString.substring(1);
                    itemBuf.append(String.format("%s ", vowelStringCapitalized));
                }
                if (item.hasFlag(ItemFlag.ISKNOW)) {
                    itemBuf.append(String.format("%s %s", ((Weapon) item).num(), realName));
                } else {
                    itemBuf.append(String.format("%s", realName));
                }
                if (count > 1) {
                    itemBuf.append('s');
                }
                if (item.getLabel() != null) {
                    itemBuf.append(String.format(" called %s", item.getLabel()));
                }
            }
            case ARMOR -> {
                // Format armor name with protection details if known
                final String realName = Templates.findTemplateBySubType(itemSubtype).getName();
                if (item.hasFlag(ItemFlag.ISKNOW)) {
                    itemBuf.append(String.format("%s %s [", ((Armor) item).num(), realName));
                    if (!config.isTerse()) {
                        itemBuf.append("protection ");
                    }
                    itemBuf.append(String.format("%d]", config.getMinArmorClass() - item.getArmorClass()));
                } else {
                    itemBuf.append(String.format("%s", realName));
                }
                if (item.getLabel() != null) {
                    itemBuf.append(String.format(" called %s", item.getLabel()));
                }
            }
            // Fixed name for gold based on quantity
            case GOLD -> itemBuf.append(String.format("%d Gold pieces", item.getGoldValue()));
            // Unique name for the Amulet of Yendor
            case AMULET -> {
                final ObjectInfoTemplate objectInfoTemplate = Templates.findTemplateByObjectType(objectType);
                assert objectInfoTemplate != null;
                itemBuf.append(objectInfoTemplate.getName());
            }
            default -> {
                // Handle unknown types in master mode with debug logging
                if (config.isMaster()) {
                    LOGGER.debug("Picked up something funny {}", objectType);
                    itemBuf.append(String.format("Something bizarre %s", objectType));
                }
            }
        }

        // Append usage indicators if enabled and player is provided
        if (config.isInventoryDescribe() && player != null) {
            if (item.equals(player.getCurrentArmor())) {
                itemBuf.append(" (being worn)");
            }
            if (item.equals(player.getCurrentWeapon())) {
                itemBuf.append(" (weapon in hand)");
            }
            if (item.equals(player.getLeftRing())) {
                itemBuf.append(" (on left hand)");
            }
            if (item.equals(player.getRightRing())) {
                itemBuf.append(" (on right hand)");
            }
        }

        // Adjust capitalization based on dropCapital flag
        if (dropCapital && !itemBuf.isEmpty() && Character.isUpperCase(itemBuf.charAt(0))) {
            itemBuf.setCharAt(0, Character.toLowerCase(itemBuf.charAt(0)));
        } else if (!dropCapital && !itemBuf.isEmpty() && Character.isLowerCase(itemBuf.charAt(0))) {
            itemBuf.setCharAt(0, Character.toUpperCase(itemBuf.charAt(0)));
        }

        return itemBuf.toString();
    }

    /**
     * Generates the display name for potions, rings, or rods, reflecting their known, guessed, or unknown state.
     * Mimics the Rogue C `nameit` function, formatting names based on item count, type, material/color, and effect.
     */
    @Nonnull
    private String nameIt(@Nonnull final Item item) {
        // Ensure item is non-null
        Objects.requireNonNull(item);

        // Constant for empty string to avoid magic strings
        final String emptyString = "";

        // Retrieve item metadata
        final ObjectType objectType = item.getObjectType();
        final Enum<? extends ItemSubtype> itemSubType = item.getItemSubType();
        final ObjectInfoTemplate objectInfoTemplate = Templates.findTemplateByObjectType(objectType);

        // Validate subtype and template
        Objects.requireNonNull(itemSubType);
        Objects.requireNonNull(objectInfoTemplate);

        // Return empty string for unsupported types to restrict naming to potions, rings, and rods
        if (!objectType.equals(ObjectType.POTION) && !objectType.equals(ObjectType.RING) &&
                !objectType.equals(ObjectType.ROD)) {
            return emptyString;
        }

        // Initialize buffer for building the name
        final StringBuilder nameBuf = new StringBuilder();

        // Check if item is known or has a guess name
        final boolean isKnown = isKnown(itemSubType);
        final String guessName = getGuessName(itemSubType);
        final int count = item.getCount();

        // Variables for type, appearance, and effect
        String type;
        String which;
        String effect;

        // Determine type, appearance, and effect based on object type
        if (objectType.equals(ObjectType.ROD)) {
            type = getRodFormAsString((RodType) itemSubType);
            which = getRodMaterial((RodType) itemSubType);
            effect = ((Rod) item).chargeStr(config.isTerse());
        } else {
            type = objectInfoTemplate.getName();
            which = getName(itemSubType);
            if (objectType.equals(ObjectType.RING)) {
                effect = ((Ring) item).num();
            } else {
                effect = emptyString;
            }
        }

        // Build name for known or guessed items
        if (isKnown || guessName != null) {
            if (count == 1) {
                nameBuf.append(String.format("A %s ", type));
            } else {
                nameBuf.append(String.format("%d %ss ", count, type));
            }
            if (isKnown) {
                // Use real name from template for known items
                final String realName = Templates.findTemplateBySubType(itemSubType).getName();
                nameBuf.append(String.format("of %s%s(%s)", realName, effect, which));
            } else {
                // Use player-assigned guess name for guessed items
                nameBuf.append(String.format("called %s%s(%s)", getGuessName(itemSubType), effect, which));
            }
        } else if (count == 1) {
            // Format single unknown item with indefinite article and appearance
            final String vowelString = RogueUtils.getIndefiniteArticleFor(which);
            final String vowelStringCapitalized = vowelString.substring(0, 1).toUpperCase() + vowelString.substring(1);
            nameBuf.append(String.format("%s %s %s", vowelStringCapitalized, which, type));
        } else {
            // Format multiple unknown items with count and appearance
            nameBuf.append(String.format("%d %s %ss", count, which, type));
        }

        return nameBuf.toString();
    }

    /**
     * Checks if an item subtype is known.
     *
     * @param itemSubType The item subtype to check.
     * @return True if the subtype is known, false otherwise.
     */
    public boolean isKnown(@Nonnull final Enum<? extends ItemSubtype> itemSubType) {
        Objects.requireNonNull(itemSubType);
        return itemSubTypeKnown.getOrDefault(itemSubType, false);
    }

    /**
     * Retrieves the guess name for an item subtype.
     *
     * @param itemSubType The item subtype to query.
     * @return The guess name, or null if not set.
     */
    @Nullable
    public String getGuessName(@Nonnull final Enum<? extends ItemSubtype> itemSubType) {
        Objects.requireNonNull(itemSubType);
        return itemSubTypeGuessNames.get(itemSubType);
    }

    /**
     * Returns the name for the specified item subtype, if set.
     *
     * @param itemSubType The item subtype to query (e.g., ScrollType.IDENTIFY_SCROLL).
     * @return The name, or null if not set.
     */
    @Nullable
    public String getName(@Nonnull final Enum<? extends ItemSubtype> itemSubType) {
        Objects.requireNonNull(itemSubType);
        return itemSubTypeNames.get(itemSubType);
    }

    /**
     * Returns the form ("wand" or "staff") for the specified RodType.
     *
     * @param rodType The RodType to query.
     * @return The form as a String ("wand" or "staff"), or null if not set.
     */
    @Nonnull
    public String getRodFormAsString(@Nonnull final RodType rodType) {
        Objects.requireNonNull(rodType);
        final RodFormData data = rodFormData.get(rodType);
        if (data == null) throw new IllegalArgumentException();
        return data.form == RodForm.WAND ? FORM_WAND : FORM_STAFF;
    }

    /**
     * Returns the material (metal or wood) for the specified RodType.
     *
     * @param rodType The RodType to query.
     * @return The material name, or null if not set.
     */
    @Nonnull
    public String getRodMaterial(@Nonnull final RodType rodType) {
        Objects.requireNonNull(rodType);
        final RodFormData data = rodFormData.get(rodType);
        if (data == null) throw new IllegalArgumentException();
        return data.material;
    }

    /**
     * Sets whether an item subtype is known.
     *
     * @param itemSubType The item subtype to mark as known or unknown.
     * @param isKnown     True if the subtype is known, false otherwise.
     */
    public void setKnown(@Nonnull final Enum<? extends ItemSubtype> itemSubType, boolean isKnown) {
        Objects.requireNonNull(itemSubType);
        itemSubTypeKnown.put(itemSubType, isKnown);
    }

    /**
     * Assigns a guess name to an item subtype.
     *
     * @param itemSubType The item subtype to assign a guess name to.
     * @param guessName   The guess name to assign, or null to clear.
     */
    public void setGuessName(@Nonnull final Enum<? extends ItemSubtype> itemSubType, @Nullable final String guessName) {
        Objects.requireNonNull(itemSubType);
        itemSubTypeGuessNames.put(itemSubType, guessName);
    }

    public int getRingWorth(@Nonnull final RingType ringType) {
        return ringWorthMap.get(ringType);
    }

    public Config getConfig() {
        return config;
    }

    public RogueRandom getRogueRandom() {
        return rogueRandom;
    }

    /**
     * A record containing the result of a random {@link ObjectType} selection, including
     * the selected type, optional {@link ItemSubtype}, bad pick status, a formatted message,
     * and the templates checked for bad picks.
     *
     * @param objectType       The selected {@link ObjectType}.
     * @param itemSubType      The selected {@link ItemSubtype}, or null if not applicable.
     * @param isBadPick        True if no template matched the random number, false otherwise.
     * @param badPickMessage   The formatted message for bad picks, null if no issue.
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

    private static class Stone {
        @JsonProperty("name")
        String name;
        @JsonProperty("value")
        int value;
    }

    private record RodFormData(RodForm form, String material) {
    }

}
