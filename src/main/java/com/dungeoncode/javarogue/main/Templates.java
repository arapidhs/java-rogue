package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Templates {

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


    private static final Set<Template> TEMPLATES_ALL = TEMPLATE_SOURCES.stream()
            .flatMap(entry -> loadTemplates(entry.getKey(), entry.getValue()).values().stream())
            .collect(Collectors.toUnmodifiableSet());

    private static final Map<Class<?>, Map<Long, Template>> TEMPLATES_BY_TYPE = TEMPLATES_ALL.stream().collect(
            Collectors.groupingBy(Template::getClass,
                    Collectors.toUnmodifiableMap(Template::getId, t -> t)));

    static {
        // Apply cumulative probability to ObjectInfoTemplate entries
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

    @SuppressWarnings("unchecked")
    public static <T extends AbstractTemplate> T getTemplate(@Nonnull final Class<T> type, final long id) {
        Objects.requireNonNull(type);
        final Map<Long, Template> typedMap = TEMPLATES_BY_TYPE.get(type);
        if (typedMap == null) {
            return null;
        }
        return (T) typedMap.get(id);
    }

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

    public record BadTemplateInfo(Class<? extends ObjectInfoTemplate> templateClass, int bound) {
        public BadTemplateInfo(@Nonnull final Class<? extends ObjectInfoTemplate> templateClass, int bound) {
            Objects.requireNonNull(templateClass);
            this.templateClass = templateClass;
            this.bound = bound;
        }
    }

}
