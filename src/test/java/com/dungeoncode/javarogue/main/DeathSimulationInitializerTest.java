package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import com.dungeoncode.javarogue.system.initializer.DeathSimulationInitializer;
import com.dungeoncode.javarogue.system.death.GameEndReason;
import com.dungeoncode.javarogue.system.MessageSystem;
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
        final Player player = gameState.getPlayer();

        assertEquals(GameEndReason.KILLED, gameState.getGameEndReason());
        assertTrue(player.getGoldAmount() >= 1 && player.getGoldAmount() <= 100);
        assertTrue(gameState.getLevelNum() >= 1 && gameState.getLevelNum() <= 100);
        assertNotNull(gameState.getDeathSource().name());

    }

}
