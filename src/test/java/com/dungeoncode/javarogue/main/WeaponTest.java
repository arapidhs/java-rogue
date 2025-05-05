package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeaponTest {

    @Test
    void numTest(){
        final Config config = new Config();
        final RogueRandom rogueRandom =  new RogueRandom(config.getSeed());
        final WeaponsFactory weaponsFactory = new WeaponsFactory(rogueRandom);

        final Weapon mace = weaponsFactory.initializeWeapon(WeaponType.MACE);
        final String zeroBonus = "+0,+0";
        assertEquals(zeroBonus,mace.num());

        mace.setHitPlus(-1);
        mace.setDamagePlus(+2);
        final String negativePositiveBonus = "-1,+2";
        assertEquals(negativePositiveBonus,mace.num());
    }

}
