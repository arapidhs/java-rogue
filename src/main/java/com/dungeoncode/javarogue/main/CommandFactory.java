package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class CommandFactory {

    public Command fromKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            char ch = keyStroke.getCharacter();
            return switch (ch) {
                case 's' -> {
                    yield new NewLevelCommand();
                }
                default -> {
                    yield  null;
                }
            }; // Left
        } else if (keyStroke.getKeyType() == KeyType.Escape) {
            return new QuitCommand();
        }
        return null;
    }
}
