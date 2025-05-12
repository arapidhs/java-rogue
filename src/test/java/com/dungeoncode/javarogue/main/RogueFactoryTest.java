package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.entity.creature.MonsterType;
import com.dungeoncode.javarogue.system.entity.item.ItemFlag;
import com.dungeoncode.javarogue.system.entity.item.ObjectType;
import com.dungeoncode.javarogue.system.entity.item.Weapon;
import com.dungeoncode.javarogue.system.entity.item.WeaponType;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RogueFactoryTest {

    /**
     * Tests the {@link RogueFactory#rndThing(int)} method to ensure correct random object type selection.
     * Verifies that:
     * <ul>
     *   <li>At or above the amulet level, the amulet ({@link ObjectType#AMULET}) can be selected.</li>
     *   <li>Below the amulet level, the amulet is never selected, and a non-null object type is returned.</li>
     * </ul>
     * Uses a fixed seed (428012268L) for reproducible results and tests 1000 iterations at random levels.
     */
    @Test
    void testRndThing(){
        final long amuletSeed=428012268L;
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(amuletSeed);
        final RogueFactory rogueFactory=new RogueFactory(config,rogueRandom);

        final int amuletLevel=config.getAmuletLevel();
        final ObjectType amuletObjectType = rogueFactory.rndThing(amuletLevel);
        assertEquals(ObjectType.AMULET,amuletObjectType);

        for(int i=0;i<1000;i++){
            final int level=rogueRandom.rnd(config.getAmuletLevel());
            final ObjectType objectType=rogueFactory.rndThing(level);
            assertNotNull(objectType);
            assertNotEquals(ObjectType.AMULET,objectType);
        }
    }

    /**
     * Repeatedly tests the {@link RogueFactory#pickOne()} method to ensure valid random
     * object type selection. Verifies that the returned {@link RogueFactory.PickResult}
     * is non-null, contains a non-null {@link ObjectType}, is not a bad pick, and has
     * null bad pick message and checked templates list, using a fixed seed for reproducible
     * results.
     */
    @RepeatedTest(100)
    void testPickOne(){
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final RogueFactory rogueFactory=new RogueFactory(config,rogueRandom);

        final RogueFactory.PickResult pickResult = rogueFactory.pickOne();
        assertNotNull(pickResult);
        assertNotNull(pickResult.objectType());
        assertFalse(pickResult.isBadPick());
        assertNull(pickResult.badPickMessage());
        assertNull(pickResult.checkedTemplates());
    }

    /**
     * Repeatedly tests the {@link RogueFactory#randMonster(boolean, int)} method to ensure
     * valid random monster selection. Verifies that non-wandering selections are from
     * {@link RogueFactory#LVL_MONS} and wandering selections are from
     * {@link RogueFactory#WAND_MONS}, with non-null results, using a random level up to
     * the amulet level and a fixed seed for reproducibility.
     */
    @RepeatedTest(50)
    void testRandMonster(){
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final RogueFactory rogueFactory=new RogueFactory(config,rogueRandom);

        boolean wander=false;
        int level=rogueRandom.rnd(config.getAmuletLevel());
        MonsterType monsterType = rogueFactory.randMonster(wander, level);
        assertNotNull(monsterType);
        assertTrue(RogueFactory.LVL_MONS.contains(monsterType));

        wander=true;
        monsterType = rogueFactory.randMonster(wander, level);
        assertNotNull(monsterType);
        assertTrue(RogueFactory.WAND_MONS.contains(monsterType));
    }

    @RepeatedTest(100)
    void testWeapon() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final RogueFactory rogueFactory = new RogueFactory(config,rogueRandom);

        final Weapon longSword = rogueFactory.weapon(WeaponType.LONG_SWORD);
        final String longSwordWieldDamage = "3x4";
        final String longSwordThrowDamage = "1x2";
        assertEquals(longSwordWieldDamage, longSword.getWieldDamage());
        assertEquals(longSwordThrowDamage, longSword.getThrowDamage());
        assertEquals(0, longSword.getGroup());
        assertNull(longSword.getLaunchWeapon());
        assertTrue(longSword.getItemFlags().isEmpty());

        final Weapon dagger = rogueFactory.weapon(WeaponType.DAGGER);
        assertTrue(dagger.getGroup() > 0);
        assertTrue(dagger.getCount() > 1);

        final Weapon dart = rogueFactory.weapon(WeaponType.DART);
        assertTrue(dart.getGroup() > 1);
        assertTrue(dart.hasFlag(ItemFlag.ISMANY));
        assertTrue(dart.hasFlag(ItemFlag.ISMISL));
        assertTrue(dart.getCount() > 7);

    }

}
