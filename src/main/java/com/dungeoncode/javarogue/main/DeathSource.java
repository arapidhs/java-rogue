package com.dungeoncode.javarogue.main;

import java.util.Objects;

/**
 * Represents a source of death in the game, selected randomly from:
 * <ul>
 *     <li>Monster templates</li>
 *     <li>Kill type templates</li>
 *     <li>Default fallback name</li>
 * </ul>
 * <p>
 * Provides type information, template ID if applicable, and the display name.
 * </p>
 */
public final class DeathSource {

    public enum Type {
        MONSTER,
        KILL_TYPE,
        DEFAULT
    }

    private final Type type;
    private final long templateId;
    private final String name;

    public DeathSource(final Type type, final long templateId, final String name) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.templateId = templateId;
        this.name = Objects.requireNonNull(name, "name cannot be null");
    }

    public Type getType() {
        return type;
    }

    public long getTemplateId() {
        return templateId;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns {@code true} if the death source represents a template
     * (either a monster or a kill type), otherwise {@code false}.
     *
     * @return {@code true} if this source is a template, otherwise {@code false}
     */
    public boolean isTemplate() {
        return type == Type.MONSTER || type == Type.KILL_TYPE;
    }
}

