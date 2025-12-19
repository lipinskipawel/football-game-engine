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
import static io.github.lipinskipawel.board.FootballBitBoard.boardToString;
import static io.github.lipinskipawel.board.FootballBitBoard.executeMove;
import static io.github.lipinskipawel.board.FootballBitBoard.init;
import static io.github.lipinskipawel.board.FootballBitBoard.legalMoves;

final class FootballBitBoardTest implements WithAssertions {

    @Test
    void printing_board() {
        var board = init();
        executeMove(board, List.of(N));
        executeMove(board, List.of(N));
        executeMove(board, List.of(E));
        executeMove(board, List.of(SW, SW));

        var moves = legalMoves(board);

        System.out.println(boardToString(board));
        printMoves(moves);
        assertThat(moves).hasSize(12);
    }

    @Test
    void left_side_of_walls_on_football_field() {
        var board = init();
        executeMove(board, List.of(W));
        executeMove(board, List.of(W));
        executeMove(board, List.of(W));
        executeMove(board, List.of(W, NE));

        var moves = legalMoves(board);

        System.out.println(boardToString(board));
        printMoves(moves);
        assertThat(moves).hasSize(42);
    }

    @Test
    void right_side_of_walls_on_football_field() {
        var board = init();
        executeMove(board, List.of(E));
        executeMove(board, List.of(E));
        executeMove(board, List.of(E));
        executeMove(board, List.of(E, NW));

        var moves = legalMoves(board);

        System.out.println(boardToString(board));
        printMoves(moves);
        assertThat(moves).hasSize(42);
    }

    @Test
    void top_side_of_walls_on_football_field() {
        var board = init();
        executeMove(board, List.of(NW));
        executeMove(board, List.of(NW));
        executeMove(board, List.of(NW));
        executeMove(board, List.of(N));
        executeMove(board, List.of(NE, SE));

        var moves = legalMoves(board);

        System.out.println(boardToString(board));
        printMoves(moves);
        assertThat(moves).hasSize(10);
    }

    @Test
    void qube_football_field() {
        var board = init();
        executeMove(board, List.of(N));
        executeMove(board, List.of(W));
        executeMove(board, List.of(S));

        var moves = legalMoves(board);

        System.out.println(boardToString(board));
        printMoves(moves);
        assertThat(moves).hasSize(20);
    }

    @Test
    void snail_football_field() {
        var board = init();
        executeMove(board, List.of(N));
        executeMove(board, List.of(W));
        executeMove(board, List.of(S));
        executeMove(board, List.of(S));
        executeMove(board, List.of(E));
        executeMove(board, List.of(E));
        executeMove(board, List.of(N));
        executeMove(board, List.of(N));
        executeMove(board, List.of(N));
        executeMove(board, List.of(W));

        var moves = legalMoves(board);

        System.out.println(boardToString(board));
        assertThat(moves).hasSize(7718);
    }

    @Test
    void big_snail_football_field() {
        var board = init();
        executeMove(board, List.of(N));
        executeMove(board, List.of(W));
        executeMove(board, List.of(S));
        executeMove(board, List.of(S));
        executeMove(board, List.of(E));
        executeMove(board, List.of(E));
        executeMove(board, List.of(N));
        executeMove(board, List.of(N));
        executeMove(board, List.of(N));
        executeMove(board, List.of(W));
        executeMove(board, List.of(W));
        executeMove(board, List.of(W));
        executeMove(board, List.of(S));
        executeMove(board, List.of(S));
        executeMove(board, List.of(S));
        // those moves casus OOM
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.S));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.E));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.E));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.E));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.E));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.N));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.N));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.N));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.N));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.N));
//        FootballBitBoard.executeMove(board, List.of(FootballBitBoard.Direction.W));

        var l = System.nanoTime();
        var moves = legalMoves(board);
        var l1 = System.nanoTime();
        var diff = (l1 - l) / 1_000_000;
        System.out.println("Time took in seconds: " + diff);

        System.out.println(boardToString(board));
        assertThat(moves).hasSize(2957071);
    }

    @Test
    void check_initial_moves() {
        var board = init();
        System.out.println(boardToString(board));
        var moves = FootballBitBoard.legalMoves(board);

        printMoves(moves);
        assertThat(moves).hasSize(8);
    }

    @Test
    void one_move_done_legalMoves() {
        var board = init();
        executeMove(board, List.of(N));

        var moves = legalMoves(board);

        printMoves(moves);
        assertThat(moves).hasSize(7);
    }

    private void printMoves(List<List<FootballBitBoard.Direction>> moves) {
        for (var move : moves) {
            System.out.println("moves");
            move.forEach(System.out::println);
        }
    }
}
