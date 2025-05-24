/**
 * Executes the player pick-up action, equivalent to the pick-up command in <code>command.c</code>
 * from the original Rogue C source. Attempts to pick up an item at the player's current position.
 * If an item is found and the player is not levitating, processes the pick-up; otherwise, displays
 * a message indicating nothing is present. Consumes a player move.
 */
package com.dungeoncode.javarogue.command.action;

import com.dungeoncode.javarogue.command.core.AbstractCommand;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.MessageSystem;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.entity.item.Item;
import com.dungeoncode.javarogue.system.world.Level;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandPlayerPickUp extends AbstractCommand {

    /**
     * Constructs a CommandPlayerPickUp instance, operating in the MAIN_TURN phase.
     * Equivalent to the pickup command logic in <code>command.c</code>.
     */
    public CommandPlayerPickUp() {
        super(Phase.MAIN_TURN);
    }

    /**
     * Executes the pick-up command. Checks for an item at the player's position and, if found 
     * and the player is not levitating, triggers the pick-up logic. If no item is present, 
     * displays a message based on the terse setting. Always consumes a player move.
     *
     * @param gameState The current game state, providing access to the level, player, and message system.
     * @return {@code true}, indicating the command consumes a player move.
     */
    @Override
    public boolean execute(@Nonnull GameState gameState) {
        Objects.requireNonNull(gameState);
        boolean found;

        final Level level = gameState.getCurrentLevel();
        final Player player = gameState.getPlayer();
        final Item item = level.findItemAt(player.getX(), player.getY());
        found = item != null;
        if (found) {
            if (!gameState.levitCheck()) {
                gameState.pickUp(item.getObjectType());
            }
        } else {
            final MessageSystem messageSystem = gameState.getMessageSystem();
            boolean terse = gameState.getConfig().isTerse();
            if (!terse) {
                messageSystem.addmssg("there is ");
            }
            messageSystem.addmssg("nothing here");
            if (!terse) {
                messageSystem.addmssg(" to pick up");
            }
            messageSystem.endmsg();
        }

        return true;
    }
}