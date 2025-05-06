package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Represents the player character in the game.
 */
public class Player extends Creature {

    private final String playerName;
    private final EnumSet<PlayerFlag> playerFlags;
    private final int foodLeft;
    private final Inventory inventory;
    private Armor currentArmor;
    private Weapon currentWeapon;
    private Ring leftRing;
    private Ring rightRing;

    public Player(@Nonnull final Config config) {
        super();
        Objects.requireNonNull(config);
        this.setStats(config.getInitialPlayerStats());
        this.playerFlags = EnumSet.copyOf(config.getInitialPlayerFlags());
        this.playerName = config.getPlayerName();
        this.foodLeft = config.getFoodLeft();
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

    public Ring getRightRing() {
        return rightRing;
    }

    public void setRightRing(final Ring rightRing) {
        this.rightRing = rightRing;
    }

    public Ring getLeftRing() {
        return leftRing;
    }

    public void setLeftRing(final Ring leftRing) {
        this.leftRing = leftRing;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(@Nullable final Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }
}

