package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymbolMapperTest {

    @Test
    void testSymbolMappings() {
        assertEquals(']', SymbolMapper.getSymbol(SymbolType.ARMOR));
        assertEquals('!', SymbolMapper.getSymbol(SymbolType.POTION));
        assertEquals('?', SymbolMapper.getSymbol(SymbolType.SCROLL));
        assertEquals(':', SymbolMapper.getSymbol(SymbolType.FOOD));
        assertEquals('=', SymbolMapper.getSymbol(SymbolType.RING));
        assertEquals(')', SymbolMapper.getSymbol(SymbolType.WEAPON));
        assertEquals('/', SymbolMapper.getSymbol(SymbolType.ROD));

        assertEquals('.', SymbolMapper.getSymbol(SymbolType.FLOOR));
        assertEquals('#', SymbolMapper.getSymbol(SymbolType.PASSAGE));
        assertEquals(' ', SymbolMapper.getSymbol(SymbolType.EMPTY));
        assertEquals('|', SymbolMapper.getSymbol(SymbolType.WALL_VERTICAL));
        assertEquals('-', SymbolMapper.getSymbol(SymbolType.WALL_HORIZONTAL));
        assertEquals('+', SymbolMapper.getSymbol(SymbolType.DOOR));

        assertEquals('@', SymbolMapper.getSymbol(Player.class));
    }

}
