package io.github.lipinskipawel.board.ai;

import io.github.lipinskipawel.board.engine.Board;

public interface BoardEvaluator {

    double evaluate(Board<?> board);

}
