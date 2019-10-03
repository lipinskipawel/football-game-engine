package io.lipinski.board.neuralnetwork;

import io.lipinski.board.engine.BoardInterface;
import io.lipinski.board.engine.Player;

class DummyBoardEvaluator implements BoardEvaluator {


    @Override
    public int evaluate(final BoardInterface board) {
        if (board.getPlayer() == Player.FIRST) {
            if (board.getBallPosition() == 3 ||
                    board.getBallPosition() == 4 ||
                    board.getBallPosition() == 5)
                return 300;
            else if (board.getBallPosition() == 111 ||
                    board.getBallPosition() == 112 ||
                    board.getBallPosition() == 113)
                return -300;
            return 50;
        }
        if (board.getBallPosition() == 111 ||
                board.getBallPosition() == 112 ||
                board.getBallPosition() == 113)
            return 300;
        else if (board.getBallPosition() == 3 ||
                board.getBallPosition() == 4 ||
                board.getBallPosition() == 5)
            return -300;
        return 50;
    }
}
