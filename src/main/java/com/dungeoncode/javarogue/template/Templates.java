package com.dungeoncode.javarogue.template;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.system.entity.item.ItemSubtype;
import com.dungeoncode.javarogue.system.entity.item.ObjectType;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages template loading and retrieval for game objects, including monsters, items, and their properties.
 * Loads templates from JSON resources and provides methods to access them by type, ID, or subtype.
 */
public class Templates {

    // Defines JSON resource paths and their corresponding template classes
    private static final Set<AbstractMap.SimpleEntry<String, Class<? extends Template>>> TEMPLATE_SOURCES = Set.of(
            new AbstractMap.SimpleEntry<>("/data/monsters.json", MonsterTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/dragon-breath.json", DragonBreathTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/killtypes.json", KillTypeTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/objects-info.json", ObjectInfoTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/armors-info.json", ArmorInfoTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/potions-info.json", PotionInfoTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/scrolls-info.json", ScrollInfoTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/weapons-info.json", WeaponInfoTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/rods-info.json", RodInfoTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/rings-info.json", RingInfoTemplate.class));

    // Stores all loaded templates from JSON resources
    private static final Set<Template> TEMPLATES_ALL = TEMPLATE_SOURCES.stream()
            .flatMap(entry -> loadTemplates(entry.getKey(), entry.getValue()).values().stream())
            .collect(Collectors.toUnmodifiableSet());

    // Maps template classes to their instances, indexed by ID
    private static final Map<Class<?>, Map<Long, Template>> TEMPLATES_BY_TYPE = TEMPLATES_ALL.stream().collect(
            Collectors.groupingBy(Template::getClass,
                    Collectors.toUnmodifiableMap(Template::getId, t -> t)));

    static {
        // Apply cumulative probability to ObjectInfoTemplate subclasses
        List<Class<? extends ObjectInfoTemplate>> cumulativeTemplates = List.of(
                ObjectInfoTemplate.class,
                ArmorInfoTemplate.class,
                PotionInfoTemplate.class,
                ScrollInfoTemplate.class,
                WeaponInfoTemplate.class,
                RodInfoTemplate.class,
                RingInfoTemplate.class
        );

        cumulativeTemplates.forEach(Templates::applyCumulativeProbability);
    }

    /**
     * Loads templates from a JSON resource into a map indexed by ID.
     *
     * @param resourcePath The path to the JSON resource.
     * @param type         The template class to deserialize into.
     * @param <T>          The template type extending Template.
     * @return A map of template IDs to template instances.
     * @throws RuntimeException If loading fails.
     */
    private static <T extends Template> Map<Long, T> loadTemplates(String resourcePath, Class<T> type) {
        try (InputStream in = Templates.class.getResourceAsStream(resourcePath)) {
            final ObjectMapper mapper = new ObjectMapper();
            final List<T> list = mapper.readValue(
                    in,
                    mapper.getTypeFactory().constructCollectionType(List.class, type));

            return list.stream().collect(Collectors.toMap(Template::getId, t -> t));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to load templates from %s", resourcePath), e);
        }
    }

    /**
     * Retrieves a template by its class and ID.
     *
     * @param type The template class.
     * @param id   The template ID.
     * @param <T>  The template type extending AbstractTemplate.
     * @return The matching template, or null if not found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractTemplate> T getTemplate(@Nonnull final Class<T> type, final long id) {
        Objects.requireNonNull(type);
        final Map<Long, Template> typedMap = TEMPLATES_BY_TYPE.get(type);
        if (typedMap == null) {
            return null;
        }
        return (T) typedMap.get(id);
    }

    /**
     * Finds a template by its ItemSubType across all ObjectInfoTemplate subclasses.
     *
     * @param itemSubType The ItemSubType to match.
     * @return The matching template.
     * @throws IllegalStateException If no template is found for the given ItemSubType.
     */
    @Nonnull
    public static ObjectInfoTemplate findTemplateBySubType(@Nonnull final Enum<? extends ItemSubtype> itemSubType) {
        Objects.requireNonNull(itemSubType);
        return (ObjectInfoTemplate) TEMPLATES_ALL.stream()
                .filter(t -> t instanceof ObjectInfoTemplate)
                .filter(t -> Objects.equals(((ObjectInfoTemplate) t).getItemSubType(), itemSubType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format(Messages.ERROR_NO_OBJECT_INFO_TEMPLATE_FOUND, "ObjectInfoTemplate", itemSubType)));
    }

    /**
     * Applies cumulative probability to ObjectInfoTemplate entries for random selection.
     *
     * @param templateClass The template class to process.
     */
    private static void applyCumulativeProbability(final Class<? extends ObjectInfoTemplate> templateClass) {
        final List<? extends ObjectInfoTemplate> sorted = getTemplates(templateClass).stream()
                .sorted(Comparator.comparingLong(ObjectInfoTemplate::getId))
                .toList();

        double cumulative = 0;
        for (ObjectInfoTemplate template : sorted) {
            cumulative += template.getProbability();
            template.setCumulativeProbability(cumulative);
        }
    }

    /**
     * Retrieves all templates of the specified class.
     *
     * @param type The template class.
     * @param <T>  The template type extending Template.
     * @return An unmodifiable set of templates, or empty if none found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Template> Set<T> getTemplates(@Nonnull final Class<T> type) {
        Objects.requireNonNull(type);
        final Map<Long, Template> map = TEMPLATES_BY_TYPE.get(type);
        if (map == null) {
            return Set.of();
        }

        return map.values().stream()
                .map(t -> (T) t)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Finds an ObjectInfoTemplate by its ObjectType.
     *
     * @param objectType The ObjectType to match.
     * @return The matching ObjectInfoTemplate, or null if not found.
     */
    @Nullable
    public static ObjectInfoTemplate findTemplateByObjectType(@Nonnull final ObjectType objectType) {
        Objects.requireNonNull(objectType);
        Set<ObjectInfoTemplate> templates = getTemplates(ObjectInfoTemplate.class);
        return templates.stream()
                .filter(t -> t.getObjectType() == objectType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifies that cumulative probabilities sum to 100 for ObjectInfoTemplate subclasses.
     *
     * @return An Optional containing BadTemplateInfo if any class fails verification, empty otherwise.
     */
    public static Optional<BadTemplateInfo> verifyProbabilities() {
        List<Class<? extends ObjectInfoTemplate>> cumulativeTemplates = List.of(
                ObjectInfoTemplate.class,
                ArmorInfoTemplate.class,
                PotionInfoTemplate.class,
                ScrollInfoTemplate.class,
                WeaponInfoTemplate.class,
                RodInfoTemplate.class,
                RingInfoTemplate.class
        );

        for (Class<? extends ObjectInfoTemplate> templateClass : cumulativeTemplates) {
            final Set<? extends ObjectInfoTemplate> templates = getTemplates(templateClass);
            if (templates.isEmpty()) continue;

            final List<? extends ObjectInfoTemplate> sorted = templates.stream()
                    .sorted(Comparator.comparingLong(ObjectInfoTemplate::getId))
                    .toList();

            final double lastCumulative = sorted.get(sorted.size() - 1).getCumulativeProbability();

            if (lastCumulative != 100.0) {
                return Optional.of(new BadTemplateInfo(templateClass, sorted.size()));
            }
        }

        return Optional.empty();
    }

    /**
     * Record holding information about invalid template probability sets.
     */
    public record BadTemplateInfo(Class<? extends ObjectInfoTemplate> templateClass, int bound) {
        public BadTemplateInfo(@Nonnull final Class<? extends ObjectInfoTemplate> templateClass, int bound) {
            Objects.requireNonNull(templateClass);
            this.templateClass = templateClass;
            this.bound = bound;
        }
    }
}