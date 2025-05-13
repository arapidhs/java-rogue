package com.dungeoncode.javarogue.system.entity.creature;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.template.MonsterTemplate;
import com.dungeoncode.javarogue.template.Templates;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Monster extends Creature {

    private final MonsterType monsterType;
    private final SymbolType symbolType;

    /**
     * Symbol when the creature appears as disguised.
     * <p>
     * Equivalent of:
     * <pre>tp -> t_disguise</pre>
     */
    private SymbolType disguiseSymbolType;

    public Monster(@Nonnull final MonsterType monsterType) {
        Objects.requireNonNull(monsterType);
        this.monsterType = monsterType;

        final MonsterTemplate template = Templates.getMonsterTemplate(monsterType);
        assert template != null;
        this.symbolType = template.getSymbolType();
        this.disguiseSymbolType=symbolType;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    @Override
    public SymbolType getSymbolType() {
        return symbolType;
    }

    public SymbolType getDisguiseSymbolType() {
        return disguiseSymbolType;
    }

    public void setDisguiseSymbolType(SymbolType disguiseSymbolType) {
        this.disguiseSymbolType = disguiseSymbolType;
    }
}
