package io.github.lipinskipawel.board.engine;

import io.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("API -- ImmutableBoard")
class ImmutableBoardTest {

    private Board<Player> board;
    private static int STARTING_BALL_POSITION;
    private static int POSITION_AFTER_N_MOVE;
    private static int POSITION_AFTER_S_MOVE;

    @BeforeAll
    static void setUpVariable() {
        STARTING_BALL_POSITION = 58;
        POSITION_AFTER_N_MOVE = 49;
        POSITION_AFTER_S_MOVE = 67;
    }

    @BeforeEach
    void setUp() {
        this.board = new ImmutableBoard<>(new PlayerProvider<>(Player.FIRST, Player.SECOND));
    }

    @Nested
    @DisplayName("sanity")
    class SanityTest {

        @Test
        @DisplayName("equality test")
        void shouldBeEqual() {
            final var first = new ImmutableBoard<>(new PlayerProvider<>(1, 2));
            final var second = new ImmutableBoard<>(new PlayerProvider<>(1, 2));

            Assertions.assertThat(first)
                .isEqualTo(second)
                .isNotSameAs(second);
        }

        @Test
        @DisplayName("0 moves, FIRST player to move")
        void noMovesFirstPlayerToMove() {
            Assertions.assertThat(board.getPlayer()).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("three moves with undo inside")
        void shouldBeThreeMoves() {
            final ImmutableBoard<Player> afterMoves = (ImmutableBoard<Player>) board
                .executeMove(Direction.SE)
                .executeMove(Direction.W)
                .executeMove(Direction.N)
                .undo()
                .executeMove(Direction.N)
                .executeMove(Direction.W);

            final ImmutableBoard<Player> undo = (ImmutableBoard<Player>) afterMoves.undoPlayerMove();

            Assertions.assertThat(afterMoves)
                .usingRecursiveComparison()
                .isEqualTo(undo);
        }

        @Test
        void shouldNotMutateBoardState() {
            final var firstEmptyBoard = new ImmutableBoard<>(new PlayerProvider<>(Player.FIRST, Player.SECOND));
            final var secondEmptyBoard = new ImmutableBoard<>(new PlayerProvider<>(Player.FIRST, Player.SECOND));
            Assertions.assertThat(firstEmptyBoard)
                .usingRecursiveComparison()
                .isEqualTo(secondEmptyBoard);

            secondEmptyBoard.executeMove(Direction.E);

            Assertions.assertThat(firstEmptyBoard)
                .usingRecursiveComparison()
                .isEqualTo(secondEmptyBoard);
        }
    }

    @Nested
    @DisplayName("executeMove")
    class MakeAMove {

        @Test
        void shouldSwitchPlayerAfterMove() {
            final var afterOne = board.executeMove(Direction.N);

            Assertions.assertThat(afterOne.getPlayer()).isEqualTo(Player.SECOND);
        }

