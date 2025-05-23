/**
 * Factory for creating command instances based on user input keystrokes, equivalent to command 
 * dispatching logic in <code>command.c</code> from the original Rogue C source. Maps keystrokes 
 * to specific commands for player movement, item pick-up, UI actions, or system operations, 
 * handling both regular and wizard-mode commands.
 */
package com.dungeoncode.javarogue.command;

import com.dungeoncode.javarogue.command.action.CommandPlayerMove;
import com.dungeoncode.javarogue.command.action.CommandPlayerPickUp;
import com.dungeoncode.javarogue.command.system.CommandIllegal;
import com.dungeoncode.javarogue.command.system.CommandNewLevel;
import com.dungeoncode.javarogue.command.system.CommandQuit;
import com.dungeoncode.javarogue.command.ui.*;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.system.entity.Position;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import javax.annotation.Nonnull;

public class CommandFactory {

    private final Config config;
    private final GameState gameState;

    /**
     * Constructs a CommandFactory instance with the specified game state.
     *
     * @param gameState The game state, providing access to configuration and game context.
     */
    public CommandFactory(@Nonnull final GameState gameState) {
        this.gameState = gameState;
        this.config = gameState.getConfig();
    }

    /**
     * Creates a command instance based on the provided keystroke. Maps character keys, control-modified 
     * keys, and arrow keys to specific commands such as movement, pick-up, or UI actions. Supports 
     * wizard-mode commands (e.g., level change, map display) if master and wizard modes are enabled. 
     * Returns a {@link CommandIllegal} for unrecognized or invalid keystrokes.
     *
     * @param keyStroke The keystroke input from the player.
     * @return A command instance corresponding to the keystroke, or {@link CommandIllegal} if invalid.
     */
    public Command fromKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.isCtrlDown()) {
            if (keyStroke.getKeyType() == KeyType.Character) {
                char ch = keyStroke.getCharacter();
                return switch (ch) {
                    case 'D', 'd' -> {
                        if (config.isMaster() && config.isWizard()) {
                            int levelNum = gameState.getLevelNum();
                            gameState.setLevelNum(++levelNum);
                            yield new CommandNewLevel(levelNum);
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'A', 'a' -> {
                        if (config.isMaster() && config.isWizard()) {
                            int levelNum = gameState.getLevelNum();
                            gameState.setLevelNum(--levelNum);
                            yield new CommandNewLevel(levelNum);
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'F', 'f' -> {
                        if (config.isMaster() && config.isWizard()) {
                            yield new CommandShowMap();
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'E', 'e' -> {
                        if (config.isMaster() && config.isWizard()) {
                            yield new CommandShowPlayerFoodLeft();
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'P', 'p' -> new CommandShowLastMessage();
                    default -> new CommandIllegal(keyStroke);
                };
            }
        } else if (keyStroke.getKeyType() == KeyType.Character) {
            char ch = keyStroke.getCharacter();
            return switch (ch) {
                case 'h' -> // move left
                        new CommandPlayerMove(new Position(-1, 0));
                case 'j' -> // move down
                        new CommandPlayerMove(new Position(0, 1));
                case 'k' -> // move up
                        new CommandPlayerMove(new Position(0, -1));
                case 'l' -> // move right
                        new CommandPlayerMove(new Position(1, 0));
                case 'y' -> // move up left
                        new CommandPlayerMove(new Position(-1, -1));
                case 'u' -> // move up right
                        new CommandPlayerMove(new Position(1, -1));
                case 'b' -> // move down left
                        new CommandPlayerMove(new Position(-1, 1));
                case 'n' -> // move down right
                        new CommandPlayerMove(new Position(1, 1));
                case ',' -> // pick up item at current position
                        new CommandPlayerPickUp();
                case 'v' -> new CommandShowVersion();
                case 'Q' -> new CommandQuit(true);
                case '|' -> {
                    if (config.isMaster() && config.isWizard()) {
                        yield new CommandShowPlayerPosition();
                    } else {
                        yield new CommandIllegal(keyStroke);
                    }
                }
                default -> new CommandIllegal(keyStroke);
            };
        } else {
            final KeyType keyType = keyStroke.getKeyType();
            return switch (keyType) {
                case ArrowUp -> fromKeyStroke(KeyStroke.fromString("k"));
                case ArrowDown -> fromKeyStroke(KeyStroke.fromString("j"));
                case ArrowLeft -> fromKeyStroke(KeyStroke.fromString("h"));
                case ArrowRight -> fromKeyStroke(KeyStroke.fromString("l"));
                case PageUp -> fromKeyStroke(KeyStroke.fromString("u"));
                case PageDown -> fromKeyStroke(KeyStroke.fromString("n"));
                case Home -> fromKeyStroke(KeyStroke.fromString("y"));
                case End -> fromKeyStroke(KeyStroke.fromString("b"));
                default -> new CommandIllegal(keyStroke);
            };
        }
        return new CommandIllegal(keyStroke);
    }
}