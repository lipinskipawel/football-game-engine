package io.github.lipinskipawel.board.ai.bruteforce;

import io.github.lipinskipawel.board.ai.MoveStrategy;
import io.github.lipinskipawel.board.engine.Board;
import io.github.lipinskipawel.board.engine.Boards;
import io.github.lipinskipawel.board.engine.Move;
import io.github.lipinskipawel.board.engine.Player;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.lipinskipawel.board.engine.Direction.E;
import static io.github.lipinskipawel.board.engine.Direction.N;
import static io.github.lipinskipawel.board.engine.Direction.NE;
import static io.github.lipinskipawel.board.engine.Direction.NW;
import static io.github.lipinskipawel.board.engine.Direction.S;
import static io.github.lipinskipawel.board.engine.Direction.SE;
import static io.github.lipinskipawel.board.engine.Direction.W;
import static io.github.lipinskipawel.board.engine.Player.FIRST;
import static io.github.lipinskipawel.board.engine.Player.SECOND;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("API -- Minimax alpha-beta")
class MiniMaxAlphaBetaTest implements WithAssertions {

    private MiniMaxAlphaBeta bruteForce;
    private Board<Player> board;

    @BeforeEach
    void setUp() {
        this.bruteForce = (MiniMaxAlphaBeta) MoveStrategy
            .defaultMoveStrategyBuilder()
            .withBoardEvaluator(new SmartBoardEvaluator())
            .withTimeoutInSeconds(5)
            .withDepth(3)
            .build();
        this.board = Boards.immutableBoard();
    }

    @Test
    void shouldReturnNonEmptyMove() {
        final var move = bruteForce.searchForTheBestMove(complicatedBoard());

        assertThat(move).isNotEqualTo(Move.emptyMove());
    }

    private Board<?> complicatedBoard() {
        return board
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(W)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(W)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(W)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S);
    }

    @Nested
    @DisplayName("execute with timeout")
    class TimeoutTest {

        @Test
        @DisplayName("should not return empty move on complicated board")
        void shouldFindAnyMoveOnVeryComplicatedBoard() {
            final var semiComplicatedBoard = getComplicatedBoard(board);

            final var aiMove = bruteForce.searchForTheBestMove(semiComplicatedBoard);

            Assertions.assertThat(aiMove).isNotEqualTo(Move.emptyMove());
        }

        private Board<Player> getComplicatedBoard(final Board<Player> board) {
            return board
                .executeMove(N)
                .executeMove(E)
                .executeMove(S)
                .executeMove(S)
                .executeMove(W)
                .executeMove(W)
                .executeMove(N)
                .executeMove(N)
                .executeMove(N)
                .executeMove(E)
                .executeMove(E)
                .executeMove(E);
        }

        @Test
        @DisplayName("should not return empty move on semi-complicated board")
        void shouldNotReturnEmptyMove() {
            final var semiComplicatedBoard = getSemiComplicatedBoard(board);

            final var aiMove = bruteForce.searchForTheBestMove(semiComplicatedBoard);

            Assertions.assertThat(aiMove).isNotEqualTo(Move.emptyMove());
        }

        private Board<Player> getSemiComplicatedBoard(final Board<Player> board) {
            return board
                .executeMove(N)
                .executeMove(E)
                .executeMove(S)
                .executeMove(S)
                .executeMove(W)
                .executeMove(W)
                .executeMove(N)
                .executeMove(N);
        }
    }

    @Nested
    @DisplayName("Smart evaluator")
    class SmartEvaluator {

        @Nested
        @DisplayName("Upper goal")
        class UpperGoal {

            private final ExecutorService pool = Executors.newSingleThreadExecutor();

            @AfterAll
            void afterAll() {
                pool.shutdown();
            }

            @Test
            @DisplayName("Should score the goal when player FIRST")
            void scoreAGoal() {
                final var after4Moves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should NOT score the goal when player FIRST")
            void shouldNotMakeSuicideMove() {
                final var after4Moves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 2")
            void scoreAGoalDepth2() {
                final var after4Moves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should NOT score the goal when player FIRST, depth 2")
            void shouldNotMakeSuicideMoveDepth2() {
                final var after4Moves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 3")
            void scoreAGoalDepth3() {
                final var after4Moves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should NOT score the goal when player FIRST, depth 3")
            void shouldNotMakeSuicideMoveDepth3() {
                final var after4Moves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }
        }

        @Nested
        @DisplayName("Bottom goal")
        class BottomGoal {

            private final ExecutorService pool = Executors.newSingleThreadExecutor();

            @AfterAll
            void afterAll() {
                pool.shutdown();
            }

            @Test
            @DisplayName("Should score a goal when player SECOND")
            void scoreAGoalInSecondDay() {
                final var after4Moves = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST")
            void shouldNotMakeSuicideMove() {
                final var after4Moves = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(SECOND)
                );
            }

            @Test
            @DisplayName("Should score a goal when player SECOND, depth 2")
            void scoreAGoalInSecondDayDepth2() {
                final var after5Moves = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S);

                final var bestMove = bruteForce.searchForTheBestMove(after5Moves, pool);
                final var afterAiMove = after5Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.takeTheWinner().get()).isEqualByComparingTo(SECOND)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 2")
            void shouldNotMakeSuicideMoveDepth2() {
                final var after4Moves = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should score a goal when player SECOND, depth 3")
            void scoreAGoalInSecondDayDepth3() {
                final var after4Moves = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 3")
            void shouldNotMakeSuicideMoveDepth3() {
                final var after4Moves = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S);

                final var bestMove = bruteForce.searchForTheBestMove(after4Moves, pool);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }
        }

        @Nested
        @DisplayName("Close corner")
        class CornerTest {

            @Test
            @DisplayName("should not hit the corner, depth 1")
            void rightUpperCorner() {
                final var afterMoves = board
                    .executeMove(NE)
                    .executeMove(S)
                    .executeMove(NE)
                    .executeMove(NE)
                    .executeMove(NW)
                    .executeMove(NE)
                    .executeMove(N)
                    .executeMove(SE)
                    .executeMove(W)
                    .executeMove(W);

                final var best = bruteForce.searchForTheBestMove(afterMoves);
                final var afterAi = afterMoves.executeMove(best);

                Assertions.assertThat(afterAi.isGameOver()).isFalse();
            }

            @Test
            @DisplayName("should not hit the corner, depth 2")
            void rightUpperCorner2() {
                final var afterMoves = board
                    .executeMove(NE)
                    .executeMove(S)
                    .executeMove(NE)
                    .executeMove(NE)
                    .executeMove(NW)
                    .executeMove(NE)
                    .executeMove(N)
                    .executeMove(SE)
                    .executeMove(W)
                    .executeMove(W);

                final var best = bruteForce.searchForTheBestMove(afterMoves);
                final var afterAi = afterMoves.executeMove(best);

                Assertions.assertThat(afterAi.isGameOver()).isFalse();
            }
        }
    }
}
