package io.github.lipinskipawel.board.engine;

import io.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;

import java.util.List;
import java.util.Optional;

/**
 * @param <T> player
 */
public interface Board<T> extends Transformation {

    /**
     * This method will move the ball in the given direction.
     *
     * @param direction this is the direction to move a ball
     * @return a new BoardInterface object with a ball position
     */
    Board<T> executeMove(final Direction direction);

    /**
     * This method will move the ball by the given move.
     * The method is safe to use and it will update Move History.
     *
     * @param move this move will be made
     * @return a new BoardInterface object with a new move
     */
    Board<T> executeMove(final Move move);

    /**
     * This method will undo only current player small moves.
     * Which means that this method will never change the current player.
     *
     * @return a new BoardInterface object with a undo Player move
     */
    Board<T> undoPlayerMove();

    /**
     * This method will undo small move and hasn't any restrictions. It means
     * that this method can change next player to move.
     * If method is call enough time it is possible to undo whole game.
     *
     * @return a new BoardInterface object with a undo move
     * @throws RuntimeException when the move can not be undo at the beggining of the game
     */
    Board<T> undo();

    /**
     * Returns all possible moves whenever this move ends up hitting the corner or not.
     * This method could be time consuming based on the board state. There can be many
     * moves already played on the board and the complexity could be high.
     * <p>
     * This method is deprecated.
     * The replacement is {@link #allLegalMovesFuture()} method. It provides the same functionality while adding more on
     * top of that like:
     * - ability of cancel the computation
     * - obtaining partial results, during computation
     *
     * @return Returns all possible moves for current {@link Player}
     */
    @Deprecated
    List<Move> allLegalMoves();

    /**
     * This method is non-blocking version of {@link #allLegalMoves()}.
     * This method returns {@link LegalMovesFuture} object that is capable of computing all legal moves.
     *
     * @return legalMoveFuture which should be used to compute all legal moves
     */
    LegalMovesFuture allLegalMovesFuture();

    /**
     * @return all Moves made by each {@link Player}
     */
    List<Move> moveHistory();

    /**
     * 'Small' move is when not done yet. Move is done when there is
     * next player to move.
     *
     * @return all 'small' which has been made
     */
    List<Direction> allMoves();

    /**
     * Move is allowed when it hasn't been made or the move from opposite
     * direction was't made yet.
     *
     * @param destination possible direction to move ball
     * @return true or false whenever the move is allowed
     */
    boolean isMoveAllowed(final Direction destination);

    /**
     * This method will be move into {@link Point} API.
     *
     * @return position where the ball is
     */
    @Deprecated
    int getBallPosition();

    /**
     * @return current {@link Point} where the ball is present
     */
    Point getBallAPI();

    /**
     * Goal area is defined by the three inner points. On the board there are
     * two goal area.
     *
     * @return true or false whenever ball in the one of the goal area
     */
    boolean isGoal();

    /**
     * <p>Returns current {@link Player} to move. When new board is created
     * then this method will return {@link Player#FIRST}. Whenever new move
     * has been made then opposite Player will be returned. <p>A new move is made
     * only when</p>:
     * - ball has been kicked to new point
     * - ball has been kicked into the goal area
     * - ball has been stuck into corner or other forms without possibility to move
     *
     * <p>This method will not return opposite player when</p>:
     * - ball has been kicked to the wall or to already drawn move
     *
     * @return T the player which is the current player
     */
    T getPlayer();

    /**
     * @return true if any of player score a goal or there are no possible moves to make. Otherwise false.
     */
    boolean isGameOver();

    /**
     * This method is able to change player which is allowed to make a move. It could be invoked in any board state.
     * This method will succeed only when non small moves has been made.
     *
     * @param nextPlayerToMove this PLayer will be next to move
     * @return a new instance of BoardInterface with the same logical state <strong>except</strong> next player to move
     * @throws ChangePlayerIsNotAllowed whenever small move has been made and wasn't undo
     */
    Board<T> nextPlayerToMove(final T nextPlayerToMove) throws ChangePlayerIsNotAllowed;

    /**
     * This method will return the winner of the game. If game ends with the ball inside of the goal area the method
     * will calculate the winner. If one of the player hits the corner the method will calculate the winner properly.
     * The result of this method can and will differ from {@link Board#getPlayer()} after the returning the
     * true from {@link Board#isGameOver()}.
     *
     * @return the winner of the game or {@link Optional#empty()} if the game is not decided yet
     */
    Optional<T> takeTheWinner();

    /**
     * This method is used to obtain a reference to an object whose is able to provide extensive details about player.
     * {@code PlayerProvider} is able to tell the current player which is able to move as well as the first and second
     * player object that were registered in the {@link Board}.
     *
     * @return player provider object
     */
    PlayerProvider<T> getPlayerProvider();
}
