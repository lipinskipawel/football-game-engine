package io.github.lipinskipawel.board.ai.bruteforce;

import io.github.lipinskipawel.board.ai.BoardEvaluator;
import io.github.lipinskipawel.board.engine.Board;

public final class SmartBoardEvaluator implements BoardEvaluator {

    @Override
    public double evaluate(Board<?> board) {
        final var numberOfPointsOnBoard = 117;
        if (board.getPlayer().equals(board.getPlayerProvider().first())) {
            if (!board.isGoal() && board.isGameOver()) {
                return 1000;
            }
            if (board.isGoal() && board.getBallPosition() < 20) {
                return 1000;
            }
            if (board.isGoal()) {
                return -1000;
            }
            return eval(numberOfPointsOnBoard - board.getBallPosition());
        } else {
            if (!board.isGoal() && board.isGameOver()) {
                return -1000;
            }
            if (board.isGoal() && board.getBallPosition() < 20) {
                return 1000;
            }
            if (board.isGoal()) {
                return -1000;
            }
            return eval(board.getBallPosition());
        }
    }

    int eval(final int position) {
        return (position / 9) * 10;
    }
}
