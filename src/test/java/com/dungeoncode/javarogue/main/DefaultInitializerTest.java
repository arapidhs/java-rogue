package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultInitializerTest extends RogueBaseTest {

    @Test
    void testPlayerInitialization() throws IOException {

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
