/**
 * Displays the player's current position (x, y coordinates) in the dungeon.
 * Equivalent to position display functionality in the original Rogue C source.
 */
package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowPlayerPosition implements Command {

    /**
     * Executes the command to display the player's x, y coordinates.
     *
     * @param gameState The current game state.
     * @return false, indicating no player move is consumed.
     */
    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final int x = gameState.getPlayer().getX();
        final int y = gameState.getPlayer().getY();
        gameState.getMessageSystem().msg(String.format("@ %d,%d", x, y));
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

    @Override
    public String getName() {
        return null;
    }
}