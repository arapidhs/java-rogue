package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDataTest {

    final Config config = new Config();
    final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
    final ItemData itemData = new ItemData(config, rogueRandom);
    final int invNameTestIterations = 50;

    /**
     * Tests initialization of ItemData, ensuring names, forms, and worth are set for all item subtypes.
     */
    @Test
    void testInitializeItemData() {
        itemData.init();

        // Verify all ScrollTypes have generated names
        Arrays.stream(ScrollType.values())
                .forEach(scrollType -> assertNotNull(itemData.getName(scrollType)));

        // Verify all PotionTypes have assigned color names
        Arrays.stream(PotionType.values())
                .forEach(potionType -> assertNotNull(itemData.getName(potionType)));

        // Verify all RingTypes have assigned stone names
        Arrays.stream(RingType.values())
                .forEach(ringType -> assertNotNull(itemData.getName(ringType)));

        // Verify all RodTypes have assigned forms (wand or staff)
        Arrays.stream(RodType.values())
                .forEach(rodType -> assertNotNull(itemData.getRodForm(rodType)));
        assertNotNull(itemData.getRodMaterial(RodType.SLOW_MONSTER));

        // Check base worth of ADORNMENT ring template
        final RingInfoTemplate adornmentRingTemplate = (RingInfoTemplate) Templates.findTemplateBySubType(RingType.ADORNMENT);
        final int ringTemplateValue = adornmentRingTemplate.getWorth();
        assertTrue(ringTemplateValue > 0);

        // Ensure ADORNMENT ring’s worth includes stone value
        final int ringStoneWorth = itemData.getRingWorth(RingType.ADORNMENT);
        assertTrue(ringStoneWorth > ringTemplateValue);
    }

    /**
     * Tests setting and checking known status for item subtypes, including reset after re-initialization.
     */
    @Test
    void testIsKnown() {
        itemData.init();

        // Initial state: no subtypes are known
        assertFalse(itemData.isKnown(ScrollType.HOLD_MONSTER));
        assertFalse(itemData.isKnown(RingType.DEXTERITY));

        // Mark subtypes as known
        itemData.setKnown(ScrollType.HOLD_MONSTER, true);
        itemData.setKnown(RingType.DEXTERITY, true);

        // Verify known status is updated
        assertTrue(itemData.isKnown(ScrollType.HOLD_MONSTER));
        assertTrue(itemData.isKnown(RingType.DEXTERITY));

        // Re-initialize to reset known status
        itemData.init();
        assertFalse(itemData.isKnown(ScrollType.HOLD_MONSTER));
        assertFalse(itemData.isKnown(RingType.DEXTERITY));
    }

    /**
     * Tests setting and retrieving guess names for item subtypes, including reset after re-initialization.
     */
    @Test
    void testGuessName() {
        itemData.init();

        final String guessNameHoldMonster = "Hold Monster Scroll";
        final String guessNameDexterityRing = "Dexterity Ring";

        // Initial state: no guess names set
        assertNull(itemData.getGuessName(ScrollType.HOLD_MONSTER));
        assertNull(itemData.getGuessName(RingType.DEXTERITY));

        // Assign guess names
        itemData.setGuessName(ScrollType.HOLD_MONSTER, guessNameHoldMonster);
        itemData.setGuessName(RingType.DEXTERITY, guessNameDexterityRing);

        // Verify guess names are correctly stored
        assertEquals(guessNameHoldMonster, itemData.getGuessName(ScrollType.HOLD_MONSTER));
        assertEquals(guessNameDexterityRing, itemData.getGuessName(RingType.DEXTERITY));

        // Re-initialize to clear guess names
        itemData.init();
        assertNull(itemData.getGuessName(ScrollType.HOLD_MONSTER));
        assertNull(itemData.getGuessName(RingType.DEXTERITY));
    }

    /**
     * Tests the invName method for Potion items, verifying correct name formatting based on count, known status, guess names,
     * and capitalization. Iterates multiple times to ensure consistency across random initializations.
     */
    @Test
    void testInvNamePotion() {
        // Run multiple iterations to test with different random seeds
        for (int i = 0; i < invNameTestIterations; i++) {
            // Initialize item data to reset names and known status
            itemData.init();

            // Test with lowercase initial for dropping items
            boolean dropCapital = true;

            // Create a HASTE_SELF potion for testing
            final Potion item = new Potion(PotionType.HASTE_SELF);
            final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

            // Verify unknown single potion starts with "a" or "an" and ends with "potion"
            String name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("an? .+ potion"));

            // Verify unknown single potion with uppercase initial
            name = itemData.invName(null, item, !dropCapital);
            assertTrue(name.matches("An? .+ potion"));

            // Test plural form for multiple unknown potions
            int count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " .+ potions"));

            // Test single potion with a guess name
            count = 1;
            final String guessedName = "guessed name";
            item.setCount(count);
            itemData.setGuessName(item.getItemSubType(), guessedName);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a potion called " + guessedName + "\\(.+\\)"));

            // Test multiple potions with a guess name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " potions called " + guessedName + "\\(.+\\)"));

            // Test single known potion with real name
            count = 1;
            item.setCount(count);
            itemData.setKnown(item.getItemSubType(), true);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a potion of " + realName + "\\(.+\\)"));

            // Test multiple known potions with real name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " potions of " + realName + "\\(.+\\)"));
        }
    }

    /**
     * Tests the invName method for Rod items, verifying correct name formatting for wands and staffs based on count, known
     * status, guess names, charges, and capitalization. Iterates multiple times to ensure consistency across random material
     * assignments and tests charge display for known rods.
     */
    @Test
    void testInvNameRod() {
        // Run multiple iterations to test with different random rod forms and materials
        for (int i = 0; i < invNameTestIterations; i++) {
            // Initialize item data to reset names, forms, and known status
            itemData.init();

            // Test with lowercase initial for dropping items
            boolean dropCapital = true;

            // Create a SLOW_MONSTER rod for testing
            final Rod item = new Rod(RodType.SLOW_MONSTER);
            final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

            // Verify unknown single rod starts with "a" or "an" and ends with "staff" or "wand"
            String name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("an? .+ (staff|wand)"));

            // Verify unknown single rod with uppercase initial
            name = itemData.invName(null, item, !dropCapital);
            assertTrue(name.matches("An? .+ (staff|wand)"));

            // Test plural form for multiple unknown rods
            int count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " .+ (staffs|wands)"));

            // Test single rod with a guess name
            count = 1;
            final String guessedName = "guessed name";
            item.setCount(count);
            itemData.setGuessName(item.getItemSubType(), guessedName);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a (staff|wand) called " + guessedName + "\\(.+\\)"));

            // Test multiple rods with a guess name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " (staffs|wands) called " + guessedName + "\\(.+\\)"));

            // Test single known rod with real name
            count = 1;
            item.setCount(count);
            itemData.setKnown(item.getItemSubType(), true);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a (staff|wand) of " + realName + "\\(.+\\)"));

            // Test multiple known rods with real name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " (staffs|wands) of " + realName + "\\(.+\\)"));
        }

        // Test known rod with specific charges to verify charge display
        itemData.init();
        boolean dropCapital = true;
        final Rod item = new Rod(RodType.SLOW_MONSTER);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();
        final int charges = 3;
        item.setCharges(charges);
        item.addFlag(ItemFlag.ISKNOW);

        // Mark rod as known and verify name includes charge count
        itemData.setKnown(item.getItemSubType(), true);
        String name = itemData.invName(null, item, dropCapital);
        System.out.println(name);
        assertTrue(name.matches("a (staff|wand) of " + realName + " \\[" + charges + " charges]\\(.+\\)"));
    }

    /**
     * Tests the invName method for Ring items, verifying correct name formatting based on count, known status, guess names,
     * armor class bonuses, and usage indicators (worn on left/right hand). Iterates multiple times to ensure consistency
     * across random stone assignments and tests specific cases for non-bonus rings and ring usage.
     */
    @Test
    void testInvNameRing() {
        // Run multiple iterations to test with different random stone names
        for (int i = 0; i < invNameTestIterations; i++) {
            // Initialize item data to reset names and known status
            itemData.init();

            // Test with lowercase initial for dropping items
            boolean dropCapital = true;

            // Create a PROTECTION ring for testing
            final Ring item = new Ring(RingType.PROTECTION);
            final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

            // Verify unknown single ring starts with "a" or "an" and ends with "ring"
            String name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("an? .+ ring"));

            // Verify unknown single ring with uppercase initial
            name = itemData.invName(null, item, !dropCapital);
            assertTrue(name.matches("An? .+ ring"));

            // Test plural form for multiple unknown rings
            int count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " .+ rings"));

            // Test single ring with a guess name
            count = 1;
            final String guessedName = "guessed name";
            item.setCount(count);
            itemData.setGuessName(item.getItemSubType(), guessedName);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a ring called " + guessedName + "\\(.+\\)"));

            // Test multiple rings with a guess name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " rings called " + guessedName + "\\(.+\\)"));

            // Test single known ring with real name
            count = 1;
            item.setCount(count);
            itemData.setKnown(item.getItemSubType(), true);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a ring of " + realName + "\\(.+\\)"));

            // Test single known ring with armor class bonus
            int armorClass = 2;
            item.setCount(count);
            itemData.setKnown(item.getItemSubType(), true);
            item.addFlag(ItemFlag.ISKNOW);
            item.setArmorClass(armorClass);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a ring of " + realName + " \\[\\+" + armorClass + "]\\(.+\\)"));

            // Test multiple known rings with armor class bonus
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " rings of " + realName + " \\[\\+" + armorClass + "]\\(.+\\)"));
        }

        // Test known ADORNMENT ring without armor class bonus
        itemData.init();
        boolean dropCapital = true;
        final Ring item = new Ring(RingType.ADORNMENT);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();
        itemData.setKnown(item.getItemSubType(), true);

        item.addFlag(ItemFlag.ISKNOW);
        int armorClass = 2;
        item.setArmorClass(armorClass);

        // Verify ADORNMENT ring omits bonus due to ring type
        String name = itemData.invName(null, item, dropCapital);
        assertTrue(name.matches("a ring of " + realName + "\\(.+\\)"));

        // Test ring usage indicators when worn
        final Player player = new Player(config);
        player.setLeftRing(item);
        name = itemData.invName(player, item, dropCapital);
        assertTrue(name.matches("a ring of " + realName + "\\(.+\\)" + " \\(on left hand\\)"));

        // Verify right hand usage indicator
        player.setLeftRing(null);
        player.setRightRing(item);
        name = itemData.invName(player, item, dropCapital);
        assertTrue(name.matches("a ring of " + realName + "\\(.+\\)" + " \\(on right hand\\)"));
    }

    /**
     * Tests the invName method for Scroll items, verifying correct name formatting based on count, known status, guess names,
     * and capitalization. Iterates multiple times to ensure consistency across random scroll name generations.
     */
    @Test
    void testInvNameScroll() {
        // Run multiple iterations to test with different random scroll names
        for (int i = 0; i < invNameTestIterations; i++) {
            // Initialize item data to reset names and known status
            itemData.init();

            // Test with lowercase initial for dropping items
            boolean dropCapital = true;

            // Create a HOLD_MONSTER scroll for testing
            final Scroll item = new Scroll(ScrollType.HOLD_MONSTER);
            final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();

            // Verify unknown single scroll starts with "a" and uses titled name
            String name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a scroll titled '.+'"));

            // Verify unknown single scroll with uppercase initial
            name = itemData.invName(null, item, !dropCapital);
            assertTrue(name.matches("A scroll titled '.+'"));

            // Test plural form for multiple unknown scrolls
            int count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " scrolls titled '.+'"));

            // Test single scroll with a guess name
            count = 1;
            final String guessedName = "guessed name";
            item.setCount(count);
            itemData.setGuessName(item.getItemSubType(), guessedName);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a scroll called " + guessedName));

            // Test multiple scrolls with a guess name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " scrolls called " + guessedName));

            // Test single known scroll with real name
            count = 1;
            item.setCount(count);
            itemData.setKnown(item.getItemSubType(), true);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches("a scroll of " + realName));

            // Test multiple known scrolls with real name
            count = 2;
            item.setCount(count);
            name = itemData.invName(null, item, dropCapital);
            assertTrue(name.matches(count + " scrolls of " + realName));
        }
    }

    /**
     * Tests the invName method for Food items, verifying correct name formatting for standard rations and favorite fruit,
     * including singular/plural forms and capitalization. Ensures proper handling of fruit vs. ration distinction.
     */
    @Test
    void testInvNameFood() {
        // Initialize item data to set up configuration
        itemData.init();
        // Test with uppercase initial for inventory listing
        boolean dropCapital = false;
        // Create a standard food ration for testing
        final Food item = new Food();
        // Verify single standard food is named correctly
        String name = itemData.invName(null, item, dropCapital);
        assertEquals("Some food", name);

        // Test plural form for multiple standard rations
        int count = 3;
        item.setCount(3);
        name = itemData.invName(null, item, dropCapital);
        assertEquals(count + " rations of food", name);

        // Switch to favorite fruit and verify plural fruit name
        item.setFruit(true);
        name = itemData.invName(null, item, dropCapital);
        assertEquals(count + " " + config.getFavoriteFruit() + "s", name);

        // Test single favorite fruit with indefinite article
        count = 1;
        item.setCount(count);
        name = itemData.invName(null, item, dropCapital);
        assertEquals("A " + config.getFavoriteFruit(), name);
    }

    /**
     * Tests the invName method for Weapon items, verifying correct name formatting based on count, known status with hit/damage
     * bonuses, custom labels, and usage indicators (weapon in hand). Ensures proper singular/plural forms and capitalization.
     */
    @Test
    void testInvNameWeapon() {
        // Initialize item data to set up configuration
        itemData.init();

        // Test with uppercase initial for inventory listing
        boolean dropCapital = false;
        // Create a TWO_HANDED_SWORD for testing
        final Weapon item = new Weapon(WeaponType.TWO_HANDED_SWORD);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();
        // Verify single unknown weapon uses indefinite article and real name
        String name = itemData.invName(null, item, dropCapital);
        assertEquals("A " + realName, name);

        // Test plural form for multiple unknown weapons
        int count = 3;
        item.setCount(3);
        name = itemData.invName(null, item, dropCapital);
        assertEquals(count + " " + realName + "s", name);

        // Test single known weapon with hit and damage bonuses
        count = 1;
        item.setCount(count);
        item.addFlag(ItemFlag.ISKNOW);
        final int hitPlus = 2;
        final int damagePlus = 3;
        item.setHitPlus(hitPlus);
        item.setDamagePlus(damagePlus);
        name = itemData.invName(null, item, dropCapital);
        assertEquals("A +" + hitPlus + ",+" + damagePlus + " " + realName, name);

        // Verify addition of custom label to known weapon
        final String label = "flame sword";
        item.setLabel(label);
        name = itemData.invName(null, item, dropCapital);
        assertEquals("A +" + hitPlus + ",+" + damagePlus + " " + realName + " called " + label, name);

        // Test usage indicator when weapon is equipped
        final Player player = new Player(config);
        player.setCurrentWeapon(item);
        name = itemData.invName(player, item, dropCapital);
        assertEquals("A +" + hitPlus + ",+" + damagePlus + " " + realName + " called " + label + " (weapon in hand)", name);
    }

    /**
     * Tests the invName method for Armor items, verifying correct name formatting based on count, known status with armor
     * class bonuses, custom labels, and usage indicators (being worn). Ensures proper handling of singular/plural forms and
     * protection values.
     */
    @Test
    void testInvNameArmor() {
        // Initialize item data to set up configuration
        itemData.init();

        // Test with lowercase initial for dropping items
        boolean dropCapital = true;
        // Create a BANDED_MAIL armor for testing
        final Armor item = new Armor(ArmorType.BANDED_MAIL);
        final String realName = Templates.findTemplateBySubType(item.getItemSubType()).getName();
        // Verify single unknown armor uses real name without bonuses
        String name = itemData.invName(null, item, dropCapital);
        assertEquals(realName, name);

        // Test multiple unknown armors (still uses singular name as count is ignored)
        int count = 3;
        item.setCount(count);
        name = itemData.invName(null, item, dropCapital);
        assertEquals(realName, name);

        // Test single known armor with armor class bonus and protection
        count = 1;
        item.setCount(count);
        item.addFlag(ItemFlag.ISKNOW);
        final int armorClassDiff = -1;
        item.setArmorClass(item.getArmorClass() + armorClassDiff);
        final int protection = config.getMinArmorClass() - item.getArmorClass();
        name = itemData.invName(null, item, dropCapital);
        assertEquals("+" + count + " " + realName + " [protection " + protection + "]", name);

        // Verify addition of custom label to known armor
        final String label = "secret armor";
        item.setLabel(label);
        name = itemData.invName(null, item, dropCapital);
        assertEquals("+" + count + " " + realName + " [protection " + protection + "] called " + label, name);

        // Test usage indicator when armor is equipped
        final Player player = new Player(config);
        player.setCurrentArmor(item);
        name = itemData.invName(player, item, dropCapital);
        assertEquals("+" + count + " " + realName + " [protection " + protection + "] called " + label + " (being worn)", name);
    }

    @Test
    void testInvNameGold() {
        itemData.init();

        boolean dropCapital = true;
        final int goldValue = 100;
        final Gold item = new Gold(goldValue);

        final String name = itemData.invName(null, item, dropCapital);
        assertEquals(goldValue + " Gold pieces", name);
    }

    @Test
    void testInvNameAmulet() {
        itemData.init();

        boolean dropCapital = false;
        final Item item = new Item(ObjectType.AMULET, null, 1);
        final String name = itemData.invName(null, item, dropCapital);
        assertEquals("The Amulet of Yendor", name);
    }

}