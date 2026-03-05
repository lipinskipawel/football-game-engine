package io.github.lipinskipawel.board.minimax;

import io.github.lipinskipawel.board.MyFootball;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.lipinskipawel.board.MyFootball.Direction.E;
import static io.github.lipinskipawel.board.MyFootball.Direction.N;
import static io.github.lipinskipawel.board.MyFootball.Direction.NE;
import static io.github.lipinskipawel.board.MyFootball.Direction.NW;
import static io.github.lipinskipawel.board.MyFootball.Direction.S;
import static io.github.lipinskipawel.board.MyFootball.Direction.SE;
import static io.github.lipinskipawel.board.MyFootball.Direction.SW;
import static io.github.lipinskipawel.board.MyFootball.Direction.W;
import static io.github.lipinskipawel.board.MyMiniMax.bestMove;

final class MyMiniMax {

    static void findBestMove() {
        var board = new MyFootball();
        board.initBoard();
        board.executePlayerMove(List.of(S));
        board.executePlayerMove(List.of(W));
        board.executePlayerMove(List.of(SE));
        board.executePlayerMove(List.of(N, NE));
        board.executePlayerMove(List.of(SE));
        board.executePlayerMove(List.of(N));
        board.executePlayerMove(List.of(W, W, SW, SW));
        board.executePlayerMove(List.of(E));
        board.executePlayerMove(List.of(SW));
        board.executePlayerMove(List.of(N, N));
        board.executePlayerMove(List.of(SE, SE));
        board.executePlayerMove(List.of(N, SE));
        board.executePlayerMove(List.of(E));
        board.executePlayerMove(List.of(NW));
        board.executePlayerMove(List.of(SW, W));
        board.executePlayerMove(List.of(NE, W, NE, SE, S, NE));
        board.executePlayerMove(List.of(S, SE));
        board.executePlayerMove(List.of(S, NE, W, W));
        board.executePlayerMove(List.of(SW, N));
        board.executePlayerMove(List.of(SE, N, W, NE, S, NW, W, SE, SW));
        board.executePlayerMove(List.of(W, NW));
        board.executePlayerMove(List.of(NE, SE));
        board.executePlayerMove(List.of(SE, W, N, SW, N));
        System.out.println(board.isFirstToMove());
        System.out.println(board.legalMoves().size());

        var l = System.nanoTime();
        final var move = bestMove(board, 3);
        var l1 = System.nanoTime();
        var diff = (l1 - l) / 1_000_000;
        System.out.println("Time took in milliseconds: " + diff + "ms");

        System.out.println(toDirs(move));
    }

    private static List<MyFootball.Direction> toDirs(List<Integer> moves) {
        return moves.stream()
            .map(MyMiniMax::dir)
            .collect(Collectors.toList());
    }

    private static MyFootball.Direction dir(int num) {
        if (num == 0) {
            return MyFootball.Direction.N;
        }
        if (num == 1) {
            return MyFootball.Direction.NE;
        }
        if (num == 2) {
            return MyFootball.Direction.E;
        }
        if (num == 3) {
            return MyFootball.Direction.SE;
        }
        if (num == 4) {
            return MyFootball.Direction.S;
        }
        if (num == 5) {
            return MyFootball.Direction.SW;
        }
        if (num == 6) {
            return MyFootball.Direction.W;
        }
        if (num == 7) {
            return MyFootball.Direction.NW;
        }
        return null;
    }

}
