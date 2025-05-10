package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultInitializerTest extends RogueBaseTest {

    /**
     * Tests the DefaultInitializer to ensure it correctly sets up the initial game state,
     * including player properties and inventory items (Food, Armor, Weapons).
     */
    @Test
    void testPlayerInitialization() throws IOException {

        // Set up game state with necessary dependencies
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DefaultInitializer initializer = new DefaultInitializer();

        final GameState gameState = new GameState(config, rogueRandom, screen, initializer, messageSystem);
        final Player player = gameState.getPlayer();

        // Verify initial player configuration matches config settings
        assertEquals(config.getPlayerName(), player.getPlayerName());
        assertEquals(config.getMaxPack(), player.getInventory().getMaxPack());
        assertEquals(config.getFoodLeft(), player.getFoodLeft());
        assertTrue(player.getPlayerFlags().isEmpty()); // Ensure no unexpected player flags

        final Inventory inventory = player.getInventory();

        // Verify Mace is correctly initialized with bonuses and known status
        final Weapon mace = (Weapon) inventory.getItems().stream()
                .filter(item -> item.getItemSubType() == WeaponType.MACE)
                .findFirst()
                .orElse(null);
        assertNotNull(mace); // Ensure Mace is present
        assertTrue(mace.hasFlag(ItemFlag.ISKNOW)); // Check Mace is identified
        assertEquals(1, mace.getHitPlus()); // Validate hit bonus
        assertEquals(1, mace.getDamagePlus()); // Validate damage bonus

        // Verify Arrow has expected quantity (randomized range)
        final Item arrow = inventory.getItems().stream()
                .filter(item -> item.getItemSubType() == WeaponType.ARROW)
                .findFirst()
                .orElse(null);
        assertNotNull(arrow);
        assertTrue(arrow.getCount() > 24);

        // Verify initial player's Ring Mail has lower armor class than the default
        final Armor initialRingMail = (Armor) inventory.getItems().stream()
                .filter(item -> item.getItemSubType() == ArmorType.RING_MAIL)
                .findFirst()
                .orElse(null);
        assertNotNull(initialRingMail); // Ensure Ring Mail is present
        final Armor ringMail = new Armor(ArmorType.RING_MAIL);
        assertTrue(initialRingMail.getArmorClass() < ringMail.getArmorClass());

        assertNull(gameState.getGameEndReason());
        assertNull(gameState.getDeathSource());
        assertEquals(0,gameState.getGoldAmount());

    }

    @Test
    void testItemDataInitialization() throws IOException {
        // Set up game state with necessary dependencies
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DefaultInitializer initializer = new DefaultInitializer();

        final GameState gameState = new GameState(config, rogueRandom, screen, initializer, messageSystem);
        final ItemData itemData = gameState.getItemData();

        Arrays.stream(ScrollType.values())
                .forEach(scrollType -> assertNotNull(
                        itemData.getName(scrollType)));

        Arrays.stream(PotionType.values())
                .forEach(potionType -> assertNotNull(
                        itemData.getName(potionType)));

        Arrays.stream(RingType.values())
                .forEach(ringType -> assertNotNull(itemData.getName(ringType)));

        Arrays.stream(RingType.values())
                .forEach(ringType -> assertTrue(itemData.getRingWorth(ringType) > 0));

        Arrays.stream(RodType.values())
                .forEach(rodType -> assertNotNull(itemData.getRodForm(rodType)));

    }

    @Test
    void testLevelInitialization() throws IOException {
        // Set up game state with necessary dependencies
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DefaultInitializer initializer = new DefaultInitializer();

        final GameState gameState = new GameState(config, rogueRandom, screen, initializer, messageSystem);

        final int startLevel = 1;
        assertNotNull(gameState.getCurrentLevel());
        assertEquals(startLevel, gameState.getLevelNum());
        assertEquals(startLevel, gameState.getMaxLevel());
        assertFalse(gameState.getPlayer().hasFlag(CreatureFlag.ISHELD));

    }

    @Test
    void testGameStateInitialization() throws IOException {
        // Set up game state with necessary dependencies
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DefaultInitializer initializer = new DefaultInitializer();

        final GameState gameState = new GameState(config, rogueRandom, screen, initializer, messageSystem);

        Arrays.stream(Phase.values()).forEach(phase ->
                assertTrue(gameState.getPhaseActivity().get(phase)));
    }

}
