package io.github.lipinskipawel.board;

import java.util.ArrayList;
import java.util.List;

public final class FootballBitBoard {

    // Constants
    private static final int GRID_WIDTH = 9;
    private static final int GRID_HEIGHT = 13;
    private static final int TOTAL_NODES = GRID_WIDTH * GRID_HEIGHT; // 117
    private static final int LONGS_PER_BITBOARD = 2;  // ceil(117 / 64)
    private static final int TOTAL_DIR_LONGS = 8 * LONGS_PER_BITBOARD;  // 16

    public enum Direction {
        // dx, dy, index
        // dx = 0 do not move either left or right
        // dx = 1 move right
        // dx = -1 move left
        // dy = 0 do not move either up or down
        // dy = 1 move down
        // dy = -1 move up
        // index the position inside the dirMask
        N(0, -1, 0),
        NE(1, -1, 1),
        E(1, 0, 2),
        SE(1, 1, 3),
        S(0, 1, 4),
        SW(-1, 1, 5),
        W(-1, 0, 6),
        NW(-1, -1, 7);

        public final int dx, dy, index;

        Direction(int dx, int dy, int index) {
            this.dx = dx;
            this.dy = dy;
            this.index = index;
        }

        public Direction opposite() {
            return values()[(index + 4) % 8];
        }
    }

    // Neighbor lookup table
    private static final int[][] neighborIndex = new int[TOTAL_NODES][8];

    static {
        buildNeighborIndex();
    }

    public enum Player {
        SOUTH, NORTH
    }

    // Board state fields
    // indexes [0,1] -> represents direction N for 117 notes
    // indexes [2,3] -> represents direction NE for 117 notes
    // and so on
    private final long[] dirMask;         // 16 longs: 8 directions × 2 longs each
    private final long[] visited;         // 2 longs for visited nodes
    private int ballPosition;             // Direct ball position (0-116)
    private Player activePlayer;

    /**
     * Private constructor for copying
     */
    private FootballBitBoard() {
        dirMask = new long[TOTAL_DIR_LONGS];
        visited = new long[LONGS_PER_BITBOARD];
        ballPosition = -1;
        activePlayer = Player.SOUTH;
    }

    /**
     * Public constructor - initializes board with starting position
     */
    public FootballBitBoard(boolean initialize) {
        this();
        if (initialize) {
            initializeBoard(false);
        }
    }

    /**
     * Public constructor - initializes board with starting position
     */
    public FootballBitBoard(boolean initialize, boolean isFirstPlayerMove) {
        this();
        if (initialize) {
            initializeBoard(isFirstPlayerMove);
        }
    }

    /**
     * Copy constructor
     */
    private FootballBitBoard(FootballBitBoard other) {
        this.dirMask = new long[TOTAL_DIR_LONGS];
        this.visited = new long[LONGS_PER_BITBOARD];
        System.arraycopy(other.dirMask, 0, this.dirMask, 0, TOTAL_DIR_LONGS);
        System.arraycopy(other.visited, 0, this.visited, 0, LONGS_PER_BITBOARD);
        this.ballPosition = other.ballPosition;
        this.activePlayer = other.activePlayer;
    }

    /**
     * Create a copy of this board
     */
    public FootballBitBoard copy() {
        return new FootballBitBoard(this);
    }

    // ============= BITBOARD OPERATIONS =============

    /**
     * Set a bit in a direction mask
     */
    private void setDirBit(int dirIndex, int node) {
        int longIndex = (dirIndex << 1) + (node >>> 6);  // dirIndex * 2 + node / 64
        dirMask[longIndex] |= (1L << node);
    }

    /**
     * Clear a bit in a direction mask
     */
    private void clearDirBit(int dirIndex, int node) {
        int longIndex = (dirIndex << 1) + (node >>> 6);
        dirMask[longIndex] &= ~(1L << node);
    }

    /**
     * Test a bit in a direction mask
     */
    private boolean testDirBit(int dirIndex, int node) {
        // Note: (1L << node) works because Java only uses the lower 6 bits of the shift amount for long, so node % 64 is implicit!
        int longIndex = (dirIndex << 1) + (node >>> 6);
        return (dirMask[longIndex] & (1L << node)) != 0;
    }

    /**
     * Set a bit in visited mask
     */
    private void setBit(int node) {
        visited[node >>> 6] |= (1L << node);
    }

    /**
     * Test a bit in visited mask
     */
    private boolean testBit(int node) {
        return (visited[node >>> 6] & (1L << node)) != 0;
    }

    // ============= FIELD STRUCTURE (same as original) =============

