package com.dungeoncode.javarogue.command.action;

import com.dungeoncode.javarogue.command.core.CommandParameterized;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.RogueScreen;
import com.dungeoncode.javarogue.system.SymbolMapper;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.world.Place;
import com.dungeoncode.javarogue.system.world.PlaceType;
import com.googlecode.lanterna.SGR;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandPlayerMove extends CommandParameterized<Position> {

    // TODO this is implemented as PoC for testing equivalent void do_move(int dy, int dx)
    public CommandPlayerMove(@Nonnull final Position params) {
        super(params, Phase.MAIN_TURN);
    }

    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final int dx = getParams().getX();
        final int dy = getParams().getY();
        final int px = gameState.getPlayer().getX();
        final int py = gameState.getPlayer().getY();
        final int newx = px + dx;
        final int newy = py + dy;
        if (newx < 0 || newx >= gameState.getConfig().getTerminalCols() || newy <= 0 || newy >= gameState.getConfig().getTerminalRows() - 1) {
            return false;
        }
        final Place place = gameState.getCurrentLevel().getPlaceAt(newx, newy);
        assert place != null;
        if (place.isType(PlaceType.WALL) || place.isType(PlaceType.EMPTY)) {
            return false;
        }
        if (place.isType(PlaceType.PASSAGE) && !place.isReal()) {
            return false;
        }

        final RogueScreen screen = gameState.getScreen();
        screen.putChar(px, py, SymbolMapper.getSymbol(gameState.floorAt()));
        gameState.getPlayer().setPosition(newx, newy);

        if (place.isType(PlaceType.PASSAGE)) {
            screen.enableModifiers(SGR.REVERSE);
        }
        screen.putChar(newx, newy, SymbolMapper.getSymbol(gameState.getPlayer().getClass()));
        if (place.isType(PlaceType.PASSAGE)) {
            screen.disableModifiers(SGR.REVERSE);
        }
        return true;
    }

    @Override
    public Position getParams() {
        return super.getParams();
    }

}
