package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.core.CommandEternal;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.dungeoncode.javarogue.core.Phase.INPUT_CLEANUP_TURN;

/**
 * An eternal command that clears the message line during the {@link Phase#INPUT_CLEANUP_TURN} phase.
 * Ensures a clean UI state by removing displayed messages before processing player commands.
 */
public class CommandClearMessage implements CommandEternal {
    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.getMessageSystem().clearMessage();
        return true;
    }

    @Override
    public Phase getPhase() {
        return INPUT_CLEANUP_TURN;
    }

    @Override
    public String getName() {
        return null;
    }
}
