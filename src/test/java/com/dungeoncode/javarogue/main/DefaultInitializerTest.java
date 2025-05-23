package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.entity.item.Inventory;
import com.dungeoncode.javarogue.system.entity.item.Item;
import com.dungeoncode.javarogue.system.entity.item.ItemFlag;
import com.dungeoncode.javarogue.system.entity.item.Armor;
import com.dungeoncode.javarogue.system.entity.item.ArmorType;
import com.dungeoncode.javarogue.system.entity.item.PotionType;
import com.dungeoncode.javarogue.system.entity.item.RingType;
import com.dungeoncode.javarogue.system.entity.item.RodType;
import com.dungeoncode.javarogue.system.entity.item.ScrollType;
import com.dungeoncode.javarogue.system.entity.item.Weapon;
import com.dungeoncode.javarogue.system.entity.item.WeaponType;
import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import com.dungeoncode.javarogue.system.initializer.DefaultInitializer;
import com.dungeoncode.javarogue.system.MessageSystem;
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
    void testPlayerInitialization() {

        // Set up game state with necessary dependencies and a specific seed
        // The specific seed is needed to make sure the generated and initialized
        // first level does not contain any food item, which in cas it had,
        // it would reset gameState#noFood back to zero.
        final long seed = 100;
        final RogueRandom rogueRandom = new RogueRandom(seed);
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
        assertEquals(0,gameState.getPlayer().getGoldAmount());

        // assert 0 levels without food
        assertEquals(1,gameState.getNoFood());

    }

    @Test
    void testRogueFactoryInitialization() throws IOException {
        // Set up game state with necessary dependencies
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DefaultInitializer initializer = new DefaultInitializer();

        final GameState gameState = new GameState(config, rogueRandom, screen, initializer, messageSystem);
        final RogueFactory rogueFactory=gameState.getRogueFactory();

        Arrays.stream(ScrollType.values())
                .forEach(scrollType -> assertNotNull(
                        rogueFactory.getName(scrollType)));

        Arrays.stream(PotionType.values())
                .forEach(potionType -> assertNotNull(
                        rogueFactory.getName(potionType)));

        Arrays.stream(RingType.values())
                .forEach(ringType -> assertNotNull(rogueFactory.getName(ringType)));

        Arrays.stream(RingType.values())
                .forEach(ringType -> assertTrue(rogueFactory.getRingWorth(ringType) > 0));

        Arrays.stream(RodType.values())
                .forEach(rodType -> assertNotNull(rogueFactory.getRodFormAsString(rodType)));

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
        assertEquals(startLevel, gameState.getCurrentLevel().getLevelNum());
        assertEquals(startLevel, gameState.getMaxLevel());
        assertEquals(startLevel,gameState.getPlayer().getCurrentLevel());
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
