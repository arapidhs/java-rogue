package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.dungeoncode.javarogue.main.Messages.*;

public class ScoreManager {

    private static final int FLAG_SHOW_NAMES = 1;
    private static final int FLAG_DELETE = 2;

    private static final String STRING_INPUT_NAMES = "names";
    private static final String STRING_INPUT_EDIT = "edit";

    private final RogueScreen screen;

    public ScoreManager(@Nonnull final RogueScreen screen) {
        Objects.requireNonNull(screen);
        this.screen = screen;
    }

    /**
     * Updates and displays the high score table, optionally inserting the current game's score.
     * <p>
     * This is a faithful Java port of the `score()` function from `rip.c` in the original Rogue source.
     * It handles the logic for:
     * <ul>
     *     <li>Displaying the top score list</li>
     *     <li>Inserting a new score entry if it qualifies</li>
     *     <li>Supporting administrative options: showing real user IDs, and entry deletion</li>
     * </ul>
     * <p>
     * Adaptations in Java:
     * <ul>
     *     <li>Uses `List<ScoreEntry>` instead of a statically allocated array (`SCORE *top_ten[]`).</li>
     *     <li>Replaces `fgets()`-based input with `RogueScreen.showBottomMessageAndWait()`.</li>
     *     <li>Maps monster names and kill-type descriptors using JSON-based templates, replacing hardcoded monster arrays and `killname()` logic.</li>
     *     <li>Highlights newly inserted entries using `SGR.BOLD`, as a counterpart to `standout()` in curses.</li>
     * </ul>
     *
     * @param state The current game state (null if we're just displaying scores, e.g., from the main menu).
     *              If non-null, it is considered for insertion into the score table.
     * @throws IOException If reading input or writing the score file fails.
     */
    public void score(@Nullable final GameState state) throws IOException {

        final GameEndReason gameEndReason = state == null ? null : state.getGameEndReason();

        int flag = 0; // This maps to the `prflags` in the C code: 1 = show real user ID, 2 = edit mode (deletion).

        // Prompt for "[Press return to continue]" and optionally accept commands like "names" or "edit" (admin-only).
        String input;
        if (gameEndReason != null || (getConfig().isMaster() && getConfig().isWizard())) {
            screen.putString(0, screen.getRows() - 1, PROMPT_PRESS_RETURN_CONTINUE);
            screen.refresh();
            input = screen.readString();
            if (getConfig().isMaster() && getConfig().isWizard()) {
                if (STRING_INPUT_NAMES.equalsIgnoreCase(input)) {
                    flag = FLAG_SHOW_NAMES;
                } else if (STRING_INPUT_EDIT.equalsIgnoreCase(input)) {
                    flag = FLAG_DELETE;
                }
            }
            screen.clearAndRefresh();
        }

        // Load the existing score entries from disk (equivalent to rd_score())
        List<ScoreEntry> scoreEntries = readScoreFile();

        // If a new score is available and scoring is enabled in config, we try to insert it
        if (state != null && getConfig().isScoring()) {

            final boolean isHighScore = scoreEntries.size() < getConfig().getNumScores()
                    || state.getGoldAmount() > scoreEntries.get(scoreEntries.size() - 1).score;

            if (isHighScore) {

                final int userId = getConfig().getUserId();
                final long monsterId = state.getDeathSource().getType().equals(DeathSource.Type.MONSTER) ? state.getDeathSource().getTemplateId() : 0;
                final long killTypeId = state.getDeathSource().getType().equals(DeathSource.Type.KILL_TYPE) ? state.getDeathSource().getTemplateId() : 0;

                // Construct new score entry
                final ScoreEntry newEntry = new ScoreEntry(
                        userId,
                        state.getGoldAmount(),
                        state.getGameEndReason(),
                        monsterId,
                        killTypeId,
                        state.getGameEndReason() == GameEndReason.WIN ? state.getMaxLevel() : state.getLevel(),
                        Instant.now().getEpochSecond(),
                        getConfig().getPlayerName()
                );
                newEntry.isNew = true; // This flag is used to highlight the entry (like standout in curses)

                // Always add WIN entries unconditionally, sort descending,
                // and keep only the top scores
                if (newEntry.gameEndReason == GameEndReason.WIN || getConfig().isAllowMultipleScores()) {
                    scoreEntries.add(newEntry);
                } else {

                    final boolean hasConflictingNonWinEntry = scoreEntries.stream()
                            .anyMatch(entry ->
                                    entry.userId == userId
                                            && !getConfig().isAllowMultipleScores()
                                            && entry.gameEndReason != GameEndReason.WIN
                                            && entry.score <= newEntry.score
                            );

                    if (hasConflictingNonWinEntry) {
                        final List<ScoreEntry> scoreEntriesCopy = new ArrayList<>(scoreEntries);
                        final OptionalInt conflictIndex = IntStream.range(0, scoreEntriesCopy.size())
                                .filter(i -> scoreEntriesCopy.get(i).userId == userId
                                        && scoreEntriesCopy.get(i).gameEndReason != GameEndReason.WIN
                                        && scoreEntriesCopy.get(i).score <= newEntry.score)
                                .findFirst();


                        if (conflictIndex.isPresent()) {
                            // Add newEntry at index, shifting elements from index to end down
                            scoreEntries.add(conflictIndex.getAsInt(), newEntry);

                            // Remove the last entry to maintain list size
                            if (scoreEntries.size() > getConfig().getNumScores()) {
                                scoreEntries.remove(scoreEntries.size() - 1);
                            }
                        }

                    }

                }

                scoreEntries = scoreEntries.stream()
                        .sorted(Comparator.comparingInt(ScoreEntry::getScore).reversed())
                        .limit(getConfig().getNumScores())
                        .collect(Collectors.toList());

            }
        }

        if (gameEndReason != null) {
            screen.clearAndRefresh();
        }

        // Display the title and table header
        int y = 0;
        final String title = String.format(MSG_TOP + " %s %s:",
                RogueUtils.numberToWord(getConfig().getNumScores()),
                getConfig().isAllowMultipleScores() ? MSG_SCORES : MSG_ROGUEISTS);
        screen.putString(0, y++, title);
        screen.putString(0, y++, "   " + MSG_SCORE_NAME);
        screen.refresh();

        int index = 1;
        for (ScoreEntry entry : scoreEntries) {
            if (entry.score == 0) break;

            String base = String.format("%2d %5d %s: %s " + MSG_ON_LEVEL + " %d",
                    index++, entry.score, entry.name, entry.gameEndReason.getDisplayName(), entry.level);

            /*
             * This replaces the call to killname() in C. We attempt to resolve the name
             * of the monster (or kill type) and prefix it with "a"/"an" appropriately.
             */
            if (entry.gameEndReason == GameEndReason.KILLED || entry.gameEndReason == GameEndReason.KILLED_WITH_AMULET) {
                String killName = null;
                boolean isUseArticle = true;

                if (entry.monsterId > 0) {
                    final MonsterTemplate monster = Templates.getTemplate(MonsterTemplate.class, entry.monsterId);
                    killName = monster == null ? null : monster.getName();
                } else if (entry.killTypeId > 0) {
                    final KillTypeTemplate killType = Templates.getTemplate(KillTypeTemplate.class, entry.killTypeId);
                    killName = killType == null ? null : killType.getName();
                    isUseArticle = killType != null ? killType.isUseArticle() : isUseArticle;
                }

                if (RogueUtils.isEmpty(killName)) {
                    killName = getConfig().getDefaultKillName(); // fallback ("Wally the Wonder Badger", etc.)
                }

                base += " " + MSG_BY + " ";
                if (isUseArticle) {
                    base += RogueUtils.getIndefiniteArticleFor(killName) + " ";
                }
                base += killName;
            }

            // Append user ID if master mode requested real name display
            if (getConfig().isMaster() && flag == FLAG_SHOW_NAMES) {
                base += " (" + entry.userId + ")";
            }

            base += ".";

            // Highlight newly inserted score entry (similar to standout() in curses)
            if (entry.isNew) {
                screen.enableModifiers(SGR.BOLD);
            }

            screen.putString(0, y++, base);

            if (entry.isNew) {
                screen.disableModifiers(SGR.BOLD);
            }

            // Handle admin score deletion prompt (interactive; inspired by `fgets()` and `if (prbuf[0] == 'd')`)
            if (getConfig().isMaster() && flag == FLAG_DELETE) {
                screen.putString(0, screen.getRows() - 1, PROMPT_DELETE_OR_SKIP);
                screen.refresh();

                final KeyStroke key = screen.readInput();
                if (key.getKeyType() == KeyType.Character && Character.toLowerCase(key.getCharacter()) == 'd') {
                    entry.isDeleted = true;
                }
            }
        }

        // Remove any marked-for-deletion entries before saving
        final boolean entryDeleted = scoreEntries.removeIf(entry -> entry.isDeleted);

        final boolean hasNewEntry = scoreEntries.stream()
                .anyMatch(entry -> entry.isNew);

        /*
         * If a new score was inserted or any score was deleted, update the score file.
         * This maps to the final "wr_score(top_ten)" and lock/unlock logic in C.
         */
        if (hasNewEntry || entryDeleted) {
            writeScoreFile(scoreEntries);
        }

        screen.clearLine(screen.getRows() - 1);
        screen.putString(0, screen.getRows() - 1, PROMPT_PRESS_RETURN_CONTINUE);
        screen.refresh();
        screen.readInput();

    }

