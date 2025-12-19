package io.github.lipinskipawel.board.engine;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Internal -- PlayerMoveLog")
class MoveHistoryTest {

    private MoveHistory moveLog;

    @BeforeEach
    void setUp() {
        moveLog = new MoveHistory();
    }

    @Nested
    @DisplayName("sanity -- recursively")
    class SanityTest {

        @Test
        @DisplayName("one move, one direction, one undo")
        void oneMoveOneDirectionOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .add(Direction.N)
                .undoMove();

            Assertions.assertThat(afterMoves).isEqualToComparingFieldByFieldRecursively(moveLog);
        }

        @Test
        @DisplayName("two moves, one small, one undo, one small, one move")
        void shouldBeOverallThreMovesMade() {
            final var prepared = moveLog
                .addMove(new Move(List.of(Direction.SE)))
                .addMove(new Move(List.of(Direction.W)))
                .addMove(new Move(List.of(Direction.N, Direction.W)));

            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.SE)))
                .addMove(new Move(List.of(Direction.W)))
                .add(Direction.N)
                .undo()
                .add(Direction.N)
                .addMove(new Move(List.of(Direction.W)));

            Assertions.assertThat(afterMoves).isEqualToComparingFieldByFieldRecursively(prepared);
        }
    }

    @Nested
    @DisplayName("undoDirection")
    class UndoDirection {

        @Test
        @DisplayName("one direction, one undo direction")
        void oneDirectionUndoOneDirection() {
            final var afterMoves = moveLog
                .add(Direction.N)
                .forceUndo();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(Lists.emptyList());
        }

        @Test
        @DisplayName("two direction, one undo direction")
        void twoDirectionUndoOneDirection() {
            final var afterMoves = moveLog
                .add(Direction.N)
                .add(Direction.S)
                .forceUndo();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.N));
        }

        @Test
        @DisplayName("two move, one undo direction")
        void twoMoveOneUndoDirection() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.E)))
                .forceUndo();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.S));
        }

        @Test
        @DisplayName("two move, one undo direction - R")
        void twoMoveOneUndoDirectionRecursively() {
            final var prepared = moveLog
                .addMove(new Move(List.of(Direction.S)));
            final var afterMoves = MoveHistoryTest.this.moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.E)))
                .forceUndo();

            Assertions.assertThat(afterMoves).isEqualToComparingFieldByFieldRecursively(prepared);
        }

        @Test
        @DisplayName("three move, one undo direction - R")
        void threeMoveOneUndoDirectionRecursively() {
            final var prepared = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.E)))
                .add(Direction.NW);
            final var afterMoves = MoveHistoryTest.this.moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.E)))
                .addMove(new Move(List.of(Direction.NW, Direction.N)))
                .forceUndo();

            Assertions.assertThat(afterMoves).isEqualToComparingFieldByFieldRecursively(prepared);
        }
    }

    @Nested
    @DisplayName("undo")
    class Undo {

        @Test
        @DisplayName("zero direction, zero moves, one undo")
        void zeroDirectionsZeroMovesOneUndo() {
            final var undo = moveLog.undo();

            Assertions.assertThat(undo).isEqualToComparingFieldByFieldRecursively(moveLog);
        }

        @Test
        @DisplayName("two direction, one undo")
        void twoDirectionsOneUndo() {
            final var afterUndo = moveLog
                .add(Direction.N)
                .add(Direction.W)
                .undo();

            Assertions.assertThat(afterUndo.allDirections()).containsExactlyElementsOf(List.of(Direction.N));
        }

        @Test
        @DisplayName("one move, one undo")
        void oneMoveOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S, Direction.E)))
                .undo();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.S, Direction.E));
        }

        @Test
        @DisplayName("one move, one direction, one undo")
        void oneMoveOneDirectionOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .add(Direction.W)
                .undo();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.S));
        }

        @Test
        @DisplayName("one move, one direction, one move, one undo")
        void oneMoveOneDirectionOneMoveOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .add(Direction.W)
                .addMove(new Move(List.of(Direction.S)))
                .undo();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.S, Direction.W, Direction.S));
        }
    }

    @Nested
    @DisplayName("undoMove")
    class UndoMove {

        @Test
        @DisplayName("two moves, one direction, one undoMove")
        void twoMovesOneDirectionOneUndoMove() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.W)))
                .add(Direction.SW)
                .undoMove();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.S));
        }

        @Test
        @DisplayName("three moves, one undoMove")
        void threeMoves() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.W)))
                .addMove(new Move(List.of(Direction.N)))
                .undoMove();

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(List.of(Direction.S, Direction.W));
        }
    }

    @Nested
    @DisplayName("allDirections")
    class AllDirectionsTest {

        @Test
        @DisplayName("0 move, 0 directions")
        void zeroMovesAndDirections() {
            Assertions.assertThat(moveLog.allDirections()).isEqualTo(Lists.emptyList());
        }

        @Test
        @DisplayName("two moves, the same order")
        void twoMovesTheSameOrder() {
            final var prepared = List.of(Direction.NE, Direction.N);

            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.NE)))
                .addMove(new Move(List.of(Direction.N)));

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(prepared);
        }

        @Test
        @DisplayName("two moves, one direction, the same order")
        void twoMovesOneDirectionTheSameOrder() {
            final var prepared = List.of(Direction.N, Direction.W, Direction.SE);

            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.N)))
                .addMove(new Move(List.of(Direction.W)))
                .add(Direction.SE);

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(prepared);
        }

        @Test
        @DisplayName("two directions, the same order")
        void twoDirectionsTheSameOrder() {
            final var prepared = List.of(Direction.N, Direction.W);
            final var afterMoves = moveLog
                .add(Direction.N)
                .add(Direction.W);

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(prepared);
        }

        @Test
        @DisplayName("two directions, one move, the same order")
        void twoDirectionOneMoveTheSameOrder() {
            final var prepared = List.of(Direction.W, Direction.N, Direction.S);
            final var afterMoves = moveLog
                .add(Direction.W)
                .add(Direction.N)
                .addMove(new Move(List.of(Direction.S)));

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(prepared);
        }

        @Test
        @DisplayName("one move, one direction, one move, the same order")
        void oneMoveOneDirectionOneMoveTheSameOrder() {
            final var prepared = List.of(Direction.S, Direction.W, Direction.S);
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .add(Direction.W)
                .addMove(new Move(List.of(Direction.S)));

            Assertions.assertThat(afterMoves.allDirections()).containsExactlyElementsOf(prepared);
        }
    }

    @Nested
    @DisplayName("currentPlayer")
    class CurrentPlayerTest {

        @Test
        @DisplayName("one move")
        void oneMove() {
            final var afterMove = moveLog.addMove(new Move(List.of(Direction.NE)));

            Assertions.assertThat(afterMove.currentPlayer()).isFalse();
        }

        @Test
        @DisplayName("one direction")
        void oneDirection() {
            final var afterMove = moveLog.add(Direction.N);

            Assertions.assertThat(afterMove.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("two moves")
        void twoMoves() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.NE)))
                .addMove(new Move(List.of(Direction.N)));

            Assertions.assertThat(afterMoves.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("two moves, one direction")
        void twoMovesOneDirection() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.NE)))
                .addMove(new Move(List.of(Direction.N)))
                .add(Direction.W);

            Assertions.assertThat(afterMoves.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("one moves, one direction, one move")
        void oneMoveOneDirectionOneMove() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.NE)))
                .add(Direction.W)
                .addMove(new Move(List.of(Direction.N)));

            Assertions.assertThat(afterMoves.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("three moves")
        void threeMoves() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.W)))
                .addMove(new Move(List.of(Direction.N)));

            Assertions.assertThat(afterMoves.currentPlayer()).isFalse();
        }

        @Test
        @DisplayName("one move, one undo")
        void oneMoveOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .undoMove();

            Assertions.assertThat(afterMoves.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("one direction, one undo")
        void oneDirectionOneUndo() {
            final var afterMoves = moveLog
                .add(Direction.N)
                .undoMove();

            Assertions.assertThat(afterMoves.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("one move, one direction, one undo")
        void oneMoveOneDirectionOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .add(Direction.N)
                .undoMove();

            Assertions.assertThat(afterMoves.currentPlayer()).isTrue();
        }

        @Test
        @DisplayName("two move, one undo")
        void twoMovesOneUndo() {
            final var afterMoves = moveLog
                .addMove(new Move(List.of(Direction.S)))
                .addMove(new Move(List.of(Direction.S)))
                .undoMove();

            Assertions.assertThat(afterMoves.currentPlayer()).isFalse();
        }
    }
}
