package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.entity.item.Inventory;
import com.dungeoncode.javarogue.system.entity.item.ItemFlag;
import com.dungeoncode.javarogue.system.entity.item.Armor;
import com.dungeoncode.javarogue.system.entity.item.ArmorType;
import com.dungeoncode.javarogue.system.entity.item.Food;
import com.dungeoncode.javarogue.system.entity.item.Potion;
import com.dungeoncode.javarogue.system.entity.item.PotionType;
import com.dungeoncode.javarogue.system.entity.item.Scroll;
import com.dungeoncode.javarogue.system.entity.item.ScrollType;
import com.dungeoncode.javarogue.system.entity.item.Weapon;
import com.dungeoncode.javarogue.system.entity.item.WeaponType;
import com.dungeoncode.javarogue.system.SymbolType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryTest.class);

    @Test
    void testAddToPack() {
        // Initialize dependencies for test setup
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final RogueFactory rogueFactory = new RogueFactory(config,rogueRandom);

        // Initialize inventory with max pack size from config
        final Inventory inventory = new Inventory(config.getMaxPack());

        // Test adding first item (Food) to empty inventory
        final Food food = new Food();
        assertNull(food.getInventoryKey()); // Verify no pack character assigned initially
        assertFalse(food.hasFlag(ItemFlag.ISFOUND)); // Verify ISFOUND flag not set initially

        // Add Food and verify it gets 'a' and ISFOUND flag
        assertTrue(inventory.addToPack(food)); // Should succeed as inventory is empty
        assertEquals(SymbolType.KEY_A, food.getInventoryKey()); // First item gets 'a'
        assertTrue(food.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set on addition
        assertEquals(1, inventory.getPackSize()); // Pack size increments
        assertEquals(1, inventory.getItems().size()); // One item in inventory

        // Test stacking additional Food (stackable item)
        final Food additionalFood = new Food();
        assertTrue(inventory.addToPack(additionalFood)); // Should stack with existing Food
        assertEquals(SymbolType.KEY_A, additionalFood.getInventoryKey()); // Shares 'a' with first Food
        assertEquals(2, inventory.getPackSize()); // Pack size increments for stack
        assertEquals(1, inventory.getItems().size()); // Still one item due to stacking

        // Test adding non-stackable item (Armor)
        final Armor plateMail = new Armor(ArmorType.PLATE_MAIL);
        assertTrue(inventory.addToPack(plateMail)); // Should add as new item
        assertEquals(SymbolType.KEY_B, plateMail.getInventoryKey()); // Gets next character 'b'
        assertTrue(plateMail.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(3, inventory.getPackSize()); // Pack size increments
        assertEquals(2, inventory.getItems().size()); // Two items in inventory

        // Test adding stackable item (Potion)
        final Potion healingPotion = new Potion(PotionType.HEALING);
        assertTrue(inventory.addToPack(healingPotion)); // Should add as new item
        assertEquals(SymbolType.KEY_C, healingPotion.getInventoryKey()); // Gets next character 'c'
        assertTrue(healingPotion.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(4, inventory.getPackSize()); // Pack size increments
        assertEquals(3, inventory.getItems().size()); // Three items in inventory

        // Test adding another stackable item (Scroll)
        final Scroll identifyScroll = new Scroll(ScrollType.IDENTIFY_SCROLL);
        assertTrue(inventory.addToPack(identifyScroll)); // Should add as new item
        assertEquals(SymbolType.KEY_D, identifyScroll.getInventoryKey()); // Gets next character 'd'
        assertTrue(identifyScroll.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(5, inventory.getPackSize()); // Pack size increments
        assertEquals(4, inventory.getItems().size()); // Four items in inventory

        // Test adding another Potion (different subtype)
        final Potion blindnessPotion = new Potion(PotionType.BLINDNESS);
        assertTrue(inventory.addToPack(blindnessPotion)); // Should add as new item (different subtype)
        assertEquals(SymbolType.KEY_E, blindnessPotion.getInventoryKey()); // Gets next character 'e'
        assertTrue(blindnessPotion.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(6, inventory.getPackSize()); // Pack size increments
        assertEquals(5, inventory.getItems().size()); // Five items in inventory

        // Test adding another non-stackable item (Armor)
        final Armor leatherArmor = new Armor(ArmorType.LEATHER);
        assertTrue(inventory.addToPack(leatherArmor)); // Should add as new item
        assertEquals(SymbolType.KEY_F, leatherArmor.getInventoryKey()); // Gets next character 'f'
        assertTrue(leatherArmor.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(7, inventory.getPackSize()); // Pack size increments
        assertEquals(6, inventory.getItems().size()); // Six items in inventory

        // Test stacking another Healing Potion
        final Potion additionalHealingPotion = new Potion(PotionType.HEALING);
        assertTrue(inventory.addToPack(additionalHealingPotion)); // Should stack with existing Healing Potion
        assertEquals(SymbolType.KEY_C, additionalHealingPotion.getInventoryKey()); // Shares 'c' with first Healing Potion
        assertTrue(additionalHealingPotion.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(8, inventory.getPackSize()); // Pack size increments
        assertEquals(6, inventory.getItems().size()); // Still six items due to stacking

        // Test adding a Dagger (non-stackable but groupable)
        final Weapon dagger = rogueFactory.weapon(WeaponType.DAGGER);
        assertTrue(inventory.addToPack(dagger)); // Should add as new item
        assertEquals(SymbolType.KEY_G, dagger.getInventoryKey()); // Gets next character 'g'
        assertEquals(9, inventory.getPackSize()); // Pack size increments
        assertEquals(7, inventory.getItems().size()); // Seven items in inventory

        // Test grouping another Dagger with the same group
        final Weapon additionalDagger = rogueFactory.weapon(WeaponType.DAGGER);
        additionalDagger.setGroup(dagger.getGroup()); // Ensure same group for grouping
        assertTrue(inventory.addToPack(additionalDagger)); // Should group with existing Dagger
        assertEquals(SymbolType.KEY_G, additionalDagger.getInventoryKey()); // Shares 'g' with first Dagger
        assertEquals(9, inventory.getPackSize()); // Pack size does not increment due to grouping
        assertEquals(7, inventory.getItems().size()); // Still seven items due to grouping

        // Test adding a Dagger with a different group
        final Weapon separateGroupDagger = rogueFactory.weapon(WeaponType.DAGGER);
        assertTrue(inventory.addToPack(separateGroupDagger)); // Should add as new item (different group)
        assertEquals(SymbolType.KEY_H, separateGroupDagger.getInventoryKey()); // Gets next character 'h'
        assertEquals(10, inventory.getPackSize()); // Pack size increments
        assertEquals(8, inventory.getItems().size()); // Eight items in inventory

        // Test filling remaining inventory slots up to maxPack limit
        final int remainingSize = inventory.getMaxPack() - inventory.getPackSize();
        for (int i = 0; i < remainingSize; i++) {
            assertTrue(inventory.addToPack(new Food())); // Add Food until inventory is full
        }
        // Verify adding beyond maxPack fails
        assertFalse(inventory.addToPack(new Food()));

        inventory.getItems().forEach(item ->
                LOGGER.debug("Test Method: addToPackTest, Inventory Item - Index: {}, Type: {}, Count: {}, SubType: {}, PackChar: {}",
                        inventory.getItems().indexOf(item),
                        item.getClass().getSimpleName(),
                        item.getCount(),
                        item.getItemSubType(),
                        item.getInventoryKey()));
    }
}
