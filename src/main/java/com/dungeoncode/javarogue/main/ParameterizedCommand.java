package com.dungeoncode.javarogue.main;

/**
 * An abstract base class for commands that require parameters to execute their logic.
 * This class extends the {@link Command} interface, adding support for parameterized
 * execution while maintaining phase-based turn processing. Parameters are stored in a
 * type-safe manner using generics, allowing flexibility for different command types
 * (e.g., movement coordinates, command arguments, or level numbers).
 * <p>
 * Inspired by the flexible input handling in the original C Rogue source code (e.g.,
 * command.c parsing user inputs), this class provides a foundation for commands that
 * need additional data to perform their actions, such as item usage (things.c) or
 * level generation (rooms.c).
 * </p>
 * @param <T> The type of parameters required by the command.
 */
public abstract class ParameterizedCommand<T> implements Command {
    /** The parameters used by the command during execution. */
    private final T params;
    /** The phase in which the command executes (START_TURN, MAIN_TURN, or END_TURN). */
    private final Phase phase;

    /**
     * Constructs a parameterized command with the specified parameters and execution phase.
     * The parameters are stored for use during execution, and the phase determines when
     * the command runs in the game's turn cycle, aligning with the C source code's turn
     * structure in main.c.
     *
     * @param params The parameters required for the command's execution.
     * @param phase  The phase in which the command should execute.
     */
    protected ParameterizedCommand(T params, Phase phase) {
        this.params = params;
        this.phase = phase;
    }

    /**
     * Returns the phase in which this command executes.
     * The phase (START_TURN, MAIN_TURN, or END_TURN) ensures proper ordering of command
     * execution within a game turn, consistent with the C source code's game loop in
     * main.c.
     *
     * @return The execution phase of the command.
     */
    @Override
    public Phase getPhase() {
        return phase;
    }

    /**
     * Returns the parameters associated with this command.
     * Subclasses can use these parameters to customize their execution logic, such as
     * processing movement directions or command arguments.
     *
     * @return The parameters of type T.
     */
    public T getParams() {
        return params;
    }

    /**
     * Executes the command, modifying the game state based on the stored parameters.
     * Subclasses must implement this method to define the specific action, such as
     * moving the player (move.c), using an item (things.c), or triggering an effect.
     *
     * @param gameState The current game state to be modified by the command.
     */
    @Override
    public abstract void execute(GameState gameState);
}