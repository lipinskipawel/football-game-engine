package io.github.lipinskipawel.board.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * This class represents point on the board.
 */
final public class Point {
    private final int position;
    private final Map<Direction, Boolean> availableDirections;

    Point(int position) {
        this.position = position;
        this.availableDirections = initAvailableDirections();
    }

    Point(Point point) {
        this.position = point.position;
        this.availableDirections = point.availableDirections.entrySet()
            .stream()
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Point(int position, Map<Direction, Boolean> availableDirections) {
        this.position = position;
        this.availableDirections = availableDirections.entrySet()
            .stream()
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * @param destinationPoint this is number of different point
     * @return Direction to reach this #destinationPoint
     */
    public Direction kickBallTo(final int destinationPoint) {
        final var findThatNumber = destinationPoint - position;
        return availableDirections.entrySet()
            .stream()
            .filter(entry -> entry.getValue().booleanValue() == Boolean.TRUE)
            .map(Map.Entry::getKey)
            .filter(direction -> direction.changeToInt() == findThatNumber)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Can't make a move in this direction"));
    }

    boolean isOnTop() {
        return this.position == 3 || this.position == 4 || this.position == 5;
    }

    boolean isOnBottom() {
        return this.position == 111 || this.position == 112 || this.position == 113;
    }

    int getPosition() {
        return this.position;
    }

    boolean isAvailable(final Direction destination) {
        return this.availableDirections.get(destination);
    }

    void setAvailableDirections(final Direction directions) {
        this.availableDirections.put(directions, Boolean.TRUE);
    }

    List<Direction> getAllowedDirection() {
        return this.availableDirections
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().booleanValue() == Boolean.TRUE)
            .map(Map.Entry::getKey)
            .collect(toUnmodifiableList());
    }

    List<Direction> getUnavailableDirection() {
        return this.availableDirections
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().booleanValue() == Boolean.FALSE)
            .map(Map.Entry::getKey)
            .collect(toUnmodifiableList());
    }

    List<Boolean> getAllDirections() {
        return List.of(
            this.availableDirections.get(Direction.N),
            this.availableDirections.get(Direction.NE),
            this.availableDirections.get(Direction.E),
            this.availableDirections.get(Direction.SE),
            this.availableDirections.get(Direction.S),
            this.availableDirections.get(Direction.SW),
            this.availableDirections.get(Direction.W),
            this.availableDirections.get(Direction.NW)
        );
    }

    /**
     * @param direction
     * @return
     */
    Point notAvailableDirection(Direction direction) {
        int position = this.position;
        Map<Direction, Boolean> collect = this.availableDirections.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        collect.put(direction, Boolean.FALSE);
        return new Point(position, collect);
    }

    void notAvailableDirections(Direction... directions) {
        for (Direction direction : directions) {
            this.availableDirections.put(direction, false);
        }
    }

    private Map<Direction, Boolean> initAvailableDirections() {
        final Map<Direction, Boolean> res = new HashMap<>();
        res.put(Direction.N, Boolean.TRUE);
        res.put(Direction.NE, Boolean.TRUE);
        res.put(Direction.E, Boolean.TRUE);
        res.put(Direction.SE, Boolean.TRUE);
        res.put(Direction.S, Boolean.TRUE);
        res.put(Direction.SW, Boolean.TRUE);
        res.put(Direction.W, Boolean.TRUE);
        res.put(Direction.NW, Boolean.TRUE);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return position == point.position && Objects.equals(availableDirections, point.availableDirections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, availableDirections);
    }

    @Override
    public String toString() {
        return "Point{" +
            "position=" + position +
            ", availableDirections=" + availableDirections +
            '}';
    }
}
