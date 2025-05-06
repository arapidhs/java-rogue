package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeaponsFactoryTest {

    static final int weaponCreationIterations = 100;

    @Test
    void testInitializeWeapon() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final WeaponsFactory weaponsFactory = new WeaponsFactory(rogueRandom);

        final Weapon longSword = weaponsFactory.initializeWeapon(WeaponType.LONG_SWORD);
        final String longSwordWieldDamage = "3x4";
        final String longSwordThrowDamage = "1x2";
        assertEquals(longSwordWieldDamage, longSword.getWieldDamage());
        assertEquals(longSwordThrowDamage, longSword.getThrowDamage());
        assertEquals(0, longSword.getGroup());
        assertNull(longSword.getLaunchWeapon());
        assertTrue(longSword.getItemFlags().isEmpty());

        for (int i = 0; i < weaponCreationIterations; i++) {
            final Weapon dagger = weaponsFactory.initializeWeapon(WeaponType.DAGGER);
            assertTrue(dagger.getGroup() > 0);
            assertTrue(dagger.getCount() > 1);

            final Weapon dart = weaponsFactory.initializeWeapon(WeaponType.DART);
            assertTrue(dart.getGroup() > 1);
            assertTrue(dart.hasFlag(ItemFlag.ISMANY));
            assertTrue(dart.hasFlag(ItemFlag.ISMISL));
            assertTrue(dart.getCount() > 7);
        }

    }
}
