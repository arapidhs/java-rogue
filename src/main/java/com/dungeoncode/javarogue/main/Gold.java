package com.dungeoncode.javarogue.main;

public class Gold extends Item {

    public Gold(final int goldValue) {
        super(ObjectType.GOLD, null, 1);
        setGoldValue(goldValue);
    }

}
