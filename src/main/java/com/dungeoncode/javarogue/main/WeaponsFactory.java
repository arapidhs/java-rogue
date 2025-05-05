package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

public class WeaponsFactory {

    private final RogueRandom rogueRandom;
    private int group=2;

    public WeaponsFactory(@Nonnull RogueRandom rogueRandom){
        this.rogueRandom = rogueRandom;
    }

    public Weapon initializeWeapon(@Nonnull final WeaponType weaponType){
        Objects.requireNonNull(weaponType);
        final Weapon weapon = new Weapon(weaponType);
        if ( WeaponType.DAGGER.equals(weapon.getWeaponType())) {
            weapon.setCount(rogueRandom.rnd(4)+2);
            weapon.setGroup(group++);
        } else if (weapon.hasFlag(ItemFlag.ISMANY)){
            weapon.setCount(rogueRandom.rnd(8)+8);
            weapon.setGroup(group++);
        } else {
            weapon.setCount(1);
            weapon.setGroup(0);
        }
        return weapon;
    }

}
