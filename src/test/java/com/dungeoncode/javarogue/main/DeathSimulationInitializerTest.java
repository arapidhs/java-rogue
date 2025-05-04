package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeathSimulationInitializerTest{

    @Mock
    RogueScreen screen;

    @Test
    void testInitializeSetsGoldLevelAndDeathCause() throws IOException {

        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DeathSimulationInitializer initializer = new DeathSimulationInitializer();

        final GameState gameState = new GameState(config,rogueRandom, screen, initializer,messageSystem);

        assertEquals( GameEndReason.KILLED, gameState.getGameEndReason());
        assertTrue(gameState.getGoldAmount() >= 1 && gameState.getGoldAmount() <= 100);
        assertTrue(gameState.getLevel() >= 1 && gameState.getLevel() <= 100);
        assertNotNull(gameState.getDeathSource().getName());

    }

}
