package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Represents the player character in the game.
 */
public class Player extends Creature {

    private final Config config;
    private final String playerName;
    private final EnumSet<PlayerFlag> playerFlags;
    private final int foodLeft;
    private final Inventory inventory;
    private Armor currentArmor;
    private Weapon currentWeapon;

    public Player(@Nonnull final Config config) {
        Objects.requireNonNull(config);
        this.config = config;
        this.setStats(this.config.getInitialPlayerStats());
        this.playerFlags = EnumSet.copyOf(this.config.getInitialPlayerFlags());
        this.playerName = this.config.getPlayerName();
        this.foodLeft = this.config.getFoodLeft();
        this.inventory = new Inventory(config.getMaxPack());
    }

    public boolean hasFlag(@Nonnull final PlayerFlag playerFlag) {
        return playerFlags.contains(playerFlag);
    }

    public String getPlayerName() {
        return playerName;
    }

    public EnumSet<PlayerFlag> getPlayerFlags() {
        return playerFlags;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getFoodLeft() {
        return foodLeft;
    }

    public Armor getCurrentArmor() {
        return currentArmor;
    }

    public void setCurrentArmor(@Nullable final Armor currentArmor) {
        this.currentArmor = currentArmor;
    }

    public void setCurrentWeapon(@Nullable final Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

}

