package com.dungeoncode.javarogue.entity.item.weapon;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.entity.item.ItemFlag;
import com.dungeoncode.javarogue.entity.item.ObjectType;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Template representing weapon information, based on the original Rogue weap_info table.
 */
public class WeaponInfoTemplate extends ObjectInfoTemplate {

    private final String wieldDamage;
    private final String throwDamage;
    private final WeaponType launchWeapon;

    @JsonCreator
    public WeaponInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("weaponType") @Nonnull final WeaponType weaponType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth,
            @JsonProperty("wieldDamage") @Nonnull final String wieldDamage,
            @JsonProperty("throwDamage") @Nonnull final String throwDamage,
            @JsonProperty("launchWeapon") @Nullable final WeaponType launchWeapon,
            @JsonProperty("itemFlags") @Nullable final EnumSet<ItemFlag> itemFlags) {

        super(id, ObjectType.WEAPON, weaponType, name, probability, worth, null, itemFlags,null);
        this.wieldDamage = wieldDamage;
        this.throwDamage = throwDamage;
        this.launchWeapon = launchWeapon;
    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_WEAPON;
    }

    /**
     * Returns the type of weapon.
     *
     * @return The WeaponType.
     */
    public WeaponType getWeaponType() {
        return (WeaponType) super.getItemSubType();
    }

    /**
     * Returns the damage dealt when the weapon is wielded.
     *
     * @return The wield damage string (e.g., "2x4").
     */
    @Nonnull
    public String getWieldDamage() {
        return wieldDamage;
    }

    /**
     * Returns the damage dealt when the weapon is thrown.
     *
     * @return The throw damage string (e.g., "1x3").
     */
    @Nonnull
    public String getThrowDamage() {
        return throwDamage;
    }

    /**
     * Returns the launching weapon type, if applicable.
     *
     * @return The launching WeaponType, or null if none.
     */
    @Nullable
    public WeaponType getLaunchWeapon() {
        return launchWeapon;
    }
}