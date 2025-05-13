package com.dungeoncode.javarogue.system.entity.creature;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.template.MonsterTemplate;
import com.dungeoncode.javarogue.template.Templates;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Monster extends Creature {

    private final MonsterType monsterType;
    private final SymbolType symbolType;

    public Monster(@Nonnull final MonsterType monsterType) {
        Objects.requireNonNull(monsterType);
        this.monsterType = monsterType;

        final MonsterTemplate template = Templates.getMonsterTemplate(monsterType);
        assert template != null;
        this.symbolType = template.getSymbolType();
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    @Override
    public SymbolType getSymbolType() {
        return symbolType;
    }

}
