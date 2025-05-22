/**
 * Displays the last message stored in the game's message system.
 * Equivalent to message review functionality in the original Rogue C source.
 */
package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.MessageSystem;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowLastMessage implements Command {

    /**
     * Executes the command to display the last message, if available.
     *
     * @param gameState The current game state.
     * @return false, indicating no player move is consumed.
     */
    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final MessageSystem messageSystem = gameState.getMessageSystem();
        final String lastMessage = messageSystem.getLastMessage();
        if (lastMessage != null) {
            messageSystem.msg(lastMessage);
        }
        return false;
    }

    /**
     * Returns the phase in which this command operates.
     *
     * @return Phase.MAIN_TURN, indicating execution during the main turn phase.
     */
    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}