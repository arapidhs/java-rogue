package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.RogueScreen;
import com.dungeoncode.javarogue.system.SymbolMapper;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.world.Place;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.screen.Screen;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.dungeoncode.javarogue.system.RogueScreen.WINDOW_HW;

/**
 * Displays the dungeon map, revealing all explored areas, equivalent to `void show_map()` in `wizard.c`
 * from the original Rogue C source. Clears the screen, renders the current level's places with appropriate
 * symbols and modifiers, positions the cursor at the player's location, and displays the map in a window.
 * The map is shown temporarily and closed after user input, without consuming a player move.
 */
public class CommandShowMap implements Command {

    /**
     * Executes the command to display the current dungeon level's map. Clears the specified window,
     * renders each place's symbol (with reverse modifier for unreal places), sets the cursor to the
     * player's position, shows the map, and waits for user input to close the window.
     *
     * @param gameState The current game state, providing access to the screen, level, and configuration.
     * @return false, indicating no player move is consumed.
     */
    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final Config config = gameState.getConfig();
        final RogueScreen screen = gameState.getScreen();

        screen.clearWindow(WINDOW_HW);

        for (int x = 0; x < config.getTerminalCols(); x++) {
            for (int y = 1; y < config.getTerminalRows() - 1; y++) {
                final Place place = gameState.getCurrentLevel().getPlaceAt(x, y);
                assert place != null;
                final SGR[] modifiers = place.isReal() ? null : new SGR[]{SGR.REVERSE};
                final SymbolType symbolType = place.getSymbolType();
                screen.putWChar(WINDOW_HW, x, y, SymbolMapper.getSymbol(symbolType), modifiers);
            }
        }

        screen.setCursorPosition(new TerminalPosition(gameState.getPlayer().getX(), gameState.getPlayer().getY()));
        screen.showWindow(WINDOW_HW);
        screen.closeWindow(WINDOW_HW, "---More (level map)---");
        screen.setCursorPosition(null);
        screen.refresh(Screen.RefreshType.DELTA);

        return false;
    }

    /**
     * Returns the phase in which this command executes.
     *
     * @return Phase.MAIN_TURN, indicating execution during the main turn phase.
     */
    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }

    @Override
    public String getName() {
        return null;
    }
}