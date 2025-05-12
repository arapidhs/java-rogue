package com.dungeoncode.javarogue.system.entity.creature;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Monster extends Creature {

    private final MonsterType monsterType;

    public Monster(@Nonnull final MonsterType monsterType) {
        Objects.requireNonNull(monsterType);
        this.monsterType = monsterType;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }
}
