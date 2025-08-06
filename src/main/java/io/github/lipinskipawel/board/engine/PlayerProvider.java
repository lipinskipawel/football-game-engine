package io.github.lipinskipawel.board.engine;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is an API which handles custom players provided into the {@link Board}.
 *
 * @param <T> type of the player
 */
public final class PlayerProvider<T> {
    private final T first;
    private final T second;
    private final AtomicBoolean current;

    public PlayerProvider(T first, T second) {
        this.first = first;
        this.second = second;
        this.current = new AtomicBoolean(true);
    }

    private PlayerProvider(T first, T second, final boolean current) {
        this.first = first;
        this.second = second;
        this.current = new AtomicBoolean(current);
    }

    /**
     * This method will return current player that is eligible to make a move.
     *
     * <p>By default this method will return {@link #first()} object. If the {@link #swap()} method was called then
     * the {@link #second()} will be returned.
     *
     * @return player to which can make a move
     */
    public T current() {
        return this.current.get() ? first() : second();
    }

    /**
     * This method will return first player.
     *
     * @return first player
     */
    public T first() {
        return first;
    }

    /**
     * This method will return second player.
     *
     * @return second player
     */
    public T second() {
        return second;
    }

    /**
     * This method affects only the result of {@link PlayerProvider#current()}. The swap method will swap the players
     * between each other. After invocation of this method the {@link #current()} will return second player. Calling
     * swap again will cause {@link #current()} to return first again.
     *
     * <p>This method is invoked by the {@link Board} only after a successful move by the player.
     *
     * <p>Invoking this method by the client most probably will have a negative effect on the correctness of player
     * moves. Thus, it is advised to <b>NOT</b> change the visibility of this method by the reflection access or any
     * other try.
     *
     * @return this instance after performing swap operation
     */
    PlayerProvider<T> swap() {
        this.current.set(!this.current.get());
        return this;
    }

    /**
     * It will create and return the copy of this object.
     *
     * @return copy of this instance
     */
    PlayerProvider<T> copy() {
        return new PlayerProvider<>(this.first, this.second, this.current.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerProvider<?> that = (PlayerProvider<?>) o;
        return Objects.equals(first, that.first) &&
            Objects.equals(second, that.second) &&
            Objects.equals(current.get(), that.current.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, current.get());
    }

    @Override
    public String toString() {
        return "PlayerProvider{" +
            "first=" + first +
            ", second=" + second +
            ", current=" + current +
            '}';
    }
}
