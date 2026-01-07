package io.github.lipinskipawel.board;

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

public final class Main {

    public static void main(String[] args) {
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

        var bestMove = bestMove(afterMoves, 3);

        System.out.println(bestMove);
    }
}
