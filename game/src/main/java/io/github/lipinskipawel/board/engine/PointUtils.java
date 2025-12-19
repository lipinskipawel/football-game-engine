package io.github.lipinskipawel.board.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PointUtils {

    static List<Point> initialPoints() {
        List<Point> tempListPoint = new ArrayList<>();

        for (int i = 0; i < 117; i++) {
            if (isInSideGoal(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.S,
                    Direction.SW,
                    Direction.W,
                    Direction.NW,
                    Direction.N,
                    Direction.NE,
                    Direction.E,
                    Direction.SE);
                tempListPoint.add(point);
            } else if (isCorner(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.S,
                    Direction.SW,
                    Direction.W,
                    Direction.NW,
                    Direction.N,
                    Direction.NE,
                    Direction.E,
                    Direction.SE);
                tempListPoint.add(point);
            } else if (isTopEdgeOfPitch(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.W,
                    Direction.NW,
                    Direction.N,
                    Direction.NE,
                    Direction.E);
                tempListPoint.add(point);
            } else if (isTopEdgeNearGoalRight(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.N,
                    Direction.NE,
                    Direction.E);
                tempListPoint.add(point);
            } else if (isTopEdgeNearGoalLeft(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.N,
                    Direction.NW,
                    Direction.W);
                tempListPoint.add(point);
            } else if (isBottomEdgeOfPitch(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.E,
                    Direction.SE,
                    Direction.S,
                    Direction.SW,
                    Direction.W);
                tempListPoint.add(point);
            } else if (isBottomEdgeNearGoalRight(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.S,
                    Direction.SE,
                    Direction.E);
                tempListPoint.add(point);
            } else if (isBottomEdgeNearGoalLeft(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.S,
                    Direction.SW,
                    Direction.W);
                tempListPoint.add(point);
            } else if (isLeftEdgeOfPitch(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.S,
                    Direction.SW,
                    Direction.W,
                    Direction.NW,
                    Direction.N);
                tempListPoint.add(point);
            } else if (isRightEdgeOfPitch(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.S,
                    Direction.N,
                    Direction.NE,
                    Direction.E,
                    Direction.SE);
                tempListPoint.add(point);
            } else if (isOutSideOfPitch(i)) {
                Point point = new Point(i);
                point.notAvailableDirections(Direction.N,
                    Direction.NE,
                    Direction.E,
                    Direction.SE,
                    Direction.S,
                    Direction.SW,
                    Direction.W,
                    Direction.NW);
                tempListPoint.add(point);
            } else {
                tempListPoint.add(new Point(i));
            }
        }
        return Collections.unmodifiableList(tempListPoint);
    }


    private static boolean isInSideGoal(final int position) {
        return position == 3 || position == 4 || position == 5
            || position == 111 || position == 112 || position == 113;
    }

    private static boolean isCorner(final int position) {
        return position == 9 || position == 17 || position == 99 || position == 107;
    }

    private static boolean isTopEdgeOfPitch(final int position) {
        return position == 10 || position == 11
            || position == 15 || position == 16;
    }

    private static boolean isTopEdgeNearGoalRight(final int position) {
        return position == 14;
    }

    private static boolean isTopEdgeNearGoalLeft(final int position) {
        return position == 12;
    }

    private static boolean isBottomEdgeOfPitch(final int position) {
        return position == 101 || position == 100
            || position == 105 || position == 106;
    }

    private static boolean isBottomEdgeNearGoalRight(final int position) {
        return position == 104;
    }

    private static boolean isBottomEdgeNearGoalLeft(final int position) {
        return position == 102;
    }

    private static boolean isLeftEdgeOfPitch(final int position) {
        return position == 18
            || position == 27
            || position == 36
            || position == 45
            || position == 54
            || position == 63
            || position == 72
            || position == 81
            || position == 90;
    }

    private static boolean isRightEdgeOfPitch(final int position) {
        return position == 26
            || position == 35
            || position == 44
            || position == 53
            || position == 62
            || position == 71
            || position == 80
            || position == 89
            || position == 98;
    }

    private static boolean isOutSideOfPitch(final int position) {
        return position == 0
            || position == 1
            || position == 2
            || position == 6
            || position == 7
            || position == 8
            || position == 108
            || position == 109
            || position == 110
            || position == 114
            || position == 115
            || position == 116;
    }


}
