package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.SGR;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandParameterizedMove extends CommandParameterized<Position> {

    // TODO this is implementing as PoC for testing equivalent void do_move(int dy, int dx)
    protected CommandParameterizedMove(@Nonnull final Position params) {
        super(params, Phase.MAIN_TURN);
    }

    @Override
    public void execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final int dx=getParams().getX();
        final int dy=getParams().getY();
        final int px=gameState.getPlayer().getX();
        final int py=gameState.getPlayer().getY();
        final int newx=px+dx;
        final int newy=py+dy;
        if(newx<0||newx>=gameState.getConfig().getTerminalCols()||newy<=0||newy>=gameState.getConfig().getTerminalRows()-1){
            return;
        }
        final Place place = gameState.getCurrentLevel().getPlaceAt(newx, newy);
        assert place!=null;
        if(place.isType(PlaceType.WALL)||place.isType(PlaceType.EMPTY)){
            return;
        }
        if(place.isType(PlaceType.PASSAGE)&&!place.isReal()){
            return;
        }

        final RogueScreen screen = gameState.getScreen();
        screen.putChar(px,py,SymbolMapper.getSymbol(gameState.floorAt()));
        gameState.getPlayer().setPosition(newx,newy);

        if(place.isType(PlaceType.PASSAGE)){
            screen.enableModifiers(SGR.REVERSE);
        }
        screen.putChar(newx,newy,SymbolMapper.getSymbol(gameState.getPlayer().getClass()));
        if(place.isType(PlaceType.PASSAGE)){
            screen.disableModifiers(SGR.REVERSE);
        }
    }

    @Override
    public Position getParams() {
        return super.getParams();
    }

}
