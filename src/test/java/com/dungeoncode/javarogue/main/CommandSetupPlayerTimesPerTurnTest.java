package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.command.status.CommandSetupPlayerTimesPerTurn;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.entity.creature.Player;
import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import com.dungeoncode.javarogue.ui.MessageSystem;
import org.junit.jupiter.api.Test;

import static com.dungeoncode.javarogue.command.status.CommandSetupPlayerTimesPerTurn.INITIAL_MOVES_PER_TURN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandSetupPlayerTimesPerTurnTest extends RogueBaseTest {

    @Test
    void testExecute(){
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final MessageSystem messageSystem = new MessageSystem(screen);
        final GameState gameState = new GameState(config, rogueRandom, screen, null, messageSystem);
        final Player player = new Player(config);
        gameState.setPlayer(player);

        assertEquals(0, player.getNtimes());

        final CommandSetupPlayerTimesPerTurn command = new CommandSetupPlayerTimesPerTurn();
        command.execute(gameState);

        assertEquals(INITIAL_MOVES_PER_TURN, player.getNtimes());

        player.addFlag(CreatureFlag.ISHASTE);
        command.execute(gameState);
        assertEquals(INITIAL_MOVES_PER_TURN+1, player.getNtimes());

    }

}
