package com.dungeoncode.javarogue.command.system;

import com.dungeoncode.javarogue.command.core.CommandParameterized;
import com.dungeoncode.javarogue.command.ui.CommandShowPlayerStatus;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.main.Rogue;
import com.dungeoncode.javarogue.system.MessageSystem;
import com.dungeoncode.javarogue.system.RogueScreen;
import com.dungeoncode.javarogue.system.ScoreManager;
import com.dungeoncode.javarogue.system.death.GameEndReason;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A command that prompts the player to confirm quitting, mirroring the C Rogue
 * <code>quit</code> function. Exits with a score display if confirmed, or restores
 * the game state if canceled.
 * <p>
 * Equivalent of <pre>quit(int sig) from main.c</pre>
 */
public class CommandQuit extends CommandParameterized<Boolean> {

    /**
     * Constructs a quit command with a flag indicating if it's a deliberate quit.
     *
     * @param isQuitCommand True if triggered by 'Q', false if via interrupt (e.g., Ctrl+C).
     */
    public CommandQuit(final boolean isQuitCommand) {
        super(isQuitCommand, Phase.MAIN_TURN);
    }

    /**
     * Prompts the player to confirm quitting. On 'y', clears the screen, shows the
     * score, records it, and exits. Otherwise, clears the prompt, redraws the status,
     * and resets state. Returns false to indicate the command isn't a turn action.
     *
     * @param gameState The game state to modify or exit.
     * @return Always false, as quit does not count as an executed command.
     */
    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        boolean isQuitCommand = getParams();
        final MessageSystem messageSystem = gameState.getMessageSystem();
        final RogueScreen screen = gameState.getScreen();

        // Clear message position for interrupts (mimics mpos = 0 in C)
        if (!isQuitCommand) {
            messageSystem.clearMessagePosition();
        }

        messageSystem.msg("really quit?");
        screen.refresh();
        final KeyStroke keyStroke = screen.readInput();
        final Character character = keyStroke.getCharacter();

        if (keyStroke.getKeyType().equals(KeyType.Character) && character != null && character == 'y') {
            screen.clear();
            final String msg = String.format("You quit with %d gold pieces", gameState.getPlayer().getGoldAmount());
            screen.putString(0, screen.getRows() - 2, msg);
            screen.refresh();
            gameState.setGameEndReason(GameEndReason.QUIT);
            gameState.getPlayer().setGoldAmount(gameState.getPlayer().getGoldAmount());
            final ScoreManager scoreManager = new ScoreManager(screen);
            scoreManager.score(gameState);
            Rogue.exit(screen);
        } else {
            screen.clearLine(0);
            new CommandShowPlayerStatus().execute(gameState);
            screen.refresh();
            messageSystem.clearMessagePosition();
            gameState.setCount(0);
            gameState.setToDeath(false);
        }

        // Always return false (mimics C Rogue: quit isn't a turn action)
        return false;
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}