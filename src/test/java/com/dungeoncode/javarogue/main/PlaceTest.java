package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.creature.Monster;
import com.dungeoncode.javarogue.system.entity.creature.MonsterType;
import com.dungeoncode.javarogue.system.world.Place;
import com.dungeoncode.javarogue.system.world.PlaceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceTest {

    @Test
    void testPlace() {
        final Place emptyPlace = new Place();
        assertEquals(PlaceType.EMPTY, emptyPlace.getPlaceType());
        assertTrue(emptyPlace.isType(PlaceType.EMPTY));
        assertEquals(SymbolType.EMPTY, emptyPlace.getSymbolType());
        assertFalse(emptyPlace.isStepOk());

        final Place wallPlace = new Place();
        wallPlace.setPlaceType(PlaceType.WALL);
        assertFalse(wallPlace.isStepOk());

        final Place doorPlace = new Place();
        doorPlace.setPlaceType(PlaceType.DOOR);
        assertTrue(doorPlace.isStepOk());

        doorPlace.setMonster(new Monster(MonsterType.ICE_MONSTER));
        assertFalse(doorPlace.isStepOk());

        final Place passagePlace = new Place();
        passagePlace.setPlaceType(PlaceType.PASSAGE);
        assertTrue(passagePlace.isStepOk());

        passagePlace.setMonster(new Monster(MonsterType.ICE_MONSTER));
        assertFalse(passagePlace.isStepOk());

        final Place floorPlace = new Place();
        floorPlace.setPlaceType(PlaceType.FLOOR);
        assertTrue(floorPlace.isStepOk());

        floorPlace.setMonster(new Monster(MonsterType.ICE_MONSTER));
        assertFalse(floorPlace.isStepOk());

    }
}