    /**
     * Check if a position is valid on the playing field
     */
    private static boolean isValidPosition(int x, int y) {
        // Top goal
        if (y == 0 && x >= 3 && x <= 5) return true;
        // Bottom goal
        if (y == 12 && x >= 3 && x <= 5) return true;
        // Top wall
        if (y == 1 && x >= 0 && x <= 8) return true;
        // Bottom wall
        if (y == 11 && x >= 0 && x <= 8) return true;
        // Left and right walls + interior
        if (y >= 2 && y <= 10 && x >= 0 && x <= 8) return true;

        return false;
    }

    /**
     * Get allowed directions for a specific position
     */
    private static boolean[] getAllowedDirections(int x, int y) {
        boolean[] allowed = new boolean[8];

        // Top goal
        if (y == 0) {
            if (x == 3) {
                allowed[Direction.SE.index] = true;
            } else if (x == 4) {
                allowed[Direction.SE.index] = true;
                allowed[Direction.S.index] = true;
                allowed[Direction.SW.index] = true;
            } else if (x == 5) {
                allowed[Direction.SW.index] = true;
            }
            return allowed;
        }

        // Top wall (y=1)
        if (y == 1) {
            if (x == 0) {
                allowed[Direction.SE.index] = true;
            } else if (x == 1 || x == 2) {
                allowed[Direction.SE.index] = true;
                allowed[Direction.S.index] = true;
                allowed[Direction.SW.index] = true;
            } else if (x == 3) {
                allowed[Direction.NE.index] = true;
                allowed[Direction.E.index] = true;
                allowed[Direction.SE.index] = true;
                allowed[Direction.S.index] = true;
                allowed[Direction.SW.index] = true;
            } else if (x == 4) {
                for (int i = 0; i < 8; i++) allowed[i] = true;
            } // All 8
            else if (x == 5) {
                allowed[Direction.NW.index] = true;
                allowed[Direction.W.index] = true;
                allowed[Direction.SW.index] = true;
                allowed[Direction.S.index] = true;
                allowed[Direction.SE.index] = true;
            } else if (x == 6 || x == 7) {
                allowed[Direction.SE.index] = true;
                allowed[Direction.S.index] = true;
                allowed[Direction.SW.index] = true;
            } else if (x == 8) {
                allowed[Direction.SW.index] = true;
            }
            return allowed;
        }

        // Left wall (x=0, y=2..10)
        if (x == 0 && y >= 2 && y <= 10) {
            allowed[Direction.NE.index] = true;
            allowed[Direction.E.index] = true;
            allowed[Direction.SE.index] = true;
            return allowed;
        }

        // Right wall (x=8, y=2..10)
        if (x == 8 && y >= 2 && y <= 10) {
            allowed[Direction.NW.index] = true;
            allowed[Direction.W.index] = true;
            allowed[Direction.SW.index] = true;
            return allowed;
        }

        // Interior (y=2..10, x=1..7)
        if (y >= 2 && y <= 10 && x >= 1 && x <= 7) {
            for (int i = 0; i < 8; i++) allowed[i] = true;
            return allowed;
        }

        // Bottom wall (y=11)
        if (y == 11) {
            if (x == 0) {
                allowed[Direction.NE.index] = true;
            } else if (x == 1 || x == 2) {
                allowed[Direction.NE.index] = true;
                allowed[Direction.N.index] = true;
                allowed[Direction.NW.index] = true;
            } else if (x == 3) {
                allowed[Direction.NE.index] = true;
                allowed[Direction.E.index] = true;
                allowed[Direction.SE.index] = true;
                allowed[Direction.N.index] = true;
                allowed[Direction.NW.index] = true;
            } else if (x == 4) {
                for (int i = 0; i < 8; i++) allowed[i] = true;
            } // All 8
            else if (x == 5) {
                allowed[Direction.NW.index] = true;
                allowed[Direction.W.index] = true;
                allowed[Direction.SW.index] = true;
                allowed[Direction.N.index] = true;
                allowed[Direction.NE.index] = true;
            } else if (x == 6 || x == 7) {
                allowed[Direction.NE.index] = true;
                allowed[Direction.N.index] = true;
                allowed[Direction.NW.index] = true;
            } else if (x == 8) {
                allowed[Direction.NW.index] = true;
            }
            return allowed;
        }

        // Bottom goal
        if (y == 12) {
            if (x == 3) {
                allowed[Direction.NE.index] = true;
            } else if (x == 4) {
                allowed[Direction.NE.index] = true;
                allowed[Direction.N.index] = true;
                allowed[Direction.NW.index] = true;
            } else if (x == 5) {
                allowed[Direction.NW.index] = true;
            }
            return allowed;
        }

        return allowed;
    }

