package io.github.lipinskipawel.board.snail;

final class MainSnail {

    public static void main(String[] args) {
        final var board = AiSnail.snailFootballField();
        System.out.println("AI Snail test");
//        final var board = MySnail.snailFootballField();
//        System.out.println("My Snail test");

        var l = System.nanoTime();
        var moves = board.legalMoves();
        var l1 = System.nanoTime();
        var diff = (l1 - l) / 1_000_000;
        System.out.println("Time took in milliseconds: " + diff + "ms");

        System.out.println("legal moves: " + moves.size());
        if (moves.size() != 36368449) {
            System.err.printf("Got [%s] moves instead 36368449%n", moves.size());
        } else {
            System.out.println("You got correct answer 36368449");
        }
    }
}
