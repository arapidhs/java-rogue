package com.dungeoncode.javarogue.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Template representing weapon information, based on the original Rogue weap_info table.
 */
public class WeaponInfoTemplate extends ObjectInfoTemplate {

    private final WeaponType weaponType;

    @JsonCreator
    public WeaponInfoTemplate(
            @JsonProperty("id") final long id,
            @JsonProperty("weaponType") @Nonnull final WeaponType weaponType,
            @JsonProperty("name") @Nonnull final String name,
            @JsonProperty("probability") final double probability,
            @JsonProperty("worth") final int worth) {

        super(id, ObjectType.WEAPON, name, probability, worth, null);

        Objects.requireNonNull(weaponType);

        this.weaponType = weaponType;
    }

    @Override
    public String getTemplateName() {
        return Messages.MSG_TEMPLATE_WEAPON;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }
}
