package io.github.lipinskipawel.board.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Sanity checks of board state")
class SanityChecksBoardTests {

    private Board<Player> board;
    private static int STARTING_BALL_POSITION = 58;


    @BeforeAll
    static void setUpBall() {
        STARTING_BALL_POSITION = 58;
    }

    @BeforeEach
    void setUp() throws Exception {
        this.board = new ImmutableBoard<>(new PlayerProvider<>(Player.FIRST, Player.SECOND));
    }


    @Test
    @DisplayName("Starting ball position is 58")
    void startingBallPosition() {

        assertEquals(STARTING_BALL_POSITION,
            this.board.getBallPosition());
    }

    @Test
    @DisplayName("Returning new instance of board after return")
    void afterMoveReturnNewBoard() {

        //When:
        Board afterMove = board.executeMove(Direction.N);

        //Then:
        assertNotEquals(afterMove, board);
    }

    @Test
    @DisplayName("Make a move and don't track reference. Prove of board immutability")
    void boardImmutability() {

        //When:
        if (board.isMoveAllowed(Direction.N))
            board.executeMove(Direction.N);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(STARTING_BALL_POSITION, actualBallPosition);

    }

    @Test
    @DisplayName("Default first player")
    void defaultBoardPlayerOne() {

        assertEquals(Player.FIRST, board.getPlayer());

    }

}
