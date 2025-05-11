package com.dungeoncode.javarogue.main;

public class Gold extends Item {

    private static final int GOLDGRP=1;

    public Gold(final int goldValue) {
        super(ObjectType.GOLD, null, 1);
        setGoldValue(goldValue);
        addFlag(ItemFlag.ISMANY);
        setGroup(GOLDGRP);
    }

}
