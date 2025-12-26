package io.github.lipinskipawel.board;

import java.util.List;

import static io.github.lipinskipawel.board.Evaluate.evaluate;
import static io.github.lipinskipawel.board.FootballBitBoard.Player.NORTH;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

final class MiniMax {

    public static List<FootballBitBoard.Direction> bestMove(FootballBitBoard position, int depth) {
        final var legalMoves = position.legalMoves();
        final var activePlayer = position.getActivePlayer();
        List<FootballBitBoard.Direction> bestMove = null;

        if (activePlayer == NORTH) {
            var maxEval = MIN_VALUE;
            for (var move : legalMoves) {
                final var eval = minmax(position.executeMove(move), depth - 1, false);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
            }
            return bestMove;
        } else {
            var minEval = MAX_VALUE;
            for (var move : legalMoves) {
                final var eval = minmax(position.executeMove(move), depth - 1, true);
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
            }
            return bestMove;
        }
    }

    private static int minmax(FootballBitBoard board, int depth, boolean maximizing) {
        if (depth == 0 || board.isGameOver()) {
            return evaluate(board);
        }

        final var moves = board.legalMoves();
        if (maximizing) {
            var maxEval = MIN_VALUE;
            for (var move : moves) {
                final var eval = minmax(board.executeMove(move), depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            var minEval = MAX_VALUE;
            for (var move : moves) {
                final var afterMove = board.executeMove(move);
                final var eval = minmax(afterMove, depth - 1, false);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
}
