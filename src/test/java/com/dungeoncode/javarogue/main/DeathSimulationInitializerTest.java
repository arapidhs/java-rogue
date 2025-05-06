package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DeathSimulationInitializerTest extends RogueBaseTest {

    @Test
    void testInitializeSetsGoldLevelAndDeathCause() throws IOException {

        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DeathSimulationInitializer initializer = new DeathSimulationInitializer();

        final GameState gameState = new GameState(config, rogueRandom, screen, initializer, messageSystem);

        assertEquals(GameEndReason.KILLED, gameState.getGameEndReason());
        assertTrue(gameState.getGoldAmount() >= 1 && gameState.getGoldAmount() <= 100);
        assertTrue(gameState.getLevelNum() >= 1 && gameState.getLevelNum() <= 100);
        assertNotNull(gameState.getDeathSource().name());

    }

}
