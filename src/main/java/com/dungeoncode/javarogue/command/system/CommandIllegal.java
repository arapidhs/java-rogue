package com.dungeoncode.javarogue.command.system;

import com.dungeoncode.javarogue.command.core.CommandParameterized;
import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandIllegal extends CommandParameterized<KeyStroke> {

    public  CommandIllegal(KeyStroke params) {
        super(params, Phase.MAIN_TURN);
    }

    @Override
    public boolean execute(@Nonnull final  GameState gameState) {
        Objects.requireNonNull(gameState);
        final Config config = gameState.getConfig();

        boolean messageSaveSetting = config.isMessageSave();
        config.setMessageSave(false);

        // no need to repeat with 'count' an illegal command
        gameState.setCount(0);

        gameState.getMessageSystem().msg(
                String.format("illegal command '%s'",unctrl(getParams()))
        );
        config.setMessageSave(messageSaveSetting);

        // this illegal command does not consume a player move
        return false;
    }

    @Override
    public KeyStroke getParams() {
        return super.getParams();
    }

    private String unctrl(@Nonnull final KeyStroke keyStroke){
        String com="";
        if(keyStroke.isCtrlDown()){
            com="^";
        }
        if(keyStroke.getKeyType() == KeyType.Character){
            com+=keyStroke.getCharacter();
        }
        return com;
    }
}
