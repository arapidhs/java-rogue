package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest extends RogueBaseTest {

    @Test
    void testAddToPackSilently() {
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
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(),rogueRandom);
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
    void testPickupItemFromFloor() {
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
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(),rogueRandom);
        gameState.setCurrentLevel(level);

        // Test picking up a Food item
        final Food food = new Food();
        food.setPosition(playerX, playerY);
        level.addItem(food);

        // Add a room at player's position for valid pickup context
        final Room room = new Room();
        final int sizeX = 1;
        final int sizeY = 1;
        room.setPosition(playerX, playerY);
        room.setSize(sizeX, sizeY);
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

    @Test
    void testProcessPhase() throws IOException {
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);

        // Set up player and level
        final Player player = new Player(config);
        final int playerX = 1;
        final int playerY = 1;
        player.setPosition(playerX, playerY);
        gameState.setPlayer(player);

        final int levelWidth = config.getLevelMaxWidth();
        final int levelHeight = config.getLevelMaxHeight();
        final Level level = new Level(levelWidth, levelHeight, rogueRandom);
        gameState.setCurrentLevel(level);

        // Test START_TURN phase with regular FunctionalCommand
        final int startGoldIncrease = 5;
        final AtomicInteger startExecutions = new AtomicInteger(0);
        gameState.addCommand(new FunctionalCommand(
                gs -> {
                    gs.setGoldAmount(gs.getGoldAmount() + startGoldIncrease);
                    startExecutions.incrementAndGet();
                }, Phase.START_TURN));

        // Test MAIN_TURN phase with TimedCommand
        final int initialTimer = 2;
        final int mainGoldIncrease = 10;
        final AtomicInteger mainTimer = new AtomicInteger(initialTimer);
        gameState.addCommand(new DelayedCommand<Integer>(initialTimer, 0, Phase.MAIN_TURN) {
            @Override
            public void execute(GameState gs) {
                gs.setGoldAmount(gs.getGoldAmount() + mainGoldIncrease);
            }

            @Override
            public int getTurnsRemaining() {
                return mainTimer.get();
            }

            @Override
            public void decrementTimer() {
                mainTimer.decrementAndGet();
            }

            @Override
            public boolean isReadyToExecute() {
                return mainTimer.get() <= 0;
            }
        });

        // Test END_TURN phase with inline TestEternalCommand
        final AtomicInteger endEternalExecutions = new AtomicInteger(0);
        class TestEternalCommand implements EternalCommand {
            private final AtomicInteger executions;
            private final Phase phase;

            TestEternalCommand(AtomicInteger executions, Phase phase) {
                this.executions = executions;
                this.phase = phase;
            }

            @Override
            public void execute(GameState gameState) {
                executions.incrementAndGet();
            }

            @Override
            public Phase getPhase() {
                return phase;
            }
        }
        gameState.addCommand(new TestEternalCommand(endEternalExecutions, Phase.END_TURN));

        // Process START_TURN phase
        final int expectedStartExecutions = 1;
        final int expectedQueueSizeAfterStart = 2;
        gameState.processPhase(Phase.START_TURN);
        assertEquals(startGoldIncrease, gameState.getGoldAmount()); // START_TURN command executed
        assertEquals(expectedStartExecutions, startExecutions.get()); // START_TURN command executed once
        assertEquals(expectedQueueSizeAfterStart, gameState.getCommandQueue().size()); // START_TURN command removed

        // Process MAIN_TURN phase once
        final int expectedMainTimerAfterFirst = initialTimer - 1;
        final int expectedQueueSizeAfterMain = 2;
        gameState.processPhase(Phase.MAIN_TURN);
        assertEquals(startGoldIncrease, gameState.getGoldAmount()); // No MAIN_TURN execution yet
        assertEquals(expectedMainTimerAfterFirst, mainTimer.get()); // TimedCommand timer decremented
        assertEquals(expectedQueueSizeAfterMain, gameState.getCommandQueue().size()); // No commands removed

        // Process MAIN_TURN again
        final int expectedGoldAfterMainSecond = startGoldIncrease + mainGoldIncrease;
        final int expectedMainTimerAfterSecond = 0;
        gameState.processPhase(Phase.MAIN_TURN);
        assertEquals(expectedGoldAfterMainSecond, gameState.getGoldAmount()); // TimedCommand executed
        assertEquals(expectedMainTimerAfterSecond, mainTimer.get()); // TimedCommand timer at 0
        assertEquals(1, gameState.getCommandQueue().size()); // TimedCommand removed

        // Process END_TURN phase
        final int expectedEndEternalExecutions = 1;
        final int expectedQueueSizeAfterEnd = 1;
        gameState.processPhase(Phase.END_TURN);
        assertEquals(expectedEndEternalExecutions, endEternalExecutions.get()); // EternalCommand executed once
        assertEquals(expectedQueueSizeAfterEnd, gameState.getCommandQueue().size()); // EternalCommand persists

        // Process END_TURN again
        final int expectedEndEternalExecutionsSecond = 2;
        gameState.processPhase(Phase.END_TURN);
        assertEquals(expectedEndEternalExecutionsSecond, endEternalExecutions.get()); // EternalCommand executed again
        assertEquals(expectedQueueSizeAfterEnd, gameState.getCommandQueue().size()); // EternalCommand still persists
    }

}
