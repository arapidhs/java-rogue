package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;

/**
 * Manages names and other properties/metadata for game item subtypes, including default and custom names.
 * Provides methods for name assignment and scroll name initialization with random generation.
 */
public class ItemData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemData.class);

    private static final String FORM_WAND = "wand";
    private static final String FORM_STAFF = "staff";

    private static final String SYLLABLES_JSON_PATH = "/data/syllables.json";
    private static final String COLORS_JSON_PATH = "/data/colors.json";
    private static final String STONES_JSON_PATH = "/data/stones.json";
    private static final String METALS_JSON_PATH = "/data/metals.json";
    private static final String WOODS_JSON_PATH = "/data/woods.json";

    private final Config config;
    private final RogueRandom random;
    private final int maxScrollGeneratedNameLength;
    private final Map<Enum<? extends ItemSubtype>, String> itemSubTypeNames;
    private final Map<RingType, Integer> ringWorthMap;
    private final Map<RodType, RodFormData> rodFormData;
    private final Map<Enum<? extends ItemSubtype>, Boolean> itemSubTypeKnown;
    private final Map<Enum<? extends ItemSubtype>, String> itemSubTypeGuessNames;

    /**
     * Constructs an ItemData instance with a random number generator for future name generation.
     *
     * @param random The RogueRandom instance for random operations.
     * @param config The Config instance.
     */
    public ItemData(@Nonnull final Config config, @Nonnull final RogueRandom random) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(random);
        this.config = config;
        this.random = random;
        this.maxScrollGeneratedNameLength = config.getMaxScrollItemGeneratedNameLength();
        this.itemSubTypeNames = new HashMap<>();
        this.ringWorthMap = new HashMap<>();
        this.rodFormData = new HashMap<>();
        this.itemSubTypeKnown = new HashMap<>();
        this.itemSubTypeGuessNames = new HashMap<>();
    }

    public void init() {
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
                int numWords = random.rnd(3) + 2;

                while (numWords-- > 0) {
                    // 1 to 3 syllables per word
                    int numSyllables = random.rnd(3) + 1;
                    while (numSyllables-- > 0) {
                        // Randomly select a syllable
                        String syllable = syllables.get(random.rnd(syllables.size()));
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
                int index = random.rnd(availableColors.size());
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
                int index = random.rnd(availableStones.size());
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
                    if (random.rnd(2) == 0 && !availableMetals.isEmpty()) {
                        // Select wand with random metal
                        int index = random.rnd(availableMetals.size());
                        material = availableMetals.remove(index);
                        form = RodForm.WAND;
                        break;
                    } else if (!availableWoods.isEmpty()) {
                        // Select staff with random wood
                        int index = random.rnd(availableWoods.size());
                        material = availableWoods.remove(index);
                        form = RodForm.STAFF;
                        break;
                    }
                }
                rodFormData.put(rodType, new RodFormData(form, material));
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
            type = getRodForm((RodType) itemSubType);
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
    public String getRodForm(@Nonnull final RodType rodType) {
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

    private static class Stone {
        @JsonProperty("name")
        String name;
        @JsonProperty("value")
        int value;
    }

    private record RodFormData(RodForm form, String material) {
    }

}