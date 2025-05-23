package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.system.entity.creature.PlayerFlag;
import com.dungeoncode.javarogue.system.entity.creature.Stats;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;

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
    public static final String DEFAULT_PLAYER_INIT_STATS = "/data/player-init-stats.json";
    private static final int DEFAULT_MAX_STRING_LENGTH = 1024;
    private static final int DEFAULT_MAX_SCROLL_ITEM_GENERATED_NAME_LENGTH = 40;
    private static final String SYSTEM_PROPERTY_USER_NAME = "user.name";
    private static final String SYSTEM_PROPERTY_USER_HOME = "user.home";
    private static final String DEFAULT_JAVAROGUE_DIR_NAME = ".java-rogue";
    private static final String DEFAULT_SAVE_FILE_NAME = "rogue.save";
    private static final String DEFAULT_SCORE_FILE_NAME = "rogue54.scr";
    private static final String DEFAULT_FAVORITE_FRUIT = "slime-mold";
    private static final String DEFAULT_KILL_NAME = "Wally the Wonder Badger";
    private static final int DEFAULT_TERMINAL_ROWS = 24;
    private static final int DEFAULT_TERMINAL_COLS = 80;
    private static final int DEFAULT_NUM_SCORES = 10;
    private static final int DEFAULT_PLAYER_MAX_PACK = 23;
    private static final int DEFAULT_MIN_ARMOR_CLASS = 10;
    private static final boolean DEFAULT_INVENTORY_DESCRIBE = true;
    private static final int DEFAULT_AMULET_LEVEL = 26;
    private static final boolean DEFAULT_STATUS_AS_MESSAGE = false;

    /**
     * The equivalent of HUNGERTIME	1300
     */
    private static final int DEFAULT_PLAYER_STARTING_FOOD = 1300;
    private static final int DEFAULT_MAX_ROOMS = 9;
    private static final int DEFAULT_MAX_GONE_ROOMS = 4;
    private static final int DEFAULT_MAX_PASSAGES = 13;

    private static final boolean DEFAULT_MESSAGE_SAVE = true;
    private static final boolean DEFAULT_MESSAGE_ALLOW_LOWERCASE = false;
    private static final boolean DEFAULT_MESSAGE_ALLOW_ESCAPE = false;
    private static final int DEFAULT_LEVEL_MAX_WIDTH = 80;
    private static final int DEFAULT_LEVEL_MAX_HEIGHT = 32;
    private static final int DEFAULT_LEVEL_LAM_DIST = 3;

    /**
     * Release version, equivalent of:
     * <pre>char *release = "5.4.4"; vers.c</pre>
     */
    private static final String RELEASE_VERSION = "5.4.4";

    /**
     * Equivalent of <code>#define TREAS_ROOM</code> in <code>new_level.c</code>.
     */
    private static final int DEFAULT_TREAS_ROOM_CHANCE = 20;

    /**
     * Equivalent of <code>##define MINTREAS</code> in <code>new_level.c</code>.
     */
    private static final int DEFAULT_MIN_TREAS = 2;

    /**
     * Equivalent of <code>##define MAXTREAS</code> in <code>new_level.c</code>.
     */
    private static final int DEFAULT_MAX_TREAS = 10;

    /**
     * Default max tries to find a valid floor spot in a room.
     * Equivalent of <code>#define MAXTRIES</code> in <code>new_level.c</code>.
     */
    private static final int DEFAULT_MAX_TRIES_FIND_FLOOR = 10;

    /**
     * Default maximum number of tries to place items in a level.
     * Equivalent of <code>#define MAXOBJ</code> in <code>rogue.h</code>.
     */
    private static final int DEFAULT_MAX_OBJ_TRIES = 9;
    private static final int DEFAULT_MAX_TRAPS = 10;

    /**
     * Default duration in turns that player remains confused.
     * Equivalent to <code>#define HUHDURATION</code> in <code>rogue.h</code>.
     */
    private static final int DEFAULT_CONFUSE_DURATION = 20;

    private final int maxStringLength;
    private final String javaRogueDirName;
    private final String scoreFileName;
    private final byte[] encryptionKeyPrimary;
    private final byte[] encryptionKeySecondary;
    private final int numScores;
    private final int userId;
    private final String defaultKillName;

    /**
     * Equivalent of NUMLINES.
     */
    private final int terminalRows;

    /**
     * Equivalent of NUMCOLS.
     */
    private final int terminalCols;

    private final EnumSet<PlayerFlag> initialPlayerFlags;
    private final Stats initialPlayerStats;
    private final boolean messageAllowLowercase;
    private final boolean messageAllowEscape;
    /**
     * Equivalent of MAXCOLS.
     */
    private final int levelMaxWidth;
    /**
     * Equivalent of MAXLINES.
     */
    private final int levelMaxHeight;
    private final int maxPack;
    private final int foodLeft;
    private final String homeDirName;
    private final int maxScrollItemGeneratedNameLength;
    private final int minArmorClass;
    /**
     * Equivalent of <code>#define TREAS_ROOM</code> in <code>new_level.c</code>.
     */
    private final int treasureRoomChance;
    /**
     * Minimum number of treasures in a treasure room.
     * Equivalent of <code>#define MINTREAS</code> in <code>new_level.c</code>.
     */
    private final int minTreasure;
    /**
     * Minimum number of treasures in a treasure room.
     * Equivalent of <code>#define MAXTREAS</code> in <code>new_level.c</code>.
     */
    private final int maxTreasure;
    /**
     * Max tries to find a valid floor spot in a room.
     * Equivalent of <code>#define MAXTRIES</code> in new_level.c.
     */
    private final int maxTriesFindFloor;
    /**
     * Maximum number of tries to place items in a level.
     * Equivalent of <code>#define MAXOBJ</code> in <code>rogue.h</code>.
     */
    private final int maxObjTries;
    /**
     * Max number of traps per level.
     * Equivalent of <code>#define MAXTRAPS</code> in <code>rogue.h</code>.
     */
    private final int maxTraps;
    /**
     * Say which way items are being used.
     * <p>Equivalent of:
     * <pre>bool inv_describe = TRUE;</pre>
     */
    private final boolean inventoryDescribe;
    private final int maxRooms;
    private final int maxGoneRooms;
    private final int maxPassages;
    private final int amuletLevel;
    private final boolean statMsg;
    private final int statLine;
    private final String releaseVersion;
    /**
     * The squared distance threshold for proximity checks,
     * defining the range within which monsters can react to the player (e.g., chasing or visibility).
     * Mirrors the <code>LAMPDIST</code> constant in the C Rogue source (set to 3).
     */
    private final int lampDist;
    /**
     * Duration in turns that player remains confused.
     * Equivalent to <code>#define HUHDURATION</code> in <code>rogue.h</code>.
     */
    private final int confuseDuration;
    private boolean messageSave;
    private boolean master;
    private boolean wizard;
    private boolean terse;
    private boolean flush;
    private boolean seeFloor;
    private boolean passGo;
    private boolean useLegacySeed;
    private boolean tombstone;
    private String playerName;
    private String favoriteFruit;
    private String saveFileName;
    private int dungeonSeed;
    private int seed;
    private int optionsSeed;
    private boolean scoring;
    private boolean allowMultipleScores;

    public Config() {
        this(null);
    }

    public Config(@Nullable final String homeDirName) {
        if (!RogueUtils.isEmpty(homeDirName)) {
            this.homeDirName = homeDirName;
        } else {
            this.homeDirName = System.getProperty(SYSTEM_PROPERTY_USER_HOME);
        }
        this.saveFileName = DEFAULT_SAVE_FILE_NAME;
        this.javaRogueDirName = this.homeDirName + File.separator + DEFAULT_JAVAROGUE_DIR_NAME;
        this.scoreFileName = DEFAULT_SCORE_FILE_NAME;
        this.favoriteFruit = DEFAULT_FAVORITE_FRUIT;
        this.initialPlayerFlags = EnumSet.noneOf(PlayerFlag.class);

        if (this.useLegacySeed) {
            this.dungeonSeed = (int) (System.currentTimeMillis() / 1000L);
        } else {
            final Random random = new Random();
            this.dungeonSeed = random.nextInt();
        }
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
        this.initialPlayerStats = loadInitialPlayerStats();
        this.maxPack = DEFAULT_PLAYER_MAX_PACK;
        this.foodLeft = DEFAULT_PLAYER_STARTING_FOOD;
        this.messageSave = DEFAULT_MESSAGE_SAVE;
        this.messageAllowLowercase = DEFAULT_MESSAGE_ALLOW_LOWERCASE;
        this.messageAllowEscape = DEFAULT_MESSAGE_ALLOW_ESCAPE;
        this.levelMaxWidth = DEFAULT_LEVEL_MAX_WIDTH;
        this.levelMaxHeight = DEFAULT_LEVEL_MAX_HEIGHT;
        this.maxScrollItemGeneratedNameLength = DEFAULT_MAX_SCROLL_ITEM_GENERATED_NAME_LENGTH;
        this.minArmorClass = DEFAULT_MIN_ARMOR_CLASS;
        this.inventoryDescribe = DEFAULT_INVENTORY_DESCRIBE;
        this.maxRooms = DEFAULT_MAX_ROOMS;
        this.maxGoneRooms = DEFAULT_MAX_GONE_ROOMS;
        this.maxPassages = DEFAULT_MAX_PASSAGES;
        this.amuletLevel = DEFAULT_AMULET_LEVEL;
        this.statMsg = DEFAULT_STATUS_AS_MESSAGE;
        this.statLine = this.terminalRows - 1;
        this.lampDist = DEFAULT_LEVEL_LAM_DIST;
        this.releaseVersion = RELEASE_VERSION;
        this.treasureRoomChance = DEFAULT_TREAS_ROOM_CHANCE;
        this.minTreasure = DEFAULT_MIN_TREAS;
        this.maxTreasure = DEFAULT_MAX_TREAS;
        this.maxTriesFindFloor = DEFAULT_MAX_TRIES_FIND_FLOOR;
        this.maxObjTries = DEFAULT_MAX_OBJ_TRIES;
        this.maxTraps = DEFAULT_MAX_TRAPS;
        this.confuseDuration = DEFAULT_CONFUSE_DURATION;
    }

    private Stats loadInitialPlayerStats() {
        try (InputStream in = getClass().getResourceAsStream(DEFAULT_PLAYER_INIT_STATS)) {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(in, Stats.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load initial player stats", e);
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
        this.useLegacySeed = options.useLegacySeed;
        if (this.useLegacySeed) {
            this.dungeonSeed = (int) (System.currentTimeMillis() / 1000L);
            this.seed = dungeonSeed;
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

    public void setMaster(boolean master) {
        this.master = master;
    }

    public boolean isWizard() {
        return wizard;
    }

    /**
     * Enables or disables wizard mode for the player.
     * <p>
     * When enabling wizard mode, the player gains the ability to see monsters immediately.
     * If master mode is also active and an option seed was provided, the dungeon seed
     * and random number generator seed are re-initialized accordingly.
     * <p>
     * This mirrors the behavior of Rogue's original C code in {@code main.c} during wizard setup.
     *
     * @param wizard      true to enable wizard mode, false to disable
     * @param rogueRandom the {@link RogueRandom} instance used for reseeding when needed
     */
    public void setWizard(final boolean wizard, @Nonnull final RogueRandom rogueRandom) {
        Objects.requireNonNull(rogueRandom);

        this.wizard = wizard;
        if (this.wizard) {
            initialPlayerFlags.add(PlayerFlag.SEEMONST);
            if (this.master && this.optionsSeed > 0) {
                // In original Rogue C, when in wizard mode and SEED is set, dungeon seed is overridden
                this.dungeonSeed = this.optionsSeed;
                this.seed = this.dungeonSeed;
                rogueRandom.reseed(this.seed); // Ensure RogueRandom internal seed matches new dungeon seed
            }
        } else {
            initialPlayerFlags.remove(PlayerFlag.SEEMONST);
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

    public Stats getInitialPlayerStats() {
        return initialPlayerStats;
    }

    public String getFavoriteFruit() {
        return favoriteFruit;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public EnumSet<PlayerFlag> getInitialPlayerFlags() {
        return initialPlayerFlags;
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

    public void setScoring(boolean scoring) {
        this.scoring = scoring;
    }

    public String getJavaRogueDirName() {
        return javaRogueDirName;
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

    public boolean isTombstone() {
        return tombstone;
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

    public int getMaxPack() {
        return maxPack;
    }

    public int getFoodLeft() {
        return foodLeft;
    }

    public boolean isMessageSave() {
        return messageSave;
    }

    public void setMessageSave(boolean messageSave) {
        this.messageSave = messageSave;
    }

    public boolean isMessageAllowEscape() {
        return messageAllowEscape;
    }

    public boolean isMessageAllowLowercase() {
        return messageAllowLowercase;
    }

    public int getLevelMaxWidth() {
        return levelMaxWidth;
    }

    public int getLevelMaxHeight() {
        return levelMaxHeight;
    }

    public boolean isSeeFloor() {
        return seeFloor;
    }

    public void setSeeFloor(boolean seeFloor) {
        this.seeFloor = seeFloor;
    }

    public boolean isTerse() {
        return terse;
    }

    public int getMaxScrollItemGeneratedNameLength() {
        return maxScrollItemGeneratedNameLength;
    }

    public int getMinArmorClass() {
        return minArmorClass;
    }

    public boolean isInventoryDescribe() {
        return inventoryDescribe;
    }

    public int getMaxRooms() {
        return maxRooms;
    }

    public int getMaxGoneRooms() {
        return maxGoneRooms;
    }

    public int getMaxPassages() {
        return maxPassages;
    }

    public int getAmuletLevel() {
        return amuletLevel;
    }

    public boolean isStatMsg() {
        return statMsg;
    }

    public int getStatLine() {
        return statLine;
    }

    public int getLampDist() {
        return lampDist;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public int getTreasureRoomChance() {
        return treasureRoomChance;
    }

    public int getMinTreasure() {
        return minTreasure;
    }

    public int getMaxTreasure() {
        return maxTreasure;
    }

    public int getMaxTriesFindFloor() {
        return maxTriesFindFloor;
    }

    public int getMaxObjTries() {
        return maxObjTries;
    }

    public int getMaxTraps() {
        return maxTraps;
    }

    public int getConfuseDuration() {
        return confuseDuration;
    }

}