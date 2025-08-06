package io.github.lipinskipawel.board.spi;

/**
 * This is a Logger interface dedicated to logging capabilities.
 * Default implementation does no-op which means nothing inside this library
 * is logged.
 */
public interface Logger {

    /**
     * Log a message at the TRACE level.
     *
     * @param message the message string to be logged
     */
    default void trace(String message) {
    }

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message.
     *
     * @param message the message accompanying the exception
     * @param t       the exception (throwable) to log
     */
    default void trace(String message, Throwable t) {
    }

    /**
     * Log a message at the DEBUG level.
     *
     * @param message the message string to be logged
     */
    default void debug(String message) {
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param message the message accompanying the exception
     * @param t       the exception (throwable) to log
     */
    default void debug(String message, Throwable t) {
    }

    /**
     * Log a message at the INFO level.
     *
     * @param message the message string to be logged
     */
    default void info(String message) {
    }

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param message the message accompanying the exception
     * @param t       the exception (throwable) to log
     */
    default void info(String message, Throwable t) {
    }
}
