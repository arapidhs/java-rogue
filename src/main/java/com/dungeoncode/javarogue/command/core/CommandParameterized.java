package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

/**
 * An abstract base class for commands that use parameters to execute their logic.
 * Extends {@link AbstractCommand} to support type-safe parameter storage and phase-based
 * execution, enabling flexible commands like movement or item usage.
 * <p>
 * Inspired by the input parsing in the C Rogue source (e.g., command.c), where actions
 * like item use (things.c) or level changes (rooms.c) rely on contextual data.
 * </p>
 *
 * @param <T> The type of parameters required by the command.
 */
public abstract class CommandParameterized<T> extends AbstractCommand {
    private final T params;

    /**
     * Constructs a parameterized command with the specified parameters and phase.
     *
     * @param params The parameters for the command’s execution.
     * @param phase  The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     */
    protected CommandParameterized(T params, Phase phase) {
        super(phase);
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
     * Executes the command, modifying the game state using the stored parameters.
     * Subclasses must implement the specific action, such as moving the player or using an item.
     *
     * @param gameState The game state to modify.
     * @return True if the command executed successfully, false otherwise.
     */
    @Override
    public abstract boolean execute(GameState gameState);
}