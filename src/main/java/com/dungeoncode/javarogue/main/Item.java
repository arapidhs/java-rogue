package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class Item extends Entity {

    private final ObjectType objectType;
    private final Enum<? extends ItemSubtype> itemSubType;
    private final EnumSet<ItemFlag> itemFlags;
    private int count;
    private Character packChar;
    private int group;

    public Item(@Nonnull ObjectType objectType, @Nullable final Enum<? extends ItemSubtype> itemSubType, final int count) {
        Objects.requireNonNull(objectType);
        this.objectType = objectType;
        this.itemSubType = itemSubType;
        this.count = count;
        this.itemFlags = EnumSet.noneOf(ItemFlag.class);
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

    public Enum<? extends ItemSubtype> getItemSubType() {
        return itemSubType;
    }

    public Character getPackChar() {
        return packChar;
    }

    public void setPackChar(final Character packChar) {
        this.packChar = packChar;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
