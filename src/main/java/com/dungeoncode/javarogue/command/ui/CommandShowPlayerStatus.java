package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.core.CommandEternal;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.MessageSystem;
import com.dungeoncode.javarogue.system.entity.creature.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An {@link CommandEternal} that displays the player's status line during the {@link Phase#UPKEEP_TURN} phase.
 * Renders the status line (from {@link Player#status()}) either as a message via {@link MessageSystem#msg(String)}
 * if {@link Config#isStatMsg()} is true, or directly on the screen at the configured status line position.
 */
public class CommandShowPlayerStatus implements CommandEternal {

    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final String statusLine = gameState.getPlayer().status();
        final Config config = gameState.getConfig();
        if (config.isStatMsg()) {
            gameState.getMessageSystem().clearMessagePosition();
            gameState.getMessageSystem().msg(statusLine);
        } else {
            gameState.getScreen().clearLine(config.getStatLine());
            gameState.getScreen().putString(0, config.getStatLine(), statusLine);
        }
        return true;
    }

    @Override
    public Phase getPhase() {
        return Phase.UPKEEP_TURN;
    }
}
