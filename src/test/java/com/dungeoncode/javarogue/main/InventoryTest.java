package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    final Config config = new Config();

    @Test
    void addToPackTest() {
        // Initialize inventory with max pack size from config
        final Inventory inventory = new Inventory(config.getMaxPack());

        // Test adding first item (Food) to empty inventory
        final Food food = new Food();
        assertNull(food.getPackChar()); // Verify no pack character assigned initially
        assertFalse(food.hasFlag(ItemFlag.ISFOUND)); // Verify ISFOUND flag not set initially

        // Add Food and verify it gets 'a' and ISFOUND flag
        assertTrue(inventory.addToPack(food)); // Should succeed as inventory is empty
        assertEquals('a', food.getPackChar()); // First item gets 'a'
        assertTrue(food.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set on addition
        assertEquals(1, inventory.getPackSize()); // Pack size increments
        assertEquals(1, inventory.getItems().size()); // One item in inventory

        // Test stacking additional Food (stackable item)
        final Food additionalFood = new Food();
        assertTrue(inventory.addToPack(additionalFood)); // Should stack with existing Food
        assertEquals('a', additionalFood.getPackChar()); // Shares 'a' with first Food
        assertEquals(2, inventory.getPackSize()); // Pack size increments for stack
        assertEquals(1, inventory.getItems().size()); // Still one item due to stacking

        // Test adding non-stackable item (Armor)
        final Armor plateMail = new Armor(ArmorType.PLATE_MAIL);
        assertTrue(inventory.addToPack(plateMail)); // Should add as new item
        assertEquals('b', plateMail.getPackChar()); // Gets next character 'b'
        assertTrue(plateMail.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(3, inventory.getPackSize()); // Pack size increments
        assertEquals(2, inventory.getItems().size()); // Two items in inventory

        // Test adding stackable item (Potion)
        final Potion healingPotion = new Potion(PotionType.HEALING);
        assertTrue(inventory.addToPack(healingPotion)); // Should add as new item
        assertEquals('c', healingPotion.getPackChar()); // Gets next character 'c'
        assertTrue(healingPotion.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(4, inventory.getPackSize()); // Pack size increments
        assertEquals(3, inventory.getItems().size()); // Three items in inventory

        // Test adding another stackable item (Scroll)
        final Scroll identifyScroll = new Scroll(ScrollType.IDENTIFY_SCROLL);
        assertTrue(inventory.addToPack(identifyScroll)); // Should add as new item
        assertEquals('d', identifyScroll.getPackChar()); // Gets next character 'd'
        assertTrue(identifyScroll.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(5, inventory.getPackSize()); // Pack size increments
        assertEquals(4, inventory.getItems().size()); // Four items in inventory

        // Test adding another Potion (different subtype)
        final Potion blindnessPotion = new Potion(PotionType.BLINDNESS);
        assertTrue(inventory.addToPack(blindnessPotion)); // Should add as new item (different subtype)
        assertEquals('e', blindnessPotion.getPackChar()); // Gets next character 'e'
        assertTrue(blindnessPotion.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(6, inventory.getPackSize()); // Pack size increments
        assertEquals(5, inventory.getItems().size()); // Five items in inventory

        // Test adding another non-stackable item (Armor)
        final Armor leatherArmor = new Armor(ArmorType.LEATHER);
        assertTrue(inventory.addToPack(leatherArmor)); // Should add as new item
        assertEquals('f', leatherArmor.getPackChar()); // Gets next character 'f'
        assertTrue(leatherArmor.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(7, inventory.getPackSize()); // Pack size increments
        assertEquals(6, inventory.getItems().size()); // Six items in inventory

        // Test stacking another Healing Potion
        final Potion additionalHealingPotion = new Potion(PotionType.HEALING);
        assertTrue(inventory.addToPack(additionalHealingPotion)); // Should stack with existing Healing Potion
        assertEquals('c', additionalHealingPotion.getPackChar()); // Shares 'c' with first Healing Potion
        assertTrue(additionalHealingPotion.hasFlag(ItemFlag.ISFOUND)); // ISFOUND flag set
        assertEquals(8, inventory.getPackSize()); // Pack size increments
        assertEquals(6, inventory.getItems().size()); // Still six items due to stacking

        // Test filling remaining inventory slots up to maxPack limit
        final int remainingSize = inventory.getMaxPack() - inventory.getPackSize();
        for (int i = 0; i < remainingSize; i++) {
            assertTrue(inventory.addToPack(food));
        }
        // Verify adding beyond maxPack fails
        assertFalse(inventory.addToPack(food));
    }
}