    private Config getConfig() {
        return screen.getConfig();
    }

    public List<ScoreEntry> readScoreFile() throws IOException {
        final List<ScoreEntry> entries = new ArrayList<>();
        final File scoreFile = new File(getConfig().getJavaRogueDirName(), getConfig().getScoreFileName());

        if (!scoreFile.exists()) {
            return entries;
        }

        try (FileInputStream fis = new FileInputStream(scoreFile)) {
            for (int i = 0; i < getConfig().getNumScores(); i++) {
                final byte[] nameBytes = new byte[getConfig().getMaxStringLength()];
                final byte[] scoreLineBytes = new byte[100];

                final int readName = fis.read(nameBytes);
                final int readScoreLine = fis.read(scoreLineBytes);
                if (readName != nameBytes.length || readScoreLine != scoreLineBytes.length) {
                    break;
                }

                RogueUtils.xorCrypt(nameBytes,
                        getConfig().getEncryptionKeyPrimary(),
                        getConfig().getEncryptionKeySecondary());

                RogueUtils.xorCrypt(scoreLineBytes,
                        getConfig().getEncryptionKeyPrimary(),
                        getConfig().getEncryptionKeySecondary());

                final String name = new String(nameBytes, StandardCharsets.UTF_8).trim();
                final String scoreLine = new String(scoreLineBytes, StandardCharsets.UTF_8).trim();

                final Scanner scanner = new Scanner(scoreLine);
                final int uid = scanner.nextInt();
                final int score = scanner.nextInt();
                final int gameEndReasonId = scanner.nextInt();
                final int monster = scanner.nextInt();
                final int killtypeId = scanner.nextInt();
                final int level = scanner.nextInt();
                final int time = Integer.parseInt(scanner.next(), 16);

                entries.add(
                        new ScoreEntry(uid, score, GameEndReason.fromId(gameEndReasonId), monster, killtypeId, level,
                                time, name));
            }
        }
        return entries;
    }

