package io.github.lipinskipawel.board;

import java.util.List;

import static io.github.lipinskipawel.board.Evaluate.evaluate;
import static io.github.lipinskipawel.board.FootballBitBoard.Player.NORTH;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public final class MiniMax {

    static long moves = 0L;
    static long actualEvaluatedMoves = 0L;

    public static List<FootballBitBoard.Direction> bestMove(FootballBitBoard position, int depth) {
        final var legalMoves = position.legalMoves();
//        moves += legalMoves.size();
        List<FootballBitBoard.Direction> bestMove = null;

        var alpha = MIN_VALUE;
        var beta = MAX_VALUE;

        if (position.getActivePlayer() == NORTH) {
            var maxEval = MIN_VALUE;
            for (var move : legalMoves) {
                final var eval = minmax(position.executeMove(move), depth - 1, alpha, beta, false);
//                actualEvaluatedMoves++;
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
//            System.out.println(moves);
//            System.out.println(actualEvaluatedMoves);
            return bestMove;
        } else {
            var minEval = MAX_VALUE;
            for (var move : legalMoves) {
                final var eval = minmax(position.executeMove(move), depth - 1, alpha, beta, true);
//                actualEvaluatedMoves++;
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
//            System.out.println(moves);
//            System.out.println(actualEvaluatedMoves);
            return bestMove;
        }
    }

    private static int minmax(FootballBitBoard position, int depth, int alpha, int beta, boolean maximizing) {
        if (depth == 0 || position.isGameOver()) {
            return evaluate(position);
        }

        final var legalMoves = position.legalMoves();
//        moves += legalMoves.size();
        if (maximizing) {
            var maxEval = MIN_VALUE;
            for (var move : legalMoves) {
                final var eval = minmax(position.executeMove(move), depth - 1, alpha, beta, false);
//                actualEvaluatedMoves++;
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
                final var afterMove = position.executeMove(move);
                final var eval = minmax(afterMove, depth - 1, alpha, beta, false);
//                actualEvaluatedMoves++;
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

