/**
 * Manages the terminal screen for the Java port of Rogue, handling text rendering, window management,
 * and user input. Extends Lanterna's TerminalScreen with custom functionality for dungeon display,
 * equivalent to screen handling in the original Rogue C source.
 */
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

    public static final String WINDOW_HW = "hw";

    private static final Logger LOGGER = LoggerFactory.getLogger(RogueScreen.class);

    private final Config config;
    private final TextGraphics textGraphics;
    private final List<Window> windows;
    private final TextCharacter[][] buffer;

    /**
     * Constructs a RogueScreen with the specified terminal and configuration.
     * Initializes the screen, hides the cursor, and sets up the window icon if using SwingTerminalFrame.
     *
     * @param terminal The Lanterna terminal.
     * @param config   The game configuration.
     * @throws IOException If terminal initialization fails.
     */
    public RogueScreen(@Nonnull final Terminal terminal, @Nonnull final Config config) throws IOException {
        super(terminal);
        setCursorPosition(null);
        this.config = config;
        this.windows = new ArrayList<>();
        this.buffer = new TextCharacter[getRows()][getColumns()];
        if (getTerminal() instanceof SwingTerminalFrame swingTerminalFrame) {
            swingTerminalFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            swingTerminalFrame.setIconImage(Rogue.ICON_ROGUE_64);
        }
        textGraphics = newTextGraphics();
    }

    /**
     * Returns the number of rows in the terminal.
     *
     * @return The row count.
     */
    public int getRows() {
        return getTerminalSize().getRows();
    }

    /**
     * Returns the number of columns in the terminal.
     *
     * @return The column count.
     */
    public int getColumns() {
        return getTerminalSize().getColumns();
    }

    /**
     * Writes a string to the specified window at the given coordinates with optional text style.
     *
     * @param windowName The target window name.
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param str        The string to write.
     * @param sgr        Optional text style modifier.
     */
    public void putWString(@Nonnull final String windowName, final int x, final int y, @Nonnull final String str,
                           @Nullable SGR sgr) {
        Objects.requireNonNull(windowName);
        Objects.requireNonNull(str);
        getWindow(windowName).putString(x, y, str, textGraphics.getForegroundColor(), textGraphics.getBackgroundColor(), sgr);
    }

    /**
     * Retrieves a window by its name.
     *
     * @param name The window name.
     * @return The Window object.
     * @throws IllegalArgumentException If the window is not found.
     */
    private Window getWindow(@Nonnull final String name) {
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

    /**
     * Writes a character to the specified window at the given coordinates with optional modifiers.
     *
     * @param windowName The target window name.
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param character  The character to write.
     * @param modifiers  Optional text style modifiers.
     */
    public void putWChar(@Nonnull final String windowName, final int x, final int y, @Nonnull final Character character,
                         @Nullable SGR... modifiers) {
        Objects.requireNonNull(windowName);
        Objects.requireNonNull(character);
        final TextCharacter[] textCharacters = modifiers == null ?
                TextCharacter.fromCharacter(character, textGraphics.getForegroundColor(), textGraphics.getBackgroundColor()) :
                TextCharacter.fromCharacter(character, textGraphics.getForegroundColor(), textGraphics.getBackgroundColor(), modifiers);
        getWindow(windowName).putChar(x, y, textCharacters[0]);
    }

    /**
     * Clears the specified window's buffer.
     *
     * @param windowName The target window name.
     */
    public void clearWindow(@Nonnull final String windowName) {
        Objects.requireNonNull(windowName);
        getWindow(windowName).clear();
    }

    /**
     * Displays the specified window, backing up the current screen buffer and copying the window's contents.
     *
     * @param windowName The window to display.
     */
    public void showWindow(@Nonnull final String windowName) {
        Objects.requireNonNull(windowName);
        final Window window = getWindow(windowName);
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                buffer[y][x] = getFrontCharacter(x, y) == null ? TextCharacter.DEFAULT_CHARACTER : getFrontCharacter(x, y);
            }
        }
        clear();
        for (int y = 0; y < window.rows; y++) {
            for (int x = 0; x < window.cols; x++) {
                textGraphics.setCharacter(x + window.x, y + window.y, window.buffer[y][x]);
            }
        }
        refresh();
    }

    /**
     * Refreshes the screen with automatic refresh type.
     */
    public void refresh() {
        refresh(RefreshType.DELTA);
    }

    /**
     * Refreshes the screen with the specified refresh type, ensuring thread safety for Swing.
     *
     * @param refreshType The refresh type.
     */
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

    /**
     * Reads user input from the terminal.
     *
     * @return The KeyStroke input.
     */
    public KeyStroke readInput() {
        try {
            return super.readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the specified window, displays a message, waits for a space key press, and restores the screen buffer.
     *
     * @param windowName The window to close.
     * @param message    The message to display.
     */
    public void closeWindow(@Nonnull final String windowName, @Nonnull final String message) {
        Objects.requireNonNull(windowName);
        Objects.requireNonNull(message);
        final Window window = getWindow(windowName);
        putString(window.x, window.y, message);
        refresh();
        waitFor(' ');
        clear();
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getColumns(); x++) {
                textGraphics.setCharacter(x, y, buffer[y][x]);
            }
        }
        refresh();
    }

    /**
     * Writes a string to the screen at the specified coordinates.
     *
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param string The string to write.
     */
    public void putString(final int x, final int y, final String string) {
        textGraphics.putString(x, y, string);
    }

    /**
     * Waits for the user to input the specified character or Enter key.
     *
     * @param ch The character to wait for (e.g., '\n' for Enter, ' ' for space).
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

    /**
     * Clears the screen and refreshes it.
     */
    public void clearAndRefresh() {
        clear();
        refresh();
    }

    /**
     * Clears a specified row with spaces.
     *
     * @param row The row to clear.
     */
    public void clearLine(int row) {
        putString(0, row, " ".repeat(getColumns() - 1));
    }

    /**
     * Prompts the user for a password, displaying asterisks for input and handling backspace.
     *
     * @param prompt The prompt message.
     * @return The entered password.
     * @throws IOException If input fails.
     */
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
                int eraseX = inputStartX + password.length();
                setCharacter(eraseX, promptY, TextCharacter.fromCharacter(' ')[0]);
            } else if (key.getKeyType() == KeyType.Character) {
                password.append(key.getCharacter());
                int starX = inputStartX + password.length() - 1;
                setCharacter(starX, promptY, TextCharacter.fromCharacter('*')[0]);
            }
            refresh();
        }
        return password.toString();
    }

    /**
     * Reads a string input from the user, supporting backspace and Enter to finish.
     *
     * @return The entered string.
     */
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

    /**
     * Returns the game configuration.
     *
     * @return The Config object.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Writes a single character to the screen at the specified coordinates.
     *
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param symbol The character to write.
     */
    public void putChar(final int x, final int y, final char symbol) {
        textGraphics.putString(x, y, String.valueOf(symbol));
    }

    /**
     * Enables the specified text style modifier.
     *
     * @param sgr The style modifier to enable.
     */
    public void enableModifiers(@Nonnull final SGR sgr) {
        Objects.requireNonNull(sgr);
        textGraphics.enableModifiers(sgr);
    }

    /**
     * Disables the specified text style modifier.
     *
     * @param sgr The style modifier to disable.
     */
    public void disableModifiers(@Nonnull final SGR sgr) {
        Objects.requireNonNull(sgr);
        textGraphics.disableModifiers(sgr);
    }

    /**
     * Adds a new window with the specified name and dimensions.
     *
     * @param name The window name.
     * @param x    The x-coordinate of the window's top-left corner.
     * @param y    The y-coordinate of the window's top-left corner.
     * @param cols The number of columns.
     * @param rows The number of rows.
     */
    public void addWindow(@Nonnull final String name, final int x, final int y, final int cols, final int rows) {
        this.windows.add(new Window(name, x, y, cols, rows));
    }

    /**
     * Inner class representing a window with its own buffer for text rendering.
     */
    private static class Window {
        private final String name;
        private final int x;
        private final int y;
        private final int cols;
        private final int rows;
        private final TextCharacter[][] buffer;

        /**
         * Constructs a window with the specified name and dimensions.
         *
         * @param name The window name.
         * @param x    The x-coordinate of the window's top-left corner.
         * @param y    The y-coordinate of the window's top-left corner.
         * @param cols The number of columns.
         * @param rows The number of rows.
         */
        Window(@Nonnull String name, final int x, final int y, final int cols, final int rows) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.cols = cols;
            this.rows = rows;
            this.buffer = new TextCharacter[rows][cols];
        }

        /**
         * Clears the window's buffer.
         */
        void clear() {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    buffer[y][x] = TextCharacter.DEFAULT_CHARACTER;
                }
            }
        }

        /**
         * Writes a string to the window's buffer with specified colors and modifiers.
         *
         * @param x               The x-coordinate.
         * @param y               The y-coordinate.
         * @param str             The string to write.
         * @param foregroundColor The text color.
         * @param backgroundColor The background color.
         * @param modifiers       Optional style modifiers.
         */
        void putString(int x, int y, @Nonnull final String str, @Nullable final TextColor foregroundColor,
                       @Nullable TextColor backgroundColor, SGR... modifiers) {
            Objects.requireNonNull(str, "String cannot be null");
            for (int i = 0; i < str.length() && x + i < cols && y < rows; i++) {
                final TextCharacter[] textCharacters = TextCharacter.fromCharacter(
                        str.charAt(i), foregroundColor, backgroundColor,
                        Objects.requireNonNullElseGet(modifiers, () -> new SGR[0]));
                putChar(x + i, y, textCharacters[0]);
            }
        }

        /**
         * Writes a character to the window's buffer.
         *
         * @param x             The x-coordinate.
         * @param y             The y-coordinate.
         * @param textCharacter The character to write.
         * @throws IllegalArgumentException If coordinates are out of bounds.
         */
        void putChar(int x, int y, TextCharacter textCharacter) {
            if (x < 0 || x >= cols || y < 0 || y >= rows) {
                throw new IllegalArgumentException(
                        String.format("Coordinates out of bounds: (%d, %d). Window size: %d cols, %d rows",
                                x, y, cols, rows));
            }
            this.buffer[y][x] = textCharacter;
        }
    }
}