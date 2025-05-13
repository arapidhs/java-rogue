package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.creature.MonsterType;
import com.dungeoncode.javarogue.system.entity.item.*;
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
        final RogueFactory rogueFactory = getRogueFactory();
        rogueFactory.getRogueRandom().reseed(amuletSeed);

        final int amuletLevel=getRogueFactory().getConfig().getAmuletLevel();
        final ObjectType amuletObjectType = rogueFactory.rndThing(amuletLevel);
        assertEquals(ObjectType.AMULET,amuletObjectType);

        for(int i=0;i<1000;i++){
            final int level=rogueFactory.getRogueRandom().rnd(amuletLevel);
            final ObjectType objectType=rogueFactory.rndThing(level);
            assertNotNull(objectType);
            assertNotEquals(ObjectType.AMULET,objectType);
        }
    }

    /**
     * Repeatedly tests the {@link RogueFactory#pickOne(ObjectType)} method to ensure valid random
     * object type selection. Verifies that the returned {@link RogueFactory.PickResult}
     * is non-null, contains a non-null {@link ObjectType}, is not a bad pick, and has
     * null bad pick message and checked templates list, using a fixed seed for reproducible
     * results.
     */
    @RepeatedTest(100)
    void testPickOne(){
        final RogueFactory rogueFactory = getRogueFactory();

        ObjectType objectType=null;
        RogueFactory.PickResult pickResult = rogueFactory.pickOne(objectType);
        assertNotNull(pickResult);
        assertNotNull(pickResult.objectType());
        assertNull(pickResult.itemSubType());
        assertFalse(pickResult.isBadPick());
        assertNull(pickResult.badPickMessage());
        assertNull(pickResult.checkedTemplates());

        objectType=ObjectType.ARMOR;
        pickResult = rogueFactory.pickOne(objectType);
        assertNotNull(pickResult);
        assertEquals(objectType,pickResult.objectType());
        assertNotNull(pickResult.itemSubType());
        assertFalse(pickResult.isBadPick());
        assertInstanceOf(ArmorType.class, pickResult.itemSubType());

        objectType=ObjectType.POTION;
        pickResult = rogueFactory.pickOne(objectType);
        assertNotNull(pickResult);
        assertEquals(objectType,pickResult.objectType());
        assertNotNull(pickResult.itemSubType());
        assertFalse(pickResult.isBadPick());
        assertInstanceOf(PotionType.class, pickResult.itemSubType());
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
        final RogueFactory rogueFactory = getRogueFactory();

        boolean wander=false;
        int level=rogueFactory.getRogueRandom().rnd(rogueFactory.getConfig().getAmuletLevel());
        MonsterType monsterType = rogueFactory.randMonster(wander, level);
        assertNotNull(monsterType);
        assertTrue(RogueFactory.LVL_MONS.contains(monsterType));

        wander=true;
        monsterType = rogueFactory.randMonster(wander, level);
        assertNotNull(monsterType);
        assertTrue(RogueFactory.WAND_MONS.contains(monsterType));
    }

    /**
     * Repeatedly tests {@link RogueFactory#initWeapon(WeaponType)} for correct weapon initialization.
     * Verifies damage, group, count, and flags for long sword, dagger, and dart.
     */
    @RepeatedTest(100)
    void testInitWeapon() {
        final RogueFactory rogueFactory = getRogueFactory();

        final Weapon longSword = rogueFactory.initWeapon(WeaponType.LONG_SWORD);
        final String longSwordWieldDamage = "3x4";
        final String longSwordThrowDamage = "1x2";
        assertEquals(longSwordWieldDamage, longSword.getWieldDamage());
        assertEquals(longSwordThrowDamage, longSword.getThrowDamage());
        assertEquals(0, longSword.getGroup());
        assertNull(longSword.getLaunchWeapon());
        assertTrue(longSword.getItemFlags().isEmpty());

        final Weapon dagger = rogueFactory.initWeapon(WeaponType.DAGGER);
        assertTrue(dagger.getGroup() > 0);
        assertTrue(dagger.getCount() > 1);

        final Weapon dart = rogueFactory.initWeapon(WeaponType.DART);
        assertTrue(dart.getGroup() > 1);
        assertTrue(dart.hasFlag(ItemFlag.ISMANY));
        assertTrue(dart.hasFlag(ItemFlag.ISMISL));
        assertTrue(dart.getCount() > 7);
    }

    /**
     * Tests {@link RogueFactory#food()} for correct food type selection.
     * Verifies fruit/non-fruit status with specific seeds.
     */
    @Test
    void testFood(){
        final RogueFactory rogueFactory=getRogueFactory();
        rogueFactory.getRogueRandom().reseed(1);
        Food food = rogueFactory.food();
        assertNotNull(food);
        assertTrue(food.isFruit());

        rogueFactory.getRogueRandom().reseed(5);
        food = rogueFactory.food();
        assertNotNull(food);
        assertFalse(food.isFruit());
    }

    /**
     * Tests {@link RogueFactory#potion(PotionType)} for correct potion initialization.
     * Verifies subtype, count, and symbol type.
     */
    @Test
    void testPotion(){
        final RogueFactory rogueFactory=getRogueFactory();
        final PotionType potionType=PotionType.HEALING;
        Potion potion = rogueFactory.potion(potionType);
        assertNotNull(potion);
        assertEquals(potionType,potion.getItemSubType());
        assertEquals(1,potion.getCount());
        assertEquals(SymbolType.POTION,potion.getSymbolType());
    }

    /**
     * Tests {@link RogueFactory#scroll(ScrollType)} for correct scroll initialization.
     * Verifies subtype, count, and symbol type.
     */
    @Test
    void testScroll(){
        final RogueFactory rogueFactory=getRogueFactory();
        final ScrollType scrollType=ScrollType.IDENTIFY_SCROLL;
        Scroll scroll = rogueFactory.scroll(scrollType);
        assertNotNull(scroll);
        assertEquals(scrollType,scroll.getItemSubType());
        assertEquals(1,scroll.getCount());
        assertEquals(SymbolType.SCROLL,scroll.getSymbolType());
    }

    /**
     * Tests {@link RogueFactory#weapon(WeaponType)} for correct weapon initialization.
     * Verifies subtype, count, group, symbol type, and cursed status with specific seeds.
     */
    @Test
    void testWeapon(){
        final RogueFactory rogueFactory=getRogueFactory();
        final long weaponSeed=60;
        rogueFactory.getRogueRandom().reseed(weaponSeed);

        final WeaponType weaponType=WeaponType.LONG_SWORD;
        Weapon weapon = rogueFactory.weapon(weaponType);
        assertNotNull(weapon);
        assertEquals(weaponType,weapon.getItemSubType());
        assertEquals(1,weapon.getCount());
        assertEquals(0,weapon.getGroup());
        assertEquals(SymbolType.WEAPON,weapon.getSymbolType());
        assertFalse(weapon.hasFlag(ItemFlag.ISCURSED));

        final long cursedWeaponSeed=10;
        rogueFactory.getRogueRandom().reseed(cursedWeaponSeed);
        weapon = rogueFactory.weapon(weaponType);
        assertTrue(weapon.hasFlag(ItemFlag.ISCURSED));
    }

    /**
     * Tests {@link RogueFactory#armor(ArmorType)} for correct armor initialization.
     * Verifies subtype, count, group, symbol type, and cursed status with specific seeds.
     */
    @Test
    void testArmor() {
        final RogueFactory rogueFactory = getRogueFactory();
        final long armorSeed=200;
        rogueFactory.getRogueRandom().reseed(armorSeed);

        final ArmorType armorType=ArmorType.LEATHER;
        Armor armor=rogueFactory.armor(armorType);
        assertNotNull(armor);
        assertEquals(armorType,armor.getItemSubType());
        assertEquals(1,armor.getCount());
        assertEquals(0,armor.getGroup());
        assertEquals(SymbolType.ARMOR,armor.getSymbolType());
        assertFalse(armor.hasFlag(ItemFlag.ISCURSED));

        final long cursedArmorSeed=10;
        rogueFactory.getRogueRandom().reseed(cursedArmorSeed);
        armor=rogueFactory.armor(armorType);
        assertTrue(armor.hasFlag(ItemFlag.ISCURSED));
    }

    /**
     * Tests {@link RogueFactory#gold(int)} for correct gold initialization.
     * Verifies gold value and symbol type.
     */
    @Test
    void testGold() {
        final RogueFactory rogueFactory = getRogueFactory();
        final int goldValue=100;
        final Gold gold =rogueFactory.gold(goldValue);
        assertNotNull(gold);
        assertNull(gold.getItemSubType());
        assertEquals(goldValue,gold.getGoldValue());
        assertEquals(SymbolType.GOLD,gold.getSymbolType());
    }

    /**
     * Tests {@link RogueFactory#ring(RingType)} for correct ring initialization.
     * Verifies subtype, count, group, symbol type, and cursed status for specific ring types.
     */
    @Test
    void testRing() {
        final RogueFactory rogueFactory = getRogueFactory();
        final long ringSeed=100;
        rogueFactory.getRogueRandom().reseed(ringSeed);

        RingType ringType=RingType.R_ADDHIT;
        Ring ring=rogueFactory.ring(ringType);
        assertNotNull(ring);
        assertEquals(ringType,ring.getItemSubType());
        assertEquals(1,ring.getCount());
        assertEquals(0,ring.getGroup());
        assertEquals(SymbolType.RING,ring.getSymbolType());
        assertFalse(ring.hasFlag(ItemFlag.ISCURSED));

        ringType=RingType.R_AGGR;
        ring=rogueFactory.ring(ringType);
        assertTrue(ring.hasFlag(ItemFlag.ISCURSED));
    }

    /**
     * Repeatedly tests {@link RogueFactory#rod(RodType)} for correct rod initialization.
     * Verifies subtype, count, group, symbol type, and charge ranges for slow and light rods.
     */
    @RepeatedTest(50)
    void testRod() {
        final RogueFactory rogueFactory = getRogueFactory();

        RodType rodType=RodType.WS_SLOW_M;
        Rod rod=rogueFactory.rod(rodType);
        assertNotNull(rod);
        assertEquals(rodType,rod.getItemSubType());
        assertEquals(1,rod.getCount());
        assertEquals(0,rod.getGroup());
        assertEquals(SymbolType.ROD,rod.getSymbolType());
        assertTrue(rod.getCharges()<8);

        rodType=RodType.WS_LIGHT;
        rod=rogueFactory.rod(rodType);
        assertTrue(rod.getCharges()>9);
        assertTrue(rod.getCharges()<20);
    }

    /**
     * Creates a {@link RogueFactory} with initialized {@link ItemData} for testing.
     * Uses a default {@link Config} and {@link RogueRandom}.
     *
     * @return The initialized {@link RogueFactory}.
     */
    private RogueFactory getRogueFactory() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final ItemData itemData=new ItemData(config,rogueRandom);
        final RogueFactory rogueFactory = new RogueFactory(config,rogueRandom,itemData);
        itemData.init();
        return rogueFactory;
    }

}
