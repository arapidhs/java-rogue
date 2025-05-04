package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Potion extends Item {

    public Potion(@Nonnull final Enum<PotionType> potionType) {
        super(ObjectType.POTION, potionType, 1);
        Objects.requireNonNull(potionType);
    }

    @Override
    public PotionType getItemSubType() {
        return (PotionType) super.getItemSubType();
    }

}
