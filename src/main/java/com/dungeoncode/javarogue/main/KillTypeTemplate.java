package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

public class KillTypeTemplate extends AbstractTemplate {

    private final KillType killType;
    private final String name;
    private final boolean useArticle;

    @JsonCreator
    public KillTypeTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("killType") final @Nonnull KillType killType,
            @JsonProperty("name") final @Nonnull String name,
            @JsonProperty("useArticle") final boolean useArticle) {
        super(id);
        this.killType = Objects.requireNonNull(killType);
        this.name = Objects.requireNonNull(name);
        this.useArticle = useArticle;
    }

    public KillType getKillType() {
        return killType;
    }

    public String getName() {
        return name;
    }

    public boolean isUseArticle() {
        return useArticle;
    }

}
