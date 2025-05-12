package com.dungeoncode.javarogue.system.entity.item;

import com.dungeoncode.javarogue.system.entity.Entity;
import com.dungeoncode.javarogue.template.ObjectInfoTemplate;
import com.dungeoncode.javarogue.template.Templates;
import com.dungeoncode.javarogue.system.SymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class Item extends Entity {

    private final ObjectType objectType;
    private final Enum<? extends ItemSubtype> itemSubType;
    private final EnumSet<ItemFlag> itemFlags;
    private int count;
    private String wieldDamage;
    private String throwDamage;

    private SymbolType inventoryKey;
    private int group;
    private String label;
    private int armorClass;
    private int goldValue;

    public Item(@Nonnull ObjectType objectType, @Nullable final Enum<? extends ItemSubtype> itemSubType, final int count) {
        super();
        Objects.requireNonNull(objectType);
        this.objectType = objectType;
        this.itemSubType = itemSubType;
        this.count = count;
        this.itemFlags = EnumSet.noneOf(ItemFlag.class);
        final ObjectInfoTemplate objectInfoTemplate = Templates.findTemplateByObjectType(objectType);
        assert objectInfoTemplate != null;
        setSymbolType(objectInfoTemplate.getSymbolType());
    }

    public boolean isType(@Nonnull final Enum<? extends ItemSubtype> itemSubType){
        return Objects.equals(this.itemSubType,itemSubType);
    }

    public void addFlag(@Nonnull final ItemFlag itemFlag) {
        itemFlags.add(itemFlag);
    }

    public boolean hasFlag(@Nonnull final ItemFlag itemFlag) {
        return itemFlags.contains(itemFlag);
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public EnumSet<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public Enum<? extends ItemSubtype> getItemSubType() {
        return itemSubType;
    }

    public SymbolType getInventoryKey() {
        return inventoryKey;
    }

    public void setInventoryKey(final SymbolType inventoryKey) {
        this.inventoryKey = inventoryKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(final int armorClass) {
        this.armorClass = armorClass;
    }

    public int getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(int goldValue) {
        this.goldValue = goldValue;
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

    public void setWieldDamage(String wieldDamage) {
        this.wieldDamage = wieldDamage;
    }

    public void setThrowDamage(String throwDamage) {
        this.throwDamage = throwDamage;
    }
}
