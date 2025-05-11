package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.*;
import com.dungeoncode.javarogue.system.death.DeathSource;
import com.dungeoncode.javarogue.system.initializer.DeathSimulationInitializer;
import com.dungeoncode.javarogue.system.initializer.DefaultInitializer;
import com.dungeoncode.javarogue.template.KillTypeTemplate;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.dungeoncode.javarogue.template.Templates;
import com.dungeoncode.javarogue.ui.MessageSystem;
import com.dungeoncode.javarogue.ui.RogueScreen;
import com.dungeoncode.javarogue.ui.TombstoneRenderer;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.dungeoncode.javarogue.core.Messages.*;

/**
 * Main entry point for the Rogue game, responsible for initializing the terminal, processing command-line options,
 * handling wizard mode authentication, displaying scores or death screens, and starting the game.
 * Configures the game environment, including terminal size, font, and seed, and manages game state initialization.
 */
public class Rogue {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rogue.class);

    private static final String PASSWORD_SALT = "mT";
    private static final String PASSWORD_HASH = "62851374aa4abd12095d7246ae1e3c273ab5619e9967be902dc0847047d333ae";

    private static final String PATH_FONT_IBM_VGA_8x16="/fonts/Ac437_IBM_VGA_8x16.ttf";
    private static final String PATH_ROGUE_ICON_64="/icons/icon-java-rogue-64.png";

    public static BufferedImage ICON_ROGUE_64 = null;

    public static void main(String[] args) {

        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());

        RogueScreen screen = null;
        MessageSystem messageSystem;
        try {

            final BufferedImage icon = ImageIO.read(Objects.requireNonNull(Rogue.class.getResourceAsStream(PATH_ROGUE_ICON_64)));
            ICON_ROGUE_64=icon;

            final InputStream fontStream = Rogue.class.getResourceAsStream(PATH_FONT_IBM_VGA_8x16);
            assert fontStream != null;
            final Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 26);
            fontStream.close();
            // final Font font = new Font("Monospaced", Font.PLAIN, 16);

            // Initialize terminal with configured size and font
            final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory(System.out, System.in, StandardCharsets.UTF_8);
            terminalFactory.setInitialTerminalSize(
                    new TerminalSize(config.getTerminalCols(), config.getTerminalRows()));
            terminalFactory.setTerminalEmulatorTitle(TERMINAL_TITLE);
            terminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(font));

            try {
                screen = new RogueScreen(terminalFactory.createTerminal(), config);
                messageSystem = new MessageSystem(screen);
                screen.startScreen();
            } catch (IOException ex) {
                throw new RuntimeException(ERROR_FAILED_CREATE_TERMINAL, ex);
            }

            final Options options = getOptions(args);
            config.applyOptions(options);

            // Handle wizard mode password prompt
            if (options.master) {
                try {
                    final String enteredPassword = screen.promptForPassword(PROMPT_WIZARD_PASSWORD);
                    final boolean wizard = !enteredPassword.isEmpty() &&
                            RogueUtils.crypt(enteredPassword, PASSWORD_SALT).equals(PASSWORD_HASH);
                    config.setWizard(wizard, rogueRandom);
                } catch (IOException ex) {
                    throw new RuntimeException(ERROR_FAILED_CREATE_PROMPT_PASSWORD, ex);
                } finally {
                    screen.clearAndRefresh();
                }
            }

            if (options.showScores) {
                final ScoreManager scoreManager = new ScoreManager(screen);
                scoreManager.score(null);
            } else if (options.simulateDeath) {
                // Simulate death and display death screen or tombstone
                final GameState gameState = new GameState(config, rogueRandom, screen, new DeathSimulationInitializer(), messageSystem);
                gameState.death();

                if (gameState.getConfig().isTombstone()) {
                    final TombstoneRenderer tombstoneRenderer = new TombstoneRenderer(screen, gameState);
                    tombstoneRenderer.renderTombstone();
                } else {
                    // Build and display death message
                    final StringBuilder deathLine = new StringBuilder();
                    deathLine.append("Killed by ");

                    final String killerName = gameState.getDeathSource().name();
                    final boolean isKillType = gameState.getDeathSource().isTemplate() &&
                            gameState.getDeathSource().type().equals(DeathSource.Type.KILL_TYPE);
                    boolean isUseArticle = true;

                    if (isKillType) {
                        final KillTypeTemplate killTypeTemplate = Templates.getTemplate(KillTypeTemplate.class, gameState.getDeathSource().templateId());
                        isUseArticle = killTypeTemplate != null ? killTypeTemplate.isUseArticle() : isUseArticle;
                    }

                    if (isUseArticle) {
                        final String article = RogueUtils.getIndefiniteArticleFor(killerName);
                        deathLine.append(article).append(" ").append(killerName);
                    } else {
                        deathLine.append(killerName);
                    }

                    deathLine.append(' ').append("with").append(' ').append(gameState.getPlayer().getGoldAmount()).append(' ').append("gold");

                    screen.putString(0, screen.getRows() - 2, deathLine.toString());
                }

                screen.refresh();

                final ScoreManager scoreManager = new ScoreManager(screen);
                scoreManager.score(gameState);

            } else {
                // Display welcome message based on mode
                if (config.isMaster() && config.isWizard()) {
                    screen.putString(0, screen.getRows() - 1,
                            String.format("Hello %s, welcome to dungeon #%d", config.getPlayerName(), config.getDungeonSeed())
                    );
                } else {
                    screen.putString(0, screen.getRows() - 1,
                            String.format("Hello %s, just a moment while I dig the dungeon...", config.getPlayerName())
                    );
                }

                // Verify template probabilities in master mode
                if (config.isMaster()) {
                    final Optional<Templates.BadTemplateInfo> badTemplateInfo = Templates.verifyProbabilities();
                    if (badTemplateInfo.isPresent()) {
                        final Templates.BadTemplateInfo info = badTemplateInfo.get();
                        final List<? extends ObjectInfoTemplate> templates = Templates.getTemplates(info.templateClass()).stream()
                                .sorted(Comparator.comparingLong(ObjectInfoTemplate::getId))
                                .toList();

                        final String label = templates.get(0).getTemplateName();
                        final int bound = templates.size();

                        int y = 0;
                        screen.putString(0, y, String.format(ERROR_BAD_PROBABILITY_PERCENTAGES, label, bound));
                        for (ObjectInfoTemplate template : templates) {
                            screen.putString(0, ++y, String.format("%3.0f%% %s", template.getCumulativeProbability(), template.getName()));
                        }

                        screen.putString(0, y, PROMPT_HIT_RETURN_CONTINUE);
                        screen.clearAndRefresh();
                        screen.readInput();
                    }
                }

                final GameState gameState = new GameState(config, rogueRandom, screen, new DefaultInitializer(), messageSystem);
                gameState.loop();
                exit(screen);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(1);
        } catch (StackOverflowError stackOverflowError) {
            LOGGER.error("Stack Overflow!");
            System.exit(1);
        } finally {
            exit(screen);
        }
    }

    /**
     * Parses command-line arguments into Options.
     *
     * @param args The command-line arguments.
     * @return The parsed Options object.
     */
    private static Options getOptions(String[] args) {
        final Options options = new Options();
        try {
            new CommandLine(options).parseArgs(args);
        } catch (Exception ex) {
            LOGGER.error(ERROR_FAILED_PARSE_CLI_ARGS, ex);
        }
        return options;
    }

    /**
     * Closes the screen and exits the application.
     *
     * @param screen The RogueScreen to close, may be null.
     */
    public static void exit(@Nullable RogueScreen screen) {
        closeScreen(screen);
        System.exit(0);
    }

    /**
     * Closes the provided screen, handling any errors during closure.
     *
     * @param screen The Screen to close, may be null.
     */
    private static void closeScreen(@Nullable Screen screen) {
        try {
            if (screen != null) {
                screen.close();
            }
        } catch (IOException ex) {
            LOGGER.error(ERROR_FAILED_STOP_SCREEN, ex);
            System.exit(1);
        }
    }

}
