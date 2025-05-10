package com.dungeoncode.javarogue.main;

/**
 * Defines the phases of a game turn in the Rogue game, determining the order in
 * which commands are executed. Each phase represents a distinct stage of turn
 * processing: before the player's action (START_TURN), the player's action
 * (MAIN_TURN), and after the player's action (END_TURN).
 * <p>
 * This enum mirrors the structured turn-based loop in the original C Rogue source
 * code (main.c), where system actions (e.g., monster movement in monsters.c) occur
 * before the player's input (command.c), followed by post-action updates (e.g.,
 * status effects in player.c or daemon.c).
 * </p>
 */
public enum Phase {
    /**
     * The phase for actions that occur before the player's turn, such as monster
     * movement or environmental updates. Corresponds to pre-player logic in the
     * C source code, like m_move() in monsters.c.
     */
    START_TURN,

    /**
     * The phase for the player's primary action, such as movement, item use, or
     * level transitions. Corresponds to player-driven commands in the C source
     * code's command.c and related functions (e.g., move_hero() in move.c).
     */
    MAIN_TURN,

    /**
     * The phase for actions that occur after the player's turn, such as status
     * effect updates or delayed actions. Corresponds to post-player logic in the
     * C source code, like affect() in player.c or daemon runs in daemon.c.
     */
    END_TURN
}