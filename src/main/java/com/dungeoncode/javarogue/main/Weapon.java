package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;

public class Weapon extends Item {

    public Weapon(@Nonnull Enum<WeaponType> weaponType) {
        super(ObjectType.WEAPON, weaponType, 1);
    }

    @Override
    public WeaponType getItemSubType() {
        return (WeaponType) super.getItemSubType();
    }

}
