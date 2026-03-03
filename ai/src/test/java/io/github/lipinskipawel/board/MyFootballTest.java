package io.github.lipinskipawel.board;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.lipinskipawel.board.MyFootball.Direction.E;
import static io.github.lipinskipawel.board.MyFootball.Direction.N;
import static io.github.lipinskipawel.board.MyFootball.Direction.NE;
import static io.github.lipinskipawel.board.MyFootball.Direction.NW;
import static io.github.lipinskipawel.board.MyFootball.Direction.S;
import static io.github.lipinskipawel.board.MyFootball.Direction.SE;
import static io.github.lipinskipawel.board.MyFootball.Direction.SW;
import static io.github.lipinskipawel.board.MyFootball.Direction.W;

class MyFootballTest implements WithAssertions {

    @Test
    void move_and_undo_the_same() {
        // given
        final var myFootball = new MyFootball();
        myFootball.executePlayerMove(List.of(N));

        // when
        myFootball.undoPlayerMove(List.of(N));

        // then
        assertThat(myFootball).isEqualTo(new MyFootball());
    }

    @Test
    void legal_moves_does_not_change_state() {
        // given
        final var myFootball = new MyFootball();

        // when
        myFootball.legalMoves();
        myFootball.legalMoves();

        // then
        assertThat(myFootball).isEqualTo(new MyFootball());
    }

    @Test
    void legal_moves_at_start() {
        // given
        final var myFootball = new MyFootball();

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(N.index),
            List.of(NE.index),
            List.of(E.index),
            List.of(SE.index),
            List.of(S.index),
            List.of(SW.index),
            List.of(W.index),
            List.of(NW.index)
        ));
        assertThat(myFootball).isEqualTo(new MyFootball());
    }

    @Test
    void legal_moves_after_N() {
        // given
        final var myFootball = new MyFootball();
        myFootball.executeMove(N);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(N.index),
            List.of(NE.index),
            List.of(E.index),
            List.of(SE.index),
            List.of(SW.index),
            List.of(W.index),
            List.of(NW.index)
        ));
    }

    @Test
    void legal_moves_after_E() {
        // given
        final var myFootball = new MyFootball();
        myFootball.executeMove(E);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(N.index),
            List.of(NE.index),
            List.of(E.index),
            List.of(SE.index),
            List.of(S.index),
            List.of(SW.index),
            List.of(NW.index)
        ));
    }

    @Test
    void both_board_are_symmetric() {
        // given
        final var first = new MyFootball();
        final var second = new MyFootball();

        // when
        first.executeMove(NE);
        first.executeMove(NW);
        first.executeMove(SW);
        first.executeMove(SE);

        second.executeMove(NW);
        second.executeMove(NE);
        second.executeMove(SE);
        second.executeMove(SW);

        // then
        assertThat(first).isEqualTo(second);
    }

    @Test
    void legal_moves_after_N_and_E() {
        // given
        final var myFootball = new MyFootball();
        myFootball.executeMove(N);
        myFootball.executeMove(E);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(NE.index),
            List.of(E.index),
            List.of(N.index),
            List.of(NW.index),
            List.of(S.index),
            List.of(SE.index),
            List.of(SW.index, E.index),
            List.of(SW.index, SW.index),
            List.of(SW.index, NW.index),
            List.of(SW.index, W.index),
            List.of(SW.index, S.index),
            List.of(SW.index, SE.index)
        ));
    }

    @Test
    void draw_board_correctly_and_computes_legal_move_when_close_to_W_wall() {
        // given
        final var myFootball = new MyFootball();

        // and init board
        myFootball.initBoard();

        // move to left edge
        List.of(NW, NW, W).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(N.index),
            List.of(NE.index),
            List.of(SE.index),
            List.of(S.index),
            List.of(SW.index, E.index),
            List.of(SW.index, SE.index),
            List.of(W.index, NE.index),
            List.of(W.index, SE.index),
            List.of(NW.index, NE.index),
            List.of(NW.index, E.index)
        ));
    }

    @Test
    void draw_board_correctly_and_computes_legal_move_when_on_W_wall() {
        // given
        final var myFootball = new MyFootball();

        // and init board
        myFootball.initBoard();

        // move to left edge
        List.of(W, W, W, W).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(NE.index),
            List.of(SE.index)
        ));
    }

    @Test
    void draw_board_correctly_and_computes_legal_move_when_close_to_E_wall() {
        // given
        final var myFootball = new MyFootball();

        // and init board
        myFootball.initBoard();

        // move to left edge
        List.of(SE, SE, E).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).containsExactlyInAnyOrderElementsOf(List.of(
            List.of(NW.index),
            List.of(N.index),
            List.of(S.index),
            List.of(SW.index),
            List.of(NE.index, NW.index),
            List.of(NE.index, W.index),
            List.of(E.index, NW.index),
            List.of(E.index, SW.index),
            List.of(SE.index, W.index),
            List.of(SE.index, SW.index)
        ));
    }

    @Test
    void corner_NW_have_0_moves() {
        // given
        final var myFootball = new MyFootball();
        myFootball.initBoard();

        // and
        List.of(NW, NW, NW, N, NW).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).hasSize(0);
    }

    @Test
    void corner_NE_have_0_moves() {
        // given
        final var myFootball = new MyFootball();
        myFootball.initBoard();

        // and
        List.of(NE, NE, NE, N, NE).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).hasSize(0);
    }

    @Test
    void corner_SE_have_0_moves() {
        // given
        final var myFootball = new MyFootball();
        myFootball.initBoard();

        // and
        List.of(SE, SE, SE, S, SE).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).hasSize(0);
    }

    @Test
    void corner_SW_have_0_moves() {
        // given
        final var myFootball = new MyFootball();
        myFootball.initBoard();

        // and
        List.of(SW, SW, SW, S, SW).forEach(myFootball::executeMove);

        // when
        final var legalMoves = myFootball.legalMoves();

        // then
        assertThat(legalMoves).hasSize(0);
    }

    @Test
    void make_N_goal() {
        // given
        final var left = new MyFootball();
        final var center = new MyFootball();
        final var right = new MyFootball();
        left.initBoard();
        center.initBoard();
        right.initBoard();

        // and
        List.of(N, N, N, N, N).forEach(left::executeMove);
        left.executeMove(NW);
        List.of(N, N, N, N, N).forEach(center::executeMove);
        center.executeMove(N);
        List.of(N, N, N, N, N).forEach(right::executeMove);
        right.executeMove(NE);

        // when
        final var leftMoves = left.legalMoves();
        final var centerMoves = center.legalMoves();
        final var rightMoves = right.legalMoves();

        // then
        assertThat(leftMoves).hasSize(0);
        assertThat(centerMoves).hasSize(0);
        assertThat(rightMoves).hasSize(0);
    }

    @Test
    void make_S_goal() {
        // given
        final var left = new MyFootball();
        final var center = new MyFootball();
        final var right = new MyFootball();
        left.initBoard();
        center.initBoard();
        right.initBoard();

        // and
        List.of(S, S, S, S, S).forEach(left::executeMove);
        left.executeMove(SW);
        List.of(S, S, S, S, S).forEach(center::executeMove);
        center.executeMove(S);
        List.of(S, S, S, S, S).forEach(right::executeMove);
        right.executeMove(SE);

        // when
        final var leftMoves = left.legalMoves();
        final var centerMoves = center.legalMoves();
        final var rightMoves = right.legalMoves();

        // then
        assertThat(leftMoves).hasSize(0);
        assertThat(centerMoves).hasSize(0);
        assertThat(rightMoves).hasSize(0);
    }
}
