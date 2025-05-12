package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.entity.item.ObjectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RogueFactoryTest {

    /**
     * Tests the {@link RogueFactory#rndThing(int)} method to ensure correct random object type selection.
     * Verifies that:
     * <ul>
     *   <li>At or above the amulet level, the amulet ({@link ObjectType#AMULET}) can be selected.</li>
     *   <li>Below the amulet level, the amulet is never selected, and a non-null object type is returned.</li>
     * </ul>
     * Uses a fixed seed (428012268L) for reproducible results and tests 1000 iterations at random levels.
     */
    @Test
    void testRndThing(){
        final long amuletSeed=428012268L;
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(amuletSeed);
        final RogueFactory rogueFactory=new RogueFactory(config,rogueRandom);

        final int amuletLevel=config.getAmuletLevel();
        final ObjectType amuletObjectType = rogueFactory.rndThing(amuletLevel);
        assertEquals(ObjectType.AMULET,amuletObjectType);

        for(int i=0;i<1000;i++){
            final int level=rogueRandom.rnd(config.getAmuletLevel());
            final ObjectType objectType=rogueFactory.rndThing(level);
            assertNotNull(objectType);
            assertNotEquals(ObjectType.AMULET,objectType);
        }
    }

}
