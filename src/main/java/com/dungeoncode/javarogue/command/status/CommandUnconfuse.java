package com.dungeoncode.javarogue.command.status;

import com.dungeoncode.javarogue.command.core.CommandTimed;
import com.dungeoncode.javarogue.core.Constants;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.MessageSystem;
import com.dungeoncode.javarogue.system.entity.creature.CreatureFlag;

import javax.annotation.Nonnull;
import java.util.Objects;


public class CommandUnconfuse extends CommandTimed {

    public CommandUnconfuse(int turns) {
        super(turns, Phase.END_TURN, Constants.CMD_NAME_UNCONFUSE);
    }

    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.getPlayer().removeFlag(CreatureFlag.ISHUH);
        final MessageSystem messageSystem = gameState.getMessageSystem();
        messageSystem.msg(String.format("you feel less %s now",
                gameState.chooseStr("trippy", "confused")));
        return true;
    }

}