    /**
     * Build neighbor lookup table based on field boundaries
     */
    private static void buildNeighborIndex() {
        for (int node = 0; node < TOTAL_NODES; node++) {
            int x = node % GRID_WIDTH;
            int y = node / GRID_WIDTH;

            // Initialize all to -1
            for (int i = 0; i < 8; i++) {
                neighborIndex[node][i] = -1;
            }

            // Only process valid positions
            if (!isValidPosition(x, y)) continue;

            boolean[] allowed = getAllowedDirections(x, y);

            for (Direction dir : Direction.values()) {
                if (allowed[dir.index]) {
                    int nx = x + dir.dx;
                    int ny = y + dir.dy;

                    // Check if neighbor exists and is valid
                    if (isValidPosition(nx, ny)) {
                        neighborIndex[node][dir.index] = ny * GRID_WIDTH + nx;
                    }
                }
            }
        }
    }

    // ============= INITIALIZATION =============

    /**
     * Initialize this board with starting position
     */
    private void initializeBoard(boolean isFirstPlayerMove) {
        // Initialize direction masks based on valid neighbors from the field structure
        for (int node = 0; node < TOTAL_NODES; node++) {
            int x = node % GRID_WIDTH;
            int y = node / GRID_WIDTH;

            if (!isValidPosition(x, y)) continue;

            for (Direction dir : Direction.values()) {
                if (neighborIndex[node][dir.index] != -1) {
                    setDirBit(dir.index, node);
                }
            }
        }

        // Mark all wall/boundary points as already visited so players can bounce off them
        for (int node = 0; node < TOTAL_NODES; node++) {
            int x = node % GRID_WIDTH;
            int y = node / GRID_WIDTH;

            if (!isValidPosition(x, y)) continue;

            boolean isWall = false;

            // Goals are walls
            if (y == 0 || y == 12) {
                isWall = true;
            }
            // Top and bottom walls, but exclude (4, 1) and (4, 11)
            else if ((y == 1 || y == 11) && x != 4) {
                isWall = true;
            }
            // Left and right walls (x=0 or x=8, y=2..10)
            else if ((x == 0 || x == 8) && y >= 2 && y <= 10) {
                isWall = true;
            }

            if (isWall) {
                setBit(node);
            }
        }

        // Initial ball position: (4, 6)
        int startNode = coordToIndex(4, 6);
        ballPosition = startNode;
        setBit(startNode);
        if (isFirstPlayerMove) {
            activePlayer = Player.NORTH;
        } else {
            activePlayer = Player.SOUTH;
        }
    }

    // ============= UTILITY METHODS =============

    public boolean isGameOver() {
        if (!outgoingExists(ballPosition)) {
            return true;
        }
        return isTopGoal(ballPosition) || isBottomGoal(ballPosition);
    }

    public boolean outgoingExists() {
        return outgoingExists(ballPosition);
    }

    public boolean isTopGoal() {
        return isTopGoal(ballPosition);
    }

    public boolean isBottomGoal() {
        return isBottomGoal(ballPosition);
    }

    /**
     * Get current ball position
     */
    public int getBallPosition() {
        return ballPosition;
    }

    /**
     * Get active player
     */
    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * Check if direction is available from node
     */
    private boolean isDirOpen(int node, Direction dir) {
        return testDirBit(dir.index, node);
    }

