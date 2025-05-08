package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {

    final Config config = new Config();

    @Test
    void testFindItemAt() {

        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
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
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
        final int placeX = 2;
        final int placeY = 5;
        final char placeSymbol = 'p';
        final Place place = new Place(placeSymbol);
        level.setPlaceAt(placeX, placeY, place);

        Place placeFound = level.getPlaceAt(placeX, placeY);
        assertNotNull(placeFound);
        assertEquals(placeSymbol, placeFound.getSymbol());

        assertEquals(placeSymbol, level.getSymbol(placeX, placeY));

        // set place at max width and height
        final int maxX = config.getLevelMaxWidth() - 1;
        final int maxY = config.getLevelMaxHeight() - 1;
        level.setPlaceAt(maxX, maxY, place);
        placeFound = level.getPlaceAt(maxX, maxY);
        assertNotNull(placeFound);
        assertEquals(placeSymbol, placeFound.getSymbol());

        // set place out of bounds
        final int outOfBoundsX = config.getLevelMaxWidth();
        final int outOfBoundsY = config.getLevelMaxHeight();
        assertThrows(IllegalArgumentException.class, () ->
                level.setPlaceAt(outOfBoundsX, outOfBoundsY, place));

        int symbolX = 3;
        int symbolY = 6;
        char symbol = SymbolMapper.getSymbol(PlaceFlag.FLOOR);
        level.setPlaceSymbol(symbolX, symbolY, symbol);
        assertNull(level.getSymbol(symbolX, symbolY));

        symbolX = placeX;
        symbolY = placeY;
        level.setPlaceSymbol(symbolX, symbolY, symbol);
        assertEquals(symbol, level.getSymbol(symbolX, symbolY));

    }

    @Test
    void testRoom() {
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
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

}
