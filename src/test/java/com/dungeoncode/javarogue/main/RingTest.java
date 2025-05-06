package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RingTest {

    @Test
    void testNum() {
        final Ring ring = new Ring(RingType.PROTECTION);
        ring.setArmorClass(3);

        //  ring is not known
        assertTrue(ring.num().isEmpty());

        // ring is known
        ring.addFlag(ItemFlag.ISKNOW);
        final String knownRingNum = " [+3]";
        assertEquals(knownRingNum, ring.num());

        // ring type does not have armor class (not applicable)
        final Ring adornmentRing = new Ring(RingType.ADORNMENT);
        adornmentRing.addFlag(ItemFlag.ISKNOW);
        assertTrue(adornmentRing.num().isEmpty());
    }

}
