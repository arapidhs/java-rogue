/**
 * An abstract base class for commands that execute after a specified number of turns,
 * implementing delayed actions in the Rogue game. Maintains a turn-based timer that
 * decrements each turn until execution, remaining in the command queue until completed.
 * <p>
 * Inspired by the fuse and daemon mechanisms in the C Rogue source (daemon.c), which
 * schedule delayed actions like traps or effects.
 * </p>
 */
package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;

public abstract class CommandTimed extends AbstractCommand {

    private int turnsRemaining;

    /**
     * Constructs a timed command with a specified delay, execution phase, and default null name.
     *
     * @param turns The number of turns to wait before execution.
     * @param phase The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     * @throws NullPointerException if phase is null.
     */
    public CommandTimed(int turns, @Nonnull Phase phase) {
        super(phase, null);
        this.turnsRemaining = turns;
    }

    /**
     * Constructs a timed command with a specified delay, execution phase, and command name.
     *
     * @param turns The number of turns to wait before execution.
     * @param phase The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     * @param name  The name of the command for identification (e.g., "DelayedTrap", "EffectTimer").
     * @throws NullPointerException if phase or name is null.
     */
    public CommandTimed(int turns, @Nonnull Phase phase, @Nonnull String name) {
        super(phase, name);
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
     * Increases the turn timer by the specified amount, delaying the command's execution.
     * If the provided time is negative, no change is made to the timer.
     * <p>
     * Equivalent to <code>void lengthen(void( * func)(), int xtime)</code> in <code>daemon.c</code>.
     *
     * @param time The number of turns to add to the timer.
     */
    public void lengthen(int time) {
        if (time > 0) {
            turnsRemaining += time;
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
     * @return {@code true} if the timer is zero or less, {@code false} otherwise.
     */
    public boolean isReadyToExecute() {
        return turnsRemaining <= 0;
    }
}