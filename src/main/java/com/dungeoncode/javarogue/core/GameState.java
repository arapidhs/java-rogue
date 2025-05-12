package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.command.Command;
import com.dungeoncode.javarogue.command.CommandFactory;
import com.dungeoncode.javarogue.command.core.CommandEternal;
import com.dungeoncode.javarogue.command.core.CommandTimed;
import com.dungeoncode.javarogue.command.status.CommandSetupPlayerMovesPerTurn;
import com.dungeoncode.javarogue.command.system.CommandQuit;
import com.dungeoncode.javarogue.command.ui.CommandClearMessage;
import com.dungeoncode.javarogue.command.ui.CommandShowPlayerStatus;
import com.dungeoncode.javarogue.system.*;
import com.dungeoncode.javarogue.system.death.DeathSource;
import com.dungeoncode.javarogue.system.death.GameEndReason;
import com.dungeoncode.javarogue.system.entity.Position;
import com.dungeoncode.javarogue.system.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.system.entity.creature.Monster;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.entity.item.*;
import com.dungeoncode.javarogue.system.initializer.Initializer;
import com.dungeoncode.javarogue.system.world.*;
import com.dungeoncode.javarogue.template.MonsterTemplate;
import com.dungeoncode.javarogue.template.Templates;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameState {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);
    private static final String MSG_SCROLL_TURNS_TO_DUST = "the scroll turns to dust as you pick it up";

    private final Config config;
    private final RogueRandom rogueRandom;
    private final Initializer initializer;
    private final MessageSystem messageSystem;
    private final RogueScreen screen;
    private final WeaponsFactory weaponsFactory;
    private final ItemData itemData;
    private final Map<Phase, Boolean> phaseActivity;
    private final Queue<Command> commandQueue = new ConcurrentLinkedQueue<>();
    private Player player;
    private GameEndReason gameEndReason;
    private DeathSource deathSource;
    private int maxLevel;
    private int levelNum;
    private Level currentLevel;
    private boolean playing;
    private CommandFactory commandFactory;

    /**
     * Command count, number of times to repeat last command.
     * Equivalent of count variable in original Rogue code.
     **/
    private int count;

    /**
     * Fighting is to the death!
     * Equivalent of bool to_death = FALSE; in original Rogue code.
     */
    private boolean toDeath;

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
        phaseActivity = new HashMap<>();
        init();
    }

    private void init() {
        this.messageSystem.setGameState(this);
        if (this.initializer != null) {
            this.initializer.initialize(this);
        }
    }

    /**
     * Executes the main game loop, processing turns until the game ends. Each turn consists of five phases:
     * <ul>
     *   <li><b>START_TURN</b>: Executes pre-player actions (e.g., monster movement, setting player move count).</li>
     *   <li><b>UPKEEP_TURN</b>: Executes status updates (e.g., player status display).</li>
     *   <li><b>INPUT_CLEANUP_TURN</b>: Executes cleanup after player input (e.g., clearing messages).</li>
     *   <li><b>MAIN_TURN</b>: Processes player input and executes player commands, repeating based on the player's move count.</li>
     *   <li><b>END_TURN</b>: Executes post-player actions (e.g., status effects).</li>
     * </ul>
     * Phases can be enabled or disabled using {@link #enablePhase(Phase)} and {@link #disablePhase(Phase)}.
     * Commands are handled based on their type:
     * <ul>
     *   <li>{@link CommandTimed}: Decrements timer, executes when ready, then removed if successful.</li>
     *   <li>{@link CommandEternal}: Executes every turn, remains in the queue.</li>
     *   <li>Other commands: Executes once, removed if successful.</li>
     * </ul>
     * Player commands are executed up to the player's move count (set by {@link CommandSetupPlayerMovesPerTurn}, default 1, increased by {@link CreatureFlag#ISHASTE}).
     * Retries input on {@link KeyType#Escape} (e.g., from Ctrl+C) or if no command is executed.
     * Mirrors the turn-based loop in C Rogue (main.c), with pre-player actions (monsters.c), player input (command.c), and post-player updates (daemon.c).
     */
    public void loop() {
        this.playing = true;
        this.commandFactory = new CommandFactory();

        addCommand(new CommandSetupPlayerMovesPerTurn());
        addCommand(new CommandShowPlayerStatus());
        addCommand(new CommandClearMessage());

        while (playing) {

            processPhase(Phase.START_TURN);
            screen.refresh();

            processPhase(Phase.UPKEEP_TURN);
            screen.refresh();

            KeyStroke keyStroke;
            boolean commandExecuted = false;

            do {

                keyStroke = readChar();

                processPhase(Phase.INPUT_CLEANUP_TURN);

                if (!keyStroke.getKeyType().equals(KeyType.Escape)) {
                    final Command playerCommand = commandFactory.fromKeyStroke(keyStroke);
                    if (playerCommand != null) {
                        commandExecuted = playerCommand.execute(this);
                        if (commandExecuted) {
                            player.setNtimes(player.getNtimes() - 1);
                        }
                        screen.refresh();
                    }
                }
            } while (player.getNtimes() > 0 || !commandExecuted || keyStroke.getKeyType().equals(KeyType.Escape));

            processPhase(Phase.END_TURN);
            screen.refresh();
        }
    }

    public void addCommand(@Nonnull final Command command) {
        Objects.requireNonNull(command);
        commandQueue.offer(command);
    }

    /**
     * Processes all commands in the queue for the specified phase, if the phase is active.
     * Phases can be enabled or disabled using {@link #enablePhase(Phase)} and {@link #disablePhase(Phase)}.
     * Handles different command types:
     * <ul>
     *   <li>{@link CommandTimed}: Decrements the timer and executes when ready, then removed.</li>
     *   <li>{@link CommandEternal}: Executes every turn and remains in the queue.</li>
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
                if (command instanceof CommandTimed commandTimed) {
                    commandTimed.decrementTimer();
                    if (commandTimed.isReadyToExecute()) {
                        command.execute(this);
                        commandQueue.remove(command);
                    }
                } else {
                    command.execute(this);
                    if (!(command instanceof CommandEternal)) {
                        commandQueue.remove(command);
                    }
                }
            }
        });
    }

    /**
     * Reads a keystroke from the terminal, handling Ctrl+C by executing a quit command.
     * Returns the keystroke or an {@link KeyType#Escape} keystroke if Ctrl+C is pressed.
     * <p>
     * Equivalent to the <code>readchar</code> function in the C Rogue source, adapted for
     * Lanterna to process terminal input and handle interrupts.
     * </p>
     *
     * @return The read keystroke, or an Escape keystroke for Ctrl+C.
     * @throws RuntimeException if an I/O error occurs during input reading.
     */
    private KeyStroke readChar() {
        final KeyStroke keyStroke = screen.readInput();
        if (keyStroke.isCtrlDown() && keyStroke.getCharacter() != null && keyStroke.getCharacter() == 'c') {
            final CommandQuit commandQuit = new CommandQuit(false);
            commandQuit.execute(this);
            return new KeyStroke(KeyType.Escape);
        }
        return keyStroke;
    }

    /**
     * Finds the destination for a monster, prioritizing an untargeted item in the same room
     * or defaulting to the player’s position. Returns the player’s position if the monster
     * has no carry probability, is in the player’s room, or is visible to the player.
     * Otherwise, selects an item in the monster’s room (excluding scare monster scrolls)
     * with a probability check, ensuring no other monster targets it.
     * <p>
     * Equivalent to the <code>find_dest</code> function in the C Rogue source (chase.c).
     * </p>
     *
     * @param monster The monster to find a destination for.
     * @return The destination position (item or player).
     * @throws NullPointerException if monster is null.
     */
    public Position findDest(@Nonnull final Monster monster) {
        Objects.requireNonNull(monster);
        final MonsterTemplate monsterTemplate = Templates.getTemplates(MonsterTemplate.class)
                .stream()
                .filter(template -> template.getMonsterType() == monster.getMonsterType())
                .findFirst()
                .orElse(null);
        assert monsterTemplate != null; // Assumes template exists for all MonsterType values
        final int prob = monsterTemplate.getCarryProbability();
        final boolean sameRoom = Objects.equals(monster.getRoom(), player.getRoom());
        if (prob <= 0 || sameRoom || seeMonst(monster)) {
            return player.getPosition(); // Default to player if no carry chance, same room, or visible
        }
        for (Item item : currentLevel.getItems()) {
            if (Objects.equals(ObjectType.SCROLL, item.getObjectType()) &&
                    Objects.equals(ScrollType.SCARE_MONSTER, item.getItemSubType())) {
                continue; // Skip scare monster scrolls
            }
            final int ix = item.getPosition().getX();
            final int iy = item.getPosition().getY();
            final Room itemRoom = roomIn(ix, iy);
            if (Objects.equals(itemRoom, monster.getRoom()) && rogueRandom.rnd(100) < prob) {
                // Check if another monster targets this item’s position
                boolean found = currentLevel.getMonsters().stream().anyMatch(
                        m -> m.getDestination() != null && m.getDestination().equals(item.getPosition()));
                if (!found) {
                    return item.getPosition(); // Return item position if untargeted
                }
            }
        }
        return player.getPosition(); // Fallback to player if no suitable item found
    }

    /**
     * Determines if the player can see the specified monster, considering blindness,
     * invisibility, proximity, and room visibility.
     * <p>
     * Returns false if the player is blind, the monster is invisible and the player
     * cannot see invisible creatures, the monster is in a different room, or the room
     * is dark. Returns true if the monster is within the lamp distance (checked via
     * squared distance) and either in the same row/column or accessible via steppable
     * tiles, or if in the same non-dark room.
     * </p>
     * <p>
     * Equivalent to the <code>see_monst</code> function in the C Rogue source, with
     * adjusted step logic to check for walls and empty spaces.
     * </p>
     *
     * @param monster The monster to check visibility for.
     * @return True if the player can see the monster, false otherwise.
     * @throws NullPointerException if monster is null.
     */
    public boolean seeMonst(@Nonnull final Monster monster) {
        Objects.requireNonNull(monster);
        if (player.hasFlag(CreatureFlag.ISBLIND)) {
            return false;
        }
        if (monster.hasFlag(CreatureFlag.ISINVIS) && !player.hasFlag(CreatureFlag.CANSEE)) {
            return false;
        }
        final int px = player.getX();
        final int py = player.getY();
        final int mx = monster.getX();
        final int my = monster.getY();
        final int dist = RogueUtils.dist(mx, my, px, py);
        if(dist<config.getLampDist()){
            final Place placepm = currentLevel.getPlaceAt(px,my);
            final Place placemp = currentLevel.getPlaceAt(mx,py);
            assert placemp != null;
            assert placepm != null;
            return my == py || mx == px || placepm.isStepOk() || placemp.isStepOk();
        }
        if(!Objects.equals(monster.getRoom(),player.getRoom())){
            return false;
        }
        return !monster.getRoom().hasFlag(RoomFlag.DARK);
    }

    public MessageSystem getMessageSystem() {
        return messageSystem;
    }

    // TODO game state newLevel unit test
    public void newLevel(final int levelNum) {
        screen.clear();
        player.removeFlag(CreatureFlag.ISHELD);

        final LevelGenerator levelGenerator = new LevelGenerator(this);
        final Level level = levelGenerator.newLevel(levelNum);
        setCurrentLevel(level);

        final Position pos = level.findFloor(null, 0, true);
        assert pos != null;
        player.setPosition(pos.getX(), pos.getY());
        enterRoom(pos.getX(), pos.getY());
        screen.putChar(pos.getX(), pos.getY(), SymbolMapper.getSymbol(player.getClass()));
        screen.refresh();
    }

    // TODO temp enterRoom testing method, this should be the equivalent of enter_room(coord *cp)
    public void enterRoom(final int posX, final int posY) {
        final Room room = roomIn(posX, posY);
        player.setRoom(room);
        if (room != null && !room.hasFlag(RoomFlag.DARK)) {
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

    @Nullable
    public Room roomIn(final int x, final int y) {
        final Room room = currentLevel.roomIn(x, y);
        if (room == null) {
            messageSystem.msg(String.format("in some bizarre place (%d, %d)", x, y));
            LOGGER.debug("in some bizarre place ({}, {})", x, y);
            if (config.isMaster()) {
                abort();
            }
        }
        return room;
    }

    private void abort() {
        LOGGER.debug("Aborting java-rogue..");
        System.exit(1);
    }

    public int goldCalc(final int level) {
        return rogueRandom.rnd(50 + 10 * level) + 2;
    }

    //TODO: method to show map for debugging purpose only
    public void showMap() {
        Random random = new Random();
//        if(random.nextBoolean()){
//            screen.enableModifiers(SGR.BOLD);
//        } else {
//            screen.disableModifiers(SGR.BOLD);
//        }
        for (int x = 0; x < config.getTerminalCols(); x++) {
            for (int y = 1; y < config.getTerminalRows() - 1; y++) {
                final Place place = currentLevel.getPlaceAt(x, y);
                assert place != null;
                if (!place.isReal()) {
                    screen.enableModifiers(SGR.BOLD);
                }
                if (place.isType(PlaceType.PASSAGE)) {
                    screen.putChar(x, y, SymbolMapper.getSymbol(SymbolType.PASSAGE));
                } else {
                    screen.putChar(x, y, SymbolMapper.getSymbol(place.getSymbolType()));
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
                    currentLevel.setPlaceSymbol(x, y, room.getSymbolType());
                    currentLevel.removeItem(item);
                    messageSystem.msg(MSG_SCROLL_TURNS_TO_DUST);
                } else {
                    boolean itemAdded = addToPack(item, silent);
                    if (itemAdded) {
                        currentLevel.removeItem(item);
                        screen.putChar(x, y, SymbolMapper.getSymbol(floorCh()));
                        currentLevel.setPlaceSymbol(x, y, room.getSymbolType());
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
    public SymbolType floorAt() {
        final Place place = currentLevel.getPlaceAt(player.getX(), player.getY());
        if (place != null) {
            if (place.getSymbolType().equals(SymbolType.FLOOR)) {
                return floorCh();
            }
            return place.getSymbolType();
        }
        return null;
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
    public void disablePhase(@Nonnull final Phase phase) {
        Objects.requireNonNull(phase);
        phaseActivity.put(phase, false);
    }

    public void death() {
        int goldAmount = player.getGoldAmount();
        goldAmount -= goldAmount / 10;
        player.setGoldAmount(goldAmount);
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

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(final int levelNum) {
        this.levelNum = levelNum;
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

    public void setCount(final int count) {
        this.count = count;
    }

    public void setToDeath(final boolean toDeath) {
        this.toDeath = toDeath;
    }
}