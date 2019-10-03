package io.lipinski.board.neuralnetwork;

import io.lipinski.board.engine.BoardInterface;
import io.lipinski.board.engine.Move;
import io.lipinski.board.engine.Player;

class MiniMax implements MoveStrategy {


    private final BoardEvaluator evaluator;


    MiniMax(final BoardEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Move execute(final BoardInterface board,
                        final int depth) {

        Move bestMove = null;

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        for (final Move move : board.allLegalMoves()) {

            BoardInterface afterMove = board.executeMove(move);

            currentValue = board.getPlayer() == Player.FIRST ? // here is CURRENT board
                    min(afterMove, depth - 1) : // here is AFTER board
                    max(afterMove, depth - 1); // here is AFTER board


            if (board.getPlayer() == Player.FIRST &&
                    currentValue >= highestSeenValue) {

                highestSeenValue = currentValue;
                bestMove = move;
            } else if (board.getPlayer() == Player.SECOND &&
                    currentValue <= lowestSeenValue) {

                lowestSeenValue = currentValue;
                bestMove = move;
            }
        }
        return bestMove;
    }


    private int min(final BoardInterface board,
                    final int depth) {

        if (depth <= 0)
            return evaluator.evaluate(board);

        int lowestSeenValue = Integer.MAX_VALUE;

        for (Move move : board.allLegalMoves()) {
            BoardInterface afterMove = board.executeMove(move);
            int currentValue = max(afterMove, depth - 1);
            if (currentValue <= lowestSeenValue)
                lowestSeenValue = currentValue;
        }
        return lowestSeenValue;
    }


    private int max(final BoardInterface board,
                    final int depth) {

        if (depth <= 0)
            return evaluator.evaluate(board);

        int highestSeenValue = Integer.MIN_VALUE;

        for (Move move : board.allLegalMoves()) {
            BoardInterface afterMove = board.executeMove(move);
            int currentValue = min(afterMove, depth - 1);
            if (currentValue >= highestSeenValue)
                highestSeenValue = currentValue;
        }
        return highestSeenValue;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }
}
