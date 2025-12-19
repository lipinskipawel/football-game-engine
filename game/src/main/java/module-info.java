module football.game.engine {
    exports io.github.lipinskipawel.board.spi;
    exports io.github.lipinskipawel.board.engine;
    exports io.github.lipinskipawel.board.ai;
    exports io.github.lipinskipawel.board.ai.bruteforce;
    exports io.github.lipinskipawel.board.engine.exception;

    opens io.github.lipinskipawel.board.engine;
    opens io.github.lipinskipawel.board.ai.bruteforce;
}
