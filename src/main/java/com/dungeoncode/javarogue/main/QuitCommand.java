package com.dungeoncode.javarogue.main;

public class QuitCommand implements Command{

    @Override
    public void execute(GameState gameState) {
        gameState.setPlaying(false);
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}
