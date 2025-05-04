package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Default game initializer. Responsible for setting up the initial game state.
 */
public class DefaultInitializer implements Initializer {

    @Override
    public void initialize(@Nonnull GameState gameState) {

        Objects.requireNonNull(gameState);

        final Player player = new Player(gameState.getConfig());
        gameState.setPlayer(player);

        final Food food = new Food();

        //TODO complete DefaultInitializer
    }

}
