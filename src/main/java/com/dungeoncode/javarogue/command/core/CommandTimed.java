package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.command.Command;

/**
 * Represents a command that executes after a specified number of turns, supporting
 * delayed actions in the Rogue game. Timed commands maintain a turn-based timer,
 * decrementing each turn until ready to execute, and remain in the command queue
 * until their action is performed.
 * <p>
 * This interface is inspired by the delayed action system in the original C Rogue
 * source code, particularly the fuse and daemon mechanisms in daemon.c, which
 * schedule actions like traps or effects to occur after a set number of turns.
 */
public interface CommandTimed extends Command {
    /**
     * Returns the number of turns remaining before the command is ready to execute.
     * This corresponds to the turn counter used in the C source code's fuse system
     * (daemon.c), which tracks how many turns are left until the action triggers.
     *
     * @return The number of turns remaining.
     */
    int getTurnsRemaining();

    /**
     * Decrements the command's turn timer by one, advancing it closer to execution.
     * Called each turn during the command's phase, mirroring the turn-based
     * progression of delayed actions in the C source code's daemon.c.
     */
    void decrementTimer();

    /**
     * Checks if the command is ready to execute, typically when its turn timer
     * reaches zero. This determines whether the command should be executed and
     * removed from the queue, aligning with the C source code's logic for
     * triggering delayed actions in daemon.c.
     *
     * @return true if the command is ready to execute, false otherwise.
     */
    boolean isReadyToExecute();
}