package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {

    @Test
    void testGetSymbolType() {
        final Room corridor = new Room();
        corridor.addFlag(RoomFlag.GONE);
        assertEquals(SymbolType.PASSAGE, corridor.getSymbolType());

        final Room room = new Room();
        assertEquals(SymbolType.FLOOR, room.getSymbolType());
    }
}
