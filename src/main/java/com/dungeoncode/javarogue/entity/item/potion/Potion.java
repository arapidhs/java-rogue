package com.dungeoncode.javarogue.entity.item.potion;

import com.dungeoncode.javarogue.entity.item.Item;
import com.dungeoncode.javarogue.entity.item.ObjectType;

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
