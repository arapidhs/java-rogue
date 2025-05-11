package com.dungeoncode.javarogue.core;

/**
 * Defines the phases of a game turn in the Rogue game, controlling the order of command execution.
 * Each phase represents a distinct stage: pre-player actions, status updates, input cleanup,
 * player actions, and post-player actions.
 * <p>
 * Mirrors the turn-based structure of the C Rogue source (main.c), with pre-player actions
 * (monsters.c), player input processing (command.c), and post-player updates (player.c, daemon.c).
 * </p>
 */
public enum Phase {
    /**
     * Phase for pre-player actions, such as monster movement or setting player move count.
     * Corresponds to pre-player logic in C Rogue, like m_move() in monsters.c.
     */
    START_TURN,

    /**
     * Phase for status updates, such as displaying the player status line.
     * Handles upkeep tasks before player input.
     */
    UPKEEP_TURN,

    /**
     * Phase for cleanup after player input, such as clearing messages, before executing main commands.
     * Ensures a clean UI state for player actions.
     */
    INPUT_CLEANUP_TURN,

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