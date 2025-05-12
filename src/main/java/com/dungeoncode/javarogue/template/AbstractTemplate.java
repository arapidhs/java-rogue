package com.dungeoncode.javarogue.template;

import java.util.Objects;

public abstract class AbstractTemplate implements Template {
    protected final long id;

    protected AbstractTemplate(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        AbstractTemplate other = (AbstractTemplate) o;
        return id == other.id;
    }

}
