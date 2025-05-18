package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowPlayerPosition implements Command {

    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final int x=gameState.getPlayer().getX();
        final int y=gameState.getPlayer().getY();
        gameState.getMessageSystem().msg(String.format("@ %d,%d",x,y));
        return false;
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}
