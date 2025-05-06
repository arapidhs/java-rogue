package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;

/**
 * Represents a weapon item in the game, extending Item with weapon-specific properties.
 */
public class Weapon extends Item {

    private final String wieldDamage;
    private final String throwDamage;
    private final WeaponType launchWeapon;
    private int hitPlus;
    private int damagePlus;

    /**
     * Constructs a Weapon item with the specified type and default count of 1.
     *
     * @param weaponType The type of weapon (e.g., MACE, LONG_SWORD).
     */
    public Weapon(@Nonnull Enum<WeaponType> weaponType) {
        super(ObjectType.WEAPON, weaponType, 1);
        final WeaponInfoTemplate weaponInfoTemplate = (WeaponInfoTemplate) Templates.findTemplateBySubType(weaponType);
        this.wieldDamage = weaponInfoTemplate.getWieldDamage();
        this.throwDamage = weaponInfoTemplate.getThrowDamage();
        this.launchWeapon = weaponInfoTemplate.getLaunchWeapon();
        weaponInfoTemplate.getItemFlags().forEach(this::addFlag);
    }

    /**
     * Formats the hit and damage bonuses for the weapon, e.g., "+1,+2" or "-1,+0".
     *
     * @return A string representing the hit and damage bonuses.
     */
    public String num() {
        return (hitPlus < 0 ? hitPlus : "+" + hitPlus) +
                (damagePlus < 0 ? "," + damagePlus : ",+" + damagePlus);
    }

    /**
     * Returns the weapon's subtype as a WeaponType.
     *
     * @return The WeaponType of this weapon.
     */
    public WeaponType getWeaponType() {
        return (WeaponType) super.getItemSubType();
    }

    /**
     * Returns the damage dealt when the weapon is wielded.
     *
     * @return The wield damage string (e.g., "2x4").
     */
    public String getWieldDamage() {
        return wieldDamage;
    }

    /**
     * Returns the damage dealt when the weapon is thrown.
     *
     * @return The throw damage string (e.g., "1x3").
     */
    public String getThrowDamage() {
        return throwDamage;
    }

    /**
     * Returns the launching weapon type, if applicable.
     *
     * @return The launching WeaponType, or null if none.
     */
    public WeaponType getLaunchWeapon() {
        return launchWeapon;
    }

    /**
     * Returns the bonus to hit probability for this weapon.
     *
     * @return The hit plus value.
     */
    public int getHitPlus() {
        return hitPlus;
    }

    public void setHitPlus(int hitPlus) {
        this.hitPlus = hitPlus;
    }

    /**
     * Returns the bonus to damage for this weapon.
     *
     * @return The damage plus value.
     */
    public int getDamagePlus() {
        return damagePlus;
    }

    public void setDamagePlus(int damagePlus) {
        this.damagePlus = damagePlus;
    }
}
