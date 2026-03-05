package io.github.lipinskipawel.board;

final class MyEvaluate {

    static int evaluate(MyFootball position) {
        if (position.isGameOver()) {
            if (position.isTopGoal()) {
                return 100;
            }
            if (position.isBottomGoal()) {
                return -100;
            }
            if (position.isFirstToMove()) {
                return 100;
            } else {
                return -100;
            }
        }

        return 50;
    }
}
