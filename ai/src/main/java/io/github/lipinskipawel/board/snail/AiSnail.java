package io.github.lipinskipawel.board.snail;

import io.github.lipinskipawel.board.FootballBitBoard;

import java.util.List;

import static io.github.lipinskipawel.board.FootballBitBoard.Direction.E;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.N;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.S;
import static io.github.lipinskipawel.board.FootballBitBoard.Direction.W;

final class AiSnail {

    static FootballBitBoard snailFootballField() {
        return new FootballBitBoard(true)
            .executeMove(List.of(N))
            .executeMove(List.of(W))
            .executeMove(List.of(S))
            .executeMove(List.of(S))
            .executeMove(List.of(E))
            .executeMove(List.of(E))
            .executeMove(List.of(N))
            .executeMove(List.of(N))
            .executeMove(List.of(N))
            .executeMove(List.of(W))  // 7718 - 22 ms
            .executeMove(List.of(W))
            .executeMove(List.of(W))
            .executeMove(List.of(S))
            .executeMove(List.of(S))
            .executeMove(List.of(S)) // 2,957,071 - 1700 ms
            .executeMove(List.of(S)); // 4,887,554 - 2900 ms
//            .executeMove(List.of(E)); // breaking point - more than 6 minutes - Ctrl-C
//            .executeMove(List.of(E))
//            .executeMove(List.of(E))
//            .executeMove(List.of(E))
    }
}
