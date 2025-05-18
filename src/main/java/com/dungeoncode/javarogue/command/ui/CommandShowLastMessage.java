package com.dungeoncode.javarogue.command.ui;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.MessageSystem;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

public class CommandShowLastMessage implements Command {

    @Override
    public boolean execute(@NonNull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final MessageSystem messageSystem = gameState.getMessageSystem();
        final String lastMessage = messageSystem.getLastMessage();
        if(lastMessage!=null){
            messageSystem.msg(messageSystem.getLastMessage());
        }
        return false;
    }

    @Override
    public Phase getPhase() {
        return Phase.MAIN_TURN;
    }
}
