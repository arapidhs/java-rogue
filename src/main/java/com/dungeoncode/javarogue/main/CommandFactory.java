package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class CommandFactory {

    public Command fromKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            char ch = keyStroke.getCharacter();
            return switch (ch) {
                case 'h' -> {
                    yield new CommandParameterizedMove(new Position(-1, 0)); // move left
                }
                case 'j' -> {
                    yield new CommandParameterizedMove(new Position(0, 1)); // move down
                }
                case 'k' -> {
                    yield new CommandParameterizedMove(new Position(0, -1)); // move up
                }
                case 'l' -> {
                    yield new CommandParameterizedMove(new Position(1, 0)); /// move right
                }
                case 'y' -> {
                    yield new CommandParameterizedMove(new Position(-1, -1)); /// move up left
                }
                case 'u' -> {
                    yield new CommandParameterizedMove(new Position(1, -1)); /// move up right
                }
                case 'b' -> {
                    yield new CommandParameterizedMove(new Position(-1, 1)); /// move down left
                }
                case 'n' -> {
                    yield new CommandParameterizedMove(new Position(1, 1)); /// move down right
                }
                case 's' -> {
                    yield new CommandShowMap();
                }
                default -> {
                    yield null;
                }
            };
        } else {
            final KeyType keyType = keyStroke.getKeyType();
            return switch (keyType) {
                case ArrowUp -> {yield fromKeyStroke(KeyStroke.fromString("k"));}
                case ArrowDown -> {yield fromKeyStroke(KeyStroke.fromString("j"));}
                case ArrowLeft -> {yield fromKeyStroke(KeyStroke.fromString("h"));}
                case ArrowRight -> {yield fromKeyStroke(KeyStroke.fromString("l"));}
                case PageUp -> {yield fromKeyStroke(KeyStroke.fromString("u"));}
                case PageDown -> {yield fromKeyStroke(KeyStroke.fromString("n"));}
                case Home -> {yield fromKeyStroke(KeyStroke.fromString("y"));}
                case End -> {yield fromKeyStroke(KeyStroke.fromString("b"));}
                case Escape -> {
                    yield new CommandQuit();
                }
                default -> {
                    yield null;
                }
            };

        }
    }
}
