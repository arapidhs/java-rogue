package com.dungeoncode.javarogue.entity.item.weapon;

import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.entity.item.ItemFlag;

import javax.annotation.Nonnull;
import java.util.Objects;

public class WeaponsFactory {

    private final RogueRandom rogueRandom;
    private int weaponsGroup = 2;

    public WeaponsFactory(@Nonnull RogueRandom rogueRandom) {
        this.rogueRandom = rogueRandom;
    }

    public Weapon initializeWeapon(@Nonnull final WeaponType weaponType) {
        Objects.requireNonNull(weaponType);
        final Weapon weapon = new Weapon(weaponType);
        if (WeaponType.DAGGER.equals(weapon.getWeaponType())) {
            weapon.setCount(rogueRandom.rnd(4) + 2);
            weapon.setGroup(weaponsGroup++);
        } else if (weapon.hasFlag(ItemFlag.ISMANY)) {
            weapon.setCount(rogueRandom.rnd(8) + 8);
            weapon.setGroup(weaponsGroup++);
        } else {
            weapon.setCount(1);
            weapon.setGroup(0);
        }
        return weapon;
    }

}
