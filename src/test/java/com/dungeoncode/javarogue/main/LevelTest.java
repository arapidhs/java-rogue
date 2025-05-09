package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {

    final Config config = new Config();
    final RogueRandom rogueRandom = new RogueRandom(config.getSeed());

    @Test
    void testFindItemAt() {

        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(),rogueRandom);
        final Food food = new Food();
        final int foodX = 10;
        final int foodY = 5;
        food.setPosition(foodX, foodY);

        level.addItem(food);

        final Item found = level.findItemAt(foodX, foodY);
        assertNotNull(found);

        boolean removed = level.removeItem(food);
        assertTrue(removed);

        final Item notFound = level.findItemAt(foodX, foodY);
        assertNull(notFound);

    }

    @Test
    void testPlace() {
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(),rogueRandom);
        final int placeX = 2;
        final int placeY = 5;
        final SymbolType doorSymbolType = SymbolType.DOOR;
        final Place place = new Place();
        place.setSymbolType(doorSymbolType);
        level.setPlaceAt(placeX, placeY, place);

        Place placeFound = level.getPlaceAt(placeX, placeY);
        assertNotNull(placeFound);
        assertEquals(doorSymbolType, placeFound.getSymbolType());

        assertEquals(doorSymbolType, level.getSymbolType(placeX, placeY));

        // set place at max width and height
        final int maxX = config.getLevelMaxWidth() - 1;
        final int maxY = config.getLevelMaxHeight() - 1;
        level.setPlaceAt(maxX, maxY, place);
        placeFound = level.getPlaceAt(maxX, maxY);
        assertNotNull(placeFound);
        assertEquals(doorSymbolType, placeFound.getSymbolType());

        // set place out of bounds
        final int outOfBoundsX = config.getLevelMaxWidth();
        final int outOfBoundsY = config.getLevelMaxHeight();
        assertThrows(IllegalArgumentException.class, () ->
                level.setPlaceAt(outOfBoundsX, outOfBoundsY, place));

        int symbolX = 3;
        int symbolY = 6;
        SymbolType floorSymbolType = SymbolType.FLOOR;
        level.setPlaceSymbol(symbolX, symbolY, floorSymbolType);
        assertNull(level.getSymbolType(symbolX, symbolY));

        symbolX = placeX;
        symbolY = placeY;
        level.setPlaceSymbol(symbolX, symbolY, floorSymbolType);
        assertEquals(floorSymbolType, level.getSymbolType(symbolX, symbolY));

    }

    @Test
    void testRoom() {
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(),rogueRandom);
        final int roomX = 5;
        final int roomY = 5;
        final int roomSizeX = 10;
        final int roomSizeY = 10;
        final Room room = new Room();
        room.setPosition(roomX, roomY);
        room.setSize(roomSizeX, roomSizeY);

        assertTrue(level.addRoom(room));

        assertNotNull(level.findRoomAt(roomX, roomY));
        assertNotNull(level.findRoomAt(roomX + roomSizeX - 1, roomY + roomSizeY - 1));
        assertNotNull(level.findRoomAt((roomX + roomSizeX) / 2, (roomY + roomSizeY) / 2));
    }

    /**
     * Tests the {@link Level#rndRoom()} method to ensure it selects a random non-GONE room.
     * Verifies behavior with no rooms and with a mix of valid and GONE rooms.
     */
    @Test
    void testRndRoom() {
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(), rogueRandom);

        // Test with no rooms
        assertThrows(IllegalStateException.class, level::rndRoom);

        // Add rooms
        final Room room1 = new Room();
        room1.setPosition(5, 5);
        room1.setSize(10, 10);

        final Room goneRoom = new Room();
        goneRoom.setPosition(20, 20);
        goneRoom.setSize(10, 10);
        goneRoom.addFlag(RoomFlag.GONE);

        final Room room3 = new Room();
        room3.setPosition(35, 35);
        room3.setSize(10, 10);

        level.addRoom(room1);
        level.addRoom(goneRoom);
        level.addRoom(room3);

        // Test random room selection
        Room selected = level.rndRoom();
        assertNotNull(selected);
        assertTrue(selected == room1 || selected == room3);
        assertFalse(selected.hasFlag(RoomFlag.GONE));
    }

    /**
     * Tests the {@link Level#findFloor(Room, int, boolean)} method to ensure it finds a valid floor spot
     * for item or monster placement in a specified or random room. Verifies behavior with valid/invalid cases.
     */
    @RepeatedTest(50)
    void testFindFloor() {
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight(), rogueRandom);

        assertThrows(IllegalStateException.class, () -> level.findFloor(null, 0, false));

        final int roomX = 5;
        final int roomY = 5;
        final int roomWidth = 10;
        final int roomHeight = 10;
        final int minX = roomX + 1;
        final int maxX = roomX + roomWidth - 2;
        final int minY = roomY + 1;
        final int maxY = roomY + roomHeight - 2;
        final Room room = new Room();
        room.setPosition(roomX, roomY);
        room.setSize(roomWidth, roomHeight);
        level.addRoom(room);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                final Place floorPlace = new Place();
                floorPlace.setPlaceType(PlaceType.FLOOR);
                floorPlace.setSymbolType(SymbolType.FLOOR);
                level.setPlaceAt(x, y, floorPlace);
            }
        }

        Position pos = level.findFloor(room, 0, false);
        assertNotNull(pos);
        assertTrue(pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY,
                "Position out of room bounds: " + pos);

        pos = level.findFloor(room, 0, true);
        assertNotNull(pos);
        assertTrue(pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY,
                "Position out of room bounds: " + pos);

        final int mazeRoomX = 20;
        final int mazeRoomY = 20;
        final int mazeRoomWidth = 10;
        final int mazeRoomHeight = 10;
        final int mazeMinX = mazeRoomX + 1;
        final int mazeMaxX = mazeRoomX + mazeRoomWidth - 2;
        final int mazeMinY = mazeRoomY + 1;
        final int mazeMaxY = mazeRoomY + mazeRoomHeight - 2;
        final Room mazeRoom = new Room();
        mazeRoom.setPosition(mazeRoomX, mazeRoomY);
        mazeRoom.setSize(mazeRoomWidth, mazeRoomHeight);
        mazeRoom.addFlag(RoomFlag.MAZE);
        level.addRoom(mazeRoom);

        for (int x = mazeMinX; x <= mazeMaxX; x++) {
            for (int y = mazeMinY; y <= mazeMaxY; y++) {
                final Place passagePlace = new Place();
                passagePlace.setPlaceType(PlaceType.PASSAGE);
                passagePlace.setSymbolType(SymbolType.PASSAGE);
                level.setPlaceAt(x, y, passagePlace);
            }
        }

        pos = level.findFloor(mazeRoom, 0, false);
        assertNotNull(pos);
        assertTrue(pos.getX() >= mazeMinX && pos.getX() <= mazeMaxX && pos.getY() >= mazeMinY && pos.getY() <= mazeMaxY,
                "Position out of maze room bounds: " + pos);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                final Place wallPlace = new Place();
                wallPlace.setPlaceType(PlaceType.WALL);
                wallPlace.setSymbolType(SymbolType.WALL_HORIZONTAL);
                level.setPlaceAt(x, y, wallPlace);
            }
        }
        pos = level.findFloor(room, 10, false);
        assertNull(pos, "Expected null when no valid floor spot found within limit");
    }


}
