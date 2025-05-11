package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.entity.item.ItemFlag;
import com.dungeoncode.javarogue.entity.item.ring.Ring;
import com.dungeoncode.javarogue.entity.item.ring.RingType;
import com.dungeoncode.javarogue.ui.SymbolType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RingTest {

    @Test
    void testNum() {
        final Ring ring = new Ring(RingType.R_PROTECT);
        ring.setArmorClass(3);

        //  ring is not known
        assertTrue(ring.num().isEmpty());

        // ring is known
        ring.addFlag(ItemFlag.ISKNOW);
        final String knownRingNum = " [+3]";
        assertEquals(knownRingNum, ring.num());

        // ring type does not have armor class (not applicable)
        final Ring adornmentRing = new Ring(RingType.R_NOP);
        adornmentRing.addFlag(ItemFlag.ISKNOW);
        assertTrue(adornmentRing.num().isEmpty());

        assertEquals(SymbolType.RING,ring.getSymbolType());
    }

}
