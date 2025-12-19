package io.github.lipinskipawel.board.engine;

import java.io.Serializable;

/**
 * Binary compatibility of this enum will change in the future.
 * This enum is unstable only in terms of adding and removing interfaces.
 */
public enum Direction implements Serializable {

    S {
        @Override
        public Direction opposite() {
            return N;
        }

        @Override
        public int changeToInt() {
            return 9;
        }
    },
    SW {
        @Override
        public Direction opposite() {
            return NE;
        }

        @Override
        public int changeToInt() {
            return 8;
        }
    },
    W {
        @Override
        public Direction opposite() {
            return E;
        }

        @Override
        public int changeToInt() {
            return -1;
        }
    },
    NW {
        @Override
        public Direction opposite() {
            return SE;
        }

        @Override
        public int changeToInt() {
            return -10;
        }
    },
    N {
        @Override
        public Direction opposite() {
            return S;
        }

        @Override
        public int changeToInt() {
            return -9;
        }
    },
    NE {
        @Override
        public Direction opposite() {
            return SW;
        }

        @Override
        public int changeToInt() {
            return -8;
        }
    },
    E {
        @Override
        public Direction opposite() {
            return W;
        }

        @Override
        public int changeToInt() {
            return 1;
        }
    },
    SE {
        @Override
        public Direction opposite() {
            return NW;
        }

        @Override
        public int changeToInt() {
            return 10;
        }
    };

    @Override
    public String toString() {
        return this.name();
    }

    public abstract Direction opposite();

    public abstract int changeToInt();
}
