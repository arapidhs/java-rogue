package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest extends RogueBaseTest {

    @Test
    void testAddToPackSilently() throws IOException {
        // Configure silent mode to suppress messages
        boolean silent = true;

        // Initialize game state with mocked screen and random seed
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);

        // Set up player with initial position
        final Player player = new Player(config);
        player.setPosition(1, 1);
        gameState.setPlayer(player);

        // Create level with configured dimensions
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
        gameState.setCurrentLevel(level);

        // Test adding Food item silently
        final int foodX = 5;
        final int foodY = 5;
        final Food food = new Food();
        food.setPosition(foodX, foodY);

        // Add Food to inventory and verify presence
        gameState.addToPack(food, silent);
        assertTrue(player.getInventory().getItems().contains(food)); // Verify Food added to inventory

        // Test stacking additional Food and monster destination update
        final Food additionalFood = new Food();
        additionalFood.setPosition(foodX, foodY);
        final Monster monster = new Monster();
        monster.setDestination(foodX, foodY);
        assertTrue(gameState.getCurrentLevel().addMonster(monster)); // Add monster to level

        // Add additional Food silently and verify monster destination
        // updates to player's new position
        gameState.addToPack(additionalFood, silent);
        final int newPlayerX = 10;
        gameState.getPlayer().getPosition().setX(newPlayerX); // Move player to new position
        assertEquals(monster.getDestination().getX(), gameState.getPlayer().getX()); // Verify monster tracks player's new x-coordinate
    }

    @Test
    void testAddToPack() throws IOException {
        boolean silent = false;

        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);

        final Player player = new Player(config);
        gameState.setPlayer(player);

        final Food food = new Food();
        assertTrue(gameState.addToPack(food, silent));

        final Inventory inventory = gameState.getPlayer().getInventory();
        // Test filling remaining inventory slots up to maxPack limit
        final int remainingSize = inventory.getMaxPack() - inventory.getPackSize();
        for (int i = 0; i < remainingSize; i++) {
            assertTrue(gameState.addToPack(food, silent));
        }
        assertFalse(gameState.addToPack(food, silent));

    }

    /**
     * Tests the pickupItemFromFloor method to ensure it correctly handles picking up items,
     * processes scare monster scrolls, and manages inventory limits.
     */
    @Test
    void testPickupItemFromFloor() throws IOException {
        // Initialize game state with mocked screen and random seed
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);

        // Set up player at position (1, 5)
        final Player player = new Player(config);
        final int playerX = 1;
        final int playerY = 5;
        player.setPosition(playerX, playerY);
        gameState.setPlayer(player);

        // Create level with configured dimensions
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
        gameState.setCurrentLevel(level);

        // Test picking up a Food item
        final Food food = new Food();
        food.setPosition(playerX, playerY);
        level.addItem(food);

        // Add a room at player's position for valid pickup context
        final Room room = new Room(
                new Position(playerX, playerY),
                new Position(10, 10),
                new Position(7, 7),
                100,
                null,
                new ArrayList<>()
        );
        gameState.getCurrentLevel().addRoom(room);

        // Verify Food is picked up and added to inventory
        gameState.pickupItemFromFloor();
        assertTrue(player.getInventory().getItems().contains(food));
        assertFalse(gameState.getCurrentLevel().getItems().contains(food));

        // Test picking up a scare monster scroll with ISFOUND flag
        final Scroll scareMonsterScroll = new Scroll(ScrollType.SCARE_MONSTER);
        scareMonsterScroll.setPosition(playerX, playerY);
        scareMonsterScroll.getItemFlags().add(ItemFlag.ISFOUND);
        gameState.getCurrentLevel().addItem(scareMonsterScroll);
        gameState.pickupItemFromFloor(); // Should remove scroll and display message, not add to inventory
        assertFalse(gameState.getCurrentLevel().getItems().contains(scareMonsterScroll));

        final Inventory inventory = gameState.getPlayer().getInventory();
        // Test filling remaining inventory slots up to maxPack limit
        final int remainingSize = inventory.getMaxPack() - inventory.getPackSize();
        for (int i = 0; i < remainingSize; i++) {
            assertTrue(gameState.addToPack(food, true)); // Silently add Food until inventory is full
        }

        // Test picking up Food when inventory is full
        level.addItem(food);
        gameState.pickupItemFromFloor(); // Should fail to add Food and display message
    }

}
