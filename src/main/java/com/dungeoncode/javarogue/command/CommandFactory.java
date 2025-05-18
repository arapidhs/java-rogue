package com.dungeoncode.javarogue.command;

import com.dungeoncode.javarogue.command.action.CommandPlayerMove;
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

    public CommandFactory(@Nonnull final GameState gameState){
        this.gameState=gameState;
        this.config=gameState.getConfig();
    }

    public Command fromKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.isCtrlDown()){
            if (keyStroke.getKeyType() == KeyType.Character) {
                char ch = keyStroke.getCharacter();
                return switch (ch) {
                    case 'D','d' -> {
                        if(config.isMaster() && config.isWizard()) {
                            int levelNum = gameState.getLevelNum();
                            gameState.setLevelNum(++levelNum);
                            yield new CommandNewLevel(levelNum);
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'A','a' -> {
                        if(config.isMaster() && config.isWizard()) {
                            int levelNum = gameState.getLevelNum();
                            gameState.setLevelNum(--levelNum);
                            yield new CommandNewLevel(levelNum);
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'F','f' -> {
                        if(config.isMaster() && config.isWizard()) {
                            yield new CommandShowMap();
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'E','e' -> {
                        if(config.isMaster() && config.isWizard()) {
                            yield new CommandShowPlayerFoodLeft();
                        } else {
                            yield new CommandIllegal(keyStroke);
                        }
                    }
                    case 'P','p' -> new CommandShowLastMessage();
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
                case 'v' ->
                        new CommandShowVersion();
                case 'Q' -> new CommandQuit(true);
                case '|' -> {
                    if(config.isMaster() && config.isWizard()) {
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
