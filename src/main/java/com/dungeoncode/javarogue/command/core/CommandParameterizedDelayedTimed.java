package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

/**
 * A command that executes after a specified number of turns, implementing a delayed
 * action mechanism similar to a fuse in the Rogue game. This command remains in the
 * command queue, decrementing its turn timer each turn until it reaches zero, at which
 * point it executes and is removed. It supports parameterized execution with a custom
 * parameter type and phase-based processing.
 * <p>
 * This class is directly inspired by the fuse system in the original C Rogue source
 * code (daemon.c), which schedules delayed actions such as traps or effects to trigger
 * after a set number of turns.
 * </p>
 * <p>
 * Example usage: A bomb that explodes after 10 turns, dealing damage.
 * <pre>
 * public class CommandDelayedBomb extends CommandParameterizedDelayedTimed<CommandDelayedBomb.BombParams> {
 *     public CommandDelayedBomb(int turns, BombParams params, Phase phase) {
 *         super(turns, params, phase);
 *     }
 *
 *     &#64;Override
 *     public void execute(GameState gameState) {
 *         BombParams params = getParams();
 *         gameState.applyExplosionDamage(params.damage, params.name);
 *     }
 *
 *     public record BombParams(int damage, String name) {}
 * }
 *
 * // Usage
 * gameState.addCommand(new CommandDelayedBomb(
 *     10, new CommandDelayedBomb.BombParams(100, "Superbomb"), Phase.END_TURN
 * ));
 * </pre>
 * </p>
 *
 * @param <T> The type of parameters for the delayed command.
 */
public abstract class CommandParameterizedDelayedTimed<T> extends CommandParameterized<T> implements CommandTimed {
    /**
     * The number of turns remaining before the command executes.
     */
    private int turnsRemaining;

    /**
     * Constructs a delayed command with a specified number of turns, parameters, and execution phase.
     * The turn count is stored as a timer for tracking turns remaining, and the parameters are used
     * to customize the command's action, aligning with the C source code's fuse mechanism in daemon.c.
     *
     * @param turns  The number of turns to wait before executing the command.
     * @param params The parameters required for the command's execution.
     * @param phase  The phase in which the command should execute (START_TURN, MAIN_TURN, or END_TURN).
     */
    public CommandParameterizedDelayedTimed(int turns, T params, Phase phase) {
        super(params, phase);
        this.turnsRemaining = turns;
    }

    /**
     * Returns the number of turns remaining before the command is ready to execute.
     * This reflects the current state of the timer, corresponding to the turn counter
     * in the C source code's fuse system (daemon.c).
     *
     * @return The number of turns remaining.
     */
    @Override
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Decrements the turn timer by one, advancing the command closer to execution.
     * If the timer is already zero, no further decrement occurs. This mirrors the
     * turn-based progression of delayed actions in the C source code's daemon.c.
     */
    @Override
    public void decrementTimer() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    /**
     * Checks if the command is ready to execute, which occurs when the turn timer
     * reaches zero. This determines whether the command should be executed and removed
     * from the queue, consistent with the C source code's logic for triggering delayed
     * actions in daemon.c.
     *
     * @return true if the turn timer is zero or less, false otherwise.
     */
    @Override
    public boolean isReadyToExecute() {
        return turnsRemaining <= 0;
    }

    /**
     * Executes the delayed command, performing the associated action on the game state
     * using the stored parameters. Subclasses should override this method to implement
     * specific fuse-like actions (e.g., explosions, traps, or effects), referencing the
     * C source code's daemon.c and related functions.
     *
     * @param gameState The current game state to be modified by the command.
     */
    @Override
    public abstract boolean execute(GameState gameState);
}