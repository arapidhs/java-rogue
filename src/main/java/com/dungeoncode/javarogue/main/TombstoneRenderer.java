package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.TerminalPosition;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TombstoneRenderer {

    private static final List<String> TOMBSTONE_ART = List.of(
            "                       __________",
            "                      /          \\",
            "                     /    REST    \\",
            "                    /      IN      \\",
            "                   /     PEACE      \\",
            "                  /                  \\",
            "                  |                  |",
            "                  |                  |",
            "                  |   killed by a    |",
            "                  |                  |",
            "                  |       1980       |",
            "                 *|     *  *  *      | *",
            "         ________)/\\\\_//(\\/(/\\)/\\//\\/|_)_______"
    );

    private final RogueScreen screen;
    private final GameState gameState;

    public TombstoneRenderer(@Nonnull final RogueScreen screen, @Nonnull final GameState gameState) {
        Objects.requireNonNull(screen);
        Objects.requireNonNull(gameState);
        this.screen = screen;
        this.gameState = gameState;
    }

    public void renderTombstone() throws IOException {

        screen.clear();

        // Print tombstone ASCII art starting at row 8
        for (int i = 0; i < TOMBSTONE_ART.size(); i++) {
            screen.putString(0,8+i,TOMBSTONE_ART.get(i));
        }

        final String killerName = gameState.getDeathSource().getName();
        screen.putString(center(killerName),17,killerName);

        final boolean isKillType = gameState.getDeathSource().isTemplate() &&
                gameState.getDeathSource().getType().equals(DeathSource.Type.KILL_TYPE);

        boolean isUseArticle = true;

        if ( isKillType ) {
            final KillTypeTemplate killTypeTemplate = Templates.getTemplate(KillTypeTemplate.class, gameState.getDeathSource().getTemplateId());
            isUseArticle = killTypeTemplate != null ? killTypeTemplate.isUseArticle() : isUseArticle;
        }

        if ( isUseArticle ) {
            screen.putString(32,16," ");
        } else {
            screen.putString(33,16,RogueUtils.getIndefiniteArticleFor(killerName));
        }

        final String playerName = gameState.getConfig().getPlayerName();
        screen.putString(center(playerName),14,playerName);

        // Print gold (row 15, centered)
        final String goldText = String.format("%d Au", gameState.getGoldAmount());
        screen.putString(center(goldText),15,goldText);

        // Print year (row 18, column 26)
        final String year = String.valueOf(LocalDate.now().getYear());
        screen.putString(26,18,year);

        screen.refresh();
    }

    private static int center(@Nonnull String text) {
        return 28 - ((text.length() + 1) / 2);
    }

}
