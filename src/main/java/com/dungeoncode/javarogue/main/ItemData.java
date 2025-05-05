package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;

/**
 * Manages names and other properties/metadata for game item subtypes, including default and custom names.
 * Provides methods for name assignment and scroll name initialization with random generation.
 */
public class ItemData {

    private static final String FORM_WAND = "wand";
    private static final String FORM_STAFF = "staff";

    private static final String SYLLABLES_JSON_PATH = "/data/syllables.json";
    private static final String COLORS_JSON_PATH = "/data/colors.json";
    private static final String STONES_JSON_PATH = "/data/stones.json";
    private static final String METALS_JSON_PATH = "/data/metals.json";
    private static final String WOODS_JSON_PATH = "/data/woods.json";

    private final int maxScrollGeneratedNameLength;
    private final RogueRandom random;
    private final Map<ItemSubtype, String> itemSubTypeNames;
    private final Map<RingType, Integer> ringWorthMap;
    private final Map<RodType, RodFormData> rodFormData;
    /**
     * Constructs an ItemData instance with a random number generator for future name generation.
     *
     * @param random                       The RogueRandom instance for random operations.
     * @param maxScrollGeneratedNameLength The maximum String length of randomly generated scroll names.
     */
    public ItemData(@Nonnull final RogueRandom random, final int maxScrollGeneratedNameLength) {
        Objects.requireNonNull(random);
        this.random = random;
        this.maxScrollGeneratedNameLength = maxScrollGeneratedNameLength;
        this.itemSubTypeNames = new HashMap<>();
        this.ringWorthMap = new HashMap<>();
        this.rodFormData = new HashMap<>();
    }

    public void init() {
        itemSubTypeNames.clear();
        ringWorthMap.clear();
        rodFormData.clear();
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
            throw new RuntimeException(String.format(Messages.ERROR_FAILED_TO_LOAD_DATA,COLORS_JSON_PATH), e);
        }
    }

    /**
     * Initializes random stone names and updated worth for each RingType, ensuring no stone is reused.
     * Stores names in itemSubTypeNames and worth in ringWorth.
     */
    private void initializeRings() {
        try (InputStream in = getClass().getResourceAsStream(STONES_JSON_PATH)) {
            final ObjectMapper mapper = new ObjectMapper();
            final  List<Stone> stones = mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, Stone.class));
            final List<Stone> availableStones = new ArrayList<>(stones);

            for (RingType ringType : RingType.values()) {
                int index = random.rnd(availableStones.size());
                final Stone stone = availableStones.remove(index);
                setName(ringType, stone.name);
                final RingInfoTemplate template = Templates.findTemplateBySubType(RingInfoTemplate.class, ringType);
                ringWorthMap.put(ringType, template.getWorth() + stone.value);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(Messages.ERROR_FAILED_TO_LOAD_DATA,STONES_JSON_PATH), e);
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
    public void setName(@Nonnull final ItemSubtype itemSubType, @Nonnull final String name) {
        Objects.requireNonNull(itemSubType);
        Objects.requireNonNull(name);
        if (name.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_NAME);
        }
        itemSubTypeNames.put(itemSubType, name);
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
     * Returns the name for the specified item subtype, if set.
     *
     * @param itemSubType The item subtype to query (e.g., ScrollType.IDENTIFY_SCROLL).
     * @return The name, or null if not set.
     */
    @Nullable
    public String getName(@Nonnull final ItemSubtype itemSubType) {
        Objects.requireNonNull(itemSubType);
        return itemSubTypeNames.get(itemSubType);
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

    private static class RodFormData {
        final RodForm form;
        final String material;

        RodFormData(RodForm form, String material) {
            this.form = form;
            this.material = material;
        }
    }

}