    /**
     * Check if node has any outgoing edges
     */
    private boolean outgoingExists(int node) {
        // Check all 8 directions
        for (Direction dir : Direction.values()) {
            if (testDirBit(dir.index, node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert coordinates to bit index
     */
    private static int coordToIndex(int x, int y) {
        return y * GRID_WIDTH + x;
    }

    /**
     * Convert bit index to x coordinate
     */
    private static int indexToX(int index) {
        return index % GRID_WIDTH;
    }

    /**
     * Convert bit index to y coordinate
     */
    private static int indexToY(int index) {
        return index / GRID_WIDTH;
    }

    /**
     * Check if position is in top goal (SOUTH scores)
     */
    private static boolean isTopGoal(int node) {
        int x = indexToX(node);
        int y = indexToY(node);
        return y == 0 && (x == 3 || x == 4 || x == 5);
    }

    /**
     * Check if position is in bottom goal (NORTH scores)
     */
    private static boolean isBottomGoal(int node) {
        int x = indexToX(node);
        int y = indexToY(node);
        return y == 12 && (x == 3 || x == 4 || x == 5);
    }

    // ============= LEGAL MOVES =============

    /**
     * Get all legal move sequences from current position.
     * Each move is a complete sequence that ends the current player's turn.
     */
    public List<List<Direction>> legalMoves() {
        List<List<Direction>> allMoves = new ArrayList<>();
        int startPos = getBallPosition();

        if (startPos == -1) return allMoves;

        // Try each initial direction
        for (Direction dir : Direction.values()) {
            if (isDirOpen(startPos, dir)) {
                List<Direction> path = new ArrayList<>();
                path.add(dir);
                exploreMoves(path, allMoves);
            }
        }

        return allMoves;
    }

    /**
     * Recursively explore all possible move sequences from current state.
     * Uses executeMove to apply moves immutably.
     */
    private void exploreMoves(List<Direction> currentPath, List<List<Direction>> allMoves) {
        // Execute the current path to get the resulting board state
        FootballBitBoard newBoard = executeMove(currentPath);

        int ballPos = newBoard.getBallPosition();

        // Check if turn ended (goal, first visit, or no outgoing edges)
        if (isTopGoal(ballPos) || isBottomGoal(ballPos)) {
            // Goal reached - turn ends
            allMoves.add(new ArrayList<>(currentPath));
            return;
        }

        // Check if the move ended the turn (player switched)
        if (newBoard.getActivePlayer() != this.activePlayer) {
            // Turn ended - this is a complete legal move
            allMoves.add(new ArrayList<>(currentPath));
            return;
        }

        // Turn didn't end, must continue - try all available directions from current position
        boolean foundContinuation = false;
        for (Direction dir : Direction.values()) {
            if (newBoard.isDirOpen(ballPos, dir)) {
                foundContinuation = true;
                List<Direction> newPath = new ArrayList<>(currentPath);
                newPath.add(dir);
                exploreMoves(newPath, allMoves);
            }
        }

        // Safety check - if no continuation found but we're here, the move ends
        if (!foundContinuation) {
            allMoves.add(new ArrayList<>(currentPath));
        }
    }

    // ============= EXECUTE MOVE =============

    /**
     * Execute a complete move sequence immutably.
     * Returns a new Board with the move applied.
     * The sequence must be valid and complete (ending the turn).
     */
    public FootballBitBoard executeMove(List<Direction> moveSequence) {
        if (moveSequence == null || moveSequence.isEmpty()) {
            throw new IllegalArgumentException("Move sequence cannot be empty");
        }

        // Create a copy to work with
        FootballBitBoard newBoard = this.copy();

        for (Direction dir : moveSequence) {
            int nodeA = newBoard.getBallPosition();

            if (!newBoard.isDirOpen(nodeA, dir)) {
                throw new IllegalArgumentException("Invalid move: direction " + dir +
                    " not available from position " + nodeA);
            }

            int nodeB = neighborIndex[nodeA][dir.index];

            // Move ball
            newBoard.ballPosition = nodeB;

            // Consume edges
            newBoard.clearDirBit(dir.index, nodeA);
            Direction oppDir = dir.opposite();
            newBoard.clearDirBit(oppDir.index, nodeB);

            // Check for goal - game ends
            if (isTopGoal(nodeB) || isBottomGoal(nodeB)) {
                return newBoard; // Don't switch player, game is over
            }

            // Check if B was already visited
            boolean wasVisited = newBoard.testBit(nodeB);

            if (!wasVisited) {
                // First visit - turn ends
                newBoard.setBit(nodeB);
                newBoard.activePlayer = (newBoard.activePlayer == Player.SOUTH) ?
                    Player.NORTH : Player.SOUTH;
                return newBoard;
            }

            // B was visited - check for outgoing edges
            if (!newBoard.outgoingExists(nodeB)) {
                // No outgoing edges - turn ends
                newBoard.activePlayer = (newBoard.activePlayer == Player.SOUTH) ?
                    Player.NORTH : Player.SOUTH;
                return newBoard;
            }

            // B was visited and has outgoing edges - continue with next direction
        }

        return newBoard;
    }

    // ============= VISUALIZATION =============

    /**
     * Get string representation of the board
     */
    private String boardToString() {
        StringBuilder sb = new StringBuilder();
        int ballPos = getBallPosition();

        sb.append("Active Player: ").append(activePlayer).append("\n");
        sb.append("Ball Position: (").append(indexToX(ballPos))
            .append(", ").append(indexToY(ballPos)).append(")\n\n");

        // Draw the board
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (!isValidPosition(x, y)) {
                    sb.append("   ");
                    continue;
                }

                int node = coordToIndex(x, y);

                if (node == ballPos) {
                    sb.append(" O ");
                } else if (testBit(node)) {
                    sb.append(" * ");
                } else if (isTopGoal(node) || isBottomGoal(node)) {
                    sb.append(" G ");
                } else if (((y == 1 || y == 11) && x != 4) || (x == 0 || x == 8)) {
                    // Walls: top/bottom rows except x=4, and left/right columns
                    sb.append(" # ");
                } else {
                    sb.append(" . ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return boardToString();
    }
}
