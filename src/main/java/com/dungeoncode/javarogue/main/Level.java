package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a level in the game, managing items, monsters, rooms, and the map grid.
 */
public class Level {

    private final List<Item> items;
    private final List<Monster> monsters;
    private final Place[][] places;
    private final List<Room> rooms;
    private final int maxWidth;
    private final int maxHeight;

    /**
     * Constructs a new Level with the specified maximum width and height, initializing empty lists and map.
     *
     * @param maxWidth  The maximum width of the level map.
     * @param maxHeight The maximum height of the level map.
     */
    public Level(final int maxWidth, final int maxHeight) {
        this.items = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.places = new Place[maxHeight][maxWidth];
        this.rooms = new ArrayList<>();
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public void setPlaceAt(final int x, final int y, @Nonnull final Place place) {
        validateCoordinates(x, y);
        places[y][x] = place;
    }

    private void validateCoordinates(final int x, final int y) {
        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight) {
            throw new IllegalArgumentException(String.format(
                    Messages.ERROR_LEVEL_COORDS_OUT_OF_BOUNDS, x, y, maxWidth, maxHeight));
        }
    }

    /**
     * Finds an item at the specified coordinates.
     *
     * @param x The x-coordinate to search.
     * @param y The y-coordinate to search.
     * @return The Item at the coordinates, or null if none is found.
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    @Nullable
    public Item findItemAt(final int x, final int y) {
        validateCoordinates(x, y);
        final Position position = new Position(x, y);
        return items.stream()
                .filter(item -> item.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the display character at the specified coordinates on the level map.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The character at (x, y).
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    @Nullable
    public Character getSymbol(final int x, final int y) {
        final Place place = getPlaceAt(x, y);
        return place == null ? null : place.getSymbol();
    }

    /**
     * Gets the Place at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The Place at (x, y).
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    public Place getPlaceAt(final int x, final int y) {
        validateCoordinates(x, y);
        return places[y][x];
    }

    /**
     * Sets the display character at the specified coordinates on the level map.
     *
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param symbol The character to set.
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    public void setPlaceSymbol(final int x, final int y, final char symbol) {
        final Place place = getPlaceAt(x, y);
        if (place != null) {
            place.setSymbol(symbol);
        }
    }

    /**
     * Removes the specified item from the level's item list.
     *
     * @param item The item to remove.
     * @return true if the item was removed, false if it was not found.
     */
    public boolean removeItem(@Nonnull final Item item) {
        Objects.requireNonNull(item);
        return items.remove(item);
    }

    /**
     * Adds an item to the level's item list.
     *
     * @param item The item to add.
     */
    public void addItem(@Nonnull final Item item) {
        Objects.requireNonNull(item);
        items.add(item);
    }

    /**
     * Finds a room containing the specified coordinates.
     *
     * @param x The x-coordinate to search.
     * @param y The y-coordinate to search.
     * @return The Room containing the coordinates, or null if none is found.
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    @Nullable
    public Room findRoomAt(final int x, final int y) {
        validateCoordinates(x, y);
        return rooms.stream()
                .filter(room -> {
                    final Position pos = room.getPosition();
                    final Position size = room.getSize();
                    return x >= pos.getX() && x < pos.getX() + size.getX() &&
                            y >= pos.getY() && y < pos.getY() + size.getY();
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the list of items on the level.
     *
     * @return The list of items.
     */
    @Nonnull
    public List<Item> getItems() {
        return items;
    }

    /**
     * Returns the list of monsters on the level.
     *
     * @return The list of monsters.
     */
    @Nonnull
    public List<Monster> getMonsters() {
        return monsters;
    }

    /**
     * Returns the list of rooms on the level.
     *
     * @return The list of rooms.
     */
    @Nonnull
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * Adds a room to the level's room list.
     *
     * @param room The room to add.
     */
    public boolean addRoom(@Nonnull final Room room) {
        Objects.requireNonNull(room);
        return rooms.add(room);
    }

    /**
     * Adds a monster to the level's monster list.
     *
     * @param monster The monster to add.
     */
    public boolean addMonster(@Nonnull final Monster monster) {
        Objects.requireNonNull(monster);
        return monsters.add(monster);
    }

}
