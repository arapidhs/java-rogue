package com.dungeoncode.javarogue.system.initializer;

import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.death.GameEndReason;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Provides a strategy to initialize a {@link GameState} for death simulation mode.
 *
 * <p>
 * This class is used when the {@code -simulateDeath} command-line option is active.
 * </p>
 */
public class DeathSimulationInitializer implements Initializer {

    /**
     * Initializes the given {@link GameState} for death simulation.
     *
     * <p>
     * This replicates the behavior triggered by the {@code -d} option
     * in the original Rogue C source ({@code main.c}).
     * </p>
     *
     * <ul>
     *     <li>Randomizes the player's gold amount between 1 and 100.</li>
     *     <li>Randomizes the dungeon level between 1 and 100.</li>
     *     <li>Selects a random death cause name from monsters, kill types, or a default fallback.</li>
     *     <li>Sets the game end reason to {@link GameEndReason#KILLED}.</li>
     * </ul>
     *
     * @param gameState the non-null game state to initialize
     * @throws NullPointerException if {@code gameState} is {@code null}
     */
    @Override
    public void initialize(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final RogueRandom rogueRandom = gameState.getRogueRandom();

        gameState.setGameEndReason(GameEndReason.KILLED);
        gameState.setGoldAmount(rogueRandom.rnd(100) + 1);
        gameState.setLevelNum(rogueRandom.rnd(100) + 1);

        final String defaultKillName = gameState.getConfig().getDefaultKillName();
        gameState.setDeathSource(rogueRandom.selectRandomDeathSource(defaultKillName));
        gameState.death();
    }
}
