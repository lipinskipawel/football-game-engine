package io.github.lipinskipawel.board.classic;

import io.github.lipinskipawel.board.FootballBitBoard;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.lipinskipawel.board.FootballBitBoard.Direction.E;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.N;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.NE;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.NW;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.S;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.SE;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.SW;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.W;
import static io.github.lipinskipawel.board.MiniMax.bestMove;

final class MiniMaxGetBackTest implements WithAssertions {

    @Test
    void get_back_win_from_corner() {
        var afterMoves = new FootballBitBoard(true)
            .executeMove(List.of(S))
            .executeMove(List.of(W))
            .executeMove(List.of(SE))
            .executeMove(List.of(N, NE))
            .executeMove(List.of(SE))
            .executeMove(List.of(N))
            .executeMove(List.of(W, W, SW, SW))
            .executeMove(List.of(E))
            .executeMove(List.of(SW))
            .executeMove(List.of(N, N))
            .executeMove(List.of(SE, SE))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(E))
            .executeMove(List.of(NW))
            .executeMove(List.of(SW, W))
            .executeMove(List.of(NE, W, NE, SE, S, NE))
            .executeMove(List.of(S, SE));
//            .executeMove(List.of(S, NE, W, W))
//            .executeMove(List.of(SW, N))
//            .executeMove(List.of(SE, N, W, NE, S, NW, W, SE, SW))
//            .executeMove(List.of(W, NW))
//            .executeMove(List.of(NE, SE))
//            .executeMove(List.of(SE, W, N, SW, N))
//            .executeMove(List.of(SE, S));

//        var bestMove = bestMove(afterMoves, 4); // depth 4 = 146,425,612 moves = 1m15s
        var bestMove = bestMove(afterMoves, 4); // depth 4 = 1,591,609 moves = 875ms
        System.out.println(bestMove);

        System.out.println(afterMoves.getActivePlayer());
        System.out.println(afterMoves.isGameOver());
        System.out.println(afterMoves.isTopGoal());
        System.out.println(afterMoves.isBottomGoal());
    }

    @Test
    void get_back_win_from_goal_close_area() {
        var afterMoves = new FootballBitBoard(true)
            .executeMove(List.of(S))
            .executeMove(List.of(W))
            .executeMove(List.of(SE))
            .executeMove(List.of(N, NE))
            .executeMove(List.of(SE))
            .executeMove(List.of(N))
            .executeMove(List.of(W, W, SW, SW))
            .executeMove(List.of(E))
            .executeMove(List.of(SW))
            .executeMove(List.of(N, N))
            .executeMove(List.of(SE, SE))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(E))
            .executeMove(List.of(NW))
            .executeMove(List.of(SW, W))
            .executeMove(List.of(NE, W, NE, SE, S, NE))
            .executeMove(List.of(S, SE))
            .executeMove(List.of(S, NE, W, W))
            .executeMove(List.of(SW, N));
//            .executeMove(List.of(SE, N, W, NE, S, NW, W, SE, SW))
//            .executeMove(List.of(W, NW))
//            .executeMove(List.of(NE, SE))
//            .executeMove(List.of(SE, W, N, SW, N))
//            .executeMove(List.of(SE, S));

        var bestMove = bestMove(afterMoves, 5); // depth 5 = 15m16 OOM
        System.out.println(bestMove);

        System.out.println(afterMoves.getActivePlayer());
        System.out.println(afterMoves.isGameOver());
        System.out.println(afterMoves.isTopGoal());
        System.out.println(afterMoves.isBottomGoal());
    }

    @Test
    void get_back_win_from_step_back() {
        var afterMoves = new FootballBitBoard(true)
            .executeMove(List.of(S))
            .executeMove(List.of(W))
            .executeMove(List.of(SE))
            .executeMove(List.of(N, NE))
            .executeMove(List.of(SE))
            .executeMove(List.of(N))
            .executeMove(List.of(W, W, SW, SW))
            .executeMove(List.of(E))
            .executeMove(List.of(SW))
            .executeMove(List.of(N, N))
            .executeMove(List.of(SE, SE))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(E))
            .executeMove(List.of(NW))
            .executeMove(List.of(SW, W))
            .executeMove(List.of(NE, W, NE, SE, S, NE))
            .executeMove(List.of(S, SE))
            .executeMove(List.of(S, NE, W, W))
            .executeMove(List.of(SW, N))
            .executeMove(List.of(SE, N, W, NE, S, NW, W, SE, SW))
            .executeMove(List.of(W, NW));
//            .executeMove(List.of(NE, SE))
//            .executeMove(List.of(SE, W, N, SW, N))
//            .executeMove(List.of(SE, S));

        var bestMove = bestMove(afterMoves, 3); // depth 3 = 482,415 moves = 400ms
        System.out.println(bestMove);

        System.out.println(afterMoves.getActivePlayer());
        System.out.println(afterMoves.isGameOver());
        System.out.println(afterMoves.isTopGoal());
        System.out.println(afterMoves.isBottomGoal());
    }

    @Test
    void already_won() {
        // given
        var afterMoves = new FootballBitBoard(true, true)
            .executeMove(List.of(S))
            .executeMove(List.of(W))
            .executeMove(List.of(SE))
            .executeMove(List.of(N, NE))
            .executeMove(List.of(SE))
            .executeMove(List.of(N))
            .executeMove(List.of(W, W, SW, SW))
            .executeMove(List.of(E))
            .executeMove(List.of(SW))
            .executeMove(List.of(N, N))
            .executeMove(List.of(SE, SE))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(E))
            .executeMove(List.of(NW))
            .executeMove(List.of(SW, W))
            .executeMove(List.of(NE, W, NE, SE, S, NE))
            .executeMove(List.of(S, SE))
            .executeMove(List.of(S, NE, W, W))
            .executeMove(List.of(SW, N))
            .executeMove(List.of(SE, N, W, NE, S, NW, W, SE, SW))
            .executeMove(List.of(W, NW))
            .executeMove(List.of(NE, SE))
            .executeMove(List.of(SE, W, N, SW, N));
//            .executeMove(List.of(SE, S));

        System.out.println(afterMoves.getActivePlayer());
        System.out.println(afterMoves.isGameOver());
        System.out.println(afterMoves.isTopGoal());
        System.out.println(afterMoves.isBottomGoal());
        assertThat(afterMoves.getActivePlayer()).isEqualTo(FootballBitBoard.Player.SOUTH);

        // when
//        var bestMove = bestMove(afterMoves, 1); // depth 2 = 4285 moves = 61ms
//        var bestMove = bestMove(afterMoves, 2); // depth 2 = 420,458 moves = 360ms
//        var bestMove = bestMove(afterMoves, 2); // depth 2 = 7,638 moves = 280ms
        var bestMove = bestMove(afterMoves, 3); // depth 3 = 19,645,305 moves = 18s

        // then
        var resultPosition = afterMoves.executeMove(bestMove);

        assertThat(bestMove).isEqualTo(List.of(N, W, SE, NE, S, NE, S, W, W, SE, SE));
        assertThat(resultPosition.getActivePlayer()).isEqualTo(FootballBitBoard.Player.SOUTH);
        assertThat(resultPosition.isGameOver()).isTrue();
        assertThat(resultPosition.isTopGoal()).isFalse();
        assertThat(resultPosition.isBottomGoal()).isTrue();
    }
}
