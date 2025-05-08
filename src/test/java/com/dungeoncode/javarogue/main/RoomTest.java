package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {

    @Test
    void testGetChar() {
        final char corridorSymbol = SymbolMapper.getSymbol(PlaceFlag.PASSAGE);
        final Room corridor = new Room();
        corridor.addFlag(RoomFlag.GONE);
        assertEquals(corridorSymbol, corridor.getChar());

        final char floorSymbol = SymbolMapper.getSymbol(PlaceFlag.FLOOR);
        final Room room = new Room();
        assertEquals(floorSymbol, room.getChar());
    }
}
