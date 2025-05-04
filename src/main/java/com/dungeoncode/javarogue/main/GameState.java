package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class GameState {

    private static final String MSG_SCROLL_TURNS_TO_DUST = "the scroll turns to dust as you pick it up";

    private final Config config;
    private final RogueRandom rogueRandom;
    private final Initializer initializer;
    private final MessageSystem messageSystem;
    private final RogueScreen screen;
    private Player player;
    private GameEndReason gameEndReason;
    private DeathSource deathSource;
    private int maxLevel;
    private int level;
    private int goldAmount;
    private Level currentLevel;

    public GameState(@Nonnull final Config config, @Nonnull final RogueRandom rogueRandom, @Nonnull RogueScreen screen,
                     @Nullable final Initializer initializer, final @Nonnull MessageSystem messageSystem) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(rogueRandom);
        Objects.requireNonNull(messageSystem);
        Objects.requireNonNull(screen);
        this.config = config;
        this.rogueRandom = rogueRandom;
        this.initializer = initializer;
        this.messageSystem = messageSystem;
        this.screen = screen;
        init();
    }

    private void init() {
        this.messageSystem.setGameState(this);
        if (this.initializer != null) {
            this.initializer.initialize(this);
        }
    }

    /**
     * Attempts to pick up an item from the floor at the player's current position.
     * Handles scare monster scrolls by removing them and displaying a message, or adds other items to the inventory.
     * Updates the map display and notifies the player with messages unless silent.
     * If the item cannot be picked up, displays a message indicating the action.
     */
    public void pickupItemFromFloor() {
        final boolean silent = false;
        final Position pos = getPlayer().getPosition();
        int x = pos.getX();
        int y = pos.getY();
        final Item item = findItemAt(x, y);
        if (item != null) {
            final Room room = currentLevel.findRoomAt(x, y);
            if (room != null) {
                // Check for and deal with scare monster scrolls
                if (item.getObjectType() == ObjectType.SCROLL && item.getItemSubType() == ScrollType.SCARE_MONSTER
                        && item.getItemFlags().contains(ItemFlag.ISFOUND)) {
                    screen.putChar(x, y, getFloorChar());
                    setPlaceCharAt(x, y, room.getChar());
                    currentLevel.removeItem(item);
                    messageSystem.msg(MSG_SCROLL_TURNS_TO_DUST);
                } else {
                    boolean itemAdded = addToPack(item, silent);
                    if (itemAdded) {
                        currentLevel.removeItem(item);
                        screen.putChar(x, y, getFloorChar());
                        setPlaceCharAt(x, y, room.getChar());
                    } else {
                        // Notify player if item cannot be picked up
                        if (!config.isTerse()) {
                            messageSystem.addmssg(Messages.MSG_YOU + " ");
                        }
                        final boolean dropCapital = true;
                        final String itemName = getPlayer().getInventory().getItemName(item, dropCapital);
                        messageSystem.msg(String.format(Messages.MSG_MOVED_ONTO, itemName));
                    }
                }
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Finds an item at the specified coordinates.
     *
     * @param x The x-coordinate to search.
     * @param y The y-coordinate to search.
     * @return The Item at the coordinates, or null if none is found.
     */
    @Nullable
    private Item findItemAt(final int x, final int y) {
        final Position position = new Position(x, y);
        final Item foundItem = currentLevel.getItems().stream()
                .filter(item -> item.getPosition().equals(position))
                .findFirst()
                .orElse(null);
        if (foundItem == null && config.isMaster()) {
            messageSystem.msg(String.format("Non-object %d,%d", position.getY(), position.getX()));
        }
        return foundItem;
    }

    /**
     * Returns the character that should be rendered at the player's current position,
     * based on the room type and visibility.
     *
     * <p>If the player is in a corridor or the floor should be shown (e.g., lit room or non-blind state),
     * the actual room symbol is returned. Otherwise, the fallback symbol for an empty room is used.</p>
     *
     * @return the character representing the floor or empty space at the player's position
     */
    private char getFloorChar() {
        final Room room = currentLevel.findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        if (room != null && (room.hasFlag(RoomFlag.CORRIDOR) || showFloor())) {
            return room.getChar();
        } else {
            return SymbolMapper.getSymbol(RoomType.EMPTY);
        }
    }

    /**
     * Sets the display character for the given map coordinates on the current level.
     *
     * @param x      the x-coordinate
     * @param y      the y-coordinate
     * @param symbol the character to place at the specified coordinates
     */
    private void setPlaceCharAt(final int x, final int y, char symbol) {
        currentLevel.setPlaceSymbol(x, y, symbol);
    }

    /**
     * Adds an item to the player's inventory and updates game state accordingly.
     * If the item is added successfully, redirects monsters targeting the item's position to the player.
     * Displays appropriate messages unless silent mode is enabled.
     *
     * @param item   The item to add to the inventory.
     * @param silent If true, suppresses message output.
     * @return true if the item was added, false if the inventory is full or addition failed.
     * @throws NullPointerException If the item is null.
     */
    public boolean addToPack(@Nonnull final Item item, final boolean silent) {
        Objects.requireNonNull(item);
        final boolean itemAdded = getPlayer().getInventory().addToPack(item);
        final Integer itemX = item.getPosition() == null ? null : item.getPosition().getX();
        final Integer itemY = item.getPosition() == null ? null : item.getPosition().getY();
        if (itemAdded) {
            // Redirect monsters targeting the item's position to the player
            if (itemX != null && itemY != null) {
                final Position itemPosition = new Position(itemX, itemY);
                for (Monster monster : currentLevel.getMonsters()) {
                    if (itemPosition.equals(monster.getDestination())) {
                        monster.setDestination(getPlayer().getPosition());
                    }
                }
            }
        }

        if (!silent) {
            if (itemAdded) {
                if (!config.isTerse()) {
                    messageSystem.addmssg(Messages.MSG_YOU_NOW_HAVE + " ");
                }
                final String itemName = String.format("%s (%c)",
                        getPlayer().getInventory().getItemName(item, !config.isTerse()),
                        item.getPackChar());
                messageSystem.msg(itemName);
            } else {
                if (!config.isTerse()) {
                    messageSystem.addmssg(Messages.MSG_THERES + " ");
                }
                messageSystem.addmssg(Messages.MSG_NO_ROOM);
                if (!config.isTerse()) {
                    messageSystem.addmssg(" " + Messages.MSG_IN_YOUR_PACK);
                }
            }
        }
        return itemAdded;
    }

    /**
     * Determines if the floor symbol in the player's current room should be rendered.
     *
     * <p>Only rooms that are not corridors and not dark, or if the player is not blind,
     * will have their floor shown depending on the config flag {@code seeFloor}.</p>
     *
     * @return true if the floor should be shown in the current room, false otherwise
     */
    private boolean showFloor() {
        final Room room = currentLevel.findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        if (room != null && room.hasFlag(RoomFlag.DARk) &&
                !room.hasFlag(RoomFlag.CORRIDOR) &&
                !player.hasFlag(CreatureFlag.ISBLIND)) {
            return config.isSeeFloor();
        }
        return true;
    }

    public void death() {
        this.goldAmount -= this.goldAmount / 10;
    }

    public void look(final boolean wakeUp) {
        //TODO: implement look(final boolean wakeUp)
    }

    public Config getConfig() {
        return config;
    }

    public RogueRandom getRogueRandom() {
        return rogueRandom;
    }

    public GameEndReason getGameEndReason() {
        return gameEndReason;
    }

    public void setGameEndReason(final GameEndReason gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public void setGoldAmount(final int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public DeathSource getDeathSource() {
        return deathSource;
    }

    public void setDeathSource(final DeathSource deathSource) {
        this.deathSource = deathSource;
    }

}