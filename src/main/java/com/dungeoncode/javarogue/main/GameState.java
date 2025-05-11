package com.dungeoncode.javarogue.main;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.input.KeyStroke;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameState {

    private static final String MSG_SCROLL_TURNS_TO_DUST = "the scroll turns to dust as you pick it up";

    private final Config config;
    private final RogueRandom rogueRandom;
    private final Initializer initializer;
    private final MessageSystem messageSystem;
    private final RogueScreen screen;
    private final WeaponsFactory weaponsFactory;
    private final ItemData itemData;
    private final Map<Phase, Boolean> phaseActivity;
    private Player player;
    private GameEndReason gameEndReason;
    private DeathSource deathSource;
    private int maxLevel;
    private int levelNum;
    private int goldAmount;
    private Level currentLevel;
    private boolean playing;

    private final Queue<Command> commandQueue = new ConcurrentLinkedQueue<>();
    private CommandFactory commandFactory;

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
        this.weaponsFactory = new WeaponsFactory(this.rogueRandom);
        this.itemData = new ItemData(config, rogueRandom);
        phaseActivity=new HashMap<>();
        init();
    }

    private void init() {
        this.messageSystem.setGameState(this);
        if (this.initializer != null) {
            this.initializer.initialize(this);
        }
    }

    /**
     * Executes the main game loop, processing turns until the game ends. Each turn consists of four phases:
     * <ul>
     *   <li><b>START_TURN</b>: Executes commands for pre-player actions (e.g., monster movement).</li>
     *   <li><b>UPKEEP_TURN</b>: Executes commands for status updates (e.g., player status display).</li>
     *   <li><b>MAIN_TURN</b>: Reads player input, queues the resulting command, and executes MAIN_TURN commands.</li>
     *   <li><b>END_TURN</b>: Executes commands for post-player actions (e.g., status effects).</li>
     * </ul>
     * Phases can be enabled or disabled using {@link #enablePhase(Phase)} and {@link #disablePhase(Phase)}.
     * Commands are handled based on their type:
     * <ul>
     *   <li>{@link TimedCommand}: Decrements the timer and executes when ready, then removed.</li>
     *   <li>{@link EternalCommand}: Executes every turn and remains in the queue.</li>
     *   <li>Other commands: Executes once and is removed.</li>
     * </ul>
     * This method mirrors the turn-based game loop in the original C Rogue source code (main.c),
     * with pre-player actions (monsters.c), player input processing (command.c), and post-player
     * updates (daemon.c), ensuring proper command ordering and persistence.
     */
    public void loop() {
        this.playing = true;
        this.commandFactory = new CommandFactory();
        addCommand(new CommandShowPlayerStatus());
        while (true) {
            processPhase(Phase.START_TURN);
            screen.refresh();
            if (!playing) {
                break;
            }

            processPhase(Phase.UPKEEP_TURN);
            screen.refresh();
            if (!playing) {
                break;
            }

            KeyStroke keyStroke = screen.readInput();
            final Command playerCommand = commandFactory.fromKeyStroke(keyStroke);
            if (playerCommand != null) {
                addCommand(playerCommand);
            }
            getMessageSystem().clearMessage();

            processPhase(Phase.MAIN_TURN);
            screen.refresh();
            if (!playing) {
                break;
            }

            processPhase(Phase.END_TURN);
            screen.refresh();
            if (!playing) {
                break;
            }

        }
    }

    /**
     * Processes all commands in the queue for the specified phase, if the phase is active.
     * Phases can be enabled or disabled using {@link #enablePhase(Phase)} and {@link #disablePhase(Phase)}.
     * Handles different command types:
     * <ul>
     *   <li>{@link TimedCommand}: Decrements the timer and executes when ready, then removed.</li>
     *   <li>{@link EternalCommand}: Executes every turn and remains in the queue.</li>
     *   <li>Other commands: Executes once and is removed.</li>
     * </ul>
     * This method supports the turn-based structure of the C Rogue source code (main.c),
     * processing commands in a specific phase (e.g., monster movement in monsters.c for
     * START_TURN, player actions in command.c for MAIN_TURN).
     *
     * @param phase The phase to process commands for (START_TURN, MAIN_TURN, or END_TURN).
     * @throws NullPointerException if phase is null.
     */
    public void processPhase(@Nonnull final Phase phase) {
        Objects.requireNonNull(phase);
        if (!phaseActivity.getOrDefault(phase, false)) {
            return;
        }
        commandQueue.forEach(command -> {
            if (command.getPhase() == phase) {
                if (command instanceof TimedCommand timedCommand) {
                    timedCommand.decrementTimer();
                    if (timedCommand.isReadyToExecute()) {
                        command.execute(this);
                        commandQueue.remove(command);
                    }
                } else {
                    command.execute(this);
                    if (!(command instanceof EternalCommand)) {
                        commandQueue.remove(command);
                    }
                }
            }
        });
    }

    public void addCommand(@Nonnull final Command command) {
        Objects.requireNonNull(command);
        commandQueue.offer(command);
    }

    // TODO newLevel unit test
    public void newLevel(final int levelNum) {
        screen.clear();
        player.removeFlag(CreatureFlag.ISHELD);

        final LevelGenerator levelGenerator = new LevelGenerator(this);
        final Level level = levelGenerator.newLevel(levelNum);
        setCurrentLevel(level);

        final Position pos = level.findFloor(null, 0, true);
        assert pos!=null;
        player.setPosition(pos.getX(), pos.getY());
        enterRoom(pos.getX(), pos.getY());
        screen.putChar(pos.getX(),pos.getY(),SymbolMapper.getSymbol(player.getClass()));
        screen.refresh();
    }

    @Nullable
    public Room roomIn(final int x, final int y) {
        final Room room = currentLevel.roomIn(x, y);
        if(room==null){
            messageSystem.msg(String.format("in some bizarre place (%d, %d)",x,y));
            if(config.isMaster()){
                abort();
            }
        }
        return room;
    }

    public boolean hasAmulet(){
        return player.getInventory().contains(ObjectType.AMULET);
    }

    public int goldCalc(final int level){
        return  rogueRandom.rnd(50+10*level)+2;
    }

    private void abort() {
        System.exit(1);
    }

    // TODO temporary testing method, this should be the equivalent of enter_room(coord *cp)
    public void enterRoom(final int posX, final int posY){
        final Room room = roomIn(posX,posY);
        if (room!=null && !room.hasFlag(RoomFlag.DARK)){
            for (int y = room.getY(); y < room.getY() + room.getSize().getY(); y++) {
                for (int x = room.getX(); x < room.getX() + room.getSize().getX(); x++) {
                    final Place place = currentLevel.getPlaceAt(x, y);
                    if (place != null) {
                        screen.putChar(x, y, SymbolMapper.getSymbol(place.getSymbolType()));
                    }
                }
            }
        }
    }

    //TODO: method to show map for debugging purpose only
    public void showMap() {
        for (int x = 0; x < config.getTerminalCols(); x++) {
            for (int y = 1; y < config.getTerminalRows() - 1; y++) {
                final Place place = currentLevel.getPlaceAt(x, y);
                assert place != null;
                if (!place.isReal()) {
                    screen.enableModifiers(SGR.BOLD);
                }
                if(place.isType(PlaceType.PASSAGE)) {
                    screen.putChar(x, y, SymbolMapper.getSymbol(SymbolType.PASSAGE));
                } else {
                    screen.putChar(x,y,SymbolMapper.getSymbol(place.getSymbolType()));
                }
                if (!place.isReal()) {
                    screen.disableModifiers(SGR.BOLD);
                }
            }
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
                    screen.putChar(x, y, SymbolMapper.getSymbol(floorCh()));
                    setPlaceSymbolTypeAt(x, y, room.getSymbolType());
                    currentLevel.removeItem(item);
                    messageSystem.msg(MSG_SCROLL_TURNS_TO_DUST);
                } else {
                    boolean itemAdded = addToPack(item, silent);
                    if (itemAdded) {
                        currentLevel.removeItem(item);
                        screen.putChar(x, y, SymbolMapper.getSymbol(floorCh()));
                        setPlaceSymbolTypeAt(x, y, room.getSymbolType());
                    } else {
                        // Notify player if item cannot be picked up
                        if (!config.isTerse()) {
                            messageSystem.addmssg("you ");
                        }
                        final boolean dropCapital = true;
                        final String itemName = getItemName(getPlayer(), item, dropCapital);
                        messageSystem.msg(String.format("moved onto %s", itemName));
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
     * Returns the symbol type that should be rendered at the player's current position,
     * based on the room type and visibility.
     * <p>
     * It is the equivalent of char floor_ch().
     *
     * <p>If the player is in a corridor or the floor should be shown (e.g., lit room or non-blind state),
     * the actual room symbol type is returned. Otherwise, the fallback symbol for an empty room is used.</p>
     *
     * @return the character representing the floor or empty space at the player's position
     */
    public SymbolType floorCh() {
        final Room room = currentLevel.findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        if (room != null && (room.hasFlag(RoomFlag.GONE) || showFloor())) {
            return room.getSymbolType();
        } else {
            return SymbolType.EMPTY;
        }
    }

    /**
     * Returns the symbol type to render at the player's current position, based on the place and room visibility.
     * <p>
     * Equivalent to C function <code>char floor_at()</code> in the Rogue source.
     * <p>
     * If the place at the player's position is a floor, returns the room's symbol type via {@link #floorCh()}
     * if visible (e.g., lit room or non-blind state). Otherwise, returns the place's symbol type.
     * Returns null if no place exists at the position.
     *
     * @return The symbol type to render at the player's position, or null if no place exists.
     */
    public SymbolType floorAt(){
        final Place place = currentLevel.getPlaceAt(player.getX(), player.getY());
        if(place!=null){
            if(place.getSymbolType().equals(SymbolType.FLOOR)){
                return floorCh();
            };
            return place.getSymbolType();
        }
        return null;
    }

    /**
     * Sets the display SymbolType for the given map coordinates on the current level.
     *
     * @param x      the x-coordinate
     * @param y      the y-coordinate
     * @param symbolType the Symbol Type to place at the specified coordinates
     * @see SymbolType
     */
    private void setPlaceSymbolTypeAt(final int x, final int y, @Nonnull SymbolType symbolType) {
        Objects.requireNonNull(symbolType);
        currentLevel.setPlaceSymbol(x, y, symbolType);
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
                    messageSystem.addmssg("you now have ");
                }
                final String itemName = String.format("%s (%c)",
                        getItemName(getPlayer(), item, !config.isTerse()),
                        SymbolMapper.getSymbol(item.getInventoryKey()));
                messageSystem.msg(itemName);
            } else {
                if (!config.isTerse()) {
                    messageSystem.addmssg("there's ");
                }
                messageSystem.addmssg("no room");
                if (!config.isTerse()) {
                    messageSystem.addmssg(" in your pack");
                }
                messageSystem.endmsg();
            }
        }
        return itemAdded;
    }

    /**
     * Returns the display name of the given inventory item.
     *
     * <p>This method is the Java equivalent of the original Rogue C function {@code inv_name()},
     * and is responsible for producing the textual representation of an item as it appears
     * in the player's inventory or during drop/pickup interactions.</p>
     *
     * @param player      the player
     * @param item        the non-null item to describe
     * @param dropCapital if {@code true}, the returned name will start with a lowercase letter
     *                    (typically when dropping or referencing in a sentence); if {@code false},
     *                    the name will start with an uppercase letter (e.g., in terse inventory lists)
     * @return the string name of the item, formatted for display
     */
    public String getItemName(@Nonnull Player player, @Nonnull final Item item, boolean dropCapital) {
        return itemData.invName(player, item, dropCapital);
    }

    /**
     * Determines if the floor symbol in the player's current room should be rendered.
     *
     * <p>It is the equivalent of bool show_floor()
     * <p>Only rooms that are not corridors and not dark, or if the player is not blind,
     * will have their floor shown depending on the config flag {@code seeFloor}.</p>
     *
     * @return true if the floor should be shown in the current room, false otherwise
     */
    public boolean showFloor() {
        final Room room = currentLevel.findRoomAt(player.getPosition().getX(), player.getPosition().getY());
        if (room != null && room.hasFlag(RoomFlag.DARK) &&
                !room.hasFlag(RoomFlag.GONE) &&
                !player.hasFlag(CreatureFlag.ISBLIND)) {
            return config.isSeeFloor();
        }
        return true;
    }

    /**
     * Enables the specified phase, allowing its commands to be processed in the game loop.
     *
     * @param phase The phase to enable (START_TURN, MAIN_TURN, or END_TURN).
     * @throws NullPointerException if phase is null.
     */
    public void enablePhase(@Nonnull final Phase phase) {
        Objects.requireNonNull(phase);
        phaseActivity.put(phase, true);
    }

    /**
     * Disables the specified phase, preventing its commands from being processed in the game loop.
     *
     * @param phase The phase to disable (START_TURN, MAIN_TURN, or END_TURN).
     * @throws NullPointerException if phase is null.
     */
    public void disablePhase(@Nonnull final  Phase phase) {
        Objects.requireNonNull(phase);
        phaseActivity.put(phase, false);
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

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(final int levelNum) {
        this.levelNum = levelNum;
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

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
        this.player.setCurrentLevel(currentLevel.getLevelNum());
    }

    public WeaponsFactory getWeaponsFactory() {
        return weaponsFactory;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public RogueScreen getScreen() {
        return screen;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Queue<Command> getCommandQueue() {
        return commandQueue;
    }

    public Map<Phase, Boolean> getPhaseActivity() {
        return phaseActivity;
    }

    public MessageSystem getMessageSystem() {
        return messageSystem;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
}