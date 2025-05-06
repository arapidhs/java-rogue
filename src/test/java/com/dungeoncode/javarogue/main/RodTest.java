package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RodTest {

    @Test
    void testChargeStr() {
        final Rod rod = new Rod(RodType.SLOW_MONSTER);

        // item is not known
        assertTrue(rod.chargeStr(true).isEmpty());

        rod.addFlag(ItemFlag.ISKNOW);
        rod.setCharges(15);
        final String terseCharges = " [15]";
        assertEquals(terseCharges, rod.chargeStr(true));

        final String verboseCharges = " [15 charges]";
        assertEquals(verboseCharges, rod.chargeStr(false));
    }

}
