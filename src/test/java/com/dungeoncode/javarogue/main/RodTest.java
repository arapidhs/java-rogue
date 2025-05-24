package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.item.ItemFlag;
import com.dungeoncode.javarogue.system.entity.item.Rod;
import com.dungeoncode.javarogue.system.entity.item.RodType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RodTest {

    @Test
    void testChargeStr() {
        final Rod rod = new Rod(RodType.WS_SLOW_M);

        // item is not known
        assertTrue(rod.chargeStr(true).isEmpty());

        rod.addFlag(ItemFlag.ISKNOW);
        rod.setCharges(15);
        final String terseCharges = " [15]";
        assertEquals(terseCharges, rod.chargeStr(true));

        final String verboseCharges = " [15 charges]";
        assertEquals(verboseCharges, rod.chargeStr(false));

        assertEquals(SymbolType.ROD, rod.getSymbolType());
    }

}
