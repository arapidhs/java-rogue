package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.command.core.CommandEternal;
import com.dungeoncode.javarogue.command.core.CommandFunctional;
import com.dungeoncode.javarogue.command.core.CommandParameterizedTimed;
import com.dungeoncode.javarogue.command.core.CommandTimed;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import com.dungeoncode.javarogue.system.MessageSystem;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.system.entity.creature.Monster;
import com.dungeoncode.javarogue.system.entity.creature.MonsterType;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.entity.item.*;
import com.dungeoncode.javarogue.system.initializer.DefaultInitializer;
import com.dungeoncode.javarogue.system.world.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;
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
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(), rogueRandom);
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
        final Monster monster = new Monster(MonsterType.ICE_MONSTER);
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
    void testAddToPack() {
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
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(), rogueRandom);
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

    /**
     * Tests the {@link GameState#processPhase(Phase)} method to ensure correct command processing for each phase.
     * Verifies that:
     * <ul>
     *   <li><b>START_TURN</b>: Executes a regular {@link CommandFunctional}, updates state (gold amount), and removes the command.</li>
     *   <li><b>MAIN_TURN</b>: Processes a {@link CommandTimed}, decrements its timer, executes when ready, and removes it.</li>
     *   <li><b>END_TURN</b>: Executes an {@link CommandEternal}, persists it in the queue, and executes repeatedly.</li>
     * </ul>
     * Ensures phase-specific command execution, queue management, and state updates align with the C Rogue turn-based structure.
     */
    @Test
    void testProcessPhase() {
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);
        Arrays.stream(Phase.values()).forEach(gameState::enablePhase);

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

        // Test START_TURN phase with regular CommandFunctional
        final int startGoldIncrease = 5;
        final AtomicInteger startExecutions = new AtomicInteger(0);
        gameState.addCommand(new CommandFunctional(
                gs -> {
                    gs.getPlayer().setGoldAmount(gs.getPlayer().getGoldAmount() + startGoldIncrease);
                    startExecutions.incrementAndGet();
                    return true;
                }, Phase.START_TURN));

        // Test MAIN_TURN phase with CommandTimed
        final int initialTimer = 2;
        final int mainGoldIncrease = 10;
        final AtomicInteger mainTimer = new AtomicInteger(initialTimer);
        gameState.addCommand(new CommandParameterizedTimedTest(initialTimer, mainGoldIncrease, mainTimer));

        // Test END_TURN phase with inline TestCommandEternal
        final AtomicInteger endEternalExecutions = new AtomicInteger(0);
        class TestCommandEternal implements CommandEternal {
            private final AtomicInteger executions;
            private final Phase phase;

            TestCommandEternal(AtomicInteger executions, Phase phase) {
                this.executions = executions;
                this.phase = phase;
            }

            @Override
            public boolean execute(GameState gameState) {
                executions.incrementAndGet();
                return true;
            }

            @Override
            public Phase getPhase() {
                return phase;
            }
        }
        gameState.addCommand(new TestCommandEternal(endEternalExecutions, Phase.END_TURN));

        // Process START_TURN phase
        final int expectedStartExecutions = 1;
        final int expectedQueueSizeAfterStart = 2;
        gameState.processPhase(Phase.START_TURN);
        assertEquals(startGoldIncrease, gameState.getPlayer().getGoldAmount()); // START_TURN command executed
        assertEquals(expectedStartExecutions, startExecutions.get()); // START_TURN command executed once
        assertEquals(expectedQueueSizeAfterStart, gameState.getCommandQueue().size()); // START_TURN command removed

        // Process MAIN_TURN phase once
        final int expectedMainTimerAfterFirst = initialTimer - 1;
        final int expectedQueueSizeAfterMain = 2;
        gameState.processPhase(Phase.MAIN_TURN);
        assertEquals(startGoldIncrease, gameState.getPlayer().getGoldAmount()); // No MAIN_TURN execution yet
        assertEquals(expectedMainTimerAfterFirst, mainTimer.get()); // CommandTimed timer decremented
        assertEquals(expectedQueueSizeAfterMain, gameState.getCommandQueue().size()); // No commands removed

        // Process MAIN_TURN again
        final int expectedGoldAfterMainSecond = startGoldIncrease + mainGoldIncrease;
        final int expectedMainTimerAfterSecond = 0;
        gameState.processPhase(Phase.MAIN_TURN);
        assertEquals(expectedGoldAfterMainSecond, gameState.getPlayer().getGoldAmount()); // CommandTimed executed
        assertEquals(expectedMainTimerAfterSecond, mainTimer.get()); // CommandTimed timer at 0
        assertEquals(1, gameState.getCommandQueue().size()); // CommandTimed removed

        // Process END_TURN phase
        final int expectedEndEternalExecutions = 1;
        final int expectedQueueSizeAfterEnd = 1;
        gameState.processPhase(Phase.END_TURN);
        assertEquals(expectedEndEternalExecutions, endEternalExecutions.get()); // CommandEternal executed once
        assertEquals(expectedQueueSizeAfterEnd, gameState.getCommandQueue().size()); // CommandEternal persists

        // Process END_TURN again
        final int expectedEndEternalExecutionsSecond = 2;
        gameState.processPhase(Phase.END_TURN);
        assertEquals(expectedEndEternalExecutionsSecond, endEternalExecutions.get()); // CommandEternal executed again
        assertEquals(expectedQueueSizeAfterEnd, gameState.getCommandQueue().size()); // CommandEternal still persists
    }

    /**
     * Tests the creation of a new dungeon level starting in a maze, using a specific seed to reproduce a problematic case.
     * Verifies that the player's starting room is a maze, the room at the player's position is marked as gone and dark,
     * and is an instance of {@link Passage}.
     */
    @Test
    void testNewLevelStartingAtMaze() {
        final long problematicSeed = -147134407;
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(problematicSeed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);

        final Player player = new Player(config);
        final int levelNum = 20;
        gameState.setPlayer(player);
        gameState.newLevel(levelNum);

        final Room maze = gameState.getCurrentLevel().findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        assertNotNull(maze);
        assertTrue(maze.hasFlag(RoomFlag.MAZE));

        final int px = gameState.getPlayer().getX();
        final int py = gameState.getPlayer().getY();
        final Room room = gameState.getCurrentLevel().roomIn(px, py);
        assertNotNull(room);
        assertTrue(room.hasFlag(RoomFlag.GONE));
        assertTrue(room.hasFlag(RoomFlag.DARK));
        assertInstanceOf(Passage.class, room);
    }

    /**
     * Tests the initialization of a new dungeon level with default configuration.
     * Verifies that the {@link GameState#isSeenStairs()} method returns false, indicating
     * that stairs are not visible at the start of a new level.
     */
    @Test
    void testNewLevel() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);
        assertFalse(gameState.isSeenStairs());
    }

    /**
     * Tests the behavior of starting a new level in a maze, ensuring correct room and passage handling.
     * Verifies that:
     * <ul>
     *   <li>The player starts in a maze room (has {@link RoomFlag#MAZE}).</li>
     *   <li>The {@link GameState#roomIn(int, int)} method returns a {@link Passage} for the player's position.</li>
     *   <li>The passage has {@link RoomFlag#GONE} and {@link RoomFlag#DARK} flags, per Rogue's maze behavior.</li>
     * </ul>
     * Uses a specific seed (336362311) and level 20 to reproduce maze start conditions.
     */
    @Test
    void testShowFloor() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);
        final Player player = new Player(config);
        final int levelNum = 1;
        final boolean seeFloor = false;
        config.setSeeFloor(seeFloor);
        gameState.setPlayer(player);
        gameState.newLevel(levelNum);

        assertFalse(config.isSeeFloor());

        final Room room = gameState.getCurrentLevel().findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        assertNotNull(room);
        room.removeFlag(RoomFlag.DARK);
        room.removeFlag(RoomFlag.GONE);
        player.removeFlag(CreatureFlag.ISBLIND);
        assertTrue(gameState.showFloor());

        room.addFlag(RoomFlag.DARK);
        assertEquals(config.isSeeFloor(), gameState.showFloor());

        player.addFlag(CreatureFlag.ISBLIND);
        assertTrue(gameState.showFloor());
    }

    /**
     * Tests the {@link GameState#floorCh()} method to ensure correct symbol type rendering at the player's position.
     * Verifies that:
     * <ul>
     *   <li>In a dark, non-corridor room with {@code seeFloor = false} and player not blind, returns {@link SymbolType#EMPTY}.</li>
     *   <li>In a corridor room ({@link RoomFlag#GONE}), returns {@link SymbolType#PASSAGE}.</li>
     *   <li>In a lit, non-corridor room, returns {@link SymbolType#FLOOR}.</li>
     * </ul>
     */
    @Test
    void testFloorCh() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);
        final Player player = new Player(config);
        final int levelNum = 1;
        final boolean seeFloor = false;
        config.setSeeFloor(seeFloor);
        gameState.setPlayer(player);
        gameState.newLevel(levelNum);

        assertFalse(config.isSeeFloor());

        final Room room = gameState.getCurrentLevel().findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        assertNotNull(room);
        room.addFlag(RoomFlag.DARK);
        room.removeFlag(RoomFlag.GONE);
        player.removeFlag(CreatureFlag.ISBLIND);
        assertFalse(gameState.showFloor());
        assertEquals(SymbolType.EMPTY, gameState.floorCh());

        room.addFlag(RoomFlag.GONE);
        assertEquals(SymbolType.PASSAGE, gameState.floorCh());

        room.removeFlag(RoomFlag.GONE);
        room.removeFlag(RoomFlag.DARK);
        assertEquals(SymbolType.FLOOR, gameState.floorCh());
    }

    /**
     * Tests the {@link GameState#floorAt()} method when the player starts in a maze.
     * Verifies that:
     * <ul>
     *   <li>The player is in a maze room with {@link RoomFlag#MAZE}.</li>
     *   <li>The {@link GameState#roomIn(int, int)} method returns a {@link Passage}.</li>
     *   <li>The {@link GameState#floorAt()} method returns {@link SymbolType#PASSAGE} for the player's position.</li>
     * </ul>
     * Uses a specific seed (1505455994) and level 20 to ensure the player starts in a maze, with {@code seeFloor = false}.
     */
    @Test
    void testFloorAtMaze() {
        final long startInMazeSeed = 1505455994;
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(startInMazeSeed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);

        final Player player = new Player(config);
        final int levelNum = 20;
        final boolean seeFloor = false;
        config.setSeeFloor(seeFloor);
        gameState.setPlayer(player);
        gameState.newLevel(levelNum);

        final int px = gameState.getPlayer().getX();
        final int py = gameState.getPlayer().getY();

        // Starting in maze
        final Room maze = gameState.getCurrentLevel().findRoomAt(px, py);
        assertNotNull(maze);
        assertTrue(maze.hasFlag(RoomFlag.MAZE));

        final Room room = gameState.getCurrentLevel().roomIn(px, py);
        assertNotNull(room);
        assertInstanceOf(Passage.class, room);
        assertEquals(SymbolType.PASSAGE, gameState.floorAt());
    }

    /**
     * Tests the {@link GameState#floorAt()} method when the player starts in a normal room.
     * Verifies that:
     * <ul>
     *   <li>The player is in a non-maze, non-corridor room (no {@link RoomFlag#MAZE} or {@link RoomFlag#GONE}).</li>
     *   <li>The {@link GameState#roomIn(int, int)} method returns a {@link Room}, not a {@link Passage}.</li>
     *   <li>The {@link GameState#floorAt()} method returns {@link SymbolType#FLOOR} for the player's position.</li>
     * </ul>
     * Uses a specific seed (100) and level 1 with {@code seeFloor = false} to ensure a normal room start.
     */
    @Test
    void testFloorAtNormalRoom() {
        final long startInMazeSeed = 100;
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(startInMazeSeed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);
        final Player player = new Player(config);
        final int levelNum = 1;
        final boolean seeFloor = false;
        config.setSeeFloor(seeFloor);
        gameState.setPlayer(player);
        gameState.newLevel(levelNum);

        final int px = gameState.getPlayer().getX();
        final int py = gameState.getPlayer().getY();

        // Starting in a normal non-maze non-gone room
        final Room normalRoom = gameState.getCurrentLevel().findRoomAt(px, py);
        assertNotNull(normalRoom);
        assertFalse(normalRoom.hasFlag(RoomFlag.MAZE));
        assertFalse(normalRoom.hasFlag(RoomFlag.GONE));

        final Room room = gameState.getCurrentLevel().roomIn(px, py);
        assertNotNull(room);
        assertFalse(room instanceof Passage);
        assertEquals(SymbolType.FLOOR, gameState.floorAt());
    }

    /**
     * Tests the {@link GameState#seeMonst(Monster)} method to ensure correct monster visibility logic.
     * Verifies that visibility is false when the player is blind, the monster is invisible without
     * CANSEE, the monster is in a different room and not close, or the room is dark and the monster
     * is not adjacent. Confirms visibility is true when the monster is adjacent, in the same row/column,
     * or in the same non-dark room, using a fixed seed (100) for reproducible results.
     */
    @Test
    void testSeeMonst() {
        final long seed = 100;
        final RogueRandom rogueRandom = new RogueRandom(seed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);

        final Player player = gameState.getPlayer();
        final int px = player.getX();
        final int py = player.getY();
        final Monster monster = new Monster(MonsterType.ICE_MONSTER);

        player.addFlag(CreatureFlag.ISBLIND);
        assertFalse(gameState.seeMonst(monster));
        player.removeFlag(CreatureFlag.ISBLIND);

        monster.addFlag(CreatureFlag.ISINVIS);
        assertFalse(gameState.seeMonst(monster));

        player.addFlag(CreatureFlag.CANSEE);
        int dx = 2;
        int dy = 0;// monster is two squares away horizontally, not in the same room
        monster.setPosition(px + dx, py + dy);
        assertFalse(gameState.seeMonst(monster));

        dx = 1; // monster is adjacent to the player and in different room
        monster.setPosition(px + dx, py + dy);
        assertTrue(gameState.seeMonst(monster));

        dy = 1; // adjacent diagonally
        monster.setPosition(px + dx, py + dy);
        assertTrue(gameState.seeMonst(monster));

        dx = 2; // monster is distant but in the same room
        dy = 2;
        monster.setRoom(player.getRoom());
        monster.setPosition(px + dx, py + dy);
        assertTrue(gameState.seeMonst(monster));

        // room is dark and monster not adjacent
        player.getRoom().addFlag(RoomFlag.DARK);
        assertFalse(gameState.seeMonst(monster));

        dx = 1; // room is dark and monster is adjacent
        dy = 1;
        monster.setRoom(player.getRoom());
        monster.setPosition(px + dx, py + dy);
        assertTrue(gameState.seeMonst(monster));
    }

    /**
     * Tests the {@link GameState#findDest(Monster)} method to ensure correct destination selection for monsters.
     * Verifies that the method returns the player’s position when the monster has no carry probability,
     * is in the same room as the player, is visible to the player, or when an item is already targeted by
     * another monster. Confirms it returns an item’s position when the monster is in the same room as an
     * untargeted item and passes the carry probability check. Uses a fixed seed (400) for reproducible results.
     */
    @Test
    void testFindDest() {
        final long seed = 400;
        final RogueRandom rogueRandom = new RogueRandom(seed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);
        final Player player = gameState.getPlayer();
        final int px = player.getPosition().getX();
        final int py = player.getPosition().getY();

        // monster has 0 carry probability
        Monster monster = new Monster(MonsterType.AQUATOR);
        Position dest = gameState.findDest(monster);
        assertEquals(player.getPosition(), dest);

        // monster has >0 carry probability (100), but is in same room as player
        monster = new Monster(MonsterType.DRAGON);
        monster.setRoom(player.getRoom());
        dest = gameState.findDest(monster);
        assertEquals(player.getPosition(), dest);

        // monster in another room but adjacent to player (player can see it)
        int dx = 1;
        int dy = 1;
        monster.setRoom(null);
        monster.setPosition(px + dx, py + dy);
        dest = gameState.findDest(monster);
        assertEquals(player.getPosition(), dest);

        // player cannot see the monster so the monster does not normally target it
        // but since there is no item in monster's room it targets player anyway
        player.addFlag(CreatureFlag.ISBLIND);
        dest = gameState.findDest(monster);
        assertEquals(player.getPosition(), dest);

        // monster in same room with item (item not targeted by other monsters), targets the item
        final Item item = gameState.getCurrentLevel().getItems().get(0);
        final Room itemRoom = gameState.roomIn(item.getPosition().getX(), item.getPosition().getY());
        monster.setRoom(itemRoom);
        for (Monster m : gameState.getCurrentLevel().getMonsters()) {
            if (Objects.equals(m.getDestination(), item.getPosition())) {
                m.setDestination(null);
            }
        }
        dest = gameState.findDest(monster);
        assertEquals(item.getPosition(), dest);

        // item already targeted by another monster, target selection falls back to player
        final Monster iceMonster = new Monster(MonsterType.ICE_MONSTER);
        gameState.getCurrentLevel().getMonsters().add(iceMonster);
        iceMonster.setDestination(item.getPosition());
        dest = gameState.findDest(monster);
        assertEquals(player.getPosition(), dest);

    }

    /**
     * Tests the {@link GameState#runTo(Position)} method to ensure a monster is set to run toward the player.
     * Verifies that a monster placed close to the player, in the same room, has its running state enabled,
     * held state cleared, and destination set to the player’s position after execution, using a fixed seed
     * (200) for reproducible results.
     */
    @Test
    void testRunTo() {
        final long seed = 200;
        final RogueRandom rogueRandom = new RogueRandom(seed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);
        final int px = gameState.getPlayer().getX();
        final int py = gameState.getPlayer().getY();

        // create and place the monster close to the player
        final Monster monster = new Monster(MonsterType.ICE_MONSTER);
        final int dx = 1;
        final int dy = 1;
        monster.setPosition(px + dx, py + dy);
        monster.setRoom(gameState.getPlayer().getRoom());

        final Place place = gameState.getCurrentLevel().getPlaceAt(monster.getX(), monster.getY());
        assertNotNull(place);
        place.setMonster(monster);

        // assert monster runs to player
        gameState.runTo(monster.getPosition());
        assertEquals(monster.getDestination(), gameState.getPlayer().getPosition());
        assertTrue(monster.hasFlag(CreatureFlag.ISRUN));
        assertFalse(monster.hasFlag(CreatureFlag.ISHELD));
    }

    /**
     * Tests the {@link GameState#newThing()} method for correct random item creation.
     * Verifies that food is prioritized when no food count exceeds 3, resetting the count,
     * and checks that other item types (potion, scroll) are selected with specific seeds.
     */
    @Test
    void testNewThing() {
        final long seed = 200;
        final RogueRandom rogueRandom = new RogueRandom(seed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);

        final int noFood = 5;
        gameState.setNoFood(noFood);
        Item item = gameState.newThing();
        assertNotNull(item);
        assertInstanceOf(Food.class, item);
        assertEquals(0, gameState.getNoFood());

        rogueRandom.reseed(seed * 3);
        item = gameState.newThing();
        assertInstanceOf(Potion.class, item);

        rogueRandom.reseed(seed * 4);
        item = gameState.newThing();
        assertInstanceOf(Scroll.class, item);
    }

    /**
     * Tests the {@link GameState#givePack(Monster, int, int)} method for correct item assignment.
     * Verifies that a monster (Nymph) with 100% carry probability receives no item when the level
     * is below the maximum level, and receives one item when the level equals the maximum level,
     * using a fixed seed for reproducibility.
     */
    @Test
    void testGivePack() {
        final long seed = 150;
        final RogueRandom rogueRandom = new RogueRandom(seed);
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);
        int level = 1;
        int maxLevel = 2;

        gameState.setLevelNum(level);
        gameState.setMaxLevel(maxLevel);

        // Nymph has 100% chance of carrying but because level<maxLevel does not get any item
        final Monster monster = new Monster(MonsterType.NYMPH);
        gameState.givePack(monster, level, maxLevel);
        assertNull(monster.getInventory());

        // maxLevel is equal to level and Nymph gets an item
        maxLevel = level;
        gameState.setMaxLevel(maxLevel);
        gameState.givePack(monster, level, maxLevel);
        assertNotNull(monster.getInventory());
        assertEquals(1, monster.getInventory().getItems().size());
    }

    private static class CommandParameterizedTimedTest extends CommandParameterizedTimed<Integer> {
        private final int mainGoldIncrease;
        private final AtomicInteger mainTimer;

        public CommandParameterizedTimedTest(int initialTimer, int mainGoldIncrease, AtomicInteger mainTimer) {
            super(initialTimer, 0, Phase.MAIN_TURN);
            this.mainGoldIncrease = mainGoldIncrease;
            this.mainTimer = mainTimer;
        }

        @Override
        public boolean execute(GameState gs) {
            gs.getPlayer().setGoldAmount(gs.getPlayer().getGoldAmount() + mainGoldIncrease);
            return true;
        }

        @Override
        public void decrementTimer() {
            mainTimer.decrementAndGet();
        }

        @Override
        public int getTurnsRemaining() {
            return mainTimer.get();
        }

        @Override
        public boolean isReadyToExecute() {
            return mainTimer.get() <= 0;
        }
    }
}
