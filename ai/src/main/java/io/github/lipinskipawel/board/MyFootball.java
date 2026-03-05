package io.github.lipinskipawel.board;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.System.arraycopy;

public final class MyFootball {

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

    private int opposite(int dir) {
        return (dir + 4) % 8;
    }

    // Board state fields
    // indexes [0,1] -> represents direction N for 117 notes
    // indexes [2,3] -> represents direction NE for 117 notes
    // and so on
    private final long[] dirMask;         // 16 longs: 8 directions × 2 longs each
    private final long[] visited;         // 2 longs for visited nodes
    int ballPosition;             // Direct ball position (0-116)
    int player;

    private final int[] moveBall = new int[]{-9, -8, 1, 10, 9, 8, -1, -10};
    private final int[] undoBall = new int[]{9, 8, -1, -10, -9, -8, 1, 10};


    /**
     * Private constructor for copying
     */
    public MyFootball() {
        dirMask = new long[TOTAL_DIR_LONGS];
        visited = new long[LONGS_PER_BITBOARD];
        ballPosition = 58;
        player = 0;
    }

    public void initBoard() {
        drawBoard(this);
    }

    private MyFootball(MyFootball myFootball) {
        this.dirMask = new long[TOTAL_DIR_LONGS];
        this.visited = new long[LONGS_PER_BITBOARD];
        arraycopy(myFootball.dirMask, 0, this.dirMask, 0, TOTAL_DIR_LONGS);
        arraycopy(myFootball.visited, 0, this.visited, 0, LONGS_PER_BITBOARD);
        this.ballPosition = myFootball.ballPosition;
        this.player = myFootball.player;
    }

    public List<List<Integer>> legalMoves() {
        if (isGameOver()) {
            return List.of();
        }
        final class Level {
            private final MyFootball board;
            private final List<Integer> moves;

            Level(MyFootball board) {
                this.board = board;
                this.moves = new ArrayList<>();
            }

            Level(MyFootball board, List<Integer> stack) {
                this.board = board;
                this.moves = stack;
            }

            // this must be next improvement
            List<Integer> push(int direction) {
//                moves.add(direction);
//                return moves;
                final var newStack = new ArrayList<>(moves);
                newStack.add(direction);
                return newStack;
            }
        }

        var currentLevels = new ArrayDeque<Level>();
        currentLevels.push(new Level(new MyFootball(this)));

        List<List<Integer>> allMoves = new ArrayList<>();
        while (!currentLevels.isEmpty()) {
            final var level = currentLevels.pop();
            for (var dir = 0; dir < 8; dir++) {

                if (allowToMove(level.board, dir)) {
                    executeMove(level.board, dir);

                    if (isItEnd(level.board)) {
                        // alternative to emit moves to the caller via consumer.accept
                        // var next = level.push(dir);
                        // consumer.accept(next.moves);
                        allMoves.add(level.push(dir));
                    } else {
                        final var item = new Level(new MyFootball(level.board), level.push(dir));
                        currentLevels.push(item);
                    }
                    undoMove(level.board, dir);
                }
            }
        }

        return allMoves;
    }

    public void executePlayerMove(List<Direction> directions) {
        for (var dir : directions) {
            executeMove(dir);
        }
        switchPlayer();
    }

    public void executePlayerMoveInt(List<Integer> directions) {
        for (var dir : directions) {
            executeMove(dir);
        }
        switchPlayer();
    }

    public void executeMove(Integer direction) {
        executeMove(this, direction);
    }

    public void executeMove(Direction direction) {
        executeMove(this, direction.index);
    }

    public void undoPlayerMove(List<Direction> directions) {
        for (int i = directions.size() - 1; i >= 0; i--) {
            undoMove(directions.get(i).index);
        }
        switchPlayer();
    }

    public void undoPlayerMoveInt(List<Integer> directions) {
        for (int i = directions.size() - 1; i >= 0; i--) {
            undoMove(directions.get(i));
        }
        switchPlayer();
    }

    public void undoMove(int direction) {
        undoMove(this, direction);
    }

    public void switchPlayer() {
        player ^= 1;
    }

    public boolean isFirstToMove() {
        return player == 0;
    }

    private boolean isItEnd(MyFootball board) {
        var count = 0;
        for (var dir = 0; dir < 8; dir++) {
            if (allowToMove(board, dir)) {
                count++;
            }
        }
        return count == 7 || count == 0;
    }

    public boolean isGameOver() {
        var count = 0;
        for (var dir = 0; dir < 8; dir++) {
            if (allowToMove(this, dir)) {
                count++;
            }
        }
        return count == 0;
    }

    public boolean isTopGoal() {
        return ballPosition == 3 || ballPosition == 4 || ballPosition == 5;
    }

    public boolean isBottomGoal() {
        return ballPosition == 111 || ballPosition == 112 || ballPosition == 113;
    }

