package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    void testInit() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(123456L);

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

        config.setWizard(true, rogueRandom);
        assertTrue(config.getInitialPlayerFlags().contains(PlayerFlag.CAN_SEE_MONSTERS));
        assertEquals(options.seed, config.getDungeonSeed());
        assertEquals(options.seed, config.getSeed());

        config.setWizard(false, rogueRandom);
        assertFalse(config.getInitialPlayerFlags().contains(PlayerFlag.CAN_SEE_MONSTERS));
    }

    @Test
    void testInitPlayerStats() {
        final Config config = new Config();
        final Stats stats = config.getInitialPlayerStats();
        assertEquals(16, stats.getStrength());
        assertEquals(0, stats.getExperience());
        assertEquals(1, stats.getLevel());
        assertEquals(10, stats.getArmor());
        assertEquals(12, stats.getHitPoints());
        assertEquals("1x4", stats.getDamage());
        assertEquals(12, stats.getMaxHitPoints());
    }

}
