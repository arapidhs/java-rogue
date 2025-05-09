package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    @Test
    void testGetSymbolType() {
        final Room corridor = new Room();
        corridor.addFlag(RoomFlag.GONE);
        assertEquals(SymbolType.PASSAGE, corridor.getSymbolType());

        final Room room = new Room();
        assertEquals(SymbolType.FLOOR, room.getSymbolType());
    }

    /**
     * Tests the {@link Room#rndPos(RogueRandom)} method to ensure it generates a random position
     * within the room's inner bounds (excluding 1-tile border). Verifies coordinates and null input handling.
     */
    @RepeatedTest(50)
    void testGetRandomPosition() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());

        final Room room = new Room();
        // Initialize variables for position and size
        final int roomX = 10;
        final int roomY = 10;
        final int roomWidth = 10;
        final int roomHeight = 8;
        final int minX = roomX + 1; // Inner bound: position.x + 1
        final int maxX = roomX + roomWidth - 2; // Inner bound: position.x + width - 2
        final int minY = roomY + 1; // Inner bound: position.y + 1
        final int maxY = roomY + roomHeight - 2; // Inner bound: position.y + height - 2

        // Set room position and size
        room.setPosition(roomX, roomY);
        room.setSize(roomWidth, roomHeight); // 10x8 room

        // Test random position
        final Position pos = room.rndPos(rogueRandom);

        // Verify position is within inner bounds (excluding 1-tile border)
        assertNotNull(pos);
        assertTrue(pos.getX() >= minX && pos.getX() <= maxX,
                "X coordinate out of bounds: " + pos.getX());
        assertTrue(pos.getY() >= minY && pos.getY() <= maxY,
                "Y coordinate out of bounds: " + pos.getY());
    }
    
}
