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

    /**
     * Game state using long[] bitboards
     */
    public static class Board {
        // indexes [0,1] -> represents direction N for 117 notes
        // indexes [2,3] -> represents direction NE for 117 notes
        // and so on
        long[] dirMask;         // 16 longs: 8 directions × 2 longs each
        long[] visited;         // 2 longs for visited nodes
        int ballPosition;       // Direct ball position (0-116)
        Player activePlayer;

        public Board() {
            dirMask = new long[TOTAL_DIR_LONGS];
            visited = new long[LONGS_PER_BITBOARD];
            ballPosition = -1;
            activePlayer = Player.SOUTH;
        }

        public Board copy() {
            Board copy = new Board();
            System.arraycopy(this.dirMask, 0, copy.dirMask, 0, TOTAL_DIR_LONGS);
            System.arraycopy(this.visited, 0, copy.visited, 0, LONGS_PER_BITBOARD);
            copy.ballPosition = this.ballPosition;
            copy.activePlayer = this.activePlayer;
            return copy;
        }
    }

    // ============= BITBOARD OPERATIONS =============

    /**
     * Set a bit in a direction mask
     */
    private static void setDirBit(long[] dirMask, int dirIndex, int node) {
        int longIndex = (dirIndex << 1) + (node >>> 6);  // dirIndex * 2 + node / 64
        dirMask[longIndex] |= (1L << node);
    }

    /**
     * Clear a bit in a direction mask
     */
    private static void clearDirBit(long[] dirMask, int dirIndex, int node) {
        int longIndex = (dirIndex << 1) + (node >>> 6);
        dirMask[longIndex] &= ~(1L << node);
    }

    /**
     * Test a bit in a direction mask
     */
    private static boolean testDirBit(long[] dirMask, int dirIndex, int node) {
        // Note: (1L << node) works because Java only uses the lower 6 bits of the shift amount for long, so node % 64 is implicit!
        int longIndex = (dirIndex << 1) + (node >>> 6);
        return (dirMask[longIndex] & (1L << node)) != 0;
    }

    /**
     * Set a bit in visited mask
     */
    private static void setBit(long[] mask, int node) {
        mask[node >>> 6] |= (1L << node);
    }

    /**
     * Test a bit in visited mask
     */
    private static boolean testBit(long[] mask, int node) {
        return (mask[node >>> 6] & (1L << node)) != 0;
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
     * Initialize board with starting position
     */
    public static Board init() {
        Board board = new Board();

        // Initialize direction masks based on valid neighbors from the field structure
        for (int node = 0; node < TOTAL_NODES; node++) {
            int x = node % GRID_WIDTH;
            int y = node / GRID_WIDTH;

            if (!isValidPosition(x, y)) continue;

            for (Direction dir : Direction.values()) {
                if (neighborIndex[node][dir.index] != -1) {
                    setDirBit(board.dirMask, dir.index, node);
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
                setBit(board.visited, node);
            }
        }

        // Initial ball position: (4, 6)
        int startNode = coordToIndex(4, 6);
        board.ballPosition = startNode;
        setBit(board.visited, startNode);
        board.activePlayer = Player.SOUTH;

        return board;
    }

    // ============= UTILITY METHODS =============

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
     * Get current ball position
     */
    private static int getBallPosition(Board board) {
        return board.ballPosition;
    }

    /**
     * Check if direction is available from node
     */
    private static boolean isDirOpen(Board board, int node, Direction dir) {
        return testDirBit(board.dirMask, dir.index, node);
    }

    /**
     * Check if node has any outgoing edges
     */
    private static boolean outgoingExists(Board board, int node) {
        // Check all 8 directions
        for (Direction dir : Direction.values()) {
            if (testDirBit(board.dirMask, dir.index, node)) {
                return true;
            }
        }
        return false;
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
    public static List<List<Direction>> legalMoves(Board board) {
        List<List<Direction>> allMoves = new ArrayList<>();
        int startPos = getBallPosition(board);

        if (startPos == -1) return allMoves;

        // Try each initial direction
        for (Direction dir : Direction.values()) {
            if (isDirOpen(board, startPos, dir)) {
                List<Direction> path = new ArrayList<>();
                path.add(dir);
                exploreMoves(board.copy(), path, allMoves);
            }
        }

        return allMoves;
    }

    /**
     * Recursively explore all possible move sequences from current state
     */
    private static void exploreMoves(Board board, List<Direction> currentPath, List<List<Direction>> allMoves) {
        // Apply the last direction in the path
        Direction lastDir = currentPath.get(currentPath.size() - 1);
        int nodeA = getBallPosition(board);
        int nodeB = neighborIndex[nodeA][lastDir.index];

        // Move ball
        board.ballPosition = nodeB;

        // Consume edges
        clearDirBit(board.dirMask, lastDir.index, nodeA);
        Direction oppDir = lastDir.opposite();
        clearDirBit(board.dirMask, oppDir.index, nodeB);

        // Check for goal - move ends immediately
        if (isTopGoal(nodeB) || isBottomGoal(nodeB)) {
            allMoves.add(new ArrayList<>(currentPath));
            return;
        }

        // Check if B was already visited
        boolean wasVisited = testBit(board.visited, nodeB);

        if (!wasVisited) {
            // First visit - move ends here
            setBit(board.visited, nodeB);
            allMoves.add(new ArrayList<>(currentPath));
            return;
        }

        // B was visited - check for outgoing edges
        if (!outgoingExists(board, nodeB)) {
            // No outgoing edges - move ends here
            allMoves.add(new ArrayList<>(currentPath));
            return;
        }

        // B was visited and has outgoing edges - must continue
        // Try all available directions from B
        boolean foundContinuation = false;
        for (Direction dir : Direction.values()) {
            if (isDirOpen(board, nodeB, dir)) {
                foundContinuation = true;
                List<Direction> newPath = new ArrayList<>(currentPath);
                newPath.add(dir);
                exploreMoves(board.copy(), newPath, allMoves);
            }
        }

        // This shouldn't happen if outgoingExists() is correct
        if (!foundContinuation) {
            allMoves.add(new ArrayList<>(currentPath));
        }
    }

    // ============= EXECUTE MOVE =============

    /**
     * Execute a complete move sequence.
     * The sequence must be valid and complete (ending the turn).
     */
    public static void executeMove(Board board, List<Direction> moveSequence) {
        if (moveSequence == null || moveSequence.isEmpty()) {
            throw new IllegalArgumentException("Move sequence cannot be empty");
        }

        for (Direction dir : moveSequence) {
            int nodeA = getBallPosition(board);

            if (!isDirOpen(board, nodeA, dir)) {
                throw new IllegalArgumentException("Invalid move: direction " + dir +
                    " not available from position " + nodeA);
            }

            int nodeB = neighborIndex[nodeA][dir.index];

            // Move ball
            board.ballPosition = nodeB;

            // Consume edges
            clearDirBit(board.dirMask, dir.index, nodeA);
            Direction oppDir = dir.opposite();
            clearDirBit(board.dirMask, oppDir.index, nodeB);

            // Check for goal - game ends
            if (isTopGoal(nodeB) || isBottomGoal(nodeB)) {
                return; // Don't switch player, game is over
            }

            // Check if B was already visited
            boolean wasVisited = testBit(board.visited, nodeB);

            if (!wasVisited) {
                // First visit - turn ends
                setBit(board.visited, nodeB);
                board.activePlayer = (board.activePlayer == Player.SOUTH) ?
                    Player.NORTH : Player.SOUTH;
                return;
            }

            // B was visited - check for outgoing edges
            if (!outgoingExists(board, nodeB)) {
                // No outgoing edges - turn ends
                board.activePlayer = (board.activePlayer == Player.SOUTH) ?
                    Player.NORTH : Player.SOUTH;
                return;
            }

            // B was visited and has outgoing edges - continue with next direction
        }
    }

    // ============= VISUALIZATION =============

    /**
     * Get string representation of the board
     */
    public static String boardToString(Board board) {
        StringBuilder sb = new StringBuilder();
        int ballPos = getBallPosition(board);

        sb.append("Active Player: ").append(board.activePlayer).append("\n");
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
                } else if (testBit(board.visited, node)) {
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
}
