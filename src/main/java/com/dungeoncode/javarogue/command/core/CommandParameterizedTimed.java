package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

/**
 * An abstract base class for delayed commands that execute after a specified number of turns
 * with custom parameters. Maintains a turn-based timer and type-safe parameters, remaining
 * in the command queue until execution.
 * <p>
 * Inspired by the fuse system in the C Rogue source (daemon.c), which schedules delayed
 * actions like traps or effects with contextual data.
 * </p>
 * <p>Example usage:</p>
 * <pre>
 * gameState.addCommand(new CommandDelayedBomb(
 *     10, new CommandDelayedBomb.BombParams(100, "Superbomb"), Phase.END_TURN
 * ));
 * </pre>
 *
 * @param <T> The type of parameters for the delayed command.
 */
public abstract class CommandParameterizedTimed<T> extends CommandTimed {

    private final T params;

    /**
     * Constructs a delayed command with a specified delay, parameters, and phase.
     *
     * @param turns  The number of turns to wait before execution.
     * @param params The parameters for the command’s execution.
     * @param phase  The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     */
    public CommandParameterizedTimed(int turns, T params, Phase phase) {
        super(turns, phase);
        this.params = params;
    }

    /**
     * Returns the parameters for the command’s execution.
     *
     * @return The parameters of type T.
     */
    public T getParams() {
        return params;
    }

    /**
     * Executes the delayed command, modifying the game state using the stored parameters.
     * Subclasses must implement the specific action, such as triggering traps or effects.
     *
     * @param gameState The game state to modify.
     * @return True if the command executed successfully, false otherwise.
     */
    @Override
    public abstract boolean execute(GameState gameState);
}