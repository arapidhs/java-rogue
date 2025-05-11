package com.dungeoncode.javarogue.ui;

import com.dungeoncode.javarogue.config.Config;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RogueScreen extends TerminalScreen {

    private final Config config;
    private final TextGraphics textGraphics;
    private final List<Window> windows;

    public RogueScreen(@Nonnull final Terminal terminal, @Nonnull final Config config) throws IOException {
        super(terminal);
        this.config = config;
        this.windows=new ArrayList<>();

        if (getTerminal() instanceof SwingTerminalFrame swingTerminalFrame) {
            swingTerminalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        }

        textGraphics = newTextGraphics();
        hideCursor();

    }

    private void hideCursor() {
        setCursorPosition(null);
    }

    public void clearAndRefresh() throws IOException {
        clear();
        refresh();
    }

    public void refresh(){
        try {
            super.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyStroke readInput(){
        try {
            return super.readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearLine(int row) {
        putString(0, row, " ".repeat(getColumns() - 1));
    }

    public void putString(final int x, final int y, final String string) {
        textGraphics.putString(x, y, string);
    }

    public int getColumns() {
        return getTerminalSize().getColumns();
    }

    public int getRows() {
        return getTerminalSize().getRows();
    }

    /**
     * Waits for the user to input the specified character, or newline/carriage return if specified.
     *
     * @param ch The character to wait for (e.g., '\n' for newline, ' ' for space).
     * @throws IOException If an I/O error occurs while reading input.
     */
    public void waitFor(char ch) throws IOException {
        while (true) {
            final KeyStroke key = readInput();
            if ((ch == '\n' || ch == '\r') && key.getKeyType() == KeyType.Enter) {
                return;
            } else if ( key.getCharacter()!=null && key.getCharacter() == ch) {
                return;
            }
        }
    }

    public String promptForPassword(@Nonnull final String prompt) throws IOException {
        Objects.requireNonNull(prompt);

        final StringBuilder password = new StringBuilder();
        final int promptX = 0;
        final int promptY = 0;
        final int inputStartX = promptX + prompt.length();

        clear();
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

    public String readString() throws IOException {
        final StringBuilder input = new StringBuilder();
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

        return input.toString();
    }

    public Config getConfig() {
        return config;
    }

    public void putChar(final int x, final int y, final char symbol) {
        textGraphics.putString(x, y, String.valueOf(symbol));
    }

    public void enableModifiers(@Nonnull final SGR sgr) {
        Objects.requireNonNull(sgr);
        textGraphics.enableModifiers(sgr);
    }

    public void disableModifiers(@Nonnull final SGR sgr) {
        Objects.requireNonNull(sgr);
        textGraphics.disableModifiers(sgr);
    }

    public void addWindow(@Nonnull final String name,
                          final int x, final int y,
                          final int cols, final int rows) {
            this.windows.add(new Window(name,x,y,cols,rows));
    }

    private static class Window{
        private final String name;
        private final TerminalPosition size;
        private final TerminalPosition topLeft;

        Window(@Nonnull String name, final int x,  final int y, final int cols, final int rows){
            this.name=name;
            this.topLeft=new TerminalPosition(x,y);
            this.size=new TerminalPosition(cols,rows);
        }
    }

}
