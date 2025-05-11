package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.template.KillTypeTemplate;
import com.dungeoncode.javarogue.template.MonsterTemplate;
import com.dungeoncode.javarogue.template.Templates;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RogueRandom}.
 */
public class RogueRandomTest {

    @Test
    void testRndWithinRange() {
        final RogueRandom random = new RogueRandom(123456L);
        for (int i = 0; i < 1000; i++) {
            final int result = random.rnd(50);
            assertTrue(result >= 0 && result < 50, "Result should be within [0, 50): " + result);
        }
    }

    @Test
    void testRndWithZeroRange() {
        final RogueRandom random = new RogueRandom(654321L);
        final int result = random.rnd(0);
        assertEquals(0, result, "rnd(0) should always return 0");
    }

    @Test
    void testRndWithNegativeRange() {
        final RogueRandom random = new RogueRandom(654321L);
        final int result = random.rnd(-1);
        assertEquals(0, result, "rnd(0) should always return 0");
    }

    @Test
    void testDeterministicSequence() {
        final RogueRandom random1 = new RogueRandom(987654321L);
        final RogueRandom random2 = new RogueRandom(987654321L);
        for (int i = 0; i < 1000; i++) {
            assertEquals(random1.rnd(100), random2.rnd(100),
                    "RogueRandom instances with the same seed should produce identical sequences");
        }
    }

    @Test
    void testOverflowSafety() {
        final RogueRandom random = new RogueRandom(1L);

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1_000_0000; i++) { // Quick, fast unit test
                int value = random.rnd(1000);
                assertTrue(value >= 0 && value < 1000, "rnd output must be in [0, 1000): " + value);
            }
        }, "RogueRandom should handle seed overflow without throwing any exception");
    }

    @Test
    void testSelectRandomDeathCause() {
        final RogueRandom random = new RogueRandom(12345L);
        final Config config = new Config();

        final String result = random.selectRandomDeathSource(config.getDefaultKillName()).name();

        assertNotNull(result);
        assertFalse(result.isBlank());

        final Set<String> validNames = new HashSet<>();
        Templates.getTemplates(MonsterTemplate.class).stream()
                .map(MonsterTemplate::getName)
                .forEach(validNames::add);

        Templates.getTemplates(KillTypeTemplate.class).stream()
                .map(KillTypeTemplate::getName)
                .forEach(validNames::add);

        final String defaultKillName = config.getDefaultKillName();
        if (defaultKillName != null) {
            validNames.add(defaultKillName);
        }

        assertTrue(validNames.contains(result),
                "Selected death cause must belong to known templates or default kill name");
    }

    @RepeatedTest(100)
    void testRoll() {
        final Config config =new Config();
        final RogueRandom rnd = new RogueRandom(config.getSeed());
        assertEquals(0,rnd.roll(0,0));
        assertEquals(0,rnd.roll(0,5));
        assertEquals(1,rnd.roll(1,0));
        assertEquals(1,rnd.roll(1,1));
        assertEquals(2,rnd.roll(2,1));
        assertEquals(5,rnd.roll(5,1));
        assertTrue(rnd.roll(2,6)>=2);
        assertTrue(rnd.roll(3,3)>=3);
    }
}
