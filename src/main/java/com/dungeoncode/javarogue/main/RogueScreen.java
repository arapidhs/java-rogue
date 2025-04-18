package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class RogueScreen extends TerminalScreen {

    private final Config config;
    private final TextGraphics textGraphics;

    public RogueScreen(@Nonnull final Terminal terminal, @Nonnull final Config config) throws IOException {
        super(terminal);
        this.config = config;

        if (getTerminal() instanceof SwingTerminalFrame swingTerminalFrame) {
            swingTerminalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        }

        textGraphics = newTextGraphics();
    }

    public void hideCursor() {
        setCursorPosition(null);
    }

    public void clearAndRefresh() throws IOException {
        clear();
        refresh();
    }

    public String promptForPassword(@Nonnull final String prompt) throws IOException {
        Objects.requireNonNull(prompt);

        final StringBuilder password = new StringBuilder();
        final int promptX = 0;
        final int promptY = 0;
        final int inputStartX = promptX + prompt.length();

        clear();
        hideCursor();
        textGraphics.putString(promptX, promptY, prompt);
        refresh();

        while (true) {
            KeyStroke key = readInput();

            if (key.getKeyType() == KeyType.Enter) {
                break;
            }

            if (key.getKeyType() == KeyType.Backspace && !password.isEmpty()) {
                password.deleteCharAt(password.length() - 1);

                // Visually erase last asterisk
                int eraseX = inputStartX + password.length();
                setCharacter(eraseX, promptY, TextCharacter.fromCharacter(' ')[0]);

            } else if (key.getKeyType() == KeyType.Character) {
                password.append(key.getCharacter());

                // Display '*' for each typed character
                int starX = inputStartX + password.length() - 1;
                setCharacter(starX, promptY, TextCharacter.fromCharacter('*')[0]);
            }

            refresh();
        }

        return password.toString();
    }

    public String showBottomMessageAndWait(@Nonnull final String message, int column) throws IOException {

        Objects.requireNonNull(message);

        final StringBuilder input = new StringBuilder();

        final TerminalSize size = getTerminalSize();
        int row = size.getRows() - 1;

        textGraphics.putString(column, row, message);
        refresh();

        while (true) {
            KeyStroke key = readInput();
            if (key.getKeyType() == KeyType.Enter) {
                break;
            }
            if (key.getKeyType() == KeyType.Backspace && !input.isEmpty()) {
                input.deleteCharAt(input.length() - 1);
            } else if (key.getKeyType() == KeyType.Character) {
                input.append(key.getCharacter());
            }
        }

        // Overwrite the message with spaces to clear it
        final String blank = " ".repeat(message.length());
        textGraphics.putString(column, row, blank);

        return input.toString();
    }

    public Config getConfig() {
        return config;
    }

    public void putString(final int x, final int y, final String string) {
        textGraphics.putString(x, y, string);
    }

    public void enableModifiers(@Nonnull final SGR sgr) {
        Objects.requireNonNull(sgr);
        textGraphics.enableModifiers(sgr);
    }

    public void disableModifiers(@Nonnull final SGR sgr) {
        Objects.requireNonNull(sgr);
        textGraphics.disableModifiers(sgr);
    }

}
