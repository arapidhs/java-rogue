package com.dungeoncode.javarogue.entity.item.food;

import com.dungeoncode.javarogue.entity.item.Item;
import com.dungeoncode.javarogue.entity.item.ObjectType;

public class Food extends Item {

    private boolean fruit;

    public Food() {
        super(ObjectType.FOOD, null, 1);
    }

    public boolean isFruit() {
        return fruit;
    }

    public void setFruit(final boolean fruit) {
        this.fruit = fruit;
    }

}
