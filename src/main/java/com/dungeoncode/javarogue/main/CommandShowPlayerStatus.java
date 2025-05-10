package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An {@link EternalCommand} that displays the player's status line during the {@link Phase#UPKEEP_TURN} phase.
 * Renders the status line (from {@link Player#status()}) either as a message via {@link MessageSystem#msg(String)}
 * if {@link Config#isStatMsg()} is true, or directly on the screen at the configured status line position.
 */
public class CommandShowPlayerStatus implements EternalCommand {

    @Override
    public void execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final String statusLine=gameState.getPlayer().status();
        final Config config = gameState.getConfig();
        if(config.isStatMsg()){
            gameState.getMessageSystem().msg(statusLine);
        } else {
            gameState.getScreen().putString(0,config.getStatLine(),statusLine);
        }
    }

    @Override
    public Phase getPhase() {
        return Phase.UPKEEP_TURN;
    }
}
