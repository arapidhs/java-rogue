package com.dungeoncode.javarogue.command;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

/**
 * Represents a game command that modifies the game state in the Rogue game.
 * Commands encapsulate player actions (e.g., movement, item use) and system actions
 * (e.g., monster movement, status updates), executed in specific phases to ensure
 * turn-based processing.
 * <p>
 * Inspired by the command-driven structure of the C Rogue source (e.g., command.c),
 * where inputs and events trigger actions like movement (move.c) or item usage (things.c).
 * Implementations must define the action and its execution phase.
 * </p>
 */
public interface Command {
    /**
     * Executes the command, modifying the provided game state.
     * Returns true if the command executes successfully and should be considered a
     * completed action (e.g., for decrementing player move count), false otherwise
     * (e.g., for non-turn actions like quitting).
     *
     * @param gameState The current game state to modify.
     * @return True if the command executed successfully, false if it failed or is a non-turn action.
     */
    boolean execute(GameState gameState);

    /**
     * Returns the phase in which this command executes (e.g., START_TURN, MAIN_TURN, END_TURN).
     * Ensures commands are processed in the correct order, mirroring the turn structure
     * in the C Rogue main loop (main.c).
     *
     * @return The execution phase of the command.
     */
    Phase getPhase();
}