package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An abstract base class for commands, providing a default implementation of the
 * {@link Command} interface. Stores the execution phase and command name, ensuring
 * phase-based turn processing and identification for all derived commands.
 * <p>
 * Inspired by the command-driven structure of the C Rogue source (e.g., command.c),
 * where actions are triggered in specific turn stages (main.c).
 * </p>
 */
public abstract class AbstractCommand implements Command {

    private final Phase phase;
    private final String name;

    /**
     * Constructs a command with the specified execution phase and name.
     *
     * @param phase The phase in which the command executes (e.g., START_TURN, MAIN_TURN).
     * @param name  The name of the command for identification (e.g., "MoveLeft", "PickUp").
     * @throws NullPointerException if phase or name is null.
     */
    protected AbstractCommand(@Nonnull final Phase phase, @Nullable final String name) {
        Objects.requireNonNull(phase);
        this.phase = phase;
        this.name = name;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public String getName() {
        return name;
    }
}