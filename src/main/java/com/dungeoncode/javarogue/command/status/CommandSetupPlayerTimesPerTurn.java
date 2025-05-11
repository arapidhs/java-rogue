package com.dungeoncode.javarogue.command.status;

import com.dungeoncode.javarogue.command.core.CommandEternal;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.entity.creature.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An eternal command that sets up the number of times the player can execute commands per turn.
 * Initializes the player's move count to 1 and increments it by 1 if the player has the
 * {@link CreatureFlag#ISHASTE} flag, mirroring the command loop setup in the C Rogue source
 * (command.c).
 */
public class CommandSetupPlayerTimesPerTurn implements CommandEternal {

    public static final int INITIAL_MOVES_PER_TURN = 1;

    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final Player player = gameState.getPlayer();
        player.setNtimes(INITIAL_MOVES_PER_TURN);
        if (player.hasFlag(CreatureFlag.ISHASTE)) {
            player.setNtimes(player.getNtimes() + 1);
        }
        return true;
    }

    @Override
    public Phase getPhase() {
        return Phase.START_TURN;
    }
}
