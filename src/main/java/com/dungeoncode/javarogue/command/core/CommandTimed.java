package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.core.Phase;

/**
 * An abstract base class for commands that execute after a specified number of turns,
 * implementing delayed actions in the Rogue game. Maintains a turn-based timer that
 * decrements each turn until execution, remaining in the command queue until completed.
 * <p>
 * Inspired by the fuse and daemon mechanisms in the C Rogue source (daemon.c), which
 * schedule delayed actions like traps or effects.
 * </p>
 */
public abstract class CommandTimed extends AbstractCommand {

    private int turnsRemaining;

    /**
     * Constructs a timed command with a specified delay and execution phase.
     *
     * @param turns The number of turns to wait before execution.
     * @param phase The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     */
    public CommandTimed(int turns, Phase phase) {
        super(phase);
        this.turnsRemaining = turns;
    }

    /**
     * Decrements the turn timer by one, advancing the command toward execution.
     * No effect if the timer is already zero.
     */
    public void decrementTimer() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    /**
     * Returns the number of turns remaining before execution.
     *
     * @return The remaining turns.
     */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Checks if the command is ready to execute (timer is zero or less).
     *
     * @return True if the timer is zero or less, false otherwise.
     */
    public boolean isReadyToExecute() {
        return turnsRemaining <= 0;
    }
}