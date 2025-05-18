package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowPlayerFoodLeft implements Command {

    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.getMessageSystem().msg(String.format("food left: %d",
                gameState.getPlayer().getFoodLeft()));
        return false;
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}
