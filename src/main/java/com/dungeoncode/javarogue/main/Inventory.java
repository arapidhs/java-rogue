package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Manages the player's inventory, including item stacking, pack character assignment,
 * and Rogue-compatible inventory display formatting.
 *
 * <p>Modeled after the original Rogue behavior with features such as object stacking,
 * pack size limits, and type-based grouping.</p>
 */
public class Inventory {

    private final int maxPack;
    private final List<Item> items;
    private int packSize;

    /**
     * Constructs a new inventory with the specified maximum pack size.
     *
     * @param maxPack The maximum number of items (or effective item units) allowed.
     */
    public Inventory(final int maxPack) {
        this.items = new ArrayList<>();
        this.maxPack = maxPack;
    }

    /**
     * Attempts to add the given item to the inventory.
     *
     * <p>Follows Rogue rules: stackable items may merge, items are ordered by type,
     * and pack characters are assigned from 'a' to 'z'.</p>
     *
     * @param item The item to add; must not be null.
     * @return true if the item was added (or merged), false otherwise.
     */
    public boolean addToPack(@Nonnull Item item) {
        Objects.requireNonNull(item);
        boolean added = false;
        if (items.isEmpty()) {
            item.setInventoryKey(assignInventoryKey());
            items.add(item);
            packSize++;
            added = true;
        } else {
            int lastTypeMatchIndex = -1;
            int matchIndex = -1;

            // Locate last index of a matching type and exact match of subtype
            for (int i = 0; i < items.size(); i++) {
                final Item existing = items.get(i);
                if (existing.getObjectType() == item.getObjectType()) {
                    lastTypeMatchIndex = i;
                    if (Objects.equals(existing.getItemSubType(), item.getItemSubType())) {
                        matchIndex = i;
                    }
                }
            }

            if (matchIndex >= 0) {
                final Item existing = items.get(matchIndex);

                // Check stackability via template; fallback is false
                boolean isStackable = Templates.getTemplates(ObjectInfoTemplate.class)
                        .stream()
                        .filter(template -> template.getObjectType() == item.getObjectType())
                        .findFirst()
                        .map(ObjectInfoTemplate::isStackable)
                        .orElse(false);

                // Stack if identical and allowed
                if (isStackable && checkPackRoom()) {
                    existing.setCount(existing.getCount() + item.getCount());
                    item.setInventoryKey(existing.getInventoryKey());
                    packSize++;
                    added = true;
                } else if (item.getGroup() != 0 && existing.getGroup() == item.getGroup()) {
                    // Handle grouped-but-not-stackable logic (e.g., arrows of same origin)
                    existing.setCount(existing.getCount() + item.getCount());
                    item.setInventoryKey(existing.getInventoryKey());
                    added = true;
                } else if (checkPackRoom()) {
                    // Insert directly after matching subtype
                    item.setInventoryKey(assignInventoryKey());
                    items.add(matchIndex + 1, item);
                    packSize++;
                    added = true;
                }

            } else if (checkPackRoom()) {
                item.setInventoryKey(assignInventoryKey());
                // Maintain relative order by inserting after last of same type
                if (lastTypeMatchIndex >= 0) {
                    items.add(lastTypeMatchIndex + 1, item);
                } else {
                    items.add(item);
                }
                packSize++;
                added = true;
            }
        }

        if (added) {
            item.getItemFlags().add(ItemFlag.ISFOUND);
        }

        return added;
    }

    /**
     * Assigns a unique Inventory key for a new item.
     *
     * @return The next available Inventory key.
     * @throws IllegalStateException if no inventory keys are available.
     * @see SymbolType#INVENTORY_KEYS
     */
    private SymbolType assignInventoryKey() {
        for (SymbolType key : SymbolType.INVENTORY_KEYS) {
            final SymbolType currentKey = key;
            boolean isUsed = items.stream().anyMatch(item -> item.getInventoryKey() != null && item.getInventoryKey() == currentKey);
            if (!isUsed) {
                return currentKey;
            }
        }
        throw new IllegalStateException("No inventory keys available");
    }

    /**
     * Checks if there is room in the inventory without modifying packSize.
     *
     * @return true if packSize + 1 <= maxPack, false otherwise.
     */
    private boolean checkPackRoom() {
        return packSize + 1 <= maxPack;
    }

    /**
     * Returns the list of items in the inventory.
     *
     * @return A copy of the items list to prevent external modification.
     */
    @Nonnull
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Returns the maximum number of items the inventory can hold.
     *
     * @return The maximum pack size.
     */
    public int getMaxPack() {
        return maxPack;
    }

    public int getPackSize() {
        return packSize;
    }

}
