package com.dungeoncode.javarogue.command.system;

import com.dungeoncode.javarogue.command.core.CommandParameterized;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommandNewLevel extends CommandParameterized<Integer> {

    /**
     * A command that initializes a new dungeon level with the specified level number.
     * Executes the {@link GameState#newLevel(int)} method to generate and set up the level,
     * positioning the player and updating the screen.
     * <p>
     * Equivalent to level transition logic in the C Rogue source (e.g., <code>new_level</code> in main.c).
     * </p>
     */
    public CommandNewLevel(Integer params) {
        super(params, Phase.START_TURN);
    }

    @Override
    public boolean execute(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final int levelNum=getParams();
        gameState.newLevel(levelNum);
        return true;
    }

    @Override
    public Integer getParams() {
        return super.getParams();
    }
}
