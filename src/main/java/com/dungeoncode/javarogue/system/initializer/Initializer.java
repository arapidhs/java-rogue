package com.dungeoncode.javarogue.system.initializer;

import com.dungeoncode.javarogue.core.GameState;

import javax.annotation.Nonnull;

/**
 * Defines a strategy for initializing a {@link GameState}.
 *
 * <p>
 * Different implementations can configure the game state for normal gameplay,
 * death simulation, or other special modes.
 * </p>
 */
public interface Initializer {

    /**
     * Initializes the given {@link GameState} according to a specific strategy.
     *
     * @param gameState the non-null game state to initialize
     */
    void initialize(@Nonnull GameState gameState);
}
