package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LevelGeneratorTest {

    @Test
    void testLevelGeneration(){
        final int levelGenerationIterations = 1000;
        for(int i = 0; i< levelGenerationIterations; i++) {
            final Config config = new Config();
            final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
            final LevelGenerator levelGenerator = new LevelGenerator(config, rogueRandom);
            final int levelNum = 1;
            final Level level = levelGenerator.newLevel(levelNum);
            assertNotNull(level);
        }
    }
}
