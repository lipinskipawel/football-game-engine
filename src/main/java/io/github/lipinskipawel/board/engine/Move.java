package io.github.lipinskipawel.board.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final public class Move implements Serializable {
    private final List<Direction> directions;

    public Move(List<Direction> directions) {
        Direction[] arr = new Direction[directions.size()];
        for (int i = 0; i < directions.size(); i++) {
            arr[i] = directions.get(i);
        }
        this.directions = List.of(arr);
    }

    public static Move emptyMove() {
        return new Move(Collections.emptyList());
    }

    public List<Direction> getMove() {
        return new ArrayList<>(this.directions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(directions, move.directions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directions);
    }

    @Override
    public String toString() {
        return "Move{" +
            "directions=" + directions +
            '}';
    }
}
