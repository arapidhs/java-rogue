package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    void testInit() {
        final Config config = new Config();

        final Options options = new Options();
        options.master = true;
        options.name = "testname";
        options.fruit = "apple";
        options.file = "rogue.sav";
        options.seed = 100;
        options.showScores = true;

        config.applyOptions(options);

        assertEquals(options.name, config.getPlayerName());
        assertEquals(options.fruit, config.getFavoriteFruit());
        assertEquals(options.file, config.getSaveFileName());
        assertEquals(options.seed, config.getOptionsSeed());
        assertFalse(config.isScoring());

        config.setWizard(true);
        assertTrue(config.getInitialPlayerStatusFlags().contains(PlayerStatus.CAN_SEE_MONSTERS));
        assertEquals(options.seed, config.getDungeonSeed());
        assertEquals(options.seed, config.getSeed());

        config.setWizard(false);
        assertFalse(config.getInitialPlayerStatusFlags().contains(PlayerStatus.CAN_SEE_MONSTERS));
    }

}
