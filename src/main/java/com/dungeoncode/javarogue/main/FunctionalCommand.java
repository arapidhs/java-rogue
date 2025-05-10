package com.dungeoncode.javarogue.main;

import java.util.function.Consumer;

/**
 * A command that executes a provided function on the game state. This allows
 * dynamic creation of commands using lambdas or method references without
 * defining new command classes, suitable for simple or one-off actions.
 * <p>
 * Inspired by the flexible action handling in the C Rogue source code (e.g.,
 * command.c), where various actions are triggered based on input or events.
 * </p>
 */
public class FunctionalCommand implements Command {
    private final Consumer<GameState> action;
    private final Phase phase;

    /**
     * Constructs a functional command with the specified action and phase.
     * // Example usage in Rogue#main or another context
     * <pre>
     * gameState.addCommand(new FunctionalCommand(
     *     gameState -> System.out.println("Stupid command executed! Player is at level " + gameState.getLevelNum()),
     *     Phase.MAIN_TURN
     * ));
     * </pre>
     * @param action The function to execute, taking a GameState as input.
     * @param phase  The phase in which the command executes (START_TURN, MAIN_TURN, or END_TURN).
     */
    public FunctionalCommand(Consumer<GameState> action, Phase phase) {
        this.action = action;
        this.phase = phase;
    }

    /**
     * Executes the command by applying the provided function to the game state.
     *
     * @param gameState The current game state to be modified by the function.
     */
    @Override
    public void execute(GameState gameState) {
        action.accept(gameState);
    }

    /**
     * Returns the phase in which this command executes.
     *
     * @return The execution phase of the command.
     */
    @Override
    public Phase getPhase() {
        return phase;
    }
}