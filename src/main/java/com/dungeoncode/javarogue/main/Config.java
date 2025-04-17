package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class Config {

    public static final byte[] DEFAULT_ENCRYPTION_KEY_PRIMARY = new byte[]{
            (byte) 0xC0, 'k', '|', '|', (byte) 0xA9, 'Y', '.', '\'', (byte) 0xC5, (byte) 0xD1, (byte) 0x81, '+',
            (byte) 0xBF, '~', 'r', '"', ']', (byte) 0xA0, '_', (byte) 0x93, '=', '1', (byte) 0xE1, ')',
            (byte) 0x92, (byte) 0x8A, (byte) 0xA1, 't', ';', '\t', '$', (byte) 0xB8, (byte) 0xCC, '/', '<', '#',
            (byte) 0x81, (byte) 0xAC
    };
    public static final byte[] DEFAULT_ENCRYPTION_KEY_SECONDARY = new byte[]{
            (byte) 0xED, 'k', 'l', '{', '+', (byte) 0x84, (byte) 0xAD, (byte) 0xCB, 'i', 'd', 'J',
            (byte) 0xF1, (byte) 0x8C, '=', '4', ':', (byte) 0xC9, (byte) 0xB9, (byte) 0xE1, 'w', 'K', '<',
            (byte) 0xCE, (byte) 0xD1, (byte) 0x8D, ',', ',', '7', (byte) 0xB9, '/', 'R', 'k', '%', '\b',
            (byte) 0xCE, '\f', (byte) 0xA6
    };
    private static final int DEFAULT_MAX_STRING_LENGTH = 1024;
    private static final String SYSTEM_PROPERTY_USER_NAME = "user.name";
    private static final String SYSTEM_PROPERTY_USER_HOME = "user.home";
    private static final String DEFAULT_SAVE_FILE_NAME = "rogue.save";
    private static final String DEFAULT_SCORE_FILE_NAME = "rogue54.scr";
    private static final String DEFAULT_FAVORITE_FRUIT = "slime-mold";
    private static final String DEFAULT_KILL_NAME = "Wally the Wonder Badger";
    private static final int DEFAULT_TERMINAL_ROWS = 24;
    private static final int DEFAULT_TERMINAL_COLS = 80;
    private static final int DEFAULT_NUM_SCORES = 10;

    private boolean master;
    private boolean wizard;
    private final int maxStringLength;
    private boolean terse;
    private boolean flush;
    private boolean seeFloor;
    private boolean passGo;
    private boolean tombstone;
    private String playerName;
    private String favoriteFruit;
    private String homeDirName;
    private String saveFileName;
    private final String scoreFileName;
    private int dungeonSeed;
    private int seed;
    private int optionsSeed;
    private boolean scoring;
    private final byte[] encryptionKeyPrimary;
    private final byte[] encryptionKeySecondary;
    private final int numScores;
    private final int userId;
    private boolean allowMultipleScores;
    private final String defaultKillName;
    private final int terminalRows;
    private final int terminalCols;
    private final EnumSet<PlayerStatus> initialPlayerStatusFlags;

    public Config() {
        this.homeDirName = System.getProperty(SYSTEM_PROPERTY_USER_HOME);
        this.saveFileName = DEFAULT_SAVE_FILE_NAME;
        this.scoreFileName = DEFAULT_SCORE_FILE_NAME;
        this.favoriteFruit = DEFAULT_FAVORITE_FRUIT;
        this.initialPlayerStatusFlags = EnumSet.noneOf(PlayerStatus.class);
        this.dungeonSeed = (int) (System.currentTimeMillis() / 1000L);
        this.seed = this.dungeonSeed;
        this.encryptionKeyPrimary = DEFAULT_ENCRYPTION_KEY_PRIMARY;
        this.encryptionKeySecondary = DEFAULT_ENCRYPTION_KEY_SECONDARY;
        this.numScores = DEFAULT_NUM_SCORES;
        this.maxStringLength = DEFAULT_MAX_STRING_LENGTH;
        this.userId = (playerName + ":" + homeDirName).hashCode();
        this.defaultKillName = DEFAULT_KILL_NAME;
        this.terminalRows = DEFAULT_TERMINAL_ROWS;
        this.terminalCols = DEFAULT_TERMINAL_COLS;
        setPlayerName(System.getProperty(SYSTEM_PROPERTY_USER_NAME));

    }

    public Config(@Nullable final String homeDirName) {
        this();
        if (!RogueUtils.isEmpty(homeDirName)) {
            this.homeDirName = homeDirName;
        }
    }

    public void applyOptions(@Nonnull Options options) {
        Objects.requireNonNull(options);
        this.master = options.master;
        this.terse = options.terse;
        this.flush = options.flush;
        this.seeFloor = options.seeFloor;
        this.passGo = options.passGo;
        this.tombstone = options.tombstone;
        this.allowMultipleScores = options.allowMultipleScores;
        if (options.name != null && !options.name.isBlank()) {
            setPlayerName(options.name);
        }
        if (options.fruit != null && !options.fruit.isBlank()) {
            this.favoriteFruit = options.fruit;
        }
        if (options.file != null && !options.file.isBlank()) {
            this.saveFileName = options.file;
        }
        if (options.seed != null && options.seed > 0) {
            this.optionsSeed = options.seed;
        } else {
            this.optionsSeed = 0;
        }
        this.scoring = !options.showScores;
    }

    public boolean isMaster() {
        return master;
    }

    public boolean isWizard() {
        return wizard;
    }

    public void setWizard(final boolean wizard) {
        this.wizard = wizard;
        if (this.wizard) {
            initialPlayerStatusFlags.add(PlayerStatus.CAN_SEE_MONSTERS);
            if (this.master && this.optionsSeed > 0) {
                this.dungeonSeed = this.optionsSeed;
                this.seed = this.dungeonSeed;
            }
        } else {
            initialPlayerStatusFlags.remove(PlayerStatus.CAN_SEE_MONSTERS);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    private void setPlayerName(@Nullable final String playerName) {
        if (!RogueUtils.isEmpty(playerName)) {
            this.playerName = RogueUtils.limitLength(playerName, maxStringLength);
        }
    }

    public String getFavoriteFruit() {
        return favoriteFruit;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public EnumSet<PlayerStatus> getInitialPlayerStatusFlags() {
        return initialPlayerStatusFlags;
    }

    public int getOptionsSeed() {
        return optionsSeed;
    }

    public int getDungeonSeed() {
        return dungeonSeed;
    }

    public int getSeed() {
        return seed;
    }

    public boolean isScoring() {
        return scoring;
    }

    public String getHomeDirName() {
        return homeDirName;
    }

    public String getScoreFileName() {
        return scoreFileName;
    }

    public byte[] getEncryptionKeyPrimary() {
        return encryptionKeyPrimary;
    }

    public byte[] getEncryptionKeySecondary() {
        return encryptionKeySecondary;
    }

    public int getNumScores() {
        return numScores;
    }

    public int getMaxStringLength() {
        return maxStringLength;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isAllowMultipleScores() {
        return allowMultipleScores;
    }

    public String getDefaultKillName() {
        return defaultKillName;
    }

    public int getTerminalRows() {
        return terminalRows;
    }

    public int getTerminalCols() {
        return terminalCols;
    }

    //    private static final Stats INITIAL_PLAYER_STATS = new Stats(
//            16,
//            0,
//            1,
//            10,
//            12,
//            "1x4",
//            12);
//
//    private static final int DEFAULT_MAXIMUM_STRING_LENGTH = 1024;
//
//    private boolean debug;
//    private boolean verbose;
//    private int seed;
//    private int maximumStringLength;

//    private final File homeDirectory = new File(System.getProperty("user.home"));
//    private boolean doorStop = false;        // Stop running at doors
//    private String saveFilename = null;     // File name for saving the game
//    private boolean fightFlush = false;      // Flush input during combat
//    private boolean inventoryDescribe = true; // Describe inventory actions
//    private boolean jump = false;            // Show movement as jumps
//    private boolean lowerMessages = false;   // Use lowercase for messages
//    private boolean messageEscape = false;   // Allow escape key to cancel messages
//    private boolean passGo = false;          // Follow passages without stopping
//    private boolean saveMessage = true;      // Save last message for repeat
//    private boolean seeFloor = true;         // Show floor tiles in lit areas
//    private boolean terse = false;           // Use brief messages
//    private boolean tombstone = true;        // Show tombstone on player death
//    private boolean wizard = false;          // Allows wizard commands


//    public Config() {
//
//    }
//
//    public Config(final boolean debug, final boolean verbose, final int seed, final int maximumStringLength) {
//        this.debug = debug;
//        this.verbose = verbose;
//        this.seed = seed;
//        this.maximumStringLength = maximumStringLength > 0 ? maximumStringLength : DEFAULT_MAXIMUM_STRING_LENGTH;
//    }
//
//    public Config(final boolean debug, final boolean verbose, final int seed ) {
//        this( debug, verbose, seed, DEFAULT_MAXIMUM_STRING_LENGTH );
//    }
//
//    public Stats createInitialPlayerStats() {
//        return new Stats(INITIAL_PLAYER_STATS);
//    }
//
//    public boolean isVerbose() {
//        return verbose;
//    }
//
//    public int getMaxInventorySize() {
//        return 23;
//    }

//    public boolean isDebug() {
//        return debug;
//    }


//
//    public int getSeed() {
//        return seed;
//    }
//
//    public int getMaxStrLength() {
//        return 1024;
//    }
//
//    public int getMaxRows() {
//        return 32;
//    }
//
//    public int getMaxColumns() {
//        return 80;
//    }
//
//    public int getMaxRooms() {
//        return 9;
//    }
//
//    public int getMaxThings() {
//        return 9;
//    }
//
//    public int getMaxObjects() {
//        return 9;
//    }
//

//
//    public int getMaxTraps() {
//        return 10;
//    }
//
//    public int getAmuletLevel() {
//        return 26;
//    }
//
//    public int getNumThings() {
//        return 7;
//    }
//
//    public int getMaxPassages() {
//        return 13;
//    }
//
//    public int getBoreLevel() {
//        return 50;
//    }
//
//    public int getBearTime() {
//        return 3;
//    }
//
//    public int getSleepTime() {
//        return 5;
//    }
//
//    public int getHoldTime() {
//        return 2;
//    }
//
//    public int getWanderTime() {
//        return 70;
//    }
//
//    public int getBeforeTime() {
//        return 1;
//    }
//
//    public int getAfterTime() {
//        return 2;
//    }
//
//    public int getHealTime() {
//        return 30;
//    }
//
//    public int getHuhDuration() {
//        return 20;
//    }
//
//    public int getSeeDuration() {
//        return 850;
//    }
//
//    public int getHungerTime() {
//        return 1300;
//    }
//
//    public int getMoreTime() {
//        return 150;
//    }
//
//    public int getStomachSize() {
//        return 2000;
//    }
//
//    public int getStarveTime() {
//        return 850;
//    }
//
//    public int getBoltLength() {
//        return 6;
//    }
//
//    public int getLampDistance() {
//        return 3;
//    }
//
//    public int getRandomBearTime() {
//        return RogueUtils.spread(getBearTime());
//    }
//
//    public int getRandomSleepTime() {
//        return RogueUtils.spread(getSleepTime());
//    }
//
//    public int getRandomHoldTime() {
//        return RogueUtils.spread(getHoldTime());
//    }
//
//    public int getRandomWanderTime() {
//        return RogueUtils.spread(getWanderTime());
//    }
//
//    public int getRandomBeforeTime() {
//        return RogueUtils.spread(getBeforeTime());
//    }
//
//    public int getRandomAfterTime() {
//        return RogueUtils.spread(getAfterTime());
//    }
//
//    public String getFavoriteFruit() {
//        return "slime-mold";
//    }
//
//    public int getScreenRows() {
//        return 24;
//    }
//
//    public int getScreenCols() {
//        return 80;
//    }
//
//    public int getScreenStatusLine() {
//        return getScreenRows() - 1;
//    }
//
//    /**
//     * Checks if running stops at doors.
//     *
//     * @return true if stopping at doors
//     */
//    public boolean isDoorStop() {
//        return doorStop;
//    }
//
//    /**
//     * Sets whether running stops at doors.
//     *
//     * @param doorStop the new setting
//     */
//    public void setDoorStop(boolean doorStop) {
//        this.doorStop = doorStop;
//    }
//
//    /**
//     * Checks if input is flushed during combat.
//     *
//     * @return true if flushing input
//     */
//    public boolean isFightFlush() {
//        return fightFlush;
//    }
//
//    /**
//     * Sets whether input is flushed during combat.
//     *
//     * @param fightFlush the new setting
//     */
//    public void setFightFlush(boolean fightFlush) {
//        this.fightFlush = fightFlush;
//    }
//
//    /**
//     * Checks if inventory actions are described.
//     *
//     * @return true if describing actions
//     */
//    public boolean isInventoryDescribe() {
//        return inventoryDescribe;
//    }
//
//    /**
//     * Sets whether inventory actions are described.
//     *
//     * @param inventoryDescribe the new setting
//     */
//    public void setInventoryDescribe(boolean inventoryDescribe) {
//        this.inventoryDescribe = inventoryDescribe;
//    }
//
//    /**
//     * Checks if movement is shown as jumps.
//     *
//     * @return true if showing jumps
//     */
//    public boolean isJump() {
//        return jump;
//    }
//
//    /**
//     * Sets whether movement is shown as jumps.
//     *
//     * @param jump the new setting
//     */
//    public void setJump(boolean jump) {
//        this.jump = jump;
//    }
//
//    /**
//     * Checks if messages use lowercase.
//     *
//     * @return true if using lowercase
//     */
//    public boolean isLowerMessages() {
//        return lowerMessages;
//    }
//
//    /**
//     * Sets whether messages use lowercase.
//     *
//     * @param lowerMessages the new setting
//     */
//    public void setLowerMessages(boolean lowerMessages) {
//        this.lowerMessages = lowerMessages;
//    }
//
//    /**
//     * Checks if escape key cancels messages.
//     *
//     * @return true if escape cancels messages
//     */
//    public boolean isMessageEscape() {
//        return messageEscape;
//    }
//
//    /**
//     * Sets whether escape key cancels messages.
//     *
//     * @param messageEscape the new setting
//     */
//    public void setMessageEscape(boolean messageEscape) {
//        this.messageEscape = messageEscape;
//    }
//
//    /**
//     * Checks if passages are followed without stopping.
//     *
//     * @return true if following passages
//     */
//    public boolean isPassGo() {
//        return passGo;
//    }
//
//    /**
//     * Sets whether passages are followed without stopping.
//     *
//     * @param passGo the new setting
//     */
//    public void setPassGo(boolean passGo) {
//        this.passGo = passGo;
//    }
//
//    /**
//     * Checks if the last message is saved for repeat.
//     *
//     * @return true if saving messages
//     */
//    public boolean isSaveMessage() {
//        return saveMessage;
//    }
//
//    /**
//     * Sets whether the last message is saved for repeat.
//     *
//     * @param saveMessage the new setting
//     */
//    public void setSaveMessage(boolean saveMessage) {
//        this.saveMessage = saveMessage;
//    }
//
//    /**
//     * Checks if floor tiles are shown in lit areas.
//     *
//     * @return true if showing floor tiles
//     */
//    public boolean isSeeFloor() {
//        return seeFloor;
//    }
//
//    /**
//     * Sets whether floor tiles are shown in lit areas.
//     *
//     * @param seeFloor the new setting
//     */
//    public void setSeeFloor(boolean seeFloor) {
//        this.seeFloor = seeFloor;
//    }
//
//    /**
//     * Checks if brief messages are used.
//     *
//     * @return true if using brief messages
//     */
//    public boolean isTerse() {
//        return terse;
//    }
//
//    /**
//     * Sets whether brief messages are used.
//     *
//     * @param terse the new setting
//     */
//    public void setTerse(boolean terse) {
//        this.terse = terse;
//    }
//
//    /**
//     * Checks if a tombstone is shown on player death.
//     *
//     * @return true if showing tombstone
//     */
//    public boolean isTombstone() {
//        return tombstone;
//    }
//
//    /**
//     * Sets whether a tombstone is shown on player death.
//     *
//     * @param tombstone the new setting
//     */
//    public void setTombstone(boolean tombstone) {
//        this.tombstone = tombstone;
//    }
//
//    /**
//     * Checks if wizard commands are allowed.
//     *
//     * @return true if wizard mode is enabled
//     */
//    public boolean isWizard() {
//        return wizard;
//    }
//
//    /**
//     * Sets whether wizard commands are allowed.
//     *
//     * @param wizard the new setting
//     */
//    public void setWizard(boolean wizard) {
//        this.wizard = wizard;
//    }
//
//    /**
//     * Gets the filename for saving the game.
//     *
//     * @return the save filename
//     */
//    public String getSaveFilename() {
//        return saveFilename;
//    }
//
//    /**
//     * Sets the filename for saving the game.
//     *
//     * @param saveFilename the new save filename
//     */
//    public void setSaveFilename(String saveFilename) {
//        this.saveFilename = saveFilename;
//    }
//
//    public File getHomeDirectory() {
//        return homeDirectory;
//    }
}