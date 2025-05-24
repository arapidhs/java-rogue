package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

import java.util.function.Predicate;

/**
 * A command that executes a provided predicate on the game state, returning a boolean result.
 * Enables dynamic command creation using lambdas or method references for simple or one-off actions.
 * <p>
 * Inspired by the flexible action handling in the C Rogue source (e.g., command.c), where
 * inputs and events trigger varied actions like movement or item use.
 * </p>
 */
public class CommandFunctional extends AbstractCommand {
    private final Predicate<GameState> action;

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
     * @param action The predicate to execute, returning true for success, false for failure.
     * @param phase  The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     */
    public CommandFunctional(Predicate<GameState> action, Phase phase) {
        super(phase, null);
        this.action = action;
    }

    /**
     * Executes the command by applying the predicate to the game state.
     *
     * @param gameState The game state to modify.
     * @return The predicate’s result: true for success, false for failure.
     */
    @Override
    public boolean execute(GameState gameState) {
        return action.test(gameState);
    }
}