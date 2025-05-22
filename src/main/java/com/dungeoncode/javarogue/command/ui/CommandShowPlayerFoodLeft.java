/**
 * Displays the player's remaining food amount.
 * Equivalent to food status display in the original Rogue C source.
 */
package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowPlayerFoodLeft implements Command {

    /**
     * Executes the command to display the player's remaining food.
     *
     * @param gameState The current game state.
     * @return false, indicating no player move is consumed.
     */
    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.getMessageSystem().msg(String.format("food left: %d",
                gameState.getPlayer().getFoodLeft()));
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