    public void writeScoreFile(@Nullable final List<ScoreEntry> entries) throws IOException {

        if (entries == null || entries.isEmpty())
            return;

        final File scoreFile = getFile();

        try (FileOutputStream fos = new FileOutputStream(scoreFile, false)) {
            for (ScoreEntry entry : entries) {

                // 1. Name (encrypted, padded to MAXSTR)
                byte[] nameBytes = new byte[getConfig().getMaxStringLength()];
                byte[] rawName = entry.name.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(rawName, 0, nameBytes, 0, Math.min(rawName.length, nameBytes.length));

                RogueUtils.xorCrypt(nameBytes,
                        getConfig().getEncryptionKeyPrimary(),
                        getConfig().getEncryptionKeySecondary());
                fos.write(nameBytes);

                // 2. Score line (encrypted, padded to 100 bytes)
                String formatted = String.format(" %d %d %d %d %d %d %x \n",
                        entry.userId,
                        entry.score,
                        entry.gameEndReason.getId(),
                        entry.monsterId,
                        entry.killTypeId,
                        entry.level,
                        entry.time);

                byte[] scoreLineBytes = new byte[100];
                byte[] rawLine = formatted.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(rawLine, 0, scoreLineBytes, 0, Math.min(rawLine.length, scoreLineBytes.length));

                RogueUtils.xorCrypt(scoreLineBytes,
                        getConfig().getEncryptionKeyPrimary(),
                        getConfig().getEncryptionKeySecondary());

                fos.write(scoreLineBytes);
            }
        }
    }

    private File getFile() throws IOException {
        final File scoreFile = new File(
                getConfig().getJavaRogueDirName(),
                getConfig().getScoreFileName()
        );

        // Ensure parent directory exists
        if (!scoreFile.getParentFile().exists()) {
            if (!scoreFile.getParentFile().mkdirs()) {
                throw new IOException(String.format(ERROR_FAILED_CREATE_DIRS, scoreFile.getParent()));
            }
        }

        // Ensure the score file exists
        if (!scoreFile.exists()) {
            if (!scoreFile.createNewFile()) {
                throw new IOException(String.format(ERROR_FAILED_CREATE_SCORE_FILE, scoreFile.getAbsolutePath()));
            }
        }
        return scoreFile;
    }

