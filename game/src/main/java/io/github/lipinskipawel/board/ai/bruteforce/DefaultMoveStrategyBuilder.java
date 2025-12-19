package io.github.lipinskipawel.board.ai.bruteforce;

import io.github.lipinskipawel.board.ai.BoardEvaluator;
import io.github.lipinskipawel.board.ai.MoveStrategy;

public final class DefaultMoveStrategyBuilder {
    private BoardEvaluator defaultEvaluator;
    private int timeout;
    private int depth;

    private DefaultMoveStrategyBuilder() {
        this.defaultEvaluator = new SmartBoardEvaluator();
        this.timeout = 5;
        this.depth = 3;
    }

    public static DefaultMoveStrategyBuilder aDefaultMoveStrategyBuilder() {
        return new DefaultMoveStrategyBuilder();
    }

    public DefaultMoveStrategyBuilder withBoardEvaluator(final BoardEvaluator evaluator) {
        this.defaultEvaluator = evaluator;
        return this;
    }

    public DefaultMoveStrategyBuilder withDepth(final int depth) {
        this.depth = depth;
        return this;
    }

    public DefaultMoveStrategyBuilder withTimeoutInSeconds(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    public MoveStrategy build() {
        return new MiniMaxAlphaBeta(defaultEvaluator, depth, timeout);
    }
}
