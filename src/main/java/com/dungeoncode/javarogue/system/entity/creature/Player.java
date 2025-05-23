package com.dungeoncode.javarogue.system.entity.creature;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.system.entity.item.*;

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

    /**
     * Amount of food in hero's stomach.
     * Equivalent of <pre>int food_left; extern.c</pre>
     */
    private final int foodLeft;


    private final Stats maxStats;
    private Armor currentArmor;
    private Weapon currentWeapon;
    private Ring leftRing;
    private Ring rightRing;
    private int goldAmount;
    private HungryState hungryState;
    private int currentLevel;

    /**
     * If true Next move shouldn't pick up items.
     * Equivalent to <code>bool move_on = FALSE;</code> in <code>extern.c</code>.
     */
    private boolean moveOn;

    /**
     * Number of times the player can act (execute a command) in a turn.
     * Equivalent of <pre>register int ntimes = 1; Number of player moves</pre>
     * from the original Rogue source code.
     */
    private int ntimes;

    /**
     * True if player is running.
     * <p>
     * Equivalent to <code>bool running = FALSE;</code> in <code>extern.c</code>.
     */
    private boolean running;

    /**
     * Show running as series of jumps.
     * <p>
     * Equivalent to <code>bool jump = FALSE;</code> in <code>extern.c</code>.
     */
    private boolean jump;

    public Player(@Nonnull final Config config) {
        super();
        Objects.requireNonNull(config);
        this.setStats(new Stats(config.getInitialPlayerStats()));
        this.maxStats = new Stats(config.getInitialPlayerStats());
        this.playerFlags = EnumSet.copyOf(config.getInitialPlayerFlags());
        this.playerName = config.getPlayerName();
        this.foodLeft = config.getFoodLeft();
        setInventory(new Inventory(config.getMaxPack()));
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

    /**
     * Checks if the player is wearing a ring of the specified {@link RingType} on either the left or right hand.
     * <p>
     * Equivalent to the <code>ISWEARING</code> macro in the C Rogue source, checking if either hand’s ring
     * matches the given type.
     * </p>
     *
     * @param ringType The {@link RingType} to check for.
     * @return True if the player is wearing a ring of the specified type, false otherwise.
     * @throws NullPointerException if ringType is null.
     */
    public boolean isWearing(@Nonnull RingType ringType) {
        Objects.requireNonNull(ringType);
        if (getRightRing() != null && getRightRing().getItemSubType().equals(ringType)) {
            return true;
        }
        return getLeftRing() != null && getLeftRing().getItemSubType().equals(ringType);
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

    public void addFlag(@Nonnull final PlayerFlag playerFlag) {
        Objects.requireNonNull(playerFlag);
        playerFlags.add(playerFlag);
    }

    public void removeFlag(@Nonnull final PlayerFlag playerFlag) {
        Objects.requireNonNull(playerFlag);
        playerFlags.remove(playerFlag);
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

    public int getFoodLeft() {
        return foodLeft;
    }

    public Armor getCurrentArmor() {
        return currentArmor;
    }

    public void setCurrentArmor(@Nullable final Armor currentArmor) {
        this.currentArmor = currentArmor;
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

    public void setHungryState(HungryState hungryState) {
        this.hungryState = hungryState;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public int getNtimes() {
        return ntimes;
    }

    public void setNtimes(int ntimes) {
        this.ntimes = ntimes;
    }

    /**
     * If true Next move shouldn't pick up items.
     */
    public boolean isMoveOn() {
        return moveOn;
    }

    public void setMoveOn(boolean moveOn) {
        this.moveOn = moveOn;
    }

    /**
     * @return true if player is running.
     */
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return true if player is jumping during running.
     */
    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
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

