package com.dungeoncode.javarogue.entity.creature;

import com.dungeoncode.javarogue.config.Config;
import com.dungeoncode.javarogue.entity.item.Inventory;
import com.dungeoncode.javarogue.entity.item.armor.Armor;
import com.dungeoncode.javarogue.entity.item.ring.Ring;
import com.dungeoncode.javarogue.entity.item.weapon.Weapon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Represents the player character in the game.
 */
public class Player extends Creature {

    public final static String[] STATUS_HUNGER_NAMES = {"", "Hungry", "Weak", "Faint"};

    private final String playerName;
    private final EnumSet<PlayerFlag> playerFlags;
    private final int foodLeft;
    private final Inventory inventory;
    private final Stats maxStats;
    private Armor currentArmor;
    private Weapon currentWeapon;
    private Ring leftRing;
    private Ring rightRing;
    private int goldAmount;
    private HungryState hungryState;
    private int currentLevel;

    public Player(@Nonnull final Config config) {
        super();
        Objects.requireNonNull(config);
        this.setStats(config.getInitialPlayerStats());
        this.maxStats = config.getInitialPlayerStats();
        this.playerFlags = EnumSet.copyOf(config.getInitialPlayerFlags());
        this.playerName = config.getPlayerName();
        this.foodLeft = config.getFoodLeft();
        this.inventory = new Inventory(config.getMaxPack());
    }

    /**
     * Generates a status line string displaying the player's key statistics, mirroring the C Rogue <code>status()</code> function.
     * Includes level, gold, HP, strength, armor, experience, and hunger state.
     *
     * @return A formatted string representing the player's status line.
     */
    public String status() {
        final StringBuilder st = new StringBuilder();
        final Stats stats = getStats();
        final int clevel = currentLevel;
        int maxHp = stats.getMaxHitPoints();
        int hp = stats.getHitPoints();
        int level = getStats().getLevel();
        int gold = goldAmount;
        int exp = stats.getExperience();
        int str = stats.getStrength();
        int maxStr = maxStats.getStrength();
        int armor = currentArmor != null ? currentArmor.getArmorClass() : stats.getArmor();
        final String hungerState = hungryState == null ? "" : STATUS_HUNGER_NAMES[hungryState.id];

        // Calculate HP width for formatting
        final int hpWidth = String.valueOf(maxHp).length();

        // Format status line
        st.append(String.format("Level: %d  Gold: %-5d  Hp: %-" + hpWidth + "d(%-" + hpWidth + "d)  Str: %2d(%d)  Arm: %-2d  Exp: %d/%d  %s",
                clevel, gold, hp, maxHp, str, maxStr, 10 - armor, level, exp, hungerState));

        return st.toString().trim();
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

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public HungryState getHungryState() {
        return hungryState;
    }

    public void setHungryState(HungryState hungryState) {
        this.hungryState = hungryState;
    }

    public enum HungryState {
        HUNGRY(1),
        WEAK(2),
        FAINT(3);

        private final int id;

        HungryState(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

}

