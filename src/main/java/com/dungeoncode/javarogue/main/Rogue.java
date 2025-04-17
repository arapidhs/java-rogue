package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Rogue {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rogue.class);

    private static final String STRING_TERMINAL_TITLE = "Rogue";
    private static final String PROMPT_WIZARD_PASSWORD = "wizard's password: ";
    private static final String PASSWORD_SALT = "mT";
    private static final String PASSWORD_HASH = "62851374aa4abd12095d7246ae1e3c273ab5619e9967be902dc0847047d333ae";

    public static void main(String[] args) {

        final Config config = new Config();

        RogueScreen screen = null;

        try {

            final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory( System.out, System.in, StandardCharsets.UTF_8);
            terminalFactory.setInitialTerminalSize(
                    new TerminalSize(config.getTerminalCols(), config.getTerminalRows()));
            terminalFactory.setTerminalEmulatorTitle(STRING_TERMINAL_TITLE);

            try {
                screen = new RogueScreen(terminalFactory.createTerminal(), config);
                screen.startScreen();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to create terminal screen. Exception: ", ex);
            }

            final Options options = getOptions(args);
            config.applyOptions(options);

            if (options.master) {
                try {
                    final String enteredPassword = screen.promptForPassword(PROMPT_WIZARD_PASSWORD);
                    final boolean wizard = !enteredPassword.isEmpty() &&
                            RogueUtils.crypt(enteredPassword, PASSWORD_SALT).equals(PASSWORD_HASH);
                    config.setWizard(wizard);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to create prompt for password. Exception: ", ex);
                } finally {
                    screen.clearAndRefresh();
                }
            }

            if (options.showScores) {
                final ScoreManager scoreManager = new ScoreManager(screen);
                scoreManager.score(null);

                // Wait for any key to exit
                while (true) {
                    KeyStroke key = screen.readInput();
                    if (key != null) {
                        break;
                    }
                }

            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(1);
        } finally {
            exit(screen);
        }

    }

    private static void exit(@Nullable RogueScreen screen) {
        closeScreen(screen);
        System.exit(0);
    }

    private static Options getOptions(String[] args) {
        final Options options = new Options();
        try {
            new CommandLine(options).parseArgs(args);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse command line arguments. Exception: ", ex);
        }
        return options;
    }

    private static void closeScreen(@Nullable Screen screen) {
        try {
            if (screen != null) {
                screen.close();
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to stop screen. Exception: ", ex);
            System.exit(1);
        }
    }

}
