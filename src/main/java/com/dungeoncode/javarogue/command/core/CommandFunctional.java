package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

import java.util.function.Predicate;

/**
 * A command that executes a provided predicate on the game state, returning a boolean result.
 * This allows dynamic creation of commands using lambdas or method references without
 * defining new command classes, suitable for simple or one-off actions.
 * <p>
 * Inspired by the flexible action handling in the C Rogue source code (e.g., command.c),
 * where various actions are triggered based on input or events.
 * </p>
 */
public class CommandFunctional implements Command {
    private final Predicate<GameState> action;
    private final Phase phase;

    /**
     * Constructs a functional command with the specified predicate and phase.
     * <p>Example usage:</p>
     * <pre>
     * gameState.addCommand(new CommandFunctional(
     *     gameState -> {
     *         gameState.setGoldAmount(gameState.getGoldAmount() + 10);
     *         return true;
     *     },
     *     Phase.MAIN_TURN
     * ));
     * </pre>
     *
     * @param action The predicate to execute, taking a GameState and returning a boolean.
     * @param phase  The phase in which the command executes (e.g., START_TURN, MAIN_TURN, END_TURN).
     */
    public CommandFunctional(Predicate<GameState> action, Phase phase) {
        this.action = action;
        this.phase = phase;
    }

    /**
     * Executes the command by applying the provided predicate to the game state.
     * Returns the boolean result of the predicate, indicating success or failure.
     *
     * @param gameState The current game state to be modified by the predicate.
     * @return The result of the predicate (true for success, false for failure).
     */
    @Override
    public boolean execute(GameState gameState) {
        return action.test(gameState);
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