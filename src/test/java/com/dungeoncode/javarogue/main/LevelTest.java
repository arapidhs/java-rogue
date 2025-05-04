package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {

    @Test
    void testFindItemAt() {
        final Config config = new Config();
        final Level level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
        final Food food = new Food();
        final int foodX=10;
        final int foodY=5;
        food.getPosition().setX(foodX);
        food.getPosition().setY(foodY);

        level.addItem(food);

        final Item found = level.findItemAt(foodX, foodY);
        assertNotNull(found);

        boolean removed = level.removeItem(food);
        assertTrue(removed);

        final Item notFound = level.findItemAt(foodX, foodY);
        assertNull(notFound);

    }

}
