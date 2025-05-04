package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class MessageSystem {

    private static final String MSG_MORE = Messages.MSG_SYSTEM_MORE;

    private final StringBuilder messageBuffer;
    private final Config config;
    private final RogueScreen screen;
    private final int maxMessageLength;
    private String lastMessage;
    private int messagePosition;
    private GameState gameState;

    public MessageSystem(@Nonnull final RogueScreen screen) throws IOException {
        Objects.requireNonNull(screen);
        this.screen = screen;
        this.config = screen.getConfig();
        this.messageBuffer = new StringBuilder();
        this.maxMessageLength = screen.getColumns() - MSG_MORE.length();
    }

    /**
     * Displays a formatted message at the top of the screen.
     * If the message is empty, clears the top line. Otherwise, appends the message to the buffer and displays it.
     *
     * @param message The message to display, can be a format string.
     * @return true if the message is displayed successfully, false if canceled by ESC key.
     * @throws RuntimeException     If an I/O error occurs during screen operations or input reading.
     * @throws NullPointerException If the message is null.
     */
    public boolean msg(@Nonnull final String message) {
        Objects.requireNonNull(message);
        try {
            if (message.isEmpty()) {
                screen.clearLine(0);
                messagePosition = 0;
                screen.refresh();
                return true;
            } else {
                doadd(message);
                return endmsg();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Adds a formatted message to the message buffer.
     *
     * @param message The formatted message to append.
     * @see #doadd(String message)
     */
    public void addmssg(@Nonnull final String message) {
        doadd(message);
    }

    /**
     * Appends a formatted message to the message buffer.
     *
     * @param message The formatted message to append.
     */
    public void doadd(@Nonnull final String message) {
        Objects.requireNonNull(message);
        if (messageBuffer.length() + message.length() >= maxMessageLength) {
            endmsg();
        }
        messageBuffer.append(message);
    }

    /**
     * Displays the current message buffer content at the top of the screen.
     * If a previous message exists, shows a "--More--" prompt and waits for user input.
     * Capitalizes the first letter of the message unless configured otherwise or specific conditions apply.
     * Clears the message buffer after display.
     *
     * @return true if the message is displayed successfully, false if canceled by ESC key.
     * @throws RuntimeException If an I/O error occurs during screen operations or input reading.
     */
    public boolean endmsg() {
        try {
            if (config.isMessageSave()) {
                lastMessage = messageBuffer.toString();
            }
            if (messagePosition > 0) {

                gameState.look(false);
                screen.enableModifiers(SGR.REVERSE);
                screen.putString(messagePosition, 0, MSG_MORE);
                screen.disableModifiers(SGR.REVERSE);
                screen.refresh();

                if (!config.isMessageAllowEscape()) {
                    screen.waitFor(' ');
                } else {
                    while (true) {
                        final KeyStroke key = screen.readInput();
                        if (key.getKeyType() == KeyType.Character && key.getCharacter() == ' ') {
                            break;
                        }
                        if (key.getKeyType() == KeyType.Escape) {
                            messageBuffer.setLength(0);
                            messagePosition = 0;
                            return false;
                        }
                    }
                }
            }

            capitalizeMessageBuffer();
            screen.clearLine(0);
            screen.putString(0, 0, messageBuffer.toString());
            messagePosition = messageBuffer.length();
            messageBuffer.setLength(0);
            screen.refresh();
            return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void capitalizeMessageBuffer() {
        if (!messageBuffer.isEmpty() && Character.isLowerCase(messageBuffer.charAt(0))
                && !config.isMessageAllowLowercase() && (messageBuffer.length() < 2 || messageBuffer.charAt(1) != ')')) {
            messageBuffer.setCharAt(0, Character.toUpperCase(messageBuffer.charAt(0)));
        }
    }

    public void setGameState(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        this.gameState = gameState;
    }

}
