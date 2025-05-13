package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.system.SymbolMapper;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.item.ObjectType;
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

    @Test
    void testGetSymbolType() {
        final SymbolType symbolType = SymbolMapper.getSymbolType(ObjectType.FOOD);
        assertEquals(SymbolType.FOOD,symbolType);
    }
}
