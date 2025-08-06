package io.github.lipinskipawel.board.ai;

import io.github.lipinskipawel.board.ai.bruteforce.DefaultMoveStrategyBuilder;
import io.github.lipinskipawel.board.engine.Board;
import io.github.lipinskipawel.board.engine.Move;

/**
 * The underlying implementations MUST guarantee thread safety.
 * This is the abstraction layer for different implementations of artificial intelligence.
 */
public interface MoveStrategy {

    /**
     * This method searches for the best move in a given {@link Board}.
     *
     * @param board to search best move on
     * @return best move
     */
    Move searchForTheBestMove(Board<?> board);

    /**
     * This method provides default {@link MoveStrategy} builder. Builder already
     * provides default parameters.
     *
     * @return default builder object
     */
    static DefaultMoveStrategyBuilder defaultMoveStrategyBuilder() {
        return DefaultMoveStrategyBuilder.aDefaultMoveStrategyBuilder();
    }
}
