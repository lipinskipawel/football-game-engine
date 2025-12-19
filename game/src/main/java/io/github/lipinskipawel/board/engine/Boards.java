package io.github.lipinskipawel.board.engine;

import io.github.lipinskipawel.board.internal.NoOpLogger;
import io.github.lipinskipawel.board.spi.Logger;

/**
 * This class is a facade for the {@link Board} API. It is expected to create {@link Board} implementations using this
 * facade.
 */
public class Boards {

    /**
     * This method will create an immutable {@link Board} object without logging capabilities.
     *
     * @return board object
     */
    public static Board<Player> immutableBoard() {
        return immutableBoard(new NoOpLogger());
    }

    /**
     * This method will create an immutable {@link Board} object with logging capabilities.
     *
     * @param logger logger to use
     * @return board object
     */
    public static Board<Player> immutableBoard(Logger logger) {
        return new ImmutableBoard<>(new PlayerProvider<>(Player.FIRST, Player.SECOND), logger);
    }

    /**
     * This method will create an immutable {@link Board} object parametrized by player object.
     *
     * <p>This method allows passing custom players to {@link Board} which will be then used in methods like
     * {@link Board#getPlayer()}, {@link Board#takeTheWinner()} and others.
     *
     * @param first  player
     * @param second player
     * @param <T>    type of the players
     * @return board object
     */
    public static <T> Board<T> immutableBoardWithCustomPlayer(final T first, final T second) {
        return immutableBoardWithCustomPlayer(first, second, new NoOpLogger());
    }


    /**
     * This method will create an immutable {@link Board} object parametrized by player object.
     *
     * <p>This method allows passing custom players to {@link Board} which will be then used in methods like
     * {@link Board#getPlayer()}, {@link Board#takeTheWinner()} and others.
     *
     * @param first  player
     * @param second player
     * @param logger logger to use
     * @param <T>    type of the players
     * @return board object
     */
    public static <T> Board<T> immutableBoardWithCustomPlayer(final T first, final T second, final Logger logger) {
        return new ImmutableBoard<>(new PlayerProvider<>(first, second), logger);
    }
}
