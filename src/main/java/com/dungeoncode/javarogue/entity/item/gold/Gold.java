package com.dungeoncode.javarogue.entity.item.gold;

import com.dungeoncode.javarogue.entity.item.Item;
import com.dungeoncode.javarogue.entity.item.ItemFlag;
import com.dungeoncode.javarogue.entity.item.ObjectType;

public class Gold extends Item {

    private static final int GOLDGRP=1;

    public Gold(final int goldValue) {
        super(ObjectType.GOLD, null, 1);
        setGoldValue(goldValue);
        addFlag(ItemFlag.ISMANY);
        setGroup(GOLDGRP);
    }

}
