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
}
