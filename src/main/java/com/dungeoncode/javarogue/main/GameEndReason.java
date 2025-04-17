package com.dungeoncode.javarogue.main;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the reason a game ended.
 * <p>
 * This enum maps directly to the `reason[]` string array found in the original Rogue source file
 * {@code rip.c}, which described how a player died or finished the game.
 * The numeric {@code id} corresponds to the index in that array:
 * <pre>
 * static char *reason[] = {
 *     "killed",
 *     "quit",
 *     "A total winner",
 *     "killed with Amulet"
 * };
 * </pre>
 */
public enum GameEndReason {

    /**
     * The player was killed by a monster or other source.
     * Originally: {@code "killed"}
     */
    KILLED(0, "killed"),

    /**
     * The player quit the game manually.
     * Originally: {@code "quit"}
     */
    QUIT(1, "quit"),

    /**
     * The player completed the game and escaped with the Amulet.
     * Originally: {@code "A total winner"}
     */
    WIN(2, "A total winner"),

    /**
     * The player was killed while in possession of the Amulet.
     * Originally: {@code "killed with Amulet"}
     */
    KILLED_WITH_AMULET(3, "killed with Amulet");

    private static final Map<Integer, GameEndReason> ID_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(r -> r.id, r -> r));
    private final int id;
    private final String displayName;

    GameEndReason(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static GameEndReason fromId(int id) {
        GameEndReason reason = ID_MAP.get(id);
        if (reason == null) {
            throw new IllegalArgumentException(String.format("Invalid GameEndReason id: %d", id));
        }
        return reason;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns a display string suitable for UI presentation.
     *
     * @return the descriptive name of this reason
     */
    public String getDisplayName() {
        return displayName;
    }
}