        @Test
        void shouldNotSwitchPlayerWhenMakingSmallMove() {
            final var afterTwo = board.executeMove(new Move(List.of(Direction.N, Direction.W)));
            Assertions.assertThat(afterTwo.getPlayer()).isEqualTo(Player.FIRST);

            final var afterSmallMove = afterTwo.executeMove(new Move(List.of(Direction.SE)));

            Assertions.assertThat(afterSmallMove.getPlayer()).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("Make a proper full move towards North")
        void makeAMoveN() {
            final var afterMove = board.executeMove(Direction.N);

            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_N_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards South")
        void makeAMoveS() {
            final var afterMove = board.executeMove(Direction.S);

            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_S_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards East, North and check allowed moves")
        void makeAMoveEN() {
            final var afterMove = board.executeMove(Direction.E)
                .executeMove(Direction.N);

            assertAll(
                () -> assertTrue(afterMove.isMoveAllowed(Direction.N)),
                () -> assertTrue(afterMove.isMoveAllowed(Direction.NE)),
                () -> assertTrue(afterMove.isMoveAllowed(Direction.E)),
                () -> assertTrue(afterMove.isMoveAllowed(Direction.SE)),
                () -> assertFalse(afterMove.isMoveAllowed(Direction.S)),
                () -> assertTrue(afterMove.isMoveAllowed(Direction.SW)),
                () -> assertTrue(afterMove.isMoveAllowed(Direction.W)),
                () -> assertTrue(afterMove.isMoveAllowed(Direction.NW))
            );
        }

        @Test
        @DisplayName("Make a one full move and don't allow to move backwards")
        void notAllowToMakeAMove() {
            final var afterFirstMove = board.executeMove(Direction.N);
            Board afterSecondMove = null;
            if (afterFirstMove.isMoveAllowed(Direction.S)) {
                afterSecondMove = board.executeMove(Direction.S);
            }

            assertNull(afterSecondMove);
        }

        @Test
        @DisplayName("Can't follow executed moves")
        void makeTwoMovesAndTryFollowExecutedMoves() {
            final var afterMoves = board.executeMove(Direction.N)
                .executeMove(Direction.E)
                .executeMove(Direction.SW);

            assertAll(
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.NW)),
                () -> assertFalse(afterMoves.isMoveAllowed(Direction.N)),
                () -> assertFalse(afterMoves.isMoveAllowed(Direction.NE)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.E)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.SE)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.S)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.SW)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.W))
            );
        }

        @Test
        @DisplayName("Can't follow executed moves, move sample")
        void makeTwoMovesAndTryFollowExecutedMovesMoreSample() {
            final var afterMoves = board.executeMove(Direction.N)
                .executeMove(Direction.E)
                .executeMove(Direction.SW)
                .executeMove(Direction.SW)
                .executeMove(Direction.E)
                .executeMove(Direction.N);

            assertAll(
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.NW)),
                () -> assertFalse(afterMoves.isMoveAllowed(Direction.N)),
                () -> assertFalse(afterMoves.isMoveAllowed(Direction.NE)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.E)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.SE)),
                () -> assertFalse(afterMoves.isMoveAllowed(Direction.S)),
                () -> assertFalse(afterMoves.isMoveAllowed(Direction.SW)),
                () -> assertTrue(afterMoves.isMoveAllowed(Direction.W))
            );
        }

        @Test
        @DisplayName("four moves (inside is one small move)")
        void shouldBePlayerFirstToMove() {
            final var afterMoves = board
                .executeMove(Direction.NE)
                .executeMove(Direction.NW)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.W);

            Assertions.assertThat(afterMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }
    }

    @Nested
    @DisplayName("undoMove")
    class UndoAMove {

        @Test
        @DisplayName("Try to undo move when no move has been done yet")
        void undoMoveWhenGameJustBegun() {
            assertThrows(RuntimeException.class,
                () -> board.undo(),
                () -> "Can't undo move when no move has been done");
        }

        @Test
        @DisplayName("Make a one simple S move and then undo")
        void makeAMoveSAndUndoMove() {
            final var afterMove = board.executeMove(Direction.S);
            final var afterUndo = afterMove.undo();

            int actualBallPosition = afterUndo.getBallPosition();
            assertEquals(STARTING_BALL_POSITION, actualBallPosition);
        }

        @Test
        @DisplayName("Make a one simple S move and then undo")
        void makeAMoveSAndUndoMoveAndCheckSanity() {
            final var afterMove = board.executeMove(Direction.S);
            final var afterUndo = afterMove.undo();

            final var legalMoves = afterUndo.allLegalMoves();
            Assertions.assertThat(legalMoves.size()).isEqualTo(8);
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move")
        void makeAMoveNAndUndoMove() {
            final var afterOneMove = board.executeMove(Direction.N);
            final var afterSecondMove = afterOneMove.executeMove(Direction.E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertEquals(afterSecondMove.getBallPosition(), shouldBeAfterSubMove.getBallPosition(),
                () -> "Ball should be in the same spot");
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move Another Check")
        void makeAMoveNAndUndoMoveAnotherCheck() {
            final var afterOneMove = board.executeMove(Direction.N);
            final var afterSecondMove = afterOneMove.executeMove(Direction.E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertTrue(shouldBeAfterSubMove.isMoveAllowed(Direction.SW),
                () -> "Make a move in 'undo' direction must be possible");
        }

        @Test
        @DisplayName("make 4 moves and undo 4 moves")
        void undoAllMoves() {
            final var afterThreeMoves = board
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(Direction.S)
                .executeMove(Direction.W);

            final var undoAllMoves = afterThreeMoves
                .undo()
                .undo()
                .undo()
                .undo();

            Assertions.assertThat(undoAllMoves).isEqualToComparingFieldByFieldRecursively(board);
        }

        @Test
        @DisplayName("big snail football field")
        void big_snail_testing_bitboard() {
            final var afterMove = board
                .executeMove(Direction.N)
                .executeMove(Direction.W)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.E)
                .executeMove(Direction.E)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.W)
                .executeMove(Direction.W)
                .executeMove(Direction.W)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.S);
//                .executeMove(Direction.S)
//                .executeMove(Direction.E)
//                .executeMove(Direction.E)
//                .executeMove(Direction.E)
//                .executeMove(Direction.E);
//                .executeMove(Direction.N)
//                .executeMove(Direction.N)
//                .executeMove(Direction.N)
//                .executeMove(Direction.N)
//                .executeMove(Direction.N)
//                .executeMove(Direction.W);

            final var futureLegalMoves = afterMove.allLegalMovesFuture();

            var l = System.nanoTime();
            var legalMoves = findAllLegalMoves(futureLegalMoves);
            var l1 = System.nanoTime();
            var diff = (l1 - l) / 1_000_000;
            System.out.println("Time took in seconds: " + diff);

            Assertions.assertThat(legalMoves.size()).isEqualTo(2957071);
        }

        private List<Move> findAllLegalMoves(LegalMovesFuture legalMovesFuture) {
            legalMovesFuture.start(Duration.ofMinutes(2));
            final var allMoves = new ArrayList<Move>();

            while (legalMovesFuture.isRunning()) {
                allMoves.addAll(legalMovesFuture.partialResult());
            }
            allMoves.addAll(legalMovesFuture.partialResult());
            return allMoves;
        }

        @Test
        @DisplayName("make 4 moves and undo 4 moves")
        void undoOneMoves() {
            final var temo = board
                .executeMove(new Move(List.of(Direction.NE)));

            final var afterThreeMoves = board
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(Direction.N)
                .undo();

            Assertions.assertThat(afterThreeMoves).isEqualToComparingFieldByFieldRecursively(temo);
        }

        @Test
        @DisplayName("sadasdasf ")
        void saundoOneMoves() {
            final var afterThreeMoves = board
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(Direction.N);

            final var second = board
                .executeMove(Direction.NE)
                .executeMove(Direction.N);

            Assertions.assertThat(second).isEqualToComparingFieldByFieldRecursively(afterThreeMoves);
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move Another Check")
        void makeAMoveNAndUndoMoveAnotherCheckYetAnother() {
            final var afterOneMove = board.executeMove(Direction.N);
            final var afterSecondMove = afterOneMove.executeMove(Direction.E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            org.junit.jupiter.api.Assertions.assertEquals(Player.FIRST, shouldBeAfterSubMove.getPlayer(),
                () -> "Not change player");
        }
    }

    @Nested
    @DisplayName("undoPlayerMove")
    class UndoPlayerMoveTest {

        @Test
        @DisplayName("should not undo when no small moves are made")
        void noUndoNoSmallMoves() {
            final var afterTwoMoves = board
                .executeMove(Direction.N)
                .executeMove(Direction.NE);

            final var undoPlayer = afterTwoMoves.undoPlayerMove();

            Assertions.assertThat(undoPlayer.getPlayer()).isEqualByComparingTo(afterTwoMoves.getPlayer());
        }

        @Test
        @DisplayName("should undo when small move has been played")
        void undoSmallMove() {
            final var afterTwoMoves = board
                .executeMove(Direction.W)
                .executeMove(Direction.N);

            final var smallMoveAndUndo = afterTwoMoves
                .executeMove(Direction.SE)
                .undoPlayerMove();

            Assertions.assertThat(smallMoveAndUndo)
                .usingRecursiveComparison()
                .isEqualTo(afterTwoMoves);
        }

        @Test
        @DisplayName("should undo one small move even executed twice")
        void undoSmallMoveTwo() {
            final var afterTwoMoves = board
                .executeMove(Direction.W)
                .executeMove(Direction.N);

            final var smallMoveAndUndo = afterTwoMoves
                .executeMove(Direction.SE)
                .undoPlayerMove();

            final var boardInterface = smallMoveAndUndo.undoPlayerMove();

            Assertions.assertThat(boardInterface)
                .usingRecursiveComparison()
                .isEqualTo(smallMoveAndUndo);
        }
    }

    @Nested
    @DisplayName("getPlayer")
    class GetPlayerTest {

        @Test
        @DisplayName("zero moves")
        void shouldBeTheFirstPlayerToMoveEmptyBoard() {
            Assertions.assertThat(board.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("one move")
        void shouldBeSecondPlayerToMove() {
            final var afterMove = board.executeMove(Direction.E);

            org.junit.jupiter.api.Assertions.assertEquals(Player.SECOND, afterMove.getPlayer());
        }

        @Test
        @DisplayName("two moves")
        void shouldBeFirstPlayerToMove() {
            final var afterTwoMoves = board
                .executeMove(Direction.W)
                .executeMove(Direction.S);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("two moves, small move")
        void shouldBeTheFirstPlayerToMove() {
            final var afterTwoMoves = board
                .executeMove(Direction.W)
                .executeMove(Direction.S)
                .executeMove(Direction.NE);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("two moves, one undo")
        void shouldBeTheSecondPlayer() {
            final var afterTwoMoves = board
                .executeMove(Direction.W)
                .executeMove(Direction.NW);
            final var afterUndoMove = afterTwoMoves.undo();

            Assertions.assertThat(afterUndoMove.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("two moves, one small move, one undo, small move")
        void shouldBeThatSamePlayer() {
            final var firstToMove = board
                .executeMove(Direction.E)
                .executeMove(Direction.N);

            final var afterMoveAndUndo = firstToMove
                .executeMove(Direction.SW)
                .undo()
                .executeMove(Direction.SW);

            Assertions.assertThat(afterMoveAndUndo.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("5 moves to north goal")
        void fiveMovesToNorthGoal() {
            final var thisIsGoal = board
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(new Move(List.of(Direction.NW, Direction.NE)));

            Assertions.assertThat(thisIsGoal.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("5 moves to south goal")
        void fiveMovesToSouthGoal() {
            final var thisIsGoal = board
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(new Move(List.of(Direction.SE, Direction.SW)));

            Assertions.assertThat(thisIsGoal.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("moves to SE corner")
        void movesToSeCorner() {
            final var ballInTheCorner = board
                .executeMove(Direction.SE)
                .executeMove(Direction.SE)
                .executeMove(Direction.SE)
                .executeMove(Direction.S)
                .executeMove(Direction.SE);

            Assertions.assertThat(ballInTheCorner.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("15 moves and hit inner corner")
        void fifteenMovesAndHitTheInnerCorner() {
            final var afterMoves = board
                .executeMove(Direction.N)
                .executeMove(Direction.SE)
                .executeMove(Direction.W)
                .executeMove(Direction.NE)
                .executeMove(Direction.W)
                .executeMove(Direction.SW)
                .executeMove(Direction.E)
                .executeMove(Direction.NW)
                .executeMove(Direction.S)
                .executeMove(Direction.SE)
                .executeMove(Direction.N)
                .executeMove(Direction.SE)
                .executeMove(Direction.W)
                .executeMove(Direction.W)
                .executeMove(Direction.NE);

            Assertions.assertThat(afterMoves.allLegalMoves().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("moveHistory")
    class MoveHistoryTest {

        @Test
        @DisplayName("5 moves to north goal")
        void fiveMovesToNorthGoal() {
            final Board<Player> thisIsGoal = board
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(new Move(List.of(Direction.NW, Direction.NE)));

            Assertions.assertThat(thisIsGoal.moveHistory().size()).isEqualTo(5);
        }

        @Test
        @DisplayName("5 moves to south goal")
        void fiveMovesToSouthGoal() {
            final var thisIsGoal = board
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(Direction.S)
                .executeMove(new Move(List.of(Direction.SE, Direction.SW)));

            Assertions.assertThat(thisIsGoal.moveHistory().size()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("isGameOver")
    class IsGameOver {

        @Test
        @DisplayName("0 moves")
        void shouldNotEndTheGameAfter0Moves() {
            Assertions.assertThat(board.isGameOver()).isFalse();
        }

        @Test
        @DisplayName("5 moves to north goal")
        void shouldBeGameOverWhenPlayerScoreAGoal() {
            final var afterMoves = board
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.NE)
                .executeMove(Direction.NW);

            Assertions.assertThat(afterMoves.isGameOver()).isTrue();
        }

        @Test
        @DisplayName("moves to SE corner")
        void shouldEndedTheGameWhenPlayerHitsTheCorner() {
            final var afterMoves = board
                .executeMove(Direction.SE)
                .executeMove(Direction.SE)
                .executeMove(Direction.SE)
                .executeMove(Direction.S)
                .executeMove(Direction.SE);

            Assertions.assertThat(afterMoves.isGameOver()).isTrue();
        }
    }

    @Nested
    @DisplayName("nextPlayerToMove")
    class NextPlayerToMove {

        @Test
        @DisplayName("0 moves, SECOND player to move")
        void changePlayerZeroMoves() {
            final var wantedPlayer = Player.SECOND;

            final var changePlayer = board.nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(changePlayer.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("1 move, FIRST player to move")
        void oneMoveStillFirstPlayerToMove() {
            final var wantedPlayer = Player.FIRST;

            final var afterMoveChangePlayer = board
                .executeMove(Direction.N)
                .nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(afterMoveChangePlayer.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("should not change player when set the same")
        void changePlayerOnTheSamePlayer() {
            final var player = board
                .executeMove(Direction.E)
                .executeMove(Direction.E)
                .executeMove(Direction.E)
                .nextPlayerToMove(Player.SECOND)
                .getPlayer();

            Assertions.assertThat(player).isEqualTo(Player.SECOND);
        }

        @Test
        @DisplayName("5 moves to goal")
        void shouldChangePlayerInGoalArea() {
            final var wantedPlayer = Player.FIRST;

            final var afterMoves = board
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.N)
                .executeMove(Direction.NW)
                .executeMove(Direction.NE)
                .nextPlayerToMove(wantedPlayer);

            assertAll(
                () -> Assertions.assertThat(afterMoves.isGoal()).isTrue(),
                () -> Assertions.assertThat(afterMoves.getPlayer()).isEqualTo(wantedPlayer)
            );
        }

        @Test
        @DisplayName("5 moves to corner")
        void shouldChangePlayerInCornerKill() {
            final var wantedPlayer = Player.FIRST;

            final var afterMoves = board
                .executeMove(Direction.SE)
                .executeMove(Direction.SE)
                .executeMove(Direction.SE)
                .executeMove(Direction.S)
                .executeMove(Direction.SE)
                .nextPlayerToMove(wantedPlayer);

            assertAll(
                () -> Assertions.assertThat(afterMoves.isGameOver()).isTrue(),
                () -> Assertions.assertThat(afterMoves.getPlayer()).isEqualTo(wantedPlayer)
            );
        }

        @Test
        @DisplayName("one move, one undo, change player")
        void shouldChangeOnUnchangedBoard() {
            final var wantedPlayer = Player.SECOND;

            final var afterMove = board
                .executeMove(Direction.SE)
                .undo()
                .nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(afterMove.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("undo small move, change player")
        void shouldChangePlayerAfterSmallUndo() {
            final var wantedPlayer = Player.SECOND;

            final var afterMoves = board
                .executeMove(Direction.SE)
                .executeMove(Direction.W)
                .executeMove(Direction.N)
                .undoPlayerMove()
                .nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(afterMoves.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("throw exception during small move")
        void shouldThrowExceptionDuringSmallMoveForTheSamePlayer() {
            final var exception = assertThrows(ChangePlayerIsNotAllowed.class,
                () -> board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.W)
                    .executeMove(Direction.N)
                    .nextPlayerToMove(Player.FIRST),
                () -> "Switching player during small moves is NOT acceptable"
            );

            Assertions.assertThat(exception).isInstanceOf(ChangePlayerIsNotAllowed.class);
        }

        @Test
        @DisplayName("throw exception during small move")
        void shouldThrowExceptionDuringSmallMoveForNextPlayer() {
            final var exception = assertThrows(ChangePlayerIsNotAllowed.class,
                () -> board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.W)
                    .executeMove(Direction.N)
                    .nextPlayerToMove(Player.SECOND),
                () -> "Switching player during small moves is NOT acceptable"
            );

            Assertions.assertThat(exception).isInstanceOf(ChangePlayerIsNotAllowed.class);
        }
    }

    @Nested
    @DisplayName("takeTheWinner")
    class TakeTheWinnerTest {

        @Test
        @DisplayName("0 move, no winner")
        void zeroMovesNoWinner() {
            assertThrows(NoSuchElementException.class,
                () -> board
                    .takeTheWinner()
                    .orElseThrow()
            );
        }

        @Test
        @DisplayName("should give First player when upper goal by First")
        void upperGoalByFirstPlayer() {
            final var winner = board
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.NW, Direction.NE)))
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("should give First player when upper goal by Second")
        void upperGoalBySecondPlayer() {
            final var winner = board
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.N)))
                .nextPlayerToMove(Player.SECOND)
                .executeMove(new Move(List.of(Direction.NW, Direction.NE)))
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("should give Second player when bottom goal by First")
        void bottomGoalByFirstPlayer() {
            final var winner = board
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.SW, Direction.SE)))
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.SECOND);
        }

        @Test
        @DisplayName("should give Second player when bottom goal by Second")
        void bottomGoalBySecondPlayer() {
            final var winner = board
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.S)))
                .executeMove(new Move(List.of(Direction.S)))
                .nextPlayerToMove(Player.SECOND)
                .executeMove(new Move(List.of(Direction.SW, Direction.SE)))
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.SECOND);
        }

        @Test
        @DisplayName("should give Second player when First hits the corner")
        void firstHitsTheCorner() {
            final var winner = board
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(new Move(List.of(Direction.N)))
                .executeMove(new Move(List.of(Direction.NE)))
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.SECOND);
        }

        @Test
        @DisplayName("should give First player when Second hits the corner")
        void secondHitsTheCorner() {
            final var winner = board
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(new Move(List.of(Direction.NE)))
                .executeMove(new Move(List.of(Direction.N)))
                .nextPlayerToMove(Player.SECOND)
                .executeMove(new Move(List.of(Direction.NE)))
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.FIRST);
        }

        @Test
        @DisplayName("should give Second player when First hits the corner in the center of board")
        void firstHitsTheCornerInTheCenterOfBoard() {
            final var winner = board
                .executeMove(Direction.NE)
                .executeMove(Direction.W)
                .executeMove(Direction.SE)
                .executeMove(Direction.W)
                .executeMove(Direction.N)
                .executeMove(Direction.SW)
                .executeMove(Direction.E)
                .executeMove(Direction.NW)
                .executeMove(Direction.S)
                .executeMove(Direction.SE)
                .executeMove(Direction.N)
                .executeMove(Direction.SW)
                .executeMove(Direction.E)
                .executeMove(Direction.E)
                .executeMove(Direction.NW)
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.SECOND);
        }

        @Test
        @DisplayName("should give First player when Second hits the corner in the center of board")
        void secondHitsTheCornerInTheCenterOfBoard() {
            final var winner = board
                .executeMove(Direction.NE)
                .executeMove(Direction.W)
                .executeMove(Direction.SE)
                .executeMove(Direction.W)
                .executeMove(Direction.N)
                .executeMove(Direction.SW)
                .executeMove(Direction.E)
                .executeMove(Direction.NW)
                .executeMove(Direction.S)
                .executeMove(Direction.SE)
                .executeMove(Direction.N)
                .executeMove(Direction.SW)
                .executeMove(Direction.E)
                .executeMove(Direction.E)
                .nextPlayerToMove(Player.SECOND)
                .executeMove(Direction.NW)
                .takeTheWinner()
                .get();

            Assertions.assertThat(winner).isEqualTo(Player.FIRST);
        }
    }
}
