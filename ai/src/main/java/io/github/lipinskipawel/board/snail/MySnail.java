package io.github.lipinskipawel.board.snail;

import io.github.lipinskipawel.board.MyFootball;

import java.util.List;

import static io.github.lipinskipawel.board.MyFootball.Direction.E;
import static io.github.lipinskipawel.board.MyFootball.Direction.N;
import static io.github.lipinskipawel.board.MyFootball.Direction.S;
import static io.github.lipinskipawel.board.MyFootball.Direction.W;

final class MySnail {

    static MyFootball snailFootballField() {
        var board = new MyFootball();
        board.executePlayerMove(List.of(N));
        board.executePlayerMove(List.of(W));
        board.executePlayerMove(List.of(S));
        board.executePlayerMove(List.of(S));
        board.executePlayerMove(List.of(E));
        board.executePlayerMove(List.of(E));
        board.executePlayerMove(List.of(N));
        board.executePlayerMove(List.of(N));
        board.executePlayerMove(List.of(N));
        board.executePlayerMove(List.of(W));  // 7718 - 8 ms
        board.executePlayerMove(List.of(W));
        board.executePlayerMove(List.of(W));
        board.executePlayerMove(List.of(S));
        board.executePlayerMove(List.of(S));
        board.executePlayerMove(List.of(S)); // 2,957,071 - 450 ms
        board.executePlayerMove(List.of(S)); // 4,887,554 - 740 ms
//        board.executePlayerMove(List.of(E)); // breaking point - incorrect code - 36,368,449 - 3000 ms
//        board.executePlayerMove(List.of(E)); // breaking point - incorrect code - 20 seconds - OOM
//        board.executePlayerMove(List.of(E));
//        board.executePlayerMove(List.of(E));
        return board;
    }
}
