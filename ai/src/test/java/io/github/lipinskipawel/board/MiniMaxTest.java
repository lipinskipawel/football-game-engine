package io.github.lipinskipawel.board;

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

final class MiniMaxTest implements WithAssertions {

    @Test
    void find_trap() {
        var position = new FootballBitBoard(true)
            .executeMove(List.of(N))
            .executeMove(List.of(NW))
            .executeMove(List.of(NE))
            .executeMove(List.of(W))
            .executeMove(List.of(N))
            .executeMove(List.of(SW))
            .executeMove(List.of(SE, N, NE))
            .executeMove(List.of(W, SE, N, SE))
            .executeMove(List.of(N))
            .executeMove(List.of(SW, E, SE))
            .executeMove(List.of(N))
            .executeMove(List.of(NW, NE, SE))
            .executeMove(List.of(N, SW))
            .executeMove(List.of(SW, E, SW))
            .executeMove(List.of(SW, N))
            .executeMove(List.of(N, SE, N, SW, NW, SW))
            .executeMove(List.of(NW))
            .executeMove(List.of(E, S, W))
            .executeMove(List.of(N, NW, NE, SE))
            .executeMove(List.of(S, SW, NW, E, SW, E, S))
            .executeMove(List.of(NE, E, E, E, E, NE));

        var bestMove = bestMove(position, 2);

        var afterBestMove = position.executeMove(bestMove);
        assertThat(afterBestMove.getBallPosition()).isEqualTo(19);
    }

    @Test
    void find_trap_in_the_middle_when_white_to_move() {
        var position = new FootballBitBoard(true)
            .executeMove(List.of(NE))
            .executeMove(List.of(SE))
            .executeMove(List.of(NE))
            .executeMove(List.of(E, NW))
            .executeMove(List.of(E, SW, N, SW))
            .executeMove(List.of(E, NW))
            .executeMove(List.of(S, NW))
            .executeMove(List.of(E, SW, S))
            .executeMove(List.of(NE, S, W, SE))
            .executeMove(List.of(N, SW))
            .executeMove(List.of(E, SW))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(N))
            .executeMove(List.of(SW, E, NE, W, SE, W, SW))
            .executeMove(List.of(NW, NW))
            .executeMove(List.of(NE, S, NW, E, NW));

        var bestMove = bestMove(position, 2);

        assertThat(position.executeMove(bestMove).getBallPosition()).isEqualTo(61);
    }

    @Test
    void find_trap_in_the_middle_when_black_to_move() {
        var position = new FootballBitBoard(true)
            .executeMove(List.of(NE))
            .executeMove(List.of(SE))
            .executeMove(List.of(NE))
            .executeMove(List.of(E, NW))
            .executeMove(List.of(E, SW, N, SW))
            .executeMove(List.of(E, NW))
            .executeMove(List.of(S, NW))
            .executeMove(List.of(E, SW, S))
            .executeMove(List.of(NE, S, W, SE))
            .executeMove(List.of(N, SW))
            .executeMove(List.of(E, SW))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(N, SE))
            .executeMove(List.of(N))
            .executeMove(List.of(SW, E, NE, W, SE, W, SW))
            .executeMove(List.of(NW, NW))
            .executeMove(List.of(NE, S, NW, E, NW))
            .executeMove(List.of(N));

        var bestMove = bestMove(position, 3);

        assertThat(position.executeMove(bestMove).getBallPosition()).isEqualTo(61);
    }
}
