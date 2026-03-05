package io.github.lipinskipawel.board;

import java.util.List;

import static io.github.lipinskipawel.board.MyEvaluate.evaluate;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public final class MyMiniMax {

    public static List<Integer> bestMove(MyFootball position, int depth) {
        final var legalMoves = position.legalMoves();
        List<Integer> bestMove = null;

        var alpha = MIN_VALUE;
        var beta = MAX_VALUE;

        if (position.isFirstToMove()) {
            var maxEval = MIN_VALUE;
            for (var move : legalMoves) {
                position.executePlayerMoveInt(move);
                final var eval = minmax(position, depth - 1, alpha, beta, false);
                position.undoPlayerMoveInt(move);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestMove;
        } else {
            var minEval = MAX_VALUE;
            for (var move : legalMoves) {
                position.executePlayerMoveInt(move);
                final var eval = minmax(position, depth - 1, alpha, beta, true);
                position.undoPlayerMoveInt(move);
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return bestMove;
        }
    }

    private static int minmax(MyFootball position, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0 || position.isGameOver()) {
            return evaluate(position);
        }

        final var legalMoves = position.legalMoves();
        if (maximizing) {
            var maxEval = MIN_VALUE;
            for (var move : legalMoves) {
                position.executePlayerMoveInt(move);
                final var eval = minmax(position, depth - 1, alpha, beta, false);
                position.undoPlayerMoveInt(move);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            var minEval = MAX_VALUE;
            for (var move : legalMoves) {
                position.executePlayerMoveInt(move);
                final var eval = minmax(position, depth - 1, alpha, beta, true);
                position.undoPlayerMoveInt(move);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}
