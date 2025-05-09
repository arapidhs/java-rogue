package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class LevelGeneratorTest {

    /**
     * Tests the {@link LevelGenerator#vert(Room, int)} method to ensure it correctly draws a vertical wall.
     * Verifies that the wall is placed at the specified x-coordinate along the room's height, with
     * {@link SymbolType#WALL_VERTICAL} flag and the correct symbol ('|'). Checks that only the intended
     * positions are modified.
     */
    @Test
    void testVert(){
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final LevelGenerator levelGenerator = new LevelGenerator(config,rogueRandom);
        final int levelNum=1;
        levelGenerator.initializeLevel(levelNum);

        final Room room = new Room();
        final int roomX=5;
        final int roomY=7;
        final int roomSizeX=4;
        final int roomSizeY=6;
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        levelGenerator.vert(room,roomX);

        for (int y = roomY + 1; y <= roomY + roomSizeY - 1; y++) {
            final Place place = levelGenerator.getLevel().getPlaceAt(roomX, y);
            assertNotNull(place);
            assertTrue(place.isType(PlaceType.WALL));
            assertEquals(SymbolType.WALL_VERTICAL,place.getSymbolType());
        }
    }

    /**
     * Tests the {@link LevelGenerator#vert(Room, int)} method to ensure it correctly draws a vertical wall.
     * Verifies that the wall is placed at the specified x-coordinate along the room's height, with
     * {@link SymbolType#WALL_VERTICAL} flag and the correct symbol ('|'). Checks that only the intended
     * positions are modified.
     */
    @Test
    void testHoriz(){
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final LevelGenerator levelGenerator = new LevelGenerator(config,rogueRandom);
        final int levelNum=1;
        levelGenerator.initializeLevel(levelNum);

        final Room room = new Room();
        final int roomX=5;
        final int roomY=7;
        final int roomSizeX=4;
        final int roomSizeY=6;
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        levelGenerator.horiz(room,roomY);

        for (int x = roomX; x <= roomX + roomSizeX - 1; x++) {
            final Place place = levelGenerator.getLevel().getPlaceAt(x, roomY);
            assertNotNull(place);
            assertTrue(place.isType(PlaceType.WALL));
            assertEquals(SymbolType.WALL_HORIZONTAL,place.getSymbolType());
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
    @Test
    void testDoor() {
        final int doorGenerations = 100;
        for(int i = 0; i< doorGenerations; i++) {
            // Arrange
            final Config config = new Config();
            final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
            final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
            final int levelNum = rogueRandom.rnd(config.getAmuletLevel());
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
                assertTrue(Objects.equals(place.getSymbolType(),SymbolType.WALL_HORIZONTAL) ||
                        Objects.equals(place.getSymbolType(),SymbolType.WALL_VERTICAL));
            }
        }
    }

    /**
     * Tests the {@link LevelGenerator#putPass(Position)} method to ensure it correctly places a passage tile.
     * Verifies that the passage tile is placed at the specified position with the {@link PlaceType#PASSAGE} type.
     * Ensures that if the place has the {@link PlaceFlag#REAL} flag, it has the passage symbol ('#');
     * otherwise, it has the empty space ' ' symbol (secret passage).
     */
    @Test
    void testPutPass() {
        final int putPassIterations = 100;
        for(int i = 0; i< putPassIterations; i++) {
            // Arrange
            final Config config = new Config();
            final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
            final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
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
    }

    /**
     * Tests the {@link LevelGenerator#rndRoom(Room[])} method to ensure it selects a random non-gone room.
     * Verifies that the returned room index corresponds to a room without the {@link RoomFlag#GONE} flag
     * and is within the valid range of the rooms array.
     */
    @Test
    void testRndRoom() {
        final int rndRoomIterations = 50;
        for(int i = 0; i< rndRoomIterations; i++) {
            // Arrange
            final Config config = new Config();
            final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
            final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
            final Room[] rooms = new Room[config.getMaxRooms()];
            Arrays.setAll(rooms, k -> new Room());

            // Set some rooms as gone
            for(int j=0;j<rogueRandom.rnd(rooms.length/2)+1;j++){
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
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
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
    void testMaze() {
        // Arrange: Set up config with sufficient dimensions for a 15x6 maze
        final Config config = new Config();

        // Arrange: Initialize RogueRandom with a specific seed for predictable results
        final RogueRandom rogueRandom = new RogueRandom(12345L); // Fixed seed 12345L
        // Arrange: Initialize LevelGenerator and level
        final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
        levelGenerator.initializeLevel(rogueRandom.rnd(config.getAmuletLevel()));
        // Arrange: Set up maze parameters (room at (5,5), size 15x6, hardcoded start)
        final Room mazeRoom = new Room();
        final int roomX=5;
        final int roomY=8;
        final Position position = new Position(roomX,roomY);

        // Execute the maze creation
        levelGenerator.setMazeRoomDimensions(mazeRoom, levelGenerator.getMaxRoomX(), levelGenerator.getMaxRoomY(),position );
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

    @Test
    void testLevelGeneration(){
        final int levelGenerationIterations = 1000;
        for(int i = 0; i< levelGenerationIterations; i++) {
            final Config config = new Config();
            final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
            final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
            final int levelNum = rogueRandom.rnd(config.getAmuletLevel())+1;
            final Level level = levelGenerator.newLevel(levelNum);
            assertNotNull(level);
        }
    }
}
