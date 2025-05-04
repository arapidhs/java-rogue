package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    }

}
