package com.dungeoncode.javarogue.system;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.main.Rogue;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RogueScreen extends TerminalScreen {

    public static final String WINDOW_HW="hw";

    private static final Logger LOGGER = LoggerFactory.getLogger(RogueScreen.class);

    private final Config config;
    private final TextGraphics textGraphics;
    private final List<Window> windows;
    private final TextCharacter[][] buffer;

    public RogueScreen(@Nonnull final Terminal terminal, @Nonnull final Config config) throws IOException {
        super(terminal);
        // hide cursor
        setCursorPosition(null);
        this.config = config;
        this.windows = new ArrayList<>();
        this.buffer = new TextCharacter[getRows()][getColumns()];
        if (getTerminal() instanceof SwingTerminalFrame swingTerminalFrame) {
            swingTerminalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            swingTerminalFrame.setIconImage(Rogue.ICON_ROGUE_64);
        }
        textGraphics = newTextGraphics();
    }

    public void putWString(@Nonnull final String windowName,
                           final int x, final int y, @Nonnull final String str,
                           @Nullable SGR sgr){
        Objects.requireNonNull(windowName);
        Objects.requireNonNull(str);
        getWindow(windowName).putString(x,y,str,textGraphics.getForegroundColor(),textGraphics.getBackgroundColor(),sgr);

    }

    public void putWChar(@Nonnull final String windowName,
                         final int x, final int y, @Nonnull final Character character,
                         @Nullable SGR... modifiers){
        Objects.requireNonNull(windowName);
        Objects.requireNonNull(character);
        final TextCharacter[] textCharacters;
        if (modifiers == null) {
            textCharacters = TextCharacter.fromCharacter(
                    character, textGraphics.getForegroundColor(), textGraphics.getBackgroundColor());
        }
        else {
            textCharacters = TextCharacter.fromCharacter(
                    character, textGraphics.getForegroundColor(), textGraphics.getBackgroundColor(),modifiers);
        }
        getWindow(windowName).putChar(x,y,textCharacters[0]);
    }

    public void clearWindow(@Nonnull final String windowName) {
        Objects.requireNonNull(windowName);
        final Window window = getWindow(windowName);
        window.clear();
    }

    public void showWindow(@Nonnull final String windowName) {
        Objects.requireNonNull(windowName);
        final Window window=getWindow(windowName);

        // Iterate over RogueScreen's size and back up buffer
        // to be able to restore it after closing the new window
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                final TextCharacter textChar = getFrontCharacter(x, y);
                // Convert to Character or null
                buffer[y][x] = textChar==null ? TextCharacter.DEFAULT_CHARACTER : textChar;
            }
        }

        clear();

        // copy window contents to main buffer to show it
        for (int y = 0; y < window.rows; y++) {
            for (int x = 0; x < window.cols; x++) {
                final TextCharacter textCharacter = window.buffer[y][x];
                textGraphics.setCharacter(x+window.x,y+window.y,textCharacter);
            }
        }
        refresh();
    }

    public void closeWindow(@Nonnull final String windowName,@Nonnull final String message) {
        Objects.requireNonNull(windowName);
        Objects.requireNonNull(message);
        final Window window = getWindow(windowName);
        putString(window.x, window.y, message);
        refresh();
        waitFor(' ');

        clear();

        // restore buffer to main Screen
        // Iterate over RogueScreen's size and back up buffer
        // to be able to restore it after closing the new window
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                final TextCharacter textCharacter = this.buffer[y][x];
                textGraphics.setCharacter(x,y,textCharacter);
            }
        }
        refresh();
    }

    private Window getWindow(@Nonnull final String name){
        final Window window = windows.stream()
                .filter(w -> w.name.equals(name))
                .findFirst()
                .orElse(null);
        if (window == null) {
            LOGGER.debug("No window found with name: {}", name);
            throw new IllegalArgumentException(String.format("No window found with name: %s", name));
        }
        return window;
    }

    public void clearAndRefresh() {
        clear();
        refresh();
    }

    public void refresh() {
        refresh(RefreshType.AUTOMATIC);
    }

    public void refresh(final Screen.RefreshType refreshType) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    super.refresh(refreshType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyStroke readInput() {
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
     */
    public void waitFor(char ch) {
        while (true) {
            final KeyStroke key = readInput();
            if ((ch == '\n' || ch == '\r') && key.getKeyType() == KeyType.Enter) {
                return;
            } else if (key.getCharacter() != null && key.getCharacter() == ch) {
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

    public String readString() {
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
        this.windows.add(new Window(name, x, y, cols, rows));
    }


    private static class Window {
        private final String name;
        private final int x;
        private final int y;
        private final int cols;
        private final int rows;
        private final TextCharacter[][] buffer;

        Window(@Nonnull String name, final int x, final int y, final int cols, final int rows) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.cols = cols;
            this.rows = rows;
            this.buffer = new TextCharacter[rows][cols];
        }

        void clear() {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    buffer[y][x] = TextCharacter.DEFAULT_CHARACTER;
                }
            }
        }

        void putString(int x, int y, @Nonnull final String str, @Nullable final TextColor foregroundColor, @Nullable TextColor backgroundColor, SGR... modifiers) {
            Objects.requireNonNull(str, "String cannot be null");

            // Break string into chars and use putChar for each
            for (int i = 0; i < str.length(); i++) {
                // Only put char if within window bounds
                if (x + i < cols && y < rows) {

                    final TextCharacter[] textCharacters = TextCharacter.fromCharacter(
                            str.charAt(i), foregroundColor, backgroundColor,
                            Objects.requireNonNullElseGet(modifiers, () -> new SGR[0]));
                    putChar(x + i, y, textCharacters[0]);
                } else {
                    break; // Stop if we reach window bounds
                }
            }
        }

        void putChar(int x, int y, TextCharacter textCharacter) {
            // Validate coordinates
            if (x < 0 || x >= cols || y < 0 || y >= rows) {
                throw new IllegalArgumentException(
                        String.format("Coordinates out of bounds: (%d, %d). Window size: %d cols, %d rows",
                                x, y, cols, rows)
                );
            }
            this.buffer[y][x] = textCharacter;
        }
    }

}
