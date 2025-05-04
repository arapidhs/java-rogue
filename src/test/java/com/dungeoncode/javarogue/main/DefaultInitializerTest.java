package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DefaultInitializerTest {

    @Mock
    RogueScreen screen;

    @Test
    void testPlayerInitialization() throws IOException {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final DefaultInitializer initializer = new DefaultInitializer();

        final GameState gameState = new GameState(config,rogueRandom, screen, initializer, messageSystem);

        assertEquals( config.getPlayerName(), gameState.getPlayer().getPlayerName());
        assertEquals( config.getMaxPack(), gameState.getPlayer().getInventory().getMaxPack());
        assertEquals( config.getFoodLeft(), gameState.getPlayer().getFoodLeft());
        assertTrue( gameState.getPlayer().getPlayerFlags().isEmpty());
    }

}
