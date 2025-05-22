/**
 * Initializes a new dungeon level with the specified level number, ported from the C Rogue source
 * (e.g., <code>new_level</code> in main.c). Executes {@link GameState#newLevel(int)} to generate
 * the level, position the player, and update the screen.
 */
package com.dungeoncode.javarogue.command.system;

import com.dungeoncode.javarogue.command.core.CommandParameterized;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandNewLevel extends CommandParameterized<Integer> {

    /**
     * Constructs a CommandNewLevel instance with the specified level number.
     *
     * @param params The dungeon level number to initialize.
     */
    public CommandNewLevel(Integer params) {
        super(params, Phase.START_TURN);
    }

    /**
     * Executes the command to generate and set up a new dungeon level.
     *
     * @param gameState The current game state.
     * @return true, indicating the command consumes a player action.
     */
    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.newLevel(getParams());
        return true;
    }

    /**
     * Retrieves the level number associated with this command.
     *
     * @return The dungeon level number.
     */
    @Override
    public Integer getParams() {
        return super.getParams();
    }
}