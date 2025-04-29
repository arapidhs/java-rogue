package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.TerminalSize;
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
        final RogueRandom rogueRandom = new RogueRandom( config.getSeed() );

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
                    config.setWizard(wizard, rogueRandom);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to create prompt for password. Exception: ", ex);
                } finally {
                    screen.clearAndRefresh();
                }
            }

            if (options.showScores) {
                final ScoreManager scoreManager = new ScoreManager(screen);
                scoreManager.score(null);
            } else if (options.simulateDeath) {

                final GameState gameState = new GameState(config, rogueRandom, new DeathSimulationInitializer());
                gameState.death();

                if ( gameState.getConfig().isTombstone()) {
                    final TombstoneRenderer tombstoneRenderer = new TombstoneRenderer(screen, gameState);
                    tombstoneRenderer.renderTombstone();
                } else {

                    final StringBuilder deathLine = new StringBuilder();
                    deathLine.append("Killed by ");

                    final String killerName = gameState.getDeathSource().getName();

                    final boolean isKillType = gameState.getDeathSource().isTemplate() &&
                            gameState.getDeathSource().getType().equals(DeathSource.Type.KILL_TYPE);

                    boolean isUseArticle = true;

                    if ( isKillType ) {
                        final KillTypeTemplate killTypeTemplate = Templates.getTemplate(KillTypeTemplate.class, gameState.getDeathSource().getTemplateId());
                        isUseArticle = killTypeTemplate != null ? killTypeTemplate.isUseArticle() : isUseArticle;
                    }

                    if ( isUseArticle ) {
                        final String article = RogueUtils.getIndefiniteArticleFor(killerName);
                        deathLine.append(article).append(" ").append(killerName);
                    } else {
                        deathLine.append(killerName);
                    }

                    deathLine.append(" with ").append(gameState.getGoldAmount()).append(" gold");

                    final TerminalSize size = screen.getTerminalSize();
                    final int row = size.getRows() - 2;
                    final int column = 0;
                    screen.putString(column, row, deathLine.toString());
                }

                screen.refresh();

                final ScoreManager scoreManager = new ScoreManager(screen);
                scoreManager.score(gameState);

            } else {
                if ( config.isMaster() && config.isWizard() ) {
                    screen.showBottomMessageAndWait(
                            String.format("Hello %s, welcome to dungeon #%d", config.getPlayerName(), config.getDungeonSeed()),1
                    );
                } else {
                    screen.showBottomMessageAndWait(
                            String.format("Hello %s, just a moment while I dig the dungeon...", config.getPlayerName()),1
                    );
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
