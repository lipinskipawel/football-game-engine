package io.github.lipinskipawel.board.engine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is an API.
 * This class is designed to be returned by the {@link Board#allLegalMovesFuture()} method. Clients are not permitted to
 * create instances of this class by themselves.
 * <p>
 * This class is capable of find all possible moves for a given {@link Board board}. This class will find even those
 * moves that result in ending the game instantaneously when played (for example by placing ball inside the corner).
 * Searching for all possible moves is described as computation in the javadoc of this class.
 * <p>
 * In order to start computation client <strong>MUST</strong> call {@link #start(Duration)} method with specified
 * duration. After this duration the computation will be stopped. Duration is required since board can be in computation
 * complex state where searching for all possible moves results in several thousands moves. Due to this inherent
 * complexity client must provide timeout.
 * <p>
 * During the computation the implementation saves already found moves in its state. Clients can access already found
 * moves during the computation. See {@link #partialResult()} method.
 * <p>
 * <h2>Cancellation Policy</h2>
 * There are three ways in which the computation will be stopped:
 * - completing the computation within given timeout
 * - running out of time due to exceeding given timeout
 * - canceling computation explicit by the caller ({@link #cancel()}
 *
 * <h2>Thread safety</h2>
 * This class is thread-safe. Any calls to its public API in concurrent way is understood as safe. Implementation does
 * not expose any lock for the client side locking.
 */
public final class LegalMovesFuture {
    private final Board<?> board;

    /**
     * This field holds results of the computation.
     */
    private final BlockingQueue<Move> allMoves;

    private final AtomicBoolean isStarted;

    private final Object lock;

    private final ExecutorService pool;

    /**
     * This field controls whether the computation should be canceled.
     *
     * @implNote This variable can not be volatile since it will introduce race condition between checking cancellation
     * and storing move onto the {@link #allMoves} variable.
     */
    // @GuardedBy("lock")
    private boolean isCancel;

    LegalMovesFuture(final Board<?> board) {
        this.board = board;
        this.allMoves = new LinkedBlockingDeque<>();
        this.isStarted = new AtomicBoolean(false);
        this.isCancel = false;
        this.lock = new Object();
        this.pool = Executors.newFixedThreadPool(2);
        Runtime.getRuntime().addShutdownHook(new Thread(this::cancel));
    }

    /**
     * This method is a part of the API.
     * <p>
     * This method is CPU bounded. Calling this method will start the task of finding all possible moves.
     * Calling this method multiple times will have no effect other than ensuring that the computation is already
     * started. Calling this method is non-blocking, since it will start the computation is separate thread pool.
     * Client can check the status of a job by {@link #isRunning()} method.
     * Client can access results by {@link #partialResult()} method.
     * Client can cancel computation in any time by {@link #cancel()} method.
     *
     * @param timeout after which the task will be stopped
     */
    public void start(final Duration timeout) {
        if (this.isStarted.compareAndSet(false, true)) {
            final Future<?> submit = this.pool.submit(this::startComputation);
            this.pool.submit(() -> {
                try {
                    submit.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
                } finally {
                    this.cancel();
                }
            });
        }
    }

    /**
     * This method pulls already computed results to the client code. When the computation has been finished this method
     * will return all found moves. Otherwise, will return partial results for computation.
     * When using this method with the conjunction with the {@link #isRunning()} be aware of race condition. Those two
     * methods are not synchronized with each other. To prevent losing moves clients usually write code like this:
     * <pre> {@code
     * class App {
     *   List<Move> globalAccess = ....
     *   void consumeMoves(LegalMovesFuture legalMoves) {
     *     while (!legalMoves.isRunning()) {
     *         globalAccess.addAll(legalMoves.partialResults());
     *     }
     *
     *     globalAccess.addAll(legalMoves.partialResults());
     *     return global;
     *   }
     * }}</pre>
     * <p>
     * This method guarantees to not lose any found moves after the computation has been finished in either way (for
     * documentation about finishing computation check the section about cancellation policy in the
     * {@link LegalMovesFuture} javadoc class).
     *
     * @return list of moves that was found so far
     */
    public List<Move> partialResult() {
        final var moves = new ArrayList<Move>();
        this.allMoves.drainTo(moves);
        return moves;
    }

    /**
     * This method cancels current computation of finding all possible moves. Cancelling already cancelled computation
     * has no effect.
     * Cancellation of computation does not happen immediately as synchronization of every step will be too expensive
     * for performance reasons. Even though the computation is still on going current implementation ensures no new results
     * will be saved into memory.
     */
    public void cancel() {
        synchronized (this.lock) {
            this.isCancel = true;
        }
        this.pool.shutdownNow();
    }

    /**
     * Returns {@code true} only if this task is still running. See javadoc for cancellation policy.
     *
     * @return true or false whether the computation is ongoing
     */
    public boolean isRunning() {
        synchronized (this.lock) {
            return !this.isCancel;
        }
    }

    private void startComputation() {
        findAllMovesIteratively(this.board);
        synchronized (this.lock) {
            this.isCancel = true;
        }
    }

    /**
     * The iterative implementation of depth-first search. It uses inner class for tracking prior found directions with
     * regard to board that contains those executed directions.
     *
     * @param board to start looking for a moves
     */
    private void findAllMovesIteratively(final Board<?> board) {
        final class Level {
            private final Board<?> board;
            private final Stack<Direction> stackOfPriorMoves;

            Level(Board<?> board) {
                this.board = board;
                this.stackOfPriorMoves = new Stack<>();
            }

            Level(Board<?> board, Stack<Direction> stack) {
                this.board = board;
                this.stackOfPriorMoves = stack;
            }

            Stack<Direction> push(Direction direction) {
                final var newStack = new Stack<Direction>();
                newStack.addAll(stackOfPriorMoves);
                newStack.push(direction);
                return newStack;
            }
        }
        var currentLevels = new Stack<Level>();
        currentLevels.push(new Level(board));

        while (!canStopComputation() && !currentLevels.isEmpty()) {
            var level = currentLevels.pop();
            for (var move : level.board.getBallAPI().getAllowedDirection()) {
                final var afterMove = level.board.executeMove(move);

                if (isItEnd(afterMove.getBallAPI())) {
                    this.allMoves.add(new Move(level.push(move)));
                    if (canStopComputation()) {
                        break;
                    }
                } else {
                    final var item = new Level(afterMove, level.push(move));
                    currentLevels.push(item);
                }
            }
        }
    }

    private boolean canStopComputation() {
        synchronized (this.lock) {
            return this.isCancel;
        }
    }

    private boolean isItEnd(final Point ball) {
        return ball.getAllowedDirection().size() == 7 || ball.getAllowedDirection().size() == 0;
    }
}
