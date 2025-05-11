package com.dungeoncode.javarogue.command;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

/**
 * Represents a game command that can be executed to modify the game state.
 * Commands are used to encapsulate player actions (e.g., movement, item use) and
 * system actions (e.g., monster movement, delayed effects) in the Rogue game.
 * Each command specifies its execution phase to ensure proper turn-based processing.
 * <p>
 * This interface is inspired by the command-driven structure of the original C Rogue
 * game (e.g., command.c), where user inputs and system events trigger specific game
 * actions. Implementations should define the specific behavior and phase of execution.
 * </p>
 */
public interface Command {
    /**
     * Executes the command, modifying the provided game state as needed.
     * Implementations should perform the action associated with the command, such as
     * updating the player's position, triggering an event, or altering game entities.
     * This method corresponds to the action logic in the C source code, such as
     * movement in move.c or item usage in things.c.
     *
     * @param gameState The current game state to be modified by the command.
     */
    void execute(GameState gameState);

    /**
     * Returns the phase in which this command should be executed within a game turn.
     * Phases (START_TURN, MAIN_TURN, END_TURN) ensure commands are processed in the
     * correct order, mirroring the turn structure in the C source code's main loop
     * (main.c). For example, monster movements occur in START_TURN, player actions
     * in MAIN_TURN, and status effects in END_TURN.
     *
     * @return The phase of execution for this command.
     */
    Phase getPhase();
}