    /**
     * Represents a single entry in the high score table.
     * <p>
     * This class corresponds directly to the {@code SCORE} struct in the original Rogue C code,
     * defined in {@code score.h} and used extensively in {@code rip.c}. It stores all the metadata needed
     * to display and persist a player's score entry, including user ID, score amount,
     * cause of death (monster or kill type), dungeon level reached, and player name.
     * </p>
     *
     * <p>
     * Additional fields like {@code isNew} and {@code isDeleted} support UI display and administrative
     * score editing features during runtime, which were handled manually in the C code.
     * </p>
     */
    public static final class ScoreEntry {

        /**
         * The system user ID that achieved this score.
         * <p>
         * In the original C implementation, this maps to {@code sc_uid} and was used to enforce
         * single-entry-per-user logic unless scoring for all was enabled.
         * </p>
         */
        int userId;

        /**
         * The numeric score achieved by the player.
         * <p>
         * Corresponds to {@code sc_score} in the Rogue source. This is what the score list is sorted on.
         * </p>
         */
        int score;

        /**
         * The reason the game ended for this entry (e.g., killed, quit, won).
         * <p>
         * In C this was {@code sc_flags}, and was an integer index into a static array of strings.
         * In Java, it's mapped to a type-safe {@link GameEndReason} enum.
         * </p>
         */
        GameEndReason gameEndReason;

        /**
         * The monster ID that killed the player, if applicable. 0 means no monster involved.
         * <p>
         * Maps to {@code sc_monster} in the C code, originally stored as a character code (e.g. 'A' for monsters).
         * Here we use long IDs from our {@link MonsterTemplate} registry.
         * </p>
         */
        long monsterId;

        /**
         * Kill type ID if death was not caused by a monster (e.g. dart, starvation).
         * <p>
         * Not explicitly separated in the C struct, but handled through logic in {@code killname()}.
         * In our design, we distinguish it explicitly for clarity and JSON template-based name resolution.
         * </p>
         */
        long killTypeId;

        /**
         * The dungeon level the player reached. In case of a win, it's the max level visited.
         * <p>
         * Corresponds to {@code sc_level} in the C code.
         * </p>
         */
        int level;

        /**
         * The UNIX timestamp (seconds since epoch) when this score was recorded.
         * <p>
         * Encodes game end time for sorting, persistence, and score file auditing.
         * </p>
         */
        long time;

        /**
         * The display name of the player (e.g., entered name).
         * <p>
         * This is stored as {@code sc_name[]} in the C code, a fixed-length char buffer.
         * </p>
         */
        String name;

        /**
         * Whether this score entry was just added in the current session.
         * <p>
         * Used for formatting (e.g. bold display in UI). In C, standout mode was used via curses.
         * </p>
         */
        boolean isNew;

        /**
         * Marks this score for deletion (via admin/editor command). Not stored to disk.
         * <p>
         * This reflects logic handled in {@code rip.c} where {@code fgets()} would read 'd' and mark
         * the current entry for deletion by zeroing the score and setting garbage data.
         * </p>
         */
        boolean isDeleted;

        /**
         * Constructs a new {@link ScoreEntry}.
         *
         * @param userId        The user ID who played.
         * @param score         The final score achieved.
         * @param gameEndReason Why the game ended (death, quit, win).
         * @param monsterId     Monster ID that caused death (or 0).
         * @param killTypeId    KillType ID (non-monster deaths), or 0.
         * @param level         Dungeon level reached.
         * @param time          UNIX timestamp of game end.
         * @param name          Player name.
         */
        ScoreEntry(final int userId, final int score, final GameEndReason gameEndReason,
                   final long monsterId, final long killTypeId,
                   final int level, final long time,
                   final String name) {
            this.userId = userId;
            this.score = score;
            this.gameEndReason = gameEndReason;
            this.monsterId = monsterId;
            this.killTypeId = killTypeId;
            this.level = level;
            this.time = time;
            this.name = name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, score, gameEndReason, monsterId, killTypeId, level, time, name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ScoreEntry other)) return false;
            return userId == other.userId &&
                    score == other.score &&
                    gameEndReason == other.gameEndReason &&
                    monsterId == other.monsterId &&
                    killTypeId == other.killTypeId &&
                    level == other.level &&
                    time == other.time &&
                    Objects.equals(name, other.name);
        }

    }


}
