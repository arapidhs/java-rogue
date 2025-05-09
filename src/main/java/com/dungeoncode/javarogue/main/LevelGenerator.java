package com.dungeoncode.javarogue.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

public class LevelGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LevelGenerator.class);

    private final Config config;
    private final RogueRandom rogueRandom;
    private Level level;
    private int levelNum;

    public LevelGenerator(@Nonnull final Config config, @Nonnull RogueRandom rogueRandom) {
        this.config = config;
        this.rogueRandom = rogueRandom;
    }

    public Level newLevel(final int levelNum) {

        initializeLevel(levelNum);

        final Room[] rooms = doRooms();
        Arrays.stream(rooms).forEach(level::addRoom);

        final Passage[] passages = doPassages(rooms);
        Arrays.stream(passages).forEach(level::addPassage);

        // TODO continue with no_food, traps etc..
        return level;
    }

    /**
     * Initializes a new level with the specified level number and prepares the level grid.
     * Creates a new {@link Level} instance with dimensions defined by {@link Config#getLevelMaxWidth()}
     * and {@link Config#getLevelMaxHeight()}. Initializes the level's places array with empty space tiles
     * (' ') that have the {@link PlaceFlag#REAL} flag and no associated monster.
     *
     * @param levelNum The level number for this level (affects random generation behavior).
     */
    public void initializeLevel(final int levelNum) {
        this.level = new Level(config.getLevelMaxWidth(), config.getLevelMaxHeight());
        this.levelNum = levelNum;

        // Initialize places array with empty spaces and REAL flag
        for (int y = 0; y < config.getLevelMaxHeight(); y++) {
            for (int x = 0; x < config.getLevelMaxWidth(); x++) {
                final Place place = new Place(SymbolType.EMPTY);
                place.addFlag(PlaceFlag.REAL);
                level.setPlaceAt(x, y, place);
            }
        }
    }

    /**
     * Generates passages to connect rooms in the game level, ensuring a navigable dungeon layout.
     * Constructs a spanning tree to connect all rooms and adds extra passages to create cycles,
     * enhancing level variety. Each passage is drawn as a corridor with doors or passage tiles
     * at endpoints, and passages are numbered for game logic.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Initializes an array of {@link Passage} objects to store passage metadata.</li>
     *   <li>Creates a connectivity graph representing possible room connections in a 3x3 grid.</li>
     *   <li>Resets the graph to clear existing connections.</li>
     *   <li>Builds a spanning tree by connecting rooms randomly until all are included, using
     *       {@link #conn(int, int, Room[])} to draw corridors.</li>
     *   <li>Adds up to 5 extra passages between unconnected adjacent rooms to create cycles.</li>
     *   <li>Numbers passages and stores door positions using {@link #passnum(Passage[])}.</li>
     * </ol>
     *
     * @param rooms The array of {@link Room} objects to connect. Must not be null and should
     *              contain exactly {@link Config#getMaxRooms()} elements.
     * @return An array of {@link Passage} objects representing the passages created, with door
     * positions stored in their exits.
     * @throws NullPointerException if {@code rooms} is null.
     * @see Room
     * @see Passage
     * @see #conn(int, int, Room[])
     * @see #passnum(Passage[])
     */
    //TODO unit test for doPassages and all inner methods
    // (bottom up approach - requires doRooms unittest first)
    public Passage[] doPassages(@Nonnull final Room[] rooms) {
        Objects.requireNonNull(rooms);
        final Passage[] passages = new Passage[config.getMaxPassages()];
        for (int i = 0; i < passages.length; i++) {
            passages[i] = new Passage();
        }

        // Define room connectivity graph structure
        class RoomDescriptor {
            final boolean[] conn;    // Possible connections to other rooms
            final boolean[] isConn;  // Actual connections made
            boolean inGraph;   // Is room in the connected graph?

            RoomDescriptor(boolean[] connections) {
                this.conn = connections;
                this.isConn = new boolean[config.getMaxRooms()];
                this.inGraph = false;
            }
        }

        // Initialize room connectivity graph (3x3 grid adjacency)
        final RoomDescriptor[] roomDescriptors = new RoomDescriptor[config.getMaxRooms()];
        roomDescriptors[0] = new RoomDescriptor(new boolean[]{false, true, false, true, false, false, false, false, false});
        roomDescriptors[1] = new RoomDescriptor(new boolean[]{true, false, true, false, true, false, false, false, false});
        roomDescriptors[2] = new RoomDescriptor(new boolean[]{false, true, false, false, false, true, false, false, false});
        roomDescriptors[3] = new RoomDescriptor(new boolean[]{true, false, false, false, true, false, true, false, false});
        roomDescriptors[4] = new RoomDescriptor(new boolean[]{false, true, false, true, false, true, false, true, false});
        roomDescriptors[5] = new RoomDescriptor(new boolean[]{false, false, true, false, true, false, false, false, true});
        roomDescriptors[6] = new RoomDescriptor(new boolean[]{false, false, false, true, false, false, false, true, false});
        roomDescriptors[7] = new RoomDescriptor(new boolean[]{false, false, false, false, true, false, true, false, true});
        roomDescriptors[8] = new RoomDescriptor(new boolean[]{false, false, false, false, false, true, false, true, false});

        // Reset room connectivity graph
        for (RoomDescriptor rd : roomDescriptors) {
            // Clear actual connections
            Arrays.fill(rd.isConn, false);
            // Mark room as not in graph
            rd.inGraph = false;
        }

        // Build spanning tree to connect all rooms
        int roomCount = 1; // Number of rooms in the graph
        // Pick a random starting room
        RoomDescriptor r1 = roomDescriptors[rnd(config.getMaxRooms())];
        // Mark it as part of the graph
        r1.inGraph = true;
        do {
            // Find a random unconnected adjacent room
            int validConnections = 0;
            RoomDescriptor r2 = null;
            for (int i = 0; i < config.getMaxRooms(); i++) {
                // Check if room with index i is connectable and not in graph
                if (r1.conn[i] && !roomDescriptors[i].inGraph && rnd(++validConnections) == 0) {
                    r2 = roomDescriptors[i];
                }
            }
            // If no unconnected adjacent rooms, pick a new room from graph
            if (validConnections == 0) {
                do {
                    r1 = roomDescriptors[rnd(config.getMaxRooms())];
                } while (!r1.inGraph);
            }
            // Otherwise, connect the selected room
            else {
                // Mark new room as part of graph
                assert r2 != null;
                r2.inGraph = true;
                // Get room indices
                int r1Index = -1, r2Index = -1;
                for (int k = 0; k < roomDescriptors.length; k++) {
                    if (roomDescriptors[k] == r1) r1Index = k;
                    if (roomDescriptors[k] == r2) r2Index = k;
                }
                // Draw corridor between rooms
                conn(r1Index, r2Index, rooms);
                // Mark connection in both directions
                r1.isConn[r2Index] = true;
                r2.isConn[r1Index] = true;
                // Increment connected room count
                roomCount++;
            }
        } while (roomCount < config.getMaxRooms());

        // Add extra passages to create cycles in the graph
        int extraPassages = rnd(5); // Random number of extra passages (0-4)
        while (extraPassages > 0) {
            // Pick a random room to connect from
            final int r1Index = rnd(config.getMaxRooms());
            final RoomDescriptor r1Extra = roomDescriptors[r1Index];
            // Find a random unconnected adjacent room
            int validConnections = 0;
            RoomDescriptor r2Extra = null;
            for (int i = 0; i < config.getMaxRooms(); i++) {
                // Check if room with index i is adjacent and not yet connected
                if (r1Extra.conn[i] && !r1Extra.isConn[i] && rnd(++validConnections) == 0) {
                    r2Extra = roomDescriptors[i];
                }
            }
            // If an unconnected adjacent room was found, connect it
            if (validConnections > 0) {
                assert r2Extra != null;
                // Get room indices
                int r2Index = -1;
                for (int k = 0; k < roomDescriptors.length; k++) {
                    if (roomDescriptors[k] == r2Extra) r2Index = k;
                }
                // Draw corridor between rooms
                conn(r1Index, r2Index, rooms);
                // Mark connection in both directions
                r1Extra.isConn[r2Index] = true;
                r2Extra.isConn[r1Index] = true;
            }
            // Decrease remaining extra passages
            extraPassages--;
        }

        // Number all passages and store door positions
        passnum(passages);

        return passages;
    }

    // TODO unit test for doRooms
    public Room[] doRooms() {
        final int maxRoomX = getMaxRoomX();
        final int maxRoomY = getMaxRoomY();

        final int maxRooms = config.getMaxRooms();
        final Room[] rooms = new Room[maxRooms];
        Arrays.setAll(rooms, i -> new Room());

        // Put the gone rooms, if any, on the level
        final int leftOut = rnd(config.getMaxGoneRooms());
        for (int i = 0; i < leftOut; i++) {
            rooms[rndRoom(rooms)].addFlag(RoomFlag.GONE);
        }

        // dig and populate all the rooms on the level
        final Position topLeftCorner = new Position();
        for (int i = 0; i < rooms.length; i++) {
            final Room room = rooms[i];
            // Find upper left corner of box that this room goes in
            topLeftCorner.setX(i % 3 * maxRoomX + 1);
            topLeftCorner.setY(i / 3 * maxRoomY);
            if (room.hasFlag(RoomFlag.GONE)) {
                // Place a gone room.  Make certain that there is a blank line
                // for passage drawing.
                do {
                    final int posX = topLeftCorner.getX() + rnd(maxRoomX - 2) + 1;
                    final int posY = topLeftCorner.getY() + rnd(maxRoomY - 2) + 1;
                    room.setPosition(posX, posY);
                    room.setSize(-config.getTerminalCols(), -config.getTerminalRows());
                } while (room.getPosition().getY() <= 0 || room.getPosition().getY() >= config.getTerminalRows() - 1);
                continue;
            }
            // set room type
            if (rnd(10) < levelNum - 1) {
                room.addFlag(RoomFlag.DARK);
                if (rnd(15) == 0) {
                    room.addFlag(RoomFlag.MAZE);
                }
            }
            // Find a place and size for a random room
            if (room.hasFlag(RoomFlag.MAZE)) {
                setMazeRoomDimensions(room, maxRoomX, maxRoomY, topLeftCorner);
            } else {
                do {
                    int sizeX = rnd(maxRoomX - 4) + 4;
                    int sizeY = rnd(maxRoomY - 4) + 4;
                    room.setSize(sizeX, sizeY);
                    int posX = topLeftCorner.getX() + rnd(maxRoomX - sizeX);
                    int posY = topLeftCorner.getY() + rnd(maxRoomY - sizeY);
                    room.setPosition(posX, posY);
                } while (room.getY() == 0);
            }
            drawRoom(room);

            //TODO: Put the gold in
            //TODO: Put the monster in
        }
        return rooms;
    }

    public void setMazeRoomDimensions(@Nonnull final Room room, final int maxRoomX, final int maxRoomY,
                                      @Nonnull final Position position) {
        Objects.requireNonNull(room);
        Objects.requireNonNull(position);
        room.setSize(maxRoomX - 1, maxRoomY - 1);
        room.setPosition(position.getX(), position.getY());
        if (room.getPosition().getX() == 1) {
            room.getPosition().setX(0);
        }
        if (room.getPosition().getY() == 0) {
            room.getPosition().setY(room.getPosition().getY() + 1);
            room.getSize().setY(room.getSize().getY() - 1);
        }
    }

    public int getMaxRoomY() {
        return config.getTerminalRows() / 3;
    }

    public int getMaxRoomX() {
        return config.getTerminalCols() / 3;
    }

    // todo unit test for drawing a normal room since doMaze is already covered
    public void drawRoom(@Nonnull final Room room) {
        Objects.requireNonNull(room);
        if (room.hasFlag(RoomFlag.MAZE)) {
            doMaze(room);
        } else {
            // Draw left and right vertical walls
            vert(room, room.getPosition().getX()); // Left side
            vert(room, room.getPosition().getX() + room.getSize().getX() - 1); // Right side

            // Draw top and bottom horizontal walls
            horiz(room, room.getPosition().getY()); // Top
            horiz(room, room.getPosition().getY() + room.getSize().getY() - 1); // Bottom

            // Fill the interior with floor tiles
            for (int y = room.getPosition().getY() + 1; y < room.getPosition().getY() + room.getSize().getY() - 1; y++) {
                for (int x = room.getPosition().getX() + 1; x < room.getPosition().getX() + room.getSize().getX() - 1; x++) {
                    final Place place = level.getPlaceAt(x,y);
                    place.setSymbolType(SymbolType.FLOOR); // '.'
                }
            }
        }
    }

    public void doMaze(@Nonnull final Room room) {
        Objects.requireNonNull(room);
        final Spot[][] maze = initializeMaze();
        final int maxy = room.getSize().getY();
        final int maxx = room.getSize().getX();
        final int topy = room.getPosition().getY();
        final int topx = room.getPosition().getX();
        final int starty = (rnd(room.getSize().getY()) / 2) * 2;
        final int startx = (rnd(room.getSize().getX()) / 2) * 2;
        final Position position = new Position(startx + topx, starty + topy);
        putPass(position);
        dig(maze, startx, starty, topx, topy, maxx, maxy);
    }

    public Spot[][] initializeMaze() {
        final Spot[][] maze = new Spot[getMaxRoomY()][getMaxRoomX()];
        // Initialize maze array with Spot objects
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                maze[y][x] = new Spot();
            }
        }
        return maze;
    }

    /**
     * Digs passages in the maze using a recursive backtracking algorithm, starting from the given cell.
     * Explores neighboring cells in the four cardinal directions (up, down, left, right) in random order,
     * creating passages and recording connections between cells. Continues until no valid neighbors are available.
     *
     * @param maze   The maze grid of Spot objects.
     * @param startx The initial x-coordinate in the maze grid (relative to the room).
     * @param starty The initial y-coordinate in the maze grid (relative to the room).
     * @param topx   The x-coordinate of the room’s top-left corner in the game grid - 'Startx' in the original code.
     * @param topy   The y-coordinate of the room’s top-left corner in the game grid. - 'Starty' in the original code.
     * @param maxx   The width of the room in the maze grid.
     * @param maxy   The height of the room in the maze grid.
     */
    public void dig(@Nonnull final Spot[][] maze, final int startx, final int starty,
                    final int topx, final int topy, final int maxx, final int maxy) {
        Objects.requireNonNull(maze);
        // Define the four possible directions to dig (up, down, right, left) with steps of 2
        final Position[] directions = {
                new Position(0, 2),   // Down
                new Position(0, -2),  // Up
                new Position(2, 0),   // Right
                new Position(-2, 0)   // Left
        };

        while (true) {
            int validDirections = 0;
            int nextY = 0;
            int nextX = 0;

            // Check each direction for a valid neighbor to dig into
            for (Position dir : directions) {
                int newY = starty + dir.getY();
                int newX = startx + dir.getX();

                // Skip if the neighbor is out of bounds
                if (newY < 0 || newY > maxy || newX < 0 || newX > maxx) {
                    continue;
                }

                if (level.getPlaceAt(newX + topx, newY + topy).isType(PlaceType.PASSAGE)) {
                    continue;
                }

                // Randomly select this direction as the next to dig (with probability 1/validDirections)
                if (rnd(++validDirections) == 0) {
                    nextY = newY;
                    nextX = newX;
                }
            }

            // If no valid directions are available, stop digging from this cell
            if (validDirections == 0) {
                return;
            }

            // Record bidirectional connections between the current cell and the next cell
            accntMaze(maze, starty, startx, nextY, nextX);
            accntMaze(maze, nextY, nextX, starty, startx);

            // Place a passage at the intermediate position (between current and next cell)
            final Position pos = new Position();
            if (nextY == starty) {
                // Moving horizontally (left or right)
                pos.setY(starty + topy);
                pos.setX(nextX < startx ? nextX + topx + 1 : nextX + topx - 1);
            } else {
                // Moving vertically (up or down)
                pos.setX(startx + topx);
                pos.setY(nextY < starty ? nextY + topy + 1 : nextY + topy - 1);
            }
            putPass(pos);

            // Place a passage at the next cell
            pos.setY(nextY + topy);
            pos.setX(nextX + topx);
            putPass(pos);

            nextX=Math.min(nextX,maxx-1);
            nextY=Math.min(nextY,maxy-1);
            // Recursively dig from the next cell
            dig(maze, nextX, nextY, topx, topy, maxx, maxy);
        }
    }

    public void putPass(@Nonnull final Position position) {
        final Place place = level.getPlaceAt(position.getX(), position.getY());
        assert place!=null;
        place.setPlaceType(PlaceType.PASSAGE);
        if ((rnd(10) + 1) < levelNum && rnd(40) == 0) {
            place.removeFlag(PlaceFlag.REAL);
            place.setSymbolType(SymbolType.EMPTY);
        } else {
            place.setSymbolType(SymbolType.PASSAGE);
        }
        level.setPlaceAt(position.getX(), position.getY(), place);
    }

    /**
     * Draws a vertical wall for a room at the specified x-coordinate.
     *
     * @param room   The room being drawn.
     * @param startx The x-coordinate of the vertical wall.
     */
    public void vert(@Nonnull final Room room, int startx) {
        Objects.requireNonNull(room);
        for (int y = room.getPosition().getY() + 1; y <= room.getPosition().getY() + room.getSize().getY() - 1; y++) {
            final Place place = level.getPlaceAt(startx,y);
            assert place!=null;
            place.setPlaceType(PlaceType.WALL);
            place.setSymbolType(SymbolType.WALL_VERTICAL);
        }
    }

    /**
     * Draws a horizontal wall for a room at the specified y-coordinate.
     *
     * @param room   The room being drawn.
     * @param starty The y-coordinate of the horizontal wall.
     */
    public void horiz(@Nonnull final Room room, int starty) {
        Objects.requireNonNull(room);
        for (int x = room.getPosition().getX(); x <= room.getPosition().getX() + room.getSize().getX() - 1; x++) {
            final Place place = level.getPlaceAt(x,starty);
            assert place!=null;
            place.setPlaceType(PlaceType.WALL);
            place.setSymbolType(SymbolType.WALL_HORIZONTAL);
        }
    }

    /**
     * Records a connection (exit) from the maze cell at (x, y) to the neighboring cell at (nx, ny).
     * Ensures the connection is not duplicated in the cell's exits list.
     *
     * @param maze The maze grid of Spot objects.
     * @param y    The y-coordinate of the current cell in the maze grid.
     * @param x    The x-coordinate of the current cell in the maze grid.
     * @param ny   The y-coordinate of the neighboring cell.
     * @param nx   The x-coordinate of the neighboring cell.
     */
    public void accntMaze(@Nonnull final Spot[][] maze, final int y, final int x, final int ny, final int nx) {
        Objects.requireNonNull(maze);
        final Spot sp = maze[y][x];
        final Position[] exits = sp.getExits();
        final int nexits = sp.getNexits();

        // Check if (ny, nx) is already listed as an exit to avoid duplicates
        for (int i = 0; i < nexits; i++) {
            if (exits[i].getY() == ny && exits[i].getX() == nx) {
                return; // Exit already exists, no need to add
            }
        }

        // Add the new exit (ny, nx) to the cell's exits list
        sp.addExit(new Position(nx, ny));
    }

    /**
     * Draws a corridor between two rooms, placing doors or passages at the endpoints.
     * The corridor moves either right ('r') or down ('d') with a random turn to connect the rooms.
     *
     * @param r1    Index of the first room in the rooms array.
     * @param r2    Index of the second room in the rooms array.
     * @param rooms Array of rooms in the level.
     * @throws IllegalArgumentException If room indices are invalid.
     * @throws IllegalStateException    If destination room index is out of bounds.
     */
    public void conn(final int r1, final int r2, @Nonnull final Room[] rooms) {
        // Validate input parameters
        Objects.requireNonNull(rooms);
        if (r1 < 0 || r1 >= rooms.length || r2 < 0 || r2 >= rooms.length) {
            throw new IllegalArgumentException("Invalid room indices: r1=" + r1 + ", r2=" + r2);
        }

        // Initialize variables
        Room rpf; // Starting room (from)
        Room rpt; // Destination room (to)
        int rm;   // Index of starting room
        int rmt;  // Index of destination room
        char direc; // Direction: 'r' (right) or 'd' (down)
        final Position del = new Position();       // Movement direction (delta)
        final Position curr = new Position();      // Current position while drawing
        final Position turnDelta = new Position(); // Turn direction
        final Position spos = new Position();      // Start position (door/passage of rpf)
        final Position epos = new Position();      // End position (door/passage of rpt)
        int distance;     // Steps to move in primary direction
        int turnSpot;     // Where to make the turn
        int turnDistance; // Steps to move in turn direction

        // Explanation: Lower Index Selection
        // The C code always uses the lower room index as the starting room (rpf) to ensure
        // consistent corridor drawing. For example:
        // - If r1 < r2, r1 is the starting room, and we move right ('r') if r1 + 1 == r2
        //   (horizontally adjacent) or down ('d') if r1 + 3 == r2 (vertically below).
        // - If r2 < r1, r2 is the starting room, and we move right ('r') if r2 + 1 == r1
        //   or down ('d') if r2 + 3 == r1.
        // This avoids duplicate logic by normalizing the direction based on the lower index.
        // For example, connecting rooms 0 and 1 (right) is the same as 1 and 0, but we
        // always start from the lower index (0) and move right.

        // Select the lower index as the starting room and determine direction
        if (r1 < r2) {
            rm = r1; // Use r1 as starting room
            direc = (r1 + 1 == r2) ? 'r' : 'd'; // 'r' if adjacent horizontally, 'd' if vertically
        } else {
            rm = r2; // Use r2 as starting room
            direc = (r2 + 1 == r1) ? 'r' : 'd'; // 'r' if adjacent horizontally, 'd' if vertically
        }
        rpf = rooms[rm]; // Starting room

        // Explanation: Movement Set up
        // The corridor is drawn from spos (start position, door of rpf) to epos (end position,
        // door of rpt). The movement is split into:
        // 1. Primary movement (del): Move right (del.x = 1) for 'r' or down (del.y = 1) for 'd'.
        // 2. Turn movement (turnDelta): At a random turnSpot, switch to a perpendicular direction
        //    (e.g., left/right for 'd', up/down for 'r') to align with the destination room.
        // - For 'd': Move down until turnSpot, then turn left/right to reach epos.x.
        // - For 'r': Move right until turnSpot, then turn up/down to reach epos.y.
        // Variables:
        // - del: Primary movement direction (e.g., (0,1) for down).
        // - spos: Door position on rpf’s bottom ('d') or right ('r') wall.
        // - epos: Door position on rpt’s top ('d') or left ('r') wall.
        // - distance: Number of steps in the primary direction (excluding doors).
        // - turnDelta: Direction to turn (e.g., (-1,0) for left).
        // - turnDistance: Number of steps to turn to align with epos.
        // - turnSpot: Random point to make the turn (1 to distance-1).

        // Set up movement variables based on direction
        if (direc == 'd') {
            // Connect to room below (e.g., room 0 to 3)
            rmt = rm + 3;
            if (rmt >= rooms.length) {
                throw new IllegalStateException("Invalid destination room index: " + rmt);
            }
            rpt = rooms[rmt];
            del.setX(0);
            del.setY(1); // Move down
            // Start at rpf’s top-left corner (adjusted later if not gone)
            spos.setX(rpf.getPosition().getX());
            spos.setY(rpf.getPosition().getY());
            // End at rpt’s top-left corner (adjusted later if not gone)
            epos.setX(rpt.getPosition().getX());
            epos.setY(rpt.getPosition().getY());

            // Pick door positions for non-gone rooms
            if (!rpf.hasFlag(RoomFlag.GONE)) {
                do {
                    // Random x on bottom wall, fixed y at bottom edge
                    spos.setX(rpf.getPosition().getX() + rnd(rpf.getSize().getX() - 2) + 1);
                    spos.setY(rpf.getPosition().getY() + rpf.getSize().getY() - 1);
                } while (rpf.hasFlag(RoomFlag.MAZE) &&
                        !level.getPlaceAt(spos.getX(), spos.getY()).isType(PlaceType.PASSAGE));
            }
            if (!rpt.hasFlag(RoomFlag.GONE)) {
                do {
                    // Random x on top wall, fixed y at top edge
                    epos.setX(rpt.getPosition().getX() + rnd(rpt.getSize().getX() - 2) + 1);
                    epos.setY(rpt.getPosition().getY());
                } while (rpt.hasFlag(RoomFlag.MAZE) &&
                        !level.getPlaceAt(epos.getX(), epos.getY()).isType(PlaceType.PASSAGE));
            }

            // Distance to move down (excluding doors)
            distance = Math.abs(spos.getY() - epos.getY()) - 1;
            // Turn left (-1) or right (+1) to align with epos.x
            turnDelta.setY(0);
            turnDelta.setX(spos.getX() < epos.getX() ? 1 : -1);
            // Number of steps to turn
            turnDistance = Math.abs(spos.getX() - epos.getX());
        } else { // direc == 'r'
            // Connect to room to the right (e.g., room 0 to 1)
            rmt = rm + 1;
            if (rmt >= rooms.length) {
                throw new IllegalStateException("Invalid destination room index: " + rmt);
            }
            rpt = rooms[rmt];
            del.setX(1);
            del.setY(0); // Move right
            // Start at rpf’s top-left corner
            spos.setX(rpf.getPosition().getX());
            spos.setY(rpf.getPosition().getY());
            // End at rpt’s top-left corner
            epos.setX(rpt.getPosition().getX());
            epos.setY(rpt.getPosition().getY());
            // Pick door positions for non-gone rooms
            if (!rpf.hasFlag(RoomFlag.GONE)) {
                do {
                    // Fixed x at right edge, random y on right wall
                    spos.setX(rpf.getPosition().getX() + rpf.getSize().getX() - 1);
                    spos.setY(rpf.getPosition().getY() + rnd(rpf.getSize().getY() - 2) + 1);
                } while (rpf.hasFlag(RoomFlag.MAZE) &&
                        !level.getPlaceAt(spos.getX(), spos.getY()).isType(PlaceType.PASSAGE));
            }
            if (!rpt.hasFlag(RoomFlag.GONE)) {
                do {
                    // Fixed x at left edge, random y on left wall
                    epos.setX(rpt.getPosition().getX());
                    epos.setY(rpt.getPosition().getY() + rnd(rpt.getSize().getY() - 2) + 1);
                } while (rpt.hasFlag(RoomFlag.MAZE) &&
                        !level.getPlaceAt(epos.getX(), epos.getY()).isType(PlaceType.PASSAGE));
            }
            // Distance to move right (excluding doors)
            distance = Math.abs(spos.getX() - epos.getX()) - 1;
            // Turn up (-1) or down (+1) to align with epos.y
            turnDelta.setY(spos.getY() < epos.getY() ? 1 : -1);
            turnDelta.setX(0);
            // Number of steps to turn
            turnDistance = Math.abs(spos.getY() - epos.getY());
        }

        // Pick a random turn point
        turnSpot = rnd(distance - 1) + 1;

        // Place doors or passages at endpoints
        if (!rpf.hasFlag(RoomFlag.GONE)) {
            door(rpf, spos); // Place door at start
        } else {
            putPass(spos); // Place passage for gone rooms
        }
        if (!rpt.hasFlag(RoomFlag.GONE)) {
            door(rpt, epos); // Place door at end
        } else {
            putPass(epos); // Place passage for gone rooms
        }

        // Explanation: Movement Execution
        // The corridor is drawn by moving from spos to epos:
        // 1. Start at curr = spos.
        // 2. Move in the primary direction (del) for 'distance' steps, placing passages.
        // 3. At turnSpot, switch to turnDelta for turnDistance steps to align with epos.
        // 4. Continue moving in del until reaching epos (excluding the endpoint).
        // 5. Place a final passage and check if curr equals epos.
        // For example, for 'd' from room 0 to 3:
        // - Move down (0,1) from spos (e.g., (7,9)) until turnSpot.
        // - Turn left (-1,0) or right (1,0) to align with epos.x.
        // - Continue down to just before epos (e.g., (x,11)).
        // The endpoint (epos) already has a door/passage from the earlier placement.

        // Draw the corridor
        curr.setX(spos.getX());
        curr.setY(spos.getY());
        while (distance > 0) {
            // Move one step in primary direction
            curr.setX(curr.getX() + del.getX());
            curr.setY(curr.getY() + del.getY());
            // Check if at turn point
            if (distance == turnSpot) {
                // Make the turn
                while (turnDistance-- > 0) {
                    putPass(curr); // Place passage during turn
                    curr.setX(curr.getX() + turnDelta.getX());
                    curr.setY(curr.getY() + turnDelta.getY());
                }
            }
            putPass(curr); // Place passage in primary direction
            distance--;
        }
        // Final step to reach just before epos
        curr.setX(curr.getX() + del.getX());
        curr.setY(curr.getY() + del.getY());

        // Validate connectivity
        if (!curr.equals(epos)) {
            // Log warning if final position doesn’t match endpoint
            LOGGER.debug("Warning: Connectivity problem on this level at ({},{}) to ({},{})",
                    curr.getX(), curr.getY(), epos.getX(), epos.getY());
        }
    }

    /**
     * Adds a door at the specified position in a room, updating the room's exits.
     * For non-maze rooms, places a door tile ('+') or a secret door ('-' or '|' with hidden nature).
     *
     * @param room The room to place the door in.
     * @param pos  The position of the door.
     */
    public void door(@Nonnull final Room room, @Nonnull final Position pos) {
        Objects.requireNonNull(room);
        Objects.requireNonNull(pos);

        // Add door position to room's exits (mimics r_exit[r_nexits++])
        room.getExits().add(new Position(pos.getX(), pos.getY()));

        // Skip tile placement for maze rooms
        if (room.hasFlag(RoomFlag.MAZE)) {
            return;
        }

        // Get or create the Place at the door's position
        final Place place = level.getPlaceAt(pos.getX(), pos.getY());
        assert place!=null;

        // Determine if it's a secret door based on level and random chance
        if (rnd(10) + 1 < levelNum && rnd(5) == 0) {
            // Secret door: disguise as a wall
            place.removeFlag(PlaceFlag.REAL); // Hide true nature
            place.setPlaceType(PlaceType.WALL);
            if (pos.getY() == room.getPosition().getY() ||
                    pos.getY() == room.getPosition().getY() + room.getSize().getY() - 1) {
                place.setSymbolType(SymbolType.WALL_HORIZONTAL); // '-'
            } else {
                place.setSymbolType(SymbolType.WALL_VERTICAL); // '|'
            }
        } else {
            // Normal door
            place.setPlaceType(PlaceType.DOOR);
            place.setSymbolType(SymbolType.DOOR); // '+'
        }

    }

    /**
     * Assigns unique numbers to all passages by iterating over room exits and triggering
     * passage numbering for connected passage and door tiles.
     *
     * @param passages Array of passages to reset and store exit positions.
     */
    private void passnum(@Nonnull final Passage[] passages) {
        // Validate input
        Objects.requireNonNull(passages);

        // Initialize passage numbering state
        final PassageNumberState state = new PassageNumberState();

        // Clear exits for all passages (mimics r_nexits = 0)
        for (Passage passage : passages) {
            passage.getExits().clear();
        }

        // Iterate over all rooms
        for (Room room : level.getRooms()) {
            // Process each exit (door position)
            for (Position exit : room.getExits()) {
                // Signal numpass to start a new passage number
                state.setNewPnum(true);
                // Number the passage starting at this exit
                numpass(exit, passages, state);
            }
        }
    }

    /**
     * Assigns a passage number to the tile at the given position and propagates it to
     * connected passage or door tiles using a flood-fill algorithm.
     *
     * @param pos      The position to start numbering.
     * @param passages Array of passages to store exit positions.
     * @param state    Passage numbering state (pnum and newPnum).
     */
    public void numpass(@Nonnull final Position pos, @Nonnull final Passage[] passages,
                        @Nonnull final PassageNumberState state) {
        // Validate inputs
        Objects.requireNonNull(pos);
        Objects.requireNonNull(passages);
        Objects.requireNonNull(state);

        // Check if position is within level bounds
        if (pos.getX() >= config.getTerminalCols() || pos.getX() < 0 ||
                pos.getY() >= config.getTerminalRows() || pos.getY() <= 0) {
            return;
        }

        // Get the Place at the position
        final Place place = level.getPlaceAt(pos.getX(), pos.getY());
        assert place!=null;

        // Skip if tile is already numbered
        if (place.getPassageNumber()!=null) {
            return;
        }

        // If newPnum is true, increment pnum and reset newPnum
        if (state.isNewPnum()) {
            state.incrementPnum();
            state.setNewPnum(false);
        }

        // Check if tile is a door or secret door
        boolean isDoor = place.isType(PlaceType.DOOR);

        boolean isSecret = !place.isReal() && place.isType(PlaceType.WALL);

        // If door or secret door, add position to passage exits
        if (isDoor || isSecret) {
            // Ensure pnum is within passages array bounds
            if (state.getPnum() < passages.length) {
                final Passage passage = passages[state.getPnum()];
                passage.addExit(pos.getX(), pos.getY());
            }
        }
        // Skip if not a passage tile (unless it’s a door/secret door)
        else if (!place.isType(PlaceType.PASSAGE)) {
            return;
        }

        // Assign passage number to the passage and the tile
        final Passage passage = passages[state.getPnum()];
        passage.setPassageNumber(state.getPnum());
        place.setPassageNumber(state.getPnum());

        // Recursively number adjacent tiles
        numpass(new Position(pos.getX(), pos.getY() + 1), passages, state); // Down
        numpass(new Position(pos.getX(), pos.getY() - 1), passages, state); // Up
        numpass(new Position(pos.getX() + 1, pos.getY()), passages, state); // Right
        numpass(new Position(pos.getX() - 1, pos.getY()), passages, state); // Left
    }

    /**
     * Picks a valid 'real' non-gone room.
     * @param rooms array of all rooms.
     * @return index of a 'real' non-gone room.
     */
    public int rndRoom(Room[] rooms) {
        int rm;
        do {
            rm = rnd(rooms.length);
        } while (rooms[rm].getRoomFlags().contains(RoomFlag.GONE));
        return rm;
    }

    /**
     * @see RogueRandom#rnd(int)
     */
    private int rnd(final int range) {
        return rogueRandom.rnd(range);
    }

    public static class Spot {
        /**
         * Array of exit coordinates.
         */
        private final Position[] exits;
        /**
         * Number of exits.
         */
        private int nexits;

        public Spot() {
            this.nexits = 0;
            this.exits = new Position[4]; // Max 4 exits (north, south, east, west)
        }

        public int getNexits() {
            return nexits;
        }

        public void addExit(Position exit) {
            if (nexits < exits.length) {
                exits[nexits] = exit;
                nexits++;
            }
        }

        public Position[] getExits() {
            return exits;
        }

    }

    /**
     * Manages state for passage numbering in the level generation process.
     * Holds the current passage number ({@code pnum}) and a flag ({@code newPnum})
     * indicating whether a new passage number should be assigned. Used by
     * {@code passnum} and {@code numpass} to track and update passage numbers
     * during flood-fill numbering of connected passage and door tiles.
     */
    public static class PassageNumberState {
        private Integer pnum;
        private boolean newPnum;

        /**
         * Returns the current passage number.
         *
         * @return The current passage number.
         */
        public Integer getPnum() {
            return pnum;
        }

        /**
         * Increments the passage number by 1.
         */
        public void incrementPnum() {
            if ( this.pnum == null ){
                this.pnum=0;
            }else{
                this.pnum++;
            }
        }

        /**
         * Checks if a new passage number should be assigned.
         *
         * @return True if a new passage number is needed, false otherwise.
         */
        public boolean isNewPnum() {
            return newPnum;
        }

        /**
         * Sets the flag indicating whether a new passage number should be assigned.
         *
         * @param newPnum True to start a new passage number, false otherwise.
         */
        public void setNewPnum(boolean newPnum) {
            this.newPnum = newPnum;
        }
    }

    public Level getLevel() {
        return level;
    }
}
