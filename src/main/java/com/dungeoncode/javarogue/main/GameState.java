package com.dungeoncode.javarogue.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class GameState {

    private final Config config;
    private RogueRandom rogueRandom;
    private final Initializer initializer;
    private GameEndReason gameEndReason;
    private DeathSource deathSource;
    private int maxLevel;
    private int level;
    private int goldAmount;

    public GameState(@Nonnull final Config config,@Nonnull final RogueRandom rogueRandom, final @Nullable Initializer initializer) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(rogueRandom);
        this.config = config;
        this.rogueRandom = rogueRandom;
        this.initializer = initializer;

        init();
    }

    public void init() {
        if ( this.initializer != null ) {
            this.initializer.initialize(this);
        }
    }

    public void death() {
        this.goldAmount -= this.goldAmount / 10;
    }

    public Config getConfig() {
        return config;
    }

    public RogueRandom getRogueRandom() {
        return rogueRandom;
    }

    public GameEndReason getGameEndReason() {
        return gameEndReason;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public DeathSource getDeathSource() {
        return deathSource;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public void setGoldAmount(final int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public void setGameEndReason(final GameEndReason gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    public void setDeathSource(final DeathSource deathSource) {
        this.deathSource = deathSource;
    }

}