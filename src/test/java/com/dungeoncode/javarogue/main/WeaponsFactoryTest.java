package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.config.Config;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.entity.item.ItemFlag;
import com.dungeoncode.javarogue.entity.item.weapon.Weapon;
import com.dungeoncode.javarogue.entity.item.weapon.WeaponType;
import com.dungeoncode.javarogue.entity.item.weapon.WeaponsFactory;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class WeaponsFactoryTest {


    @RepeatedTest(100)
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
