package io.github.lipinskipawel.board.engine;

public enum Player {


    FIRST {
        @Override
        public Player opposite() {
            return SECOND;
        }

    },
    SECOND {
        @Override
        public Player opposite() {
            return FIRST;
        }
    };


    public abstract Player opposite();
}
