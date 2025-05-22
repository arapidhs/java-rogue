/**
 * Handles illegal command inputs in the Java port of the video game Rogue,
 * equivalent to the C function `void illcom(int ch)` in `command.c` from the original source.
 * Processes invalid keystrokes, displays an error message, and ensures the command does not consume a player move.
 */
package com.dungeoncode.javarogue.command.system;

import com.dungeoncode.javarogue.command.core.CommandParameterized;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandIllegal extends CommandParameterized<KeyStroke> {

    /**
     * Constructs a CommandIllegal instance with the specified keystroke.
     *
     * @param params The invalid keystroke to process.
     */
    public CommandIllegal(KeyStroke params) {
        super(params, Phase.MAIN_TURN);
    }

    /**
     * Executes the illegal command, displaying an error message and resetting the command count.
     * Does not consume a player move.
     *
     * @param gameState The current game state.
     * @return false, indicating no player move is consumed.
     */
    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final Config config = gameState.getConfig();

        boolean messageSaveSetting = config.isMessageSave();
        config.setMessageSave(false);

        gameState.setCount(0);

        gameState.getMessageSystem().msg(
                String.format("illegal command '%s'", unctrl(getParams()))
        );
        config.setMessageSave(messageSaveSetting);

        return false;
    }

    /**
     * Retrieves the keystroke associated with this illegal command.
     *
     * @return The invalid keystroke.
     */
    @Override
    public KeyStroke getParams() {
        return super.getParams();
    }

    /**
     * Converts the keystroke to a displayable string, including control key notation if applicable.
     *
     * @param keyStroke The keystroke to convert.
     * @return A string representation of the keystroke.
     */
    private String unctrl(@Nonnull final KeyStroke keyStroke) {
        String com = "";
        if (keyStroke.isCtrlDown()) {
            com = "^";
        }
        if (keyStroke.getKeyType() == KeyType.Character) {
            com += keyStroke.getCharacter();
        }
        return com;
    }
}