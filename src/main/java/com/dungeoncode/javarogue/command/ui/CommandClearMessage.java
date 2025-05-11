package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.core.CommandEternal;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.dungeoncode.javarogue.core.Phase.INPUT_CLEANUP_TURN;

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
}
