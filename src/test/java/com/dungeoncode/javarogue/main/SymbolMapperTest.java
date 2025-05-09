package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymbolMapperTest {

    @Test
    void testSymbolMappings() {
        assertEquals(']', SymbolMapper.getSymbol(ObjectType.ARMOR));
        assertEquals('!', SymbolMapper.getSymbol(ObjectType.POTION));
        assertEquals('?', SymbolMapper.getSymbol(ObjectType.SCROLL));
        assertEquals(':', SymbolMapper.getSymbol(ObjectType.FOOD));
        assertEquals('=', SymbolMapper.getSymbol(ObjectType.RING));
        assertEquals(')', SymbolMapper.getSymbol(ObjectType.WEAPON));
        assertEquals('/', SymbolMapper.getSymbol(ObjectType.ROD));

        assertEquals('.', SymbolMapper.getSymbol(SymbolType.FLOOR));
        assertEquals('#', SymbolMapper.getSymbol(SymbolType.PASSAGE));
        assertEquals(' ', SymbolMapper.getSymbol(SymbolType.EMPTY));
        assertEquals('|', SymbolMapper.getSymbol(SymbolType.WALL_VERTICAL));
        assertEquals('-', SymbolMapper.getSymbol(SymbolType.WALL_HORIZONTAL));
        assertEquals('+', SymbolMapper.getSymbol(SymbolType.DOOR));
    }

}
