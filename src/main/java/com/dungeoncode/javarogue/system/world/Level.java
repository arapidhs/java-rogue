package com.dungeoncode.javarogue.system.world;

import com.dungeoncode.javarogue.core.Messages;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.Entity;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.entity.creature.Monster;
import com.dungeoncode.javarogue.system.entity.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a level in the game, managing items, monsters, rooms, and the map grid.
 */
public class Level extends Entity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Level.class);

    private final RogueRandom rogueRandom;
    private final List<Item> items;
    private final List<Monster> monsters;
    private final Place[][] places;
    private final List<Room> rooms;
    private final List<Passage> passages;
    private final int maxWidth;
    private final int maxHeight;
    private final int levelNum;
    private Position stairs;

    public Level(final int maxWidth, final int maxHeight, @Nonnull final RogueRandom rogueRandom) {
        this(maxWidth, maxHeight, rogueRandom, 0);
    }

    /**
     * Constructs a new Level with the specified maximum width and height, initializing empty lists and map.
     *
     * @param maxWidth  The maximum width of the level map.
     * @param maxHeight The maximum height of the level map.
     */
    public Level(final int maxWidth, final int maxHeight, @Nonnull final RogueRandom rogueRandom, final int levelNum) {
        super();
        Objects.requireNonNull(rogueRandom);
        this.rogueRandom = rogueRandom;
        this.items = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.places = new Place[maxHeight][maxWidth];
        this.rooms = new ArrayList<>();
        this.passages = new ArrayList<>();
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.levelNum = levelNum;
    }

    /**
     * Finds the room or passage containing the specified coordinates.
     * Returns null if the coordinates are not in any room or passage.
     * <p>
     * This method is a port of the C function roomin() from the Rogue source code,
     * which checks if coordinates are in a passage (F_PASS flag) or a room based on its
     * position and size. Unlike the original C code, which uses inclusive bounds
     * (<=) for room edges, this implementation uses exclusive bounds (<) for the right
     * and bottom edges to align with the Java drawRoom method, which places walls at
     * size-1 (e.g., x=3 for width 4). This ensures coordinates beyond walls are not
     * considered part of the room.
     * </p>
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return The Room or Passage containing the coordinates, or null if none found.
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    @Nullable
    public Room roomIn(final int x, final int y) {
        validateCoordinates(x, y);

        // Check if coordinates are in a passage
        final Place place = getPlaceAt(x, y);
        assert place != null;
        if (place.isType(PlaceType.PASSAGE)) {
            final Integer passageNum = place.getPassageNumber();
            if (passageNum != null) {
                return passages.stream()
                        .filter(passage -> passage.getPassageNumber() == passageNum)
                        .findFirst()
                        .orElse(null);
            }
        }

        // Check if coordinates are in a room, using exclusive bounds (<) for right and bottom
        // edges to exclude coordinates beyond walls, unlike C's inclusive bounds (<=)
        for (Room room : rooms) {
            final Position pos = room.getPosition();
            final Position size = room.getSize();
            if (x >= pos.getX() && x < pos.getX() + size.getX() &&
                    y >= pos.getY() && y < pos.getY() + size.getY()) {
                return room;
            }
        }

        // No room or passage found
        LOGGER.debug("No room found at {},{}", x, y);
        return null;
    }

    private void validateCoordinates(final int x, final int y) {
        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight) {
            throw new IllegalArgumentException(String.format(
                    Messages.ERROR_LEVEL_COORDS_OUT_OF_BOUNDS, x, y, maxWidth, maxHeight));
        }
    }

    /**
     * Gets the Place at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The Place at (x, y).
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    @Nullable
    public Place getPlaceAt(final int x, final int y) {
        validateCoordinates(x, y);
        final Place place = places[y][x];
        if (place == null) {
            LOGGER.debug("No place found at {},{}", x, y);
        }
        return place;
    }

    /**
     * Finds a valid floor spot in a room for item or monster placement.
     * If room is null, picks a random non-GONE room each attempt.
     * Based on C function find_floor() in Rogue source.
     *
     * @param room        The room to search, or null to pick randomly.
     * @param limit       Maximum attempts; 0 for unlimited.
     * @param forCreature True if placing a creature, false for items.
     * @return A Position with a valid floor spot, or null if none found.
     * @throws IllegalStateException if no valid rooms exist when room is null.
     */
    @Nullable
    public Position findFloor(@Nullable Room room, final int limit, final boolean forCreature) {
        int attempts = limit;
        boolean pickRoom = room == null;
        while (true) {
            if (limit > 0 && attempts-- == 0) {
                return null;
            }

            if (pickRoom) {
                room = rndRoom();
            }

            final Position pos = room.rndPos(rogueRandom);
            final Place place = getPlaceAt(pos.getX(), pos.getY());
            final SymbolType expectedType = room.hasFlag(RoomFlag.MAZE) ? SymbolType.PASSAGE : SymbolType.FLOOR;
            assert place != null;
            if (forCreature && place.isStepOk()) {
                return pos;
            } else if (place.getSymbolType() == expectedType) {
                return pos;
            }
        }
    }

    /**
     * Picks a random room that is not marked as GONE.
     * Based on C function rnd_room() in Rogue source.
     *
     * @return A non-null Room that exists.
     * @throws IllegalStateException if no valid rooms exist.
     */
    @Nonnull
    public Room rndRoom() {
        if (rooms.isEmpty()) {
            throw new IllegalStateException("No rooms available");
        }
        Room room;
        do {
            int rm = rogueRandom.rnd(rooms.size());
            room = rooms.get(rm);
        } while (room.hasFlag(RoomFlag.GONE));
        return room;
    }

    public void setPlaceAt(final int x, final int y, @Nonnull final Place place) {
        validateCoordinates(x, y);
        places[y][x] = place;
        place.setPosition(x, y);
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
     * Gets the display symbol type at the specified coordinates on the level map.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The symbol type at (x, y).
     * @throws IllegalArgumentException If coordinates are out of bounds.
     * @see SymbolType
     */
    @Nullable
    public SymbolType getSymbolType(final int x, final int y) {
        final Place place = getPlaceAt(x, y);
        return place == null ? null : place.getSymbolType();
    }

    /**
     * Sets the display symbol type at the specified coordinates on the level map.
     *
     * @param x          The x-coordinate.
     * @param y          The y-coordinate.
     * @param symbolType The symbol type to set.
     * @throws IllegalArgumentException If coordinates are out of bounds.
     */
    public void setPlaceSymbol(final int x, final int y, @Nonnull final SymbolType symbolType) {
        Objects.requireNonNull(symbolType);
        final Place place = getPlaceAt(x, y);
        if (place != null) {
            place.setSymbolType(symbolType);
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

    public void addPassage(@Nonnull final Passage passage) {
        Objects.requireNonNull(passage);
        passages.add(passage);
    }

    public int getLevelNum() {
        return levelNum;
    }

    public Position getStairs() {
        return stairs;
    }

    public void setStairs(Position stairs) {
        this.stairs = stairs;
    }
}
