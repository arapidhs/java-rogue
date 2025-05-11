package com.dungeoncode.javarogue.command.system;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

public class CommandQuit implements Command {

    @Override
    public void execute(GameState gameState) {
        gameState.setPlaying(false);
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}