    private boolean allowToMove(MyFootball board, int direction) {
        final var increaseBit = direction << 1; // move right, so we can catch 15. 7 becomes 14 --- 0111 -> 1110
        final var reduce64 = board.ballPosition >>> 6; // either 1, >= 64, or < 0
        int longIndex = increaseBit + reduce64; // we have 16 buckets

        final var flags = board.dirMask[longIndex];
        final var checkBit = board.ballPosition & 63; // bit index resets within each long

        final var mask = 1L << checkBit; // long literal, so the shift operates on 64 bits instead of 32, avoiding Java's int shift wrapping
        return (flags & mask) == 0;
    }

    private void executeMove(MyFootball board, int direction) {
        blockDirection(board, direction);
        board.ballPosition += moveBall[direction];
        blockDirection(board, opposite(direction));
    }

    private void undoMove(MyFootball board, int direction) {
        unblockDirection(board, opposite(direction));
        board.ballPosition += undoBall[direction];
        unblockDirection(board, direction);
    }

    private void blockDirection(MyFootball board, int direction) {
        final var increaseBit = direction << 1;
        final var reduce64 = board.ballPosition >>> 6;
        final int longIndex = increaseBit + reduce64;

        final var flags = board.dirMask[longIndex];
        final var turnOnBit = board.ballPosition & 63;

        final var mask = 1L << turnOnBit;
        board.dirMask[longIndex] |= mask;
    }

    private void unblockDirection(MyFootball board, int direction) {
        final var increaseBit = direction << 1;
        final var reduce64 = board.ballPosition >>> 6;
        final int longIndex = increaseBit + reduce64;

        final var flags = board.dirMask[longIndex];
        final var turnOffBit = board.ballPosition & 63;

        final var prepareMask = 1L << turnOffBit;
        final var mask = ~prepareMask;
        board.dirMask[longIndex] &= mask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFootball that = (MyFootball) o;
        return ballPosition == that.ballPosition
            && player == that.player
            && Objects.deepEquals(dirMask, that.dirMask)
            && Objects.deepEquals(visited, that.visited)
            && Objects.deepEquals(moveBall, that.moveBall)
            && Objects.deepEquals(undoBall, that.undoBall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            Arrays.hashCode(dirMask),
            Arrays.hashCode(visited),
            ballPosition,
            player,
            Arrays.hashCode(moveBall),
            Arrays.hashCode(undoBall)
        );
    }

    @Override
    public String toString() {
        return "MyFootball{" +
            "dirMask=" + Arrays.toString(dirMask) +
            ", visited=" + Arrays.toString(visited) +
            ", ballPosition=" + ballPosition +
            ", player=" + player +
            '}';
    }

    private void drawBoard(MyFootball board) {
        board.ballPosition = 9;
        goTopLeftToGoal(board);
        goAroundTopGoal(board);
        goTopRightToGoal(board);

        goBottom(board);

        goBottomRightToGoal(board);
        goAroundBottomGoal(board);
        goBottomLeftToGoal(board);

        goUp(board);
        board.ballPosition = 58;
    }

    private void goTopLeftToGoal(MyFootball board) {
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.NW.index);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.NW.index);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.NW.index);
    }

    private void goAroundTopGoal(MyFootball board) {
        executeMove(Direction.N);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.SE.index);
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);

        executeMove(Direction.E);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.SE.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.NW.index);

        executeMove(Direction.S);
    }

    private void goTopRightToGoal(MyFootball board) {
        blockDirection(board, Direction.NE.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.NW.index);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.NW.index);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        executeMove(Direction.E);

        blockDirection(board, Direction.NW.index);
        blockDirection(board, Direction.N.index);
    }

    private void goBottom(MyFootball board) {
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.S);

        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
    }

    private void goBottomRightToGoal(MyFootball board) {
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SW.index);
        executeMove(Direction.W);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.W);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.W);

        blockDirection(board, Direction.SE.index);
    }

    private void goAroundBottomGoal(MyFootball board) {
        executeMove(Direction.S);
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.E.index);
        blockDirection(board, Direction.SE.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.NW.index);

        executeMove(Direction.W);
        blockDirection(board, Direction.N.index);
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.SE.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.NW.index);

        executeMove(Direction.W);
        blockDirection(board, Direction.NE.index);
        blockDirection(board, Direction.SE.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);

        executeMove(Direction.N);
    }

    private void goBottomLeftToGoal(MyFootball board) {
        blockDirection(board, Direction.SW.index);
        executeMove(Direction.W);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.W);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SE.index);
        executeMove(Direction.W);

        blockDirection(board, Direction.S.index);
        blockDirection(board, Direction.SE.index);
    }

    private void goUp(MyFootball board) {
        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
        executeMove(Direction.N);

        blockDirection(board, Direction.SW.index);
        blockDirection(board, Direction.W.index);
        blockDirection(board, Direction.NW.index);
    }
}
