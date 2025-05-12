package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.dungeoncode.javarogue.system.entity.creature.Player.STATUS_HUNGER_NAMES;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    void testStatus(){
        final Config config = new Config();
        final Player player = new Player(config);
        final int currentLevel=1;
        player.setCurrentLevel(currentLevel);

        final String statusLine = player.status();
        assertTrue(statusLine.startsWith("Level"));
        assertTrue(statusLine.matches(".*\\d$"), "Status should end with a number");

        Arrays.stream(Player.HungryState.values()).forEach(state -> {
            player.setHungryState(state);
            assertTrue(player.status().matches(".*(" + STATUS_HUNGER_NAMES[state.getId()] + ")$"),
                    "Status should end with hunger state: " + state);
        });
    }

}
