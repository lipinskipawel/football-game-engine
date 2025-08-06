package io.github.lipinskipawel.board.engine;

import io.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;
import io.github.lipinskipawel.board.internal.NoOpLogger;
import io.github.lipinskipawel.board.spi.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class ImmutableBoard<T> implements Board<T> {
    private final Logger logger;
    private final LogicalPoints points;
    private final PlayerProvider<T> playerProvider;
    private final MoveHistory moveLog;

    ImmutableBoard(PlayerProvider<T> provider) {
        this(provider, new NoOpLogger());
    }

    ImmutableBoard(final PlayerProvider<T> provider, Logger logger) {
        this.points = new LogicalPoints();
        this.playerProvider = provider.copy();
        this.moveLog = new MoveHistory();
        this.logger = logger;
    }

    private ImmutableBoard(final LogicalPoints points,
                           final PlayerProvider<T> provider,
                           final MoveHistory moveHistory,
                           final Logger logger) {
        this.points = points;
        this.playerProvider = provider;
        this.moveLog = moveHistory;
        this.logger = logger;
    }

    @Override
    public List<Direction> allMoves() {
        return this.moveLog.allDirections();
    }

    @Override
    public List<Move> moveHistory() {
        return this.moveLog.allMoves();
    }

    @Override
    public boolean isMoveAllowed(final Direction destination) {
        return this.points.isAvailable(destination);
    }

    @Override
    public ImmutableBoard<T> executeMove(final Direction destination) {
        if (isMoveAllowed(destination)) {
            logger.trace("executeMove: " + destination);
            final var logicalPoints = this.points.makeAMove(destination);
            final var player = computePlayerToMove(logicalPoints);
            final var moveLogg = this.playerProvider.current().equals(player) ? this.moveLog.add(destination) : this.moveLog.addMove(new Move(List.of(destination)));
            final var providedPlayer = computePlayerProvider(player);

            logger.debug("Move has been made: " + destination);
            return new ImmutableBoard<>(logicalPoints, providedPlayer, moveLogg, logger);
        } else {
            logger.debug("Move has NOT been made: " + destination);
            return this;
        }
    }

    private PlayerProvider<T> computePlayerProvider(final T player) {
        if (!this.playerProvider.current().equals(player)) {
            return this.playerProvider.copy().swap();
        }
        return this.playerProvider.copy();
    }

    @Override
    public Board<T> executeMove(final Move move) {
        var afterMove = new ImmutableBoard<>(this.points, this.playerProvider, this.moveLog, this.logger);
        for (var dir : move.getMove()) {
            afterMove = afterMove.executeMove(dir);
        }
        return afterMove;
    }

    @Override
    public int getBallPosition() {
        return this.points.getBallPosition();
    }

    @Override
    public ImmutableBoard<T> undo() {
        logger.trace("undo executes");
        final var lastDirection = this.moveLog
            .getLastDirection()
            .orElseThrow(() -> new RuntimeException("There is no move to undo"));
        final var logicalPoints = this.points.undoMove(lastDirection);
        final var moveLogg = this.moveLog.forceUndo();
        final var isFirst = moveLogg.currentPlayer();
        final var providedPlayer = computePlayer(isFirst);

        final var newImmutableBoard = new ImmutableBoard<T>(logicalPoints, providedPlayer, moveLogg, logger);
        if (this.equals(newImmutableBoard)) {
            logger.debug("undo returned THIS reference.");
        }
        return newImmutableBoard;
    }

    private PlayerProvider<T> computePlayer(final boolean isFirst) {
        if (isFirst) {
            if (this.playerProvider.current().equals(this.playerProvider.second())) {
                return this.playerProvider.copy().swap();
            }
        } else {
            if (this.playerProvider.current().equals(this.playerProvider.first())) {
                return this.playerProvider.copy().swap();
            }
        }
        return this.playerProvider;
    }

    @Override
    public Board<T> undoPlayerMove() {
        final var another = new ImmutableBoard<>(this.points, this.playerProvider.copy(), this.moveLog, this.logger).undo();
        if (this.playerProvider.current().equals(another.playerProvider.current())) {
            logger.debug("undoPlayerMove has been made.");
            return another;
        } else {
            logger.debug("undoPlayerMove has returned THIS reference.");
            return this;
        }
    }

    @Override
    public List<Move> allLegalMoves() {
        logger.debug("allLegalMoves executed.");
        final var legalMovesFuture = new LegalMovesFuture(this);
        legalMovesFuture.start(Duration.ofSeconds(Integer.MAX_VALUE));
        final List<Move> result = new ArrayList<>();
        while (legalMovesFuture.isRunning()) {
            result.addAll(legalMovesFuture.partialResult());
        }
        result.addAll(legalMovesFuture.partialResult());
        logger.debug("allLegalMoves finds: " + result.size() + " moves");
        return result;
    }

    @Override
    public LegalMovesFuture allLegalMovesFuture() {
        return new LegalMovesFuture(this);
    }

    @Override
    public Point getBallAPI() {
        return this.points.getBall();
    }

    @Override
    public boolean isGoal() {
        return this.points.getBall().isOnTop() || this.points.getBall().isOnBottom();
    }

    @Override
    public boolean isGameOver() {
        return isGoal() || points.getBall().getUnavailableDirection().size() == 8;
    }

    @Override
    public Optional<T> takeTheWinner() {
        if (!isGameOver())
            return Optional.empty();
        if (isGoal() && getBallPosition() < 20) {
            return Optional.of(this.playerProvider.first());
        }
        if (!isGoal() && isGameOver()) {
            return Optional.of(getPlayer());
        }
        return Optional.of(this.playerProvider.second());
    }

    @Override
    public Board<T> nextPlayerToMove(final T nextPlayerToMove) throws ChangePlayerIsNotAllowed {
        if (this.moveLog.isSmallMoveHasBeenMade()) {
            throw new ChangePlayerIsNotAllowed();
        }
        if (nextPlayerToMove.equals(this.playerProvider.current())) {
            logger.debug(nextPlayerToMove + " is the same as current player to move " +
                this.playerProvider.current() +
                ". Returning THIS reference.");
            return this;
        }
        final var newPlayer = computePlayerToMove(this.points);
        final var providedPlayer = this.playerProvider.current().equals(newPlayer)
            ? this.playerProvider
            : this.playerProvider.copy().swap();
        logger.debug("nextPlayerToMove returns board with player to move " + providedPlayer.current());
        return new ImmutableBoard<>(this.points, providedPlayer, this.moveLog, logger);
    }

    @Override
    public T getPlayer() {
        return this.playerProvider.current();
    }

    @Override
    public PlayerProvider<T> getPlayerProvider() {
        return this.playerProvider.copy();
    }

    @Override
    public int[] transform() {
        return this.points.transform();
    }

    @Override
    public double[] nonBinaryTransformation() {
        return this.points.nonBinaryTransformation();
    }

    private T computePlayerToMove(final LogicalPoints logicalPoints) {
        var player = this.playerProvider.current();

        if (logicalPoints.isOtherPlayerToMove()) {
            return player.equals(this.playerProvider.first()) ? this.playerProvider.second() : this.playerProvider.first();
        }

        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableBoard<?> that = (ImmutableBoard<?>) o;
        return Objects.equals(points, that.points) &&
            Objects.equals(playerProvider, that.playerProvider) &&
            Objects.equals(moveLog, that.moveLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, playerProvider, moveLog);
    }

    @Override
    public String toString() {
        return "ImmutableBoard{" +
            "points=" + points +
            ", playerProvider=" + playerProvider +
            ", moveLog=" + moveLog +
            '}';
    }
}
