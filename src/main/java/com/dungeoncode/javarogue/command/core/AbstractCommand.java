package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An abstract base class for commands, providing a default implementation of the
 * {@link Command} interface. Stores the execution phase and ensures phase-based
 * turn processing for all derived commands.
 * <p>
 * Inspired by the command-driven structure of the C Rogue source (e.g., command.c),
 * where actions are triggered in specific turn stages (main.c).
 * </p>
 */
public abstract class AbstractCommand implements Command {

    private final Phase phase;

    /**
     * Constructs a command with the specified execution phase.
     *
     * @param phase The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     * @throws NullPointerException if phase is null.
     */
    protected AbstractCommand(@Nonnull final Phase phase) {
        Objects.requireNonNull(phase);
        this.phase = phase;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }
}
