
```java
public void legalMoves(Consumer<long[]> onMoveFound) {
    final class Level {
        private final MyFootball board;
        private final long[] moves;
        private final int count;

        Level(MyFootball board) {
            this.board = board;
            this.moves = new long[4]; // start with space for 4 longs = 84 directions
            this.count = 0;
        }

        Level(MyFootball board, long[] moves, int count) {
            this.board = board;
            this.moves = moves;
            this.count = count;
        }

        Level push(Direction direction) {
            int longIndex = count / 21;
            int bitOffset = (count % 21) * 3;

            long[] newMoves = Arrays.copyOf(moves, Math.max(moves.length, longIndex + 1));
            newMoves[longIndex] |= ((long) direction.ordinal() << bitOffset);
            return new Level(board, newMoves, count + 1);
        }
    }

    var currentLevels = new ArrayDeque<Level>();
    currentLevels.push(new Level(new MyFootball(this)));

    while (!currentLevels.isEmpty()) {
        final var level = currentLevels.pop();
        for (var dir : Direction.values()) {
            if (allowToMove(level.board, dir)) {
                executeMove(level.board, dir);
                final var next = level.push(dir);

                if (isItEnd(level.board)) {
                    onMoveFound.accept(next.moves);
                } else {
                    currentLevels.push(new Level(new MyFootball(level.board), next.moves, next.count));
                }
                undoMove(level.board, dir);
            }
        }
    }
}
```

### How the packing works across multiple `long`s
```
moves[0] → directions 0–20   (21 directions × 3 bits = 63 bits)
moves[1] → directions 21–41
moves[2] → directions 42–62
...
```

### Unpacking helper
```java
Direction unpack(long[] moves, int index) {
    int longIndex = index / 21;
    int bitOffset = (index % 21) * 3;
    int ordinal = (int) (moves[longIndex] >> bitOffset) & 0b111;
    return Direction.values()[ordinal];
}
```
