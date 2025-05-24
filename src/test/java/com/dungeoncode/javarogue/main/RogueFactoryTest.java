package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.creature.*;
import com.dungeoncode.javarogue.system.entity.item.*;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.dungeoncode.javarogue.template.RingInfoTemplate;
import com.dungeoncode.javarogue.template.Templates;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

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
    void testRndThing() {
        final long amuletSeed = 428012268L;
        final RogueFactory rogueFactory = getRogueFactory();
        rogueFactory.getRogueRandom().reseed(amuletSeed);

        final int amuletLevel = getRogueFactory().getConfig().getAmuletLevel();
        final ObjectType amuletObjectType = rogueFactory.rndThing(amuletLevel);
        assertEquals(ObjectType.AMULET, amuletObjectType);

        for (int i = 0; i < 1000; i++) {
            final int level = rogueFactory.getRogueRandom().rnd(amuletLevel);
            final ObjectType objectType = rogueFactory.rndThing(level);
            assertNotNull(objectType);
            assertNotEquals(ObjectType.AMULET, objectType);
        }
    }

    /**
     * Creates a {@link RogueFactory} for testing.
     * Uses a default {@link Config} and {@link RogueRandom}.
     *
     * @return The initialized {@link RogueFactory}.
     */
    private RogueFactory getRogueFactory() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        return new RogueFactory(config, rogueRandom);
    }

    /**
     * Repeatedly tests the {@link RogueFactory#pickOne(ObjectType)} method to ensure valid random
     * object type selection. Verifies that the returned {@link RogueFactory.PickResult}
     * is non-null, contains a non-null {@link ObjectType}, is not a bad pick, and has
     * null bad pick message and checked templates list, using a fixed seed for reproducible
     * results.
     */
    @RepeatedTest(100)
    void testPickOne() {
        final RogueFactory rogueFactory = getRogueFactory();

        ObjectType objectType = null;
        RogueFactory.PickResult pickResult = rogueFactory.pickOne(objectType);
        assertNotNull(pickResult);
        assertNotNull(pickResult.objectType());
        assertNull(pickResult.itemSubType());
        assertFalse(pickResult.isBadPick());
        assertNull(pickResult.badPickMessage());
        assertNull(pickResult.checkedTemplates());

        objectType = ObjectType.ARMOR;
        pickResult = rogueFactory.pickOne(objectType);
        assertNotNull(pickResult);
        assertEquals(objectType, pickResult.objectType());
        assertNotNull(pickResult.itemSubType());
        assertFalse(pickResult.isBadPick());
        assertInstanceOf(ArmorType.class, pickResult.itemSubType());

        objectType = ObjectType.POTION;
        pickResult = rogueFactory.pickOne(objectType);
        assertNotNull(pickResult);
        assertEquals(objectType, pickResult.objectType());
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
    void testRandMonster() {
        final RogueFactory rogueFactory = getRogueFactory();

        boolean wander = false;
        int level = rogueFactory.getRogueRandom().rnd(rogueFactory.getConfig().getAmuletLevel());
        MonsterType monsterType = rogueFactory.randMonster(wander, level);
        assertNotNull(monsterType);
        assertTrue(RogueFactory.LVL_MONS.contains(monsterType));

        wander = true;
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
    void testFood() {
        final RogueFactory rogueFactory = getRogueFactory();
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
    void testPotion() {
        final RogueFactory rogueFactory = getRogueFactory();
        final PotionType potionType = PotionType.HEALING;
        Potion potion = rogueFactory.potion(potionType);
        assertNotNull(potion);
        assertEquals(potionType, potion.getItemSubType());
        assertEquals(1, potion.getCount());
        assertEquals(SymbolType.POTION, potion.getSymbolType());
    }

    /**
     * Tests {@link RogueFactory#scroll(ScrollType)} for correct scroll initialization.
     * Verifies subtype, count, and symbol type.
     */
    @Test
    void testScroll() {
        final RogueFactory rogueFactory = getRogueFactory();
        final ScrollType scrollType = ScrollType.IDENTIFY_SCROLL;
        Scroll scroll = rogueFactory.scroll(scrollType);
        assertNotNull(scroll);
        assertEquals(scrollType, scroll.getItemSubType());
        assertEquals(1, scroll.getCount());
        assertEquals(SymbolType.SCROLL, scroll.getSymbolType());
    }

    /**
     * Tests {@link RogueFactory#weapon(WeaponType)} for correct weapon initialization.
     * Verifies subtype, count, group, symbol type, and cursed status with specific seeds.
     */
    @Test
    void testWeapon() {
        final RogueFactory rogueFactory = getRogueFactory();
        final long weaponSeed = 60;
        rogueFactory.getRogueRandom().reseed(weaponSeed);

        final WeaponType weaponType = WeaponType.LONG_SWORD;
        Weapon weapon = rogueFactory.weapon(weaponType);
        assertNotNull(weapon);
        assertEquals(weaponType, weapon.getItemSubType());
        assertEquals(1, weapon.getCount());
        assertEquals(0, weapon.getGroup());
        assertEquals(SymbolType.WEAPON, weapon.getSymbolType());
        assertFalse(weapon.hasFlag(ItemFlag.ISCURSED));

        final long cursedWeaponSeed = 10;
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
        final long armorSeed = 200;
        rogueFactory.getRogueRandom().reseed(armorSeed);

        final ArmorType armorType = ArmorType.LEATHER;
        Armor armor = rogueFactory.armor(armorType);
        assertNotNull(armor);
        assertEquals(armorType, armor.getItemSubType());
        assertEquals(1, armor.getCount());
        assertEquals(0, armor.getGroup());
        assertEquals(SymbolType.ARMOR, armor.getSymbolType());
        assertFalse(armor.hasFlag(ItemFlag.ISCURSED));

        final long cursedArmorSeed = 10;
        rogueFactory.getRogueRandom().reseed(cursedArmorSeed);
        armor = rogueFactory.armor(armorType);
        assertTrue(armor.hasFlag(ItemFlag.ISCURSED));
    }

    /**
     * Tests {@link RogueFactory#gold(int)} for correct gold initialization.
     * Verifies gold value and symbol type.
     */
    @Test
    void testGold() {
        final RogueFactory rogueFactory = getRogueFactory();
        final int goldValue = 100;
        final Gold gold = rogueFactory.gold(goldValue);
        assertNotNull(gold);
        assertNull(gold.getItemSubType());
        assertEquals(goldValue, gold.getGoldValue());
        assertEquals(SymbolType.GOLD, gold.getSymbolType());
    }

    /**
     * Tests {@link RogueFactory#ring(RingType)} for correct ring initialization.
     * Verifies subtype, count, group, symbol type, and cursed status for specific ring types.
     */
    @Test
    void testRing() {
        final RogueFactory rogueFactory = getRogueFactory();
        final long ringSeed = 100;
        rogueFactory.getRogueRandom().reseed(ringSeed);

        RingType ringType = RingType.R_ADDHIT;
        Ring ring = rogueFactory.ring(ringType);
        assertNotNull(ring);
        assertEquals(ringType, ring.getItemSubType());
        assertEquals(1, ring.getCount());
        assertEquals(0, ring.getGroup());
        assertEquals(SymbolType.RING, ring.getSymbolType());
        assertFalse(ring.hasFlag(ItemFlag.ISCURSED));

        ringType = RingType.R_AGGR;
        ring = rogueFactory.ring(ringType);
        assertTrue(ring.hasFlag(ItemFlag.ISCURSED));
    }

    /**
     * Repeatedly tests {@link RogueFactory#rod(RodType)} for correct rod initialization.
     * Verifies subtype, count, group, symbol type, and charge ranges for slow and light rods.
     */
    @RepeatedTest(50)
    void testRod() {
        final RogueFactory rogueFactory = getRogueFactory();

        RodType rodType = RodType.WS_SLOW_M;
        Rod rod = rogueFactory.rod(rodType);
        assertNotNull(rod);
        assertEquals(rodType, rod.getItemSubType());
        assertEquals(1, rod.getCount());
        assertEquals(0, rod.getGroup());
        assertEquals(SymbolType.ROD, rod.getSymbolType());
        assertTrue(rod.getCharges() < 8);

        rodType = RodType.WS_LIGHT;
        rod = rogueFactory.rod(rodType);
        assertTrue(rod.getCharges() > 9);
        assertTrue(rod.getCharges() < 20);
    }

    /**
     * Tests the {@link RogueFactory#expAdd(int, int)} method for correct experience point calculations.
     * Verifies additional experience points for various monster levels and hit points, matching the
     * C Rogue <code>exp_add</code> logic.
     */
    @Test
    void testExpAdd() {
        final RogueFactory rogueFactory = getRogueFactory();

        // Test level 1: maxHP / 8
        assertEquals(2, rogueFactory.expAdd(1, 16), "Level 1 with 16 HP should yield 16/8 = 2");
        assertEquals(1, rogueFactory.expAdd(1, 8), "Level 1 with 8 HP should yield 8/8 = 1");

        // Test levels 2-6: maxHP / 6
        assertEquals(2, rogueFactory.expAdd(2, 12), "Level 2 with 12 HP should yield 12/6 = 2");
        assertEquals(4, rogueFactory.expAdd(6, 24), "Level 6 with 24 HP should yield 24/6 = 4");

        // Test levels 7-9: (maxHP / 6) * 4
        assertEquals(16, rogueFactory.expAdd(7, 24), "Level 7 with 24 HP should yield (24/6) * 4 = 16");
        assertEquals(24, rogueFactory.expAdd(9, 36), "Level 9 with 36 HP should yield (36/6) * 4 = 24");

        // Test levels 10+: (maxHP / 6) * 20
        assertEquals(40, rogueFactory.expAdd(10, 12), "Level 10 with 12 HP should yield (12/6) * 20 = 40");
        assertEquals(100, rogueFactory.expAdd(15, 30), "Level 15 with 30 HP should yield (30/6) * 20 = 100");
    }

    /**
     * Tests the {@link RogueFactory#monster(MonsterType, int)} method for correct monster creation.
     * Verifies stats, flags, and disguise for various monster types and levels, including level-based
     * adjustments, haste flag for high levels, and Xeroc’s random disguise, using specific seeds for
     * predictable results.
     */
    @Test
    void testMonster() {
        final RogueFactory rogueFactory = getRogueFactory();
        final long seed = 150;
        rogueFactory.getRogueRandom().reseed(seed); // Set seed for predictable random results

        // Test basic monster (BAT, level 1, below amulet level)
        final Monster bat = rogueFactory.monster(MonsterType.BAT, 1);
        // BAT monster should not be null
        assertNotNull(bat);
        // Monster type should be BAT
        assertEquals(MonsterType.BAT, bat.getMonsterType());
        Stats batStats = bat.getStats();
        // BAT level should be 1
        assertEquals(1, batStats.getLevel());
        // BAT max HP should be 1-8
        assertTrue(batStats.getMaxHitPoints() >= 1 && batStats.getMaxHitPoints() <= 8);
        // BAT armor should be 3
        assertEquals(3, batStats.getArmor());
        // BAT damage should be 1x2
        assertEquals("1x2", batStats.getDamage());
        // BAT strength should be 10
        assertEquals(10, batStats.getStrength());
        // BAT flags should include ISFLY
        assertEquals(EnumSet.of(CreatureFlag.ISFLY), bat.getCreatureFlags());
        // BAT turn should be enabled
        assertTrue(bat.isTurn());
        // BAT inventory should be null
        assertNull(bat.getInventory());
        // BAT should have no disguise, same as it is primary symbol
        assertEquals(bat.getSymbolType(), bat.getDisguiseSymbolType());

        // Test level adjustment (DRAGON, level 30, above amulet level)
        rogueFactory.getRogueRandom().reseed(seed); // Reset seed
        final Monster dragon = rogueFactory.monster(MonsterType.DRAGON, 30);
        // DRAGON monster should not be null
        assertNotNull(dragon);
        Stats dragonStats = dragon.getStats();
        int levelAdd = Math.max(0, 30 - rogueFactory.getConfig().getAmuletLevel()); // e.g., 30 - 26 = 4
        // DRAGON level should be 10 + levelAdd
        assertEquals(10 + levelAdd, dragonStats.getLevel());
        // DRAGON armor should be -1 - levelAdd
        assertEquals(-1 - levelAdd, dragonStats.getArmor());
        // DRAGON experience should include level adjustment
        assertTrue(dragonStats.getExperience() >= 5000 + levelAdd * 10);
        // DRAGON at level 30 should have ISHASTE flag
        assertTrue(dragon.getCreatureFlags().contains(CreatureFlag.ISHASTE));

        // Test Xeroc with disguise (level 10)
        rogueFactory.getRogueRandom().reseed(200); // Specific seed for predictable disguise
        final Monster xeroc = rogueFactory.monster(MonsterType.XEROC, 10);
        // XEROC monster should not be null
        assertNotNull(xeroc);
        // XEROC should have a disguise symbol
        assertNotNull(xeroc.getDisguiseSymbolType());
        // XEROC base symbol should be a monster symbol
        assertTrue(SymbolType.MONSTER_SYMBOLS.contains(xeroc.getSymbolType()));
        // XEROC disguise should differ from base symbol
        assertNotSame(xeroc.getDisguiseSymbolType(), xeroc.getSymbolType());
    }

    /**
     * Tests initialization of RogueFactory, ensuring names, forms, and worth are set for all item subtypes.
     */
    @Test
    void testInitializeRogueFactory() {
        RogueFactory rogueFactory = getRogueFactory();

        // Verify all ScrollTypes have generated names
        Arrays.stream(ScrollType.values())
                .forEach(scrollType -> assertNotNull(rogueFactory.getName(scrollType)));

        // Verify all PotionTypes have assigned color names
        Arrays.stream(PotionType.values())
                .forEach(potionType -> assertNotNull(rogueFactory.getName(potionType)));

        // Verify all RingTypes have assigned stone names
        Arrays.stream(RingType.values())
                .forEach(ringType -> assertNotNull(rogueFactory.getName(ringType)));

        // Verify all RodTypes have assigned forms (wand or staff)
        Arrays.stream(RodType.values())
                .forEach(rodType -> assertNotNull(rogueFactory.getRodFormAsString(rodType)));
        assertNotNull(rogueFactory.getRodMaterial(RodType.WS_SLOW_M));

        // Check base worth of ADORNMENT ring template
        final RingInfoTemplate adornmentRingTemplate = (RingInfoTemplate) Templates.findTemplateBySubType(RingType.R_NOP);
        final int ringTemplateValue = adornmentRingTemplate.getWorth();
        assertTrue(ringTemplateValue > 0);

        // Ensure ADORNMENT ring’s worth includes stone value
        final int ringStoneWorth = rogueFactory.getRingWorth(RingType.R_NOP);
        assertTrue(ringStoneWorth > ringTemplateValue);
    }

    /**
     * Tests setting and checking known status for item subtypes, including reset after re-initialization.
     */
    @Test
    void testIsKnown() {
        final RogueFactory rogueFactory = getRogueFactory();

        // Initial state: no subtypes are known
        assertFalse(rogueFactory.isKnown(ScrollType.HOLD_MONSTER));
        assertFalse(rogueFactory.isKnown(RingType.R_ADDHIT));

        // Mark subtypes as known
        rogueFactory.setKnown(ScrollType.HOLD_MONSTER, true);
        rogueFactory.setKnown(RingType.R_ADDHIT, true);

        // Verify known status is updated
        assertTrue(rogueFactory.isKnown(ScrollType.HOLD_MONSTER));
        assertTrue(rogueFactory.isKnown(RingType.R_ADDHIT));

        // Re-initialize to reset known status
        rogueFactory.init();
        assertFalse(rogueFactory.isKnown(ScrollType.HOLD_MONSTER));
        assertFalse(rogueFactory.isKnown(RingType.R_ADDHIT));
    }

    /**
     * Tests setting and retrieving guess names for item subtypes, including reset after re-initialization.
     */
    @Test
    void testGuessName() {
        final RogueFactory rogueFactory = getRogueFactory();

        final String guessNameHoldMonster = "Hold Monster Scroll";
        final String guessNameDexterityRing = "Dexterity Ring";

        // Initial state: no guess names set
        assertNull(rogueFactory.getGuessName(ScrollType.HOLD_MONSTER));
        assertNull(rogueFactory.getGuessName(RingType.R_ADDHIT));

        // Assign guess names
        rogueFactory.setGuessName(ScrollType.HOLD_MONSTER, guessNameHoldMonster);
        rogueFactory.setGuessName(RingType.R_ADDHIT, guessNameDexterityRing);

        // Verify guess names are correctly stored
        assertEquals(guessNameHoldMonster, rogueFactory.getGuessName(ScrollType.HOLD_MONSTER));
        assertEquals(guessNameDexterityRing, rogueFactory.getGuessName(RingType.R_ADDHIT));

        // Re-initialize to clear guess names
        rogueFactory.init();
        assertNull(rogueFactory.getGuessName(ScrollType.HOLD_MONSTER));
        assertNull(rogueFactory.getGuessName(RingType.R_ADDHIT));
    }

    /**
     * Tests the invName method for Potion items, verifying correct name formatting based on count, known status, guess names,
     * and capitalization. Iterates multiple times to ensure consistency across random initializations.
     */
    @RepeatedTest(50)
    void testInvNamePotion() {
        final RogueFactory rogueFactory = getRogueFactory();

        // Test with lowercase initial for dropping items
        boolean dropCapital = true;

        // Create a HASTE_SELF potion for testing
        final Potion item = new Potion(PotionType.HASTE_SELF);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

        // Verify unknown single potion starts with "a" or "an" and ends with "potion"
        String name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("an? .+ potion"));

        // Verify unknown single potion with uppercase initial
        name = rogueFactory.invName(null, item, !dropCapital);
        assertTrue(name.matches("An? .+ potion"));

        // Test plural form for multiple unknown potions
        int count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " .+ potions"));

        // Test single potion with a guess name
        count = 1;
        final String guessedName = "guessed name";
        item.setCount(count);
        rogueFactory.setGuessName(item.getItemSubType(), guessedName);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a potion called " + guessedName + "\\(.+\\)"));

        // Test multiple potions with a guess name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " potions called " + guessedName + "\\(.+\\)"));

        // Test single known potion with real name
        count = 1;
        item.setCount(count);
        rogueFactory.setKnown(item.getItemSubType(), true);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a potion of " + realName + "\\(.+\\)"));

        // Test multiple known potions with real name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " potions of " + realName + "\\(.+\\)"));
    }

    /**
     * Tests the invName method for Rod items, verifying correct name formatting for wands and staffs based on count, known
     * status, guess names, charges, and capitalization. Iterates multiple times to ensure consistency across random material
     * assignments and tests charge display for known rods.
     */
    @RepeatedTest(50)
    void testInvNameRod() {
        final RogueFactory rogueFactory = getRogueFactory();

        // Test with lowercase initial for dropping items
        boolean dropCapital = true;

        // Create a SLOW_MONSTER rod for testing
        final Rod item = new Rod(RodType.WS_SLOW_M);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

        // Verify unknown single rod starts with "a" or "an" and ends with "staff" or "wand"
        String name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("an? .+ (staff|wand)"));

        // Verify unknown single rod with uppercase initial
        name = rogueFactory.invName(null, item, !dropCapital);
        assertTrue(name.matches("An? .+ (staff|wand)"));

        // Test plural form for multiple unknown rods
        int count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " .+ (staffs|wands)"));

        // Test single rod with a guess name
        count = 1;
        final String guessedName = "guessed name";
        item.setCount(count);
        rogueFactory.setGuessName(item.getItemSubType(), guessedName);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a (staff|wand) called " + guessedName + "\\(.+\\)"));

        // Test multiple rods with a guess name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " (staffs|wands) called " + guessedName + "\\(.+\\)"));

        // Test single known rod with real name
        count = 1;
        item.setCount(count);
        rogueFactory.setKnown(item.getItemSubType(), true);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a (staff|wand) of " + realName + "\\(.+\\)"));

        // Test multiple known rods with real name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " (staffs|wands) of " + realName + "\\(.+\\)"));

        // Test known rod with specific charges to verify charge display
        rogueFactory.init();
        final Rod rodItem = new Rod(RodType.WS_SLOW_M);
        final String rodRealName = Templates.findTemplateBySubType(rodItem.getItemSubType()).getName();
        final int charges = 3;
        rodItem.setCharges(charges);
        rodItem.addFlag(ItemFlag.ISKNOW);

        // Mark rod as known and verify name includes charge count
        rogueFactory.setKnown(rodItem.getItemSubType(), true);
        String rodInvName = rogueFactory.invName(null, rodItem, dropCapital);
        assertTrue(rodInvName.matches("a (staff|wand) of " + rodRealName + " \\[" + charges + " charges]\\(.+\\)"));
    }

    /**
     * Tests the invName method for Ring items, verifying correct name formatting based on count, known status, guess names,
     * armor class bonuses, and usage indicators (worn on left/right hand). Iterates multiple times to ensure consistency
     * across random stone assignments and tests specific cases for non-bonus rings and ring usage.
     */
    @RepeatedTest(50)
    void testInvNameRing() {
        final RogueFactory rogueFactory = getRogueFactory();

        // Test with lowercase initial for dropping items
        boolean dropCapital = true;

        // Create a PROTECTION ring for testing
        final Ring item = new Ring(RingType.R_PROTECT);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

        // Verify unknown single ring starts with "a" or "an" and ends with "ring"
        String name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("an? .+ ring"));

        // Verify unknown single ring with uppercase initial
        name = rogueFactory.invName(null, item, !dropCapital);
        assertTrue(name.matches("An? .+ ring"));

        // Test plural form for multiple unknown rings
        int count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " .+ rings"));

        // Test single ring with a guess name
        count = 1;
        final String guessedName = "guessed name";
        item.setCount(count);
        rogueFactory.setGuessName(item.getItemSubType(), guessedName);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a ring called " + guessedName + "\\(.+\\)"));

        // Test multiple rings with a guess name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " rings called " + guessedName + "\\(.+\\)"));

        // Test single known ring with real name
        count = 1;
        item.setCount(count);
        rogueFactory.setKnown(item.getItemSubType(), true);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a ring of " + realName + "\\(.+\\)"));

        // Test single known ring with armor class bonus
        int armorClass = 2;
        item.setCount(count);
        rogueFactory.setKnown(item.getItemSubType(), true);
        item.addFlag(ItemFlag.ISKNOW);
        item.setArmorClass(armorClass);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a ring of " + realName + " \\[\\+" + armorClass + "]\\(.+\\)"));

        // Test multiple known rings with armor class bonus
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " rings of " + realName + " \\[\\+" + armorClass + "]\\(.+\\)"));

        // Test known ADORNMENT ring without armor class bonus
        rogueFactory.init();
        final Ring ringItem = new Ring(RingType.R_NOP);
        final String ringRealName = Templates.findTemplateBySubType(ringItem.getItemSubType()).getName();
        rogueFactory.setKnown(ringItem.getItemSubType(), true);

        item.addFlag(ItemFlag.ISKNOW);
        armorClass = 3;
        ringItem.setArmorClass(armorClass);

        // Verify ADORNMENT ring omits bonus due to ring type
        String ringName = rogueFactory.invName(null, ringItem, dropCapital);
        assertTrue(ringName.matches("a ring of " + ringRealName + "\\(.+\\)"));

        // Test ring usage indicators when worn
        final Player player = new Player(rogueFactory.getConfig());
        player.setLeftRing(ringItem);
        name = rogueFactory.invName(player, ringItem, dropCapital);
        assertTrue(name.matches("a ring of " + ringRealName + "\\(.+\\)" + " \\(on left hand\\)"));

        // Verify right hand usage indicator
        player.setLeftRing(null);
        player.setRightRing(ringItem);
        name = rogueFactory.invName(player, ringItem, dropCapital);
        assertTrue(name.matches("a ring of " + ringRealName + "\\(.+\\)" + " \\(on right hand\\)"));
    }

    /**
     * Tests the invName method for Scroll items, verifying correct name formatting based on count, known status, guess names,
     * and capitalization. Iterates multiple times to ensure consistency across random scroll name generations.
     */
    @RepeatedTest(50)
    void testInvNameScroll() {
        final RogueFactory rogueFactory = getRogueFactory();

        // Test with lowercase initial for dropping items
        boolean dropCapital = true;

        // Create a HOLD_MONSTER scroll for testing
        final Scroll item = new Scroll(ScrollType.HOLD_MONSTER);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

        // Verify unknown single scroll starts with "a" and uses titled name
        String name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a scroll titled '.+'"));

        // Verify unknown single scroll with uppercase initial
        name = rogueFactory.invName(null, item, !dropCapital);
        assertTrue(name.matches("A scroll titled '.+'"));

        // Test plural form for multiple unknown scrolls
        int count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " scrolls titled '.+'"));

        // Test single scroll with a guess name
        count = 1;
        final String guessedName = "guessed name";
        item.setCount(count);
        rogueFactory.setGuessName(item.getItemSubType(), guessedName);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a scroll called " + guessedName));

        // Test multiple scrolls with a guess name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " scrolls called " + guessedName));

        // Test single known scroll with real name
        count = 1;
        item.setCount(count);
        rogueFactory.setKnown(item.getItemSubType(), true);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches("a scroll of " + realName));

        // Test multiple known scrolls with real name
        count = 2;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertTrue(name.matches(count + " scrolls of " + realName));
    }

    /**
     * Tests the invName method for Food items, verifying correct name formatting for standard rations and favorite fruit,
     * including singular/plural forms and capitalization. Ensures proper handling of fruit vs. ration distinction.
     */
    @Test
    void testInvNameFood() {
        final RogueFactory rogueFactory = getRogueFactory();
        final Config config = rogueFactory.getConfig();

        // Test with uppercase initial for inventory listing
        boolean dropCapital = false;
        // Create a standard food ration for testing
        final Food item = new Food();
        // Verify single standard food is named correctly
        String name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("Some food", name);

        // Test plural form for multiple standard rations
        int count = 3;
        item.setCount(3);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals(count + " rations of food", name);

        // Switch to favorite fruit and verify plural fruit name
        item.setFruit(true);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals(count + " " + config.getFavoriteFruit() + "s", name);

        // Test single favorite fruit with indefinite article
        count = 1;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("A " + config.getFavoriteFruit(), name);
    }

    /**
     * Tests the invName method for Weapon items, verifying correct name formatting based on count, known status with hit/damage
     * bonuses, custom labels, and usage indicators (weapon in hand). Ensures proper singular/plural forms and capitalization.
     */
    @Test
    void testInvNameWeapon() {
        final RogueFactory rogueFactory = getRogueFactory();
        final Config config = rogueFactory.getConfig();

        // Test with uppercase initial for inventory listing
        boolean dropCapital = false;
        // Create a TWO_HANDED_SWORD for testing
        final Weapon item = new Weapon(WeaponType.TWO_HANDED_SWORD);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();
        // Verify single unknown weapon uses indefinite article and real name
        String name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("A " + realName, name);

        // Test plural form for multiple unknown weapons
        int count = 3;
        item.setCount(3);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals(count + " " + realName + "s", name);

        // Test single known weapon with hit and damage bonuses
        count = 1;
        item.setCount(count);
        item.addFlag(ItemFlag.ISKNOW);
        final int hitPlus = 2;
        final int damagePlus = 3;
        item.setHitPlus(hitPlus);
        item.setDamagePlus(damagePlus);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("A +" + hitPlus + ",+" + damagePlus + " " + realName, name);

        // Verify addition of custom label to known weapon
        final String label = "flame sword";
        item.setLabel(label);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("A +" + hitPlus + ",+" + damagePlus + " " + realName + " called " + label, name);

        // Test usage indicator when weapon is equipped
        final Player player = new Player(config);
        player.setCurrentWeapon(item);
        name = rogueFactory.invName(player, item, dropCapital);
        assertEquals("A +" + hitPlus + ",+" + damagePlus + " " + realName + " called " + label + " (weapon in hand)", name);
    }

    /**
     * Tests the invName method for Armor items, verifying correct name formatting based on count, known status with armor
     * class bonuses, custom labels, and usage indicators (being worn). Ensures proper handling of singular/plural forms and
     * protection values.
     */
    @Test
    void testInvNameArmor() {
        final RogueFactory rogueFactory = getRogueFactory();
        final Config config = rogueFactory.getConfig();

        // Test with lowercase initial for dropping items
        boolean dropCapital = true;
        // Create a BANDED_MAIL armor for testing
        final Armor item = new Armor(ArmorType.BANDED_MAIL);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();
        // Verify single unknown armor uses real name without bonuses
        String name = rogueFactory.invName(null, item, dropCapital);
        assertEquals(realName, name);

        // Test multiple unknown armors (still uses singular name as count is ignored)
        int count = 3;
        item.setCount(count);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals(realName, name);

        // Test single known armor with armor class bonus and protection
        count = 1;
        item.setCount(count);
        item.addFlag(ItemFlag.ISKNOW);
        final int armorClassDiff = -1;
        item.setArmorClass(item.getArmorClass() + armorClassDiff);
        final int protection = config.getMinArmorClass() - item.getArmorClass();
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("+" + count + " " + realName + " [protection " + protection + "]", name);

        // Verify addition of custom label to known armor
        final String label = "secret armor";
        item.setLabel(label);
        name = rogueFactory.invName(null, item, dropCapital);
        assertEquals("+" + count + " " + realName + " [protection " + protection + "] called " + label, name);

        // Test usage indicator when armor is equipped
        final Player player = new Player(config);
        player.setCurrentArmor(item);
        name = rogueFactory.invName(player, item, dropCapital);
        assertEquals("+" + count + " " + realName + " [protection " + protection + "] called " + label + " (being worn)", name);
    }

    @Test
    void testInvNameGold() {
        final RogueFactory rogueFactory = getRogueFactory();

        boolean dropCapital = true;
        final int goldValue = 100;
        final Gold item = new Gold(goldValue);

        final String name = rogueFactory.invName(null, item, dropCapital);
        assertEquals(goldValue + " Gold pieces", name);
    }

    @Test
    void testInvNameAmulet() {
        final RogueFactory rogueFactory = getRogueFactory();

        boolean dropCapital = false;
        final Item item = new Item(ObjectType.AMULET, null, 1);
        final String name = rogueFactory.invName(null, item, dropCapital);
        final ObjectInfoTemplate objectInfoTemplate = Templates.findTemplateByObjectType(ObjectType.AMULET);
        assertNotNull(objectInfoTemplate);
        assertEquals(objectInfoTemplate.getName(), name);
    }

    /**
     * Repeatedly tests the {@link RogueFactory#fixStick(Rod)} method to ensure correct rod configuration.
     * Verifies that light rods ({@link RodType#WS_LIGHT}) have 10-19 charges and non-light rods
     * ({@link RodType#WS_SLOW_M}) have 3-7 charges, all rods have "1x1" throw damage, and wield
     * damage is either "1x1" (wand) or "2x3" (staff), using a fixed seed for reproducible results.
     */
    @RepeatedTest(50)
    void testFixStick() {
        final RogueFactory rogueFactory = getRogueFactory();

        final String wieldDamage = "1x1";
        final String wieldDamageStaff = "2x3";
        final String throwDamage = "1x1";

        final Rod lightRod = new Rod(RodType.WS_LIGHT);
        rogueFactory.fixStick(lightRod);
        assertTrue(lightRod.getCharges() > 9);
        assertEquals(throwDamage, lightRod.getThrowDamage());
        assertTrue(Objects.equals(wieldDamage, lightRod.getWieldDamage()) ||
                Objects.equals(wieldDamageStaff, lightRod.getWieldDamage()));

        final Rod slowRod = new Rod(RodType.WS_SLOW_M);
        rogueFactory.fixStick(slowRod);
        assertTrue(slowRod.getCharges() < 9);
        assertEquals(throwDamage, slowRod.getThrowDamage());
        assertTrue(Objects.equals(wieldDamage, slowRod.getWieldDamage()) ||
                Objects.equals(wieldDamageStaff, slowRod.getWieldDamage()));
    }

}
