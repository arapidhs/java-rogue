package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import com.dungeoncode.javarogue.system.LevelGenerator;
import com.dungeoncode.javarogue.system.MessageSystem;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.entity.item.Gold;
import com.dungeoncode.javarogue.system.entity.item.Item;
import com.dungeoncode.javarogue.system.initializer.DefaultInitializer;
import com.dungeoncode.javarogue.system.world.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class LevelGeneratorTest extends RogueBaseTest {

    /**
     * Tests the {@link LevelGenerator#vert(Room, int)} method to ensure it correctly draws a vertical wall.
     * Verifies that the wall is placed at the specified x-coordinate along the room's height, with
     * {@link SymbolType#WALL_VERTICAL} flag and the correct symbol ('|'). Checks that only the intended
     * positions are modified.
     */
    @Test
    void testVert() {
        final LevelGenerator levelGenerator = createLevelGenerator();
        final int levelNum = 1;
        levelGenerator.initializeLevel(levelNum);

        final Room room = new Room();
        final int roomX = 5;
        final int roomY = 7;
        final int roomSizeX = 4;
        final int roomSizeY = 6;
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        levelGenerator.vert(room, roomX);

        for (int y = roomY + 1; y <= roomY + roomSizeY - 1; y++) {
            final Place place = levelGenerator.getLevel().getPlaceAt(roomX, y);
            assertNotNull(place);
            assertTrue(place.isType(PlaceType.WALL));
            assertEquals(SymbolType.WALL_VERTICAL, place.getSymbolType());
        }
    }

    private LevelGenerator createLevelGenerator() {
        return createLevelGenerator(0);
    }

    private LevelGenerator createLevelGenerator(final long seed) {
        final RogueRandom rogueRandom;
        if (seed != 0) {
            rogueRandom = new RogueRandom(seed);
        } else {
            rogueRandom = new RogueRandom(config.getSeed());
        }
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);
        return new LevelGenerator(gameState);
    }

    /**
     * Tests the {@link LevelGenerator#vert(Room, int)} method to ensure it correctly draws a vertical wall.
     * Verifies that the wall is placed at the specified x-coordinate along the room's height, with
     * {@link SymbolType#WALL_VERTICAL} flag and the correct symbol ('|'). Checks that only the intended
     * positions are modified.
     */
    @Test
    void testHoriz() {
        final LevelGenerator levelGenerator = createLevelGenerator();
        final int levelNum = 1;
        levelGenerator.initializeLevel(levelNum);

        final Room room = new Room();
        final int roomX = 5;
        final int roomY = 7;
        final int roomSizeX = 4;
        final int roomSizeY = 6;
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        levelGenerator.horiz(room, roomY);

        for (int x = roomX; x <= roomX + roomSizeX - 1; x++) {
            final Place place = levelGenerator.getLevel().getPlaceAt(x, roomY);
            assertNotNull(place);
            assertTrue(place.isType(PlaceType.WALL));
            assertEquals(SymbolType.WALL_HORIZONTAL, place.getSymbolType());
        }
    }

    /**
     * Tests the {@link LevelGenerator#door(Room, Position)} method to ensure it correctly places a door.
     * Verifies that the door tile is placed at the specified position and added to the room's exits.
     * Ensures that if the place has the {@link PlaceFlag#REAL} flag, it has the {@link PlaceType#DOOR} type;
     * otherwise, it has {@link PlaceType#WALL} type and
     * either the {@link SymbolType#WALL_HORIZONTAL} or {@link SymbolType#WALL_VERTICAL} symbol type.
     * Tests non-maze rooms.
     */
    @RepeatedTest(100)
    void testDoor() {
        // Arrange
        final LevelGenerator levelGenerator = createLevelGenerator();
        final int levelNum = levelGenerator.getRogueRandom().rnd(config.getAmuletLevel());
        levelGenerator.initializeLevel(levelNum);

        final Room room = new Room();
        final int roomX = 5;
        final int roomY = 7;
        final int roomSizeX = 4;
        final int roomSizeY = 6;
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        final Position pos = new Position(roomX, roomY + roomSizeY - 1); // Bottom wall

        // Place door
        levelGenerator.door(room, pos);

        // Assert
        final Place place = levelGenerator.getLevel().getPlaceAt(pos.getX(), pos.getY());
        assertNotNull(place);
        //Door position should be in room's exits
        assertTrue(room.getExits().stream().anyMatch(e -> e.getX() == pos.getX() && e.getY() == pos.getY()));

        if (place.isReal()) {
            // Real place should have DOOR flag
            assertTrue(place.isType(PlaceType.DOOR));
        } else {
            // Non-real place should have WALL_HORIZONTAL or WALL_VERTICAL flag
            assertTrue(place.isType(PlaceType.WALL));
            assertTrue(Objects.equals(place.getSymbolType(), SymbolType.WALL_HORIZONTAL) ||
                    Objects.equals(place.getSymbolType(), SymbolType.WALL_VERTICAL));
        }
    }

    /**
     * Tests the {@link LevelGenerator#putPass(Position)} method to ensure it correctly places a passage tile.
     * Verifies that the passage tile is placed at the specified position with the {@link PlaceType#PASSAGE} type.
     * Ensures that if the place has the {@link PlaceFlag#REAL} flag, it has the passage symbol ('#');
     * otherwise, it has the empty space ' ' symbol (secret passage).
     */
    @RepeatedTest(100)
    void testPutPass() {
        // Arrange
        final LevelGenerator levelGenerator = createLevelGenerator();
        final int levelNum = 10; // High level to increase secret passage chance
        levelGenerator.initializeLevel(levelNum);

        final Position pos = new Position(10, 10); // Arbitrary position

        // Put hte passage
        levelGenerator.putPass(pos);

        // Assert
        final Place place = levelGenerator.getLevel().getPlaceAt(pos.getX(), pos.getY());
        assertNotNull(place);
        assertTrue(place.isType(PlaceType.PASSAGE));
        // If REAL flag is present, symbol should be '#'; otherwise, no symbol (secret passage)
        if (place.hasFlag(PlaceFlag.REAL)) {
            assertEquals(SymbolType.PASSAGE, place.getSymbolType());
        } else {
            assertEquals(SymbolType.EMPTY, place.getSymbolType());
        }
    }

    /**
     * Tests the {@link LevelGenerator#drawRoom(Room)} method for a normal (non-maze) room.
     * Verifies that:
     * <ul>
     *   <li>Left and right walls are vertical ({@link SymbolType#WALL_VERTICAL}, {@link PlaceType#WALL}).</li>
     *   <li>Top and bottom walls are horizontal ({@link SymbolType#WALL_HORIZONTAL}, {@link PlaceType#WALL}).</li>
     *   <li>Interior tiles are floors ({@link SymbolType#FLOOR}, {@link PlaceType#FLOOR}).</li>
     * </ul>
     * Uses a room at position (5,7) with size 4x6 on level 1.
     */
    @Test
    void testDrawRoomNormal() {
        // Arrange: Set up level generator and room
        final LevelGenerator levelGenerator = createLevelGenerator();
        final int levelNum = 1;
        levelGenerator.initializeLevel(levelNum);

        final Room room = new Room();
        final int roomX = 5;
        final int roomY = 7;
        final int roomSizeX = 4;
        final int roomSizeY = 6;
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        // Act: Draw the room
        levelGenerator.drawRoom(room);

        // Assert: Verify left and right vertical walls
        for (int y = roomY + 1; y <= roomY + roomSizeY - 2; y++) {
            // Check left wall
            Place place = levelGenerator.getLevel().getPlaceAt(roomX, y);
            assertNotNull(place); // Ensure place exists
            assertTrue(place.isType(PlaceType.WALL)); // Verify wall type
            assertEquals(SymbolType.WALL_VERTICAL, place.getSymbolType()); // Verify vertical wall symbol

            // Check right wall
            place = levelGenerator.getLevel().getPlaceAt(roomX + roomSizeX - 1, y);
            assertNotNull(place); // Ensure place exists
            assertTrue(place.isType(PlaceType.WALL)); // Verify wall type
            assertEquals(SymbolType.WALL_VERTICAL, place.getSymbolType()); // Verify vertical wall symbol
        }

        // Assert: Verify top and bottom horizontal walls
        for (int x = roomX; x <= roomX + roomSizeX - 1; x++) {
            // Check top wall
            Place place = levelGenerator.getLevel().getPlaceAt(x, roomY);
            assertNotNull(place); // Ensure place exists
            assertTrue(place.isType(PlaceType.WALL)); // Verify wall type
            assertEquals(SymbolType.WALL_HORIZONTAL, place.getSymbolType()); // Verify horizontal wall symbol

            // Check bottom wall
            place = levelGenerator.getLevel().getPlaceAt(x, roomY + roomSizeY - 1);
            assertNotNull(place); // Ensure place exists
            assertTrue(place.isType(PlaceType.WALL)); // Verify wall type
            assertEquals(SymbolType.WALL_HORIZONTAL, place.getSymbolType()); // Verify horizontal wall symbol
        }

        // Assert: Verify interior floor tiles
        for (int x = roomX + 1; x <= roomX + roomSizeX - 2; x++) {
            for (int y = roomY + 1; y <= roomY + roomSizeY - 2; y++) {
                final Place place = levelGenerator.getLevel().getPlaceAt(x, y);
                assertNotNull(place); // Ensure place exists
                assertTrue(place.isType(PlaceType.FLOOR)); // Verify floor type
                assertEquals(SymbolType.FLOOR, place.getSymbolType()); // Verify floor symbol
            }
        }
    }

    /**
     * Tests the {@link LevelGenerator#addGold(Room)} method to ensure gold is correctly placed in a room.
     * Verifies that:
     * <ul>
     *   <li>Gold item is created and placed at a valid floor position.</li>
     *   <li>Gold is an instance of {@link Gold} with correct position and value (>1).</li>
     *   <li>Room's gold position and value match the gold item.</li>
     *   <li>The place at the gold's position has {@link SymbolType#GOLD}.</li>
     * </ul>
     * Uses a fixed seed (100) and level 1 for consistent generation.
     */
    @Test
    void testAddGold() {
        final long seed = 100;
        final LevelGenerator levelGenerator = createLevelGenerator(seed);
        final int levelNum = 1;
        final Level level = levelGenerator.newLevel(levelNum);
        final Room room = level.rndRoom();
        final Position goldPos = levelGenerator.addGold(room);

        final Item gold = level.findItemAt(goldPos.getX(), goldPos.getY());
        assertNotNull(gold);
        assertInstanceOf(Gold.class, gold);
        assertEquals(goldPos, gold.getPosition());
        assertEquals(goldPos, room.getGoldPosition());
        assertTrue(gold.getGoldValue() > 1);
        assertEquals(gold.getGoldValue(), room.getGoldValue());

        final Place place = level.getPlaceAt(goldPos.getX(), goldPos.getY());
        assertNotNull(place);
        assertEquals(SymbolType.GOLD, place.getSymbolType());

    }

    /**
     * Tests the {@link LevelGenerator#rndRoom(Room[])} method to ensure it selects a random non-gone room.
     * Verifies that the returned room index corresponds to a room without the {@link RoomFlag#GONE} flag
     * and is within the valid range of the rooms array.
     */
    @RepeatedTest(50)
    void testRndRoom() {
        // Arrange
        final LevelGenerator levelGenerator = createLevelGenerator();
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        final Room[] rooms = new Room[config.getMaxRooms()];
        Arrays.setAll(rooms, k -> new Room());

        // Set some rooms as gone
        for (int j = 0; j < rogueRandom.rnd(rooms.length / 2) + 1; j++) {
            rooms[rogueRandom.rnd(rooms.length)].addFlag(RoomFlag.GONE);
        }

        // Select the room
        final int selectedRoom = levelGenerator.rndRoom(rooms);

        // Assert
        // Ensure selected index is within bounds
        assertTrue(selectedRoom >= 0 && selectedRoom < rooms.length);
        // Ensure selected room is not gone
        assertFalse(rooms[selectedRoom].hasFlag(RoomFlag.GONE));
    }

    /**
     * Tests the {@link LevelGenerator#accntMaze(LevelGenerator.Spot[][], int, int, int, int)} method to ensure it
     * correctly records a connection from a maze cell to a neighboring cell. Verifies that the exit
     * is added to the cell's exits list without duplicates and that the connection is properly stored.
     * Neighbor coordinates are tested with a step size of 2, reflecting maze grid movement (e.g., right neighbor at x+2).
     */
    @Test
    void testAccntMaze() {
        // Arrange
        final LevelGenerator levelGenerator = createLevelGenerator();
        final LevelGenerator.Spot[][] maze = new LevelGenerator.Spot[10][10]; // Arbitrary maze size
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                maze[y][x] = new LevelGenerator.Spot();
            }
        }

        // The neighbor (7,5) is to the right because nx=7 is two columns right of x=5
        // on the same row (ny=5, y=5).
        // This aligns with the maze’s grid-based movement where a neighboring steps increases  by 2.
        final int x = 5;
        final int y = 5;
        final int nx = 7; // Neighbor to the right
        final int ny = 5;


        // Account for maze exit
        levelGenerator.accntMaze(maze, y, x, ny, nx);

        // Assert
        final LevelGenerator.Spot spot = maze[y][x];
        // Ensure one exit was added
        assertEquals(1, spot.getNexits());
        // Verify the exit coordinates
        final Position[] exits = spot.getExits();
        assertNotNull(exits[0]);
        assertEquals(nx, exits[0].getX());
        assertEquals(ny, exits[0].getY());

        // Test duplicate prevention by calling again
        levelGenerator.accntMaze(maze, y, x, ny, nx);
        // Duplicate exit should not be added
        assertEquals(1, spot.getNexits());

        final int nx2 = 5; // Neighbor down
        final int ny2 = 7;
        levelGenerator.accntMaze(maze, y, x, ny2, nx2);
        assertEquals(2, spot.getNexits());
        assertEquals(nx2, exits[1].getX());
        assertEquals(ny2, exits[1].getY());
    }

    /**
     * Tests the {@link LevelGenerator#doMaze(Room)} method to ensure it correctly generates a maze
     * within a room using recursive backtracking. Verifies that passage tiles are placed according to
     * the expected 25x7 maze grid with the {@link PlaceType#PASSAGE} type, starting from a hardcoded
     * position (2,4) in a room at (5,8). Uses a specific seed (12345L) for predictable random number
     * generation to match the expected maze layout.
     */
    @Test
    void testDoMaze() {
        // Arrange: Initialize LevelGenerator and level
        final LevelGenerator levelGenerator = createLevelGenerator(); //726652449

        // Arrange: Initialize RogueRandom with a specific seed for predictable results
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        rogueRandom.reseed(12345L);

        levelGenerator.initializeLevel(rogueRandom.rnd(config.getAmuletLevel()));
        // Arrange: Set up maze parameters (room at (5,5), size 15x6, hardcoded start)
        final Room mazeRoom = new Room();
        final int roomX = 5;
        final int roomY = 8;
        final Position position = new Position(roomX, roomY);

        // Execute the maze creation
        levelGenerator.setMazeRoomDimensions(mazeRoom, levelGenerator.getMaxRoomX(), levelGenerator.getMaxRoomY(), position);
        levelGenerator.doMaze(mazeRoom);


        // Expected maze layout, 1s are passages 0s are empty places.
        final int[][] expectedMaze = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0},
                {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        // Assert: Verify passage tiles match expectedMaze
        for (int y = roomY; y < roomY + mazeRoom.getSize().getY(); y++) {
            for (int x = roomX; x < roomX + mazeRoom.getSize().getX(); x++) {
                final Place place = levelGenerator.getLevel().getPlaceAt(x, y);
                // Assert PASS flag where expectedMaze is 1
                if (expectedMaze[y - roomY][x - roomX] == 1) {
                    assertNotNull(place);
                    assertTrue(place.isType(PlaceType.PASSAGE));
                }
            }
        }
    }

    /**
     * This test tests level generation and specifically a seed
     * where item placement was failing with an infinite loop,
     * unable to find a correct place for the item due to bug
     * in find floor logic.
     */
    @Test
    void testLevelItemsPlacement() {
        final long seed = 856821835;
        final LevelGenerator levelGenerator = createLevelGenerator(seed);
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        final int levelNum = rogueRandom.rnd(config.getAmuletLevel()) + 1;
        final Level level = levelGenerator.newLevel(levelNum);
        assertNotNull(level);
    }

    /**
     * Specific scenario where monster placement in a maze treasure room
     * fails after maxTries and placement has to proceed to the next monster placement.
     */
    @Test
    void testTreasureRoomMonsterPlacement() {
        final long seed = -420302098;
        final LevelGenerator levelGenerator = createLevelGenerator(seed);
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        final int levelNum = rogueRandom.rnd(config.getAmuletLevel()) + config.getAmuletLevel() / 2;
        final Level level = levelGenerator.newLevel(levelNum);
        assertNotNull(level);
    }

    @RepeatedTest(100)
    void testLevelGeneration() {
        final LevelGenerator levelGenerator = createLevelGenerator();
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        final int levelNum = rogueRandom.rnd(config.getAmuletLevel()) + 1;
        final Level level = levelGenerator.newLevel(levelNum);
        assertNotNull(level);
    }

    @Tag("stress")
    @RepeatedTest(5000)
    void testLevelGenerationStressTest() {
        final LevelGenerator levelGenerator = createLevelGenerator();
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        final int levelNum = rogueRandom.rnd(config.getAmuletLevel()) + 1;
        final Level level = levelGenerator.newLevel(levelNum);
        assertNotNull(level);
    }

    @Tag("stress")
    @RepeatedTest(5000)
    void testLevelGenerationAtHighLevelsStressTest() {
        final LevelGenerator levelGenerator = createLevelGenerator();
        final RogueRandom rogueRandom = levelGenerator.getRogueRandom();
        final int levelNum = rogueRandom.rnd(config.getAmuletLevel()) + config.getAmuletLevel() / 2;
        final Level level = levelGenerator.newLevel(levelNum);
        assertNotNull(level);
    }

}
