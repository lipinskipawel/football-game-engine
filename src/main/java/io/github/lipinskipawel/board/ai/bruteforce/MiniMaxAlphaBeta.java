package io.github.lipinskipawel.board.ai.bruteforce;

import io.github.lipinskipawel.board.ai.BoardEvaluator;
import io.github.lipinskipawel.board.ai.MoveStrategy;
import io.github.lipinskipawel.board.engine.Board;
import io.github.lipinskipawel.board.engine.LegalMovesFuture;
import io.github.lipinskipawel.board.engine.Move;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.max;
import static java.lang.Math.min;

final class MiniMaxAlphaBeta implements MoveStrategy {

    private final BoardEvaluator evaluator;
    private final int depth;
    private final int timeout;
    private final AtomicReference<Move> bestMove;
    private volatile boolean cancel;

    MiniMaxAlphaBeta(final BoardEvaluator defaultEvaluator,
                     final int depth,
                     final int timeout) {
        this.evaluator = defaultEvaluator;
        this.depth = depth;
        this.timeout = timeout;
        this.bestMove = new AtomicReference<>(Move.emptyMove());
        this.cancel = false;
    }

    private MiniMaxAlphaBeta(final MiniMaxAlphaBeta miniMaxAlphaBeta) {
        this.evaluator = miniMaxAlphaBeta.evaluator;
        this.depth = miniMaxAlphaBeta.depth;
        this.timeout = miniMaxAlphaBeta.timeout;
        this.bestMove = new AtomicReference<>(Move.emptyMove());
        this.cancel = false;
    }

    Move searchForTheBestMove(Board<?> board, ExecutorService pool) {
        final var copy = new MiniMaxAlphaBeta(this);
        final var searchingForMove = pool.submit(
            () -> copy.execute(board, copy.depth)
        );
        try {
            searchingForMove.get(timeout, TimeUnit.SECONDS);
            return copy.bestMove.get();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            copy.cancel = true;
            return copy.bestMove.get();
        }
    }

    @Override
    public Move searchForTheBestMove(Board<?> board) {
        final var pool = Executors.newSingleThreadExecutor();
        try {
            return searchForTheBestMove(board, pool);
        } finally {
            pool.shutdown();
        }
    }

    void execute(final Board<?> board, final int depth) {
        final var actualDepth = this.depth == 1 ? depth : this.depth;
        final var holder = new Holder();

        final var computation = board.allLegalMovesFuture();
        computation.start(Duration.ofSeconds(this.timeout));

        while (computation.isRunning()) {
            processFoundMoves(computation, board, actualDepth, holder);
        }
        processFoundMoves(computation, board, actualDepth, holder);
    }

    private void processFoundMoves(final LegalMovesFuture legalMovesFuture,
                                   final Board<?> board,
                                   int actualDepth,
                                   final Holder holder) {
        for (final Move move : legalMovesFuture.partialResult()) {
            setFirstMoveAsBestOnlyIfGlobalBestMoveIsEmpty(move);
            if (this.cancel) {
                legalMovesFuture.cancel();
                break;
            }

            final var afterMove = board.executeMove(move);

            holder.current = minimax(
                afterMove,
                actualDepth - 1,
                0.0,
                0.0,
                board.getPlayer().equals(board.getPlayerProvider().first())
            );

            if (isFirstPlayer(board) && holder.isCurrentGE(holder.highest)) {
                holder.highest = holder.current;
                this.bestMove.set(move);
            } else if (isSecondPlayer(board) && holder.isCurrentLE(holder.lowest)) {
                holder.lowest = holder.current;
                this.bestMove.set(move);
            }
        }
    }

    private boolean isFirstPlayer(Board<?> board) {
        return board.getPlayer().equals(board.getPlayerProvider().first());
    }

    private boolean isSecondPlayer(Board<?> board) {
        return board.getPlayer().equals(board.getPlayerProvider().second());
    }

    private void setFirstMoveAsBestOnlyIfGlobalBestMoveIsEmpty(final Move move) {
        if (this.bestMove.get().equals(Move.emptyMove())) {
            this.bestMove.set(move);
        }
    }

    private double minimax(final Board<?> board,
                           final int depth,
                           double alpha,
                           double beta,
                           final boolean maximizingPlayer) {
        if (this.cancel || depth <= 0 || board.isGameOver())
            return evaluator.evaluate(board);

        if (!maximizingPlayer) {
            var maxEval = -Double.MAX_VALUE;
            final var allMoves = board.allLegalMoves();
            for (final var move : allMoves) {
                final var eval = minimax(
                    board.executeMove(move), depth - 1, alpha, beta, true);
                maxEval = max(maxEval, eval);
                beta = min(beta, eval);
                if (beta >= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            var minEval = Double.MAX_VALUE;
            final var allMoves = board.allLegalMoves();
            for (final var move : allMoves) {
                final var eval = minimax(
                    board.executeMove(move), depth - 1, alpha, beta, false);
                minEval = min(minEval, eval);
                alpha = max(alpha, eval);
                if (beta >= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }


    private static class Holder {
        double highest = -Double.MAX_VALUE;
        double lowest = Double.MAX_VALUE;
        double current;

        boolean isCurrentGE(double comparisonValue) {
            return current >= comparisonValue;
        }

        boolean isCurrentLE(double comparisonValue) {
            return current <= comparisonValue;
        }
    }
}
