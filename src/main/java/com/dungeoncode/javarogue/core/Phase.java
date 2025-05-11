package com.dungeoncode.javarogue.core;

/**
 * Defines the phases of a game turn in the Rogue game, determining the order of command execution.
 * Each phase represents a distinct stage: pre-player actions (START_TURN), status updates (UPKEEP_TURN),
 * player actions (MAIN_TURN), and post-player actions (END_TURN).
 * <p>
 * Mirrors the turn-based loop in the C Rogue source (main.c), with pre-player actions (monsters.c),
 * player input (command.c), and post-player updates (player.c, daemon.c).
 * </p>
 */
public enum Phase {
    /**
     * Phase for pre-player actions, such as monster movement or environmental updates.
     * Corresponds to pre-player logic in C Rogue, like m_move() in monsters.c.
     */
    START_TURN,

    /**
     * Phase for status updates, such as displaying the player status line.
     * Introduced to handle upkeep tasks between pre-player and player actions.
     */
    UPKEEP_TURN,

    /**
     * Phase for player actions, such as movement, item use, or level transitions.
     * Corresponds to player-driven commands in C Rogue, like move_hero() in move.c.
     */
    MAIN_TURN,

    /**
     * Phase for post-player actions, such as status effect updates or delayed actions.
     * Corresponds to post-player logic in C Rogue, like affect() in player.c or daemon.c.
     */
    END_TURN
}