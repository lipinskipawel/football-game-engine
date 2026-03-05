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
import static io.github.lipinskipawel.board.MyMiniMax.bestMove;

final class MyMiniMaxTest implements WithAssertions {

    @Test
    void find_trap() {
        // given
        var position = new MyFootball();
        position.initBoard();
        position.switchPlayer();
        position.executePlayerMove(List.of(N));
        position.executePlayerMove(List.of(NW));
        position.executePlayerMove(List.of(NE));
        position.executePlayerMove(List.of(W));
        position.executePlayerMove(List.of(N));
        position.executePlayerMove(List.of(SW));
        position.executePlayerMove(List.of(SE, N, NE));
        position.executePlayerMove(List.of(W, SE, N, SE));
        position.executePlayerMove(List.of(N));
        position.executePlayerMove(List.of(SW, E, SE));
        position.executePlayerMove(List.of(N));
        position.executePlayerMove(List.of(NW, NE, SE));
        position.executePlayerMove(List.of(N, SW));
        position.executePlayerMove(List.of(SW, E, SW));
        position.executePlayerMove(List.of(SW, N));
        position.executePlayerMove(List.of(N, SE, N, SW, NW, SW));
        position.executePlayerMove(List.of(NW));
        position.executePlayerMove(List.of(E, S, W));
        position.executePlayerMove(List.of(N, NW, NE, SE));
        position.executePlayerMove(List.of(S, SW, NW, E, SW, E, S));
        position.executePlayerMove(List.of(NE, E, E, E, E, NE));
        System.out.println(position.isFirstToMove());

        // when
        System.out.println(position.ballPosition);
        var bestMove = bestMove(position, 2);

        // then
        position.executePlayerMoveInt(bestMove);
        assertThat(position.ballPosition).isEqualTo(19);
    }


    @Test
    void dsfs() {
        /// 0001_0110
        /// 0100_0001
        var a = 22;
        var b = 65;
        var c = 66;
        var d = 67;
        var e = 117;
        System.out.println(a + " move6: " + move6(a));
        System.out.println(b + " move6: " + move6(b));
        System.out.println(c + " move6: " + move6(c));
        System.out.println(d + " move6: " + move6(d));
        System.out.println(e + " move6: " + move6(e));
    }

    private int move6(int num) {
        return num >>> 6;
    }

    @Test
    void find_trap_in_the_middle_when_white_to_move() {
        // given
        var position = new MyFootball();
        position.initBoard();
        position.executePlayerMove(List.of(NE));
        position.executePlayerMove(List.of(SE));
        position.executePlayerMove(List.of(NE));
        position.executePlayerMove(List.of(E, NW));
        position.executePlayerMove(List.of(E, SW, N, SW));
        position.executePlayerMove(List.of(E, NW));
        position.executePlayerMove(List.of(S, NW));
        position.executePlayerMove(List.of(E, SW, S));
        position.executePlayerMove(List.of(NE, S, W, SE));
        position.executePlayerMove(List.of(N, SW));
        position.executePlayerMove(List.of(E, SW));
        position.executePlayerMove(List.of(N, SE));
        position.executePlayerMove(List.of(N, SE));
        position.executePlayerMove(List.of(N));
        position.executePlayerMove(List.of(SW, E, NE, W, SE, W, SW));
        position.executePlayerMove(List.of(NW, NW));
        position.executePlayerMove(List.of(NE, S, NW, E, NW));

        // when
        var bestMove = bestMove(position, 2);
        position.executePlayerMoveInt(bestMove);

        // then
        assertThat(position.ballPosition).isEqualTo(61);
    }

    @Test
    void find_trap_in_the_middle_when_black_to_move() {
        // given
        var position = new MyFootball();
        position.initBoard();
        position.executePlayerMove(List.of(NE));
        position.executePlayerMove(List.of(SE));
        position.executePlayerMove(List.of(NE));
        position.executePlayerMove(List.of(E, NW));
        position.executePlayerMove(List.of(E, SW, N, SW));
        position.executePlayerMove(List.of(E, NW));
        position.executePlayerMove(List.of(S, NW));
        position.executePlayerMove(List.of(E, SW, S));
        position.executePlayerMove(List.of(NE, S, W, SE));
        position.executePlayerMove(List.of(N, SW));
        position.executePlayerMove(List.of(E, SW));
        position.executePlayerMove(List.of(N, SE));
        position.executePlayerMove(List.of(N, SE));
        position.executePlayerMove(List.of(N));
        position.executePlayerMove(List.of(SW, E, NE, W, SE, W, SW));
        position.executePlayerMove(List.of(NW, NW));
        position.executePlayerMove(List.of(NE, S, NW, E, NW));
        position.executePlayerMove(List.of(N));

        // when
        var bestMove = bestMove(position, 3);
        position.executePlayerMoveInt(bestMove);

        // then
        assertThat(position.ballPosition).isEqualTo(61);
    }
}
