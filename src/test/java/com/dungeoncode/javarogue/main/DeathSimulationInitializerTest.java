package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeathSimulationInitializerTest{

    @Test
    void testInitializeSetsGoldLevelAndDeathCause() {

        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(234567890L);
        final DeathSimulationInitializer initializer = new DeathSimulationInitializer();

        final GameState gameState = new GameState(config,rogueRandom,initializer);
        gameState.init();

        assertEquals( GameEndReason.KILLED, gameState.getGameEndReason());
        assertTrue(gameState.getGoldAmount() >= 1 && gameState.getGoldAmount() <= 100);
        assertTrue(gameState.getLevel() >= 1 && gameState.getLevel() <= 100);
        assertNotNull(gameState.getDeathSource().getName());

    }

}
