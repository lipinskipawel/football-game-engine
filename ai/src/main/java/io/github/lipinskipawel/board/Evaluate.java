package io.github.lipinskipawel.board;

import static io.github.lipinskipawel.board.FootballBitBoard.Player.NORTH;

final class Evaluate {

    static int evaluate(FootballBitBoard position) {
        final var player = position.getActivePlayer();
        if (!position.outgoingExists()) {
            if (position.isTopGoal()) {
                return 100;
            }
            if (position.isBottomGoal()) {
                return -100;
            }
            if (player == NORTH) {
                return 100;
            } else {
                return -100;
            }
        }

        return 50;
    }
}
