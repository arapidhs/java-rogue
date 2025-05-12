package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.RogueScreen;
import com.dungeoncode.javarogue.system.SymbolMapper;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.world.Place;
import com.dungeoncode.javarogue.system.world.PlaceType;
import com.googlecode.lanterna.SGR;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * <p>
 * A command that triggers the generation and display of a new dungeon level in the Rogue game.
 * This command clears the screen, generates a new level using a random level number, displays
 * the updated map, and refreshes the screen. It is typically executed during the MAIN_TURN phase
 * to reflect a player's action to transition to a new level.
 * <p>
 * This command corresponds to level generation logic in the original C Rogue source code,
 * particularly in rooms.c (e.g., init_rooms()), and integrates with Lanterna for rendering,
 * replacing the C code's curses-based display.
 */
// TODO this command (CommandShowMap) is a PoC - not final
public class CommandShowMap implements Command {

    /**
     * Executes the command to generate and display a new dungeon level.
     * The process involves clearing the screen, generating a new level with a random
     * level number (bounded by the game's amulet level configuration), rendering the
     * updated map, and refreshing the screen to show the changes. This mirrors the
     * level transition behavior in the C source code's rooms.c and main.c.
     *
     * @param gameState The current game state, providing access to the screen, random
     *                  number generator, and configuration needed for level generation
     *                  and rendering.
     */
    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final Config config=gameState.getConfig();
        final RogueScreen screen = gameState.getScreen();

        for (int x = 0; x < config.getTerminalCols(); x++) {
            for (int y = 1; y < config.getTerminalRows() - 1; y++) {
                final Place place = gameState.getCurrentLevel().getPlaceAt(x, y);
                assert place != null;
                if (!place.isReal()) {
                    screen.enableModifiers(SGR.BOLD);
                }
                if (place.isType(PlaceType.PASSAGE)) {
                    screen.putChar(x, y, SymbolMapper.getSymbol(SymbolType.PASSAGE));
                } else {
                    screen.putChar(x, y, SymbolMapper.getSymbol(place.getSymbolType()));
                }
                if (!place.isReal()) {
                    screen.disableModifiers(SGR.BOLD);
                }
            }
        }
        return true;
    }

    /**
     * Returns the phase in which this command executes, set to MAIN_TURN.
     * The MAIN_TURN phase ensures that level generation occurs as part of the player's
     * primary action, consistent with the turn-based structure of the C source code's
     * game loop in main.c.
     *
     * @return Phase.MAIN_TURN, indicating execution during the main turn phase.
     */
    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}