package com.dungeoncode.javarogue.system.entity.item;

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
