package com.dungeoncode.javarogue.command.core;

import com.dungeoncode.javarogue.command.Command;

/**
 * Represents a command that persists in the command queue indefinitely, executing
 * repeatedly each turn without being removed. Eternal commands are used for ongoing
 * game mechanics that require continuous processing, such as persistent environmental
 * effects or recurring system updates.
 * <p>
 * This interface is inspired by the daemon system in the original C Rogue source code
 * (daemon.c), where certain tasks (e.g., recurring status checks or background events)
 * run every turn without termination unless explicitly stopped. Implementations should
 * define the recurring action and its phase.
 * </p>
 */
public interface CommandEternal extends Command {

}