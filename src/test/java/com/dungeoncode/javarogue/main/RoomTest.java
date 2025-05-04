package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest {

    @Test
    void testGetChar() {
        final char corridorSymbol = SymbolMapper.getSymbol(RoomType.PASSAGE);
        final Room corridor = new Room(
                new Position(5, 5),
                new Position(10, 10),
                new Position(7, 7),
                100,
                EnumSet.of(RoomFlag.CORRIDOR),
                new ArrayList<>()
        );
        assertEquals(corridorSymbol, corridor.getChar());

        final char floorSymbol = SymbolMapper.getSymbol(RoomType.FLOOR);
        final Room room = new Room(
                new Position(5, 5),
                new Position(10, 10),
                new Position(7, 7),
                100,
                null,
                new ArrayList<>()
        );
        assertEquals(floorSymbol, room.getChar());
    }
}
