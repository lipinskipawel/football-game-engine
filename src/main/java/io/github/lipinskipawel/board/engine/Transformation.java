package io.github.lipinskipawel.board.engine;

/**
 * The intention of this interface is transform board into different type.
 */
interface Transformation {


    /**
     * Transform possible moves in certain point into array of int.
     * 1 means you can move, 0 means you can not move in particular direction.
     * It holds different contract when it's implemented by different class.
     * Thus contract must be explicitly by implementation class.
     * General rule is described in return statement below.
     *
     * @return Array of int which are always in the same order
     * General rule is keep order always the same.
     * For example when transforming
     * board parse each `point always in the same order (Comparator)
     * `point` pares `Direction` always in the same order (Comparator)
     */
    int[] transform();

    double[] nonBinaryTransformation();
}
