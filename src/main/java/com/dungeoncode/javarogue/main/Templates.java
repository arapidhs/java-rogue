package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Templates {

    private static final Set<AbstractMap.SimpleEntry<String, Class<? extends Template>>> TEMPLATE_SOURCES = Set.of(
            new AbstractMap.SimpleEntry<>("/data/monsters.json", MonsterTemplate.class),
            new AbstractMap.SimpleEntry<>("/data/killtypes.json", KillTypeTemplate.class));

    private static final Set<Template> TEMPLATES_ALL = TEMPLATE_SOURCES.stream()
            .flatMap(entry -> loadTemplates(entry.getKey(), entry.getValue()).values().stream())
            .collect(Collectors.toUnmodifiableSet());

    private static final Map<Class<?>, Map<Long, Template>> TEMPLATES_BY_TYPE = TEMPLATES_ALL.stream().collect(
            Collectors.groupingBy(Template::getClass,
                    Collectors.toUnmodifiableMap(Template::getId, t -> t)));

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

    @SuppressWarnings("unchecked")
    public static <T extends AbstractTemplate> T getTemplate(@Nonnull final Class<T> type, final long id) {
        Objects.requireNonNull(type);
        final Map<Long, Template> typedMap = TEMPLATES_BY_TYPE.get(type);
        if (typedMap == null) {
            return null;
        }
        return (T) typedMap.get(id);
    }


}
