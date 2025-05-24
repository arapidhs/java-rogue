package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowVersion implements Command {

    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.getMessageSystem().msg(String.format("version %s. (mctesq was here)",
                gameState.getConfig().getReleaseVersion()));
        return false;
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }

    @Override
    public String getName() {
        return null;
    }
}
