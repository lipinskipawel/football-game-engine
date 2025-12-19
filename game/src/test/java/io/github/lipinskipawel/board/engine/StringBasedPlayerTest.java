package io.github.lipinskipawel.board.engine;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StringBasedPlayerTest {

    @Test
    void shouldSetPlayersInOrder() {
        final var subject = new PlayerProvider<>(Player.FIRST, Player.SECOND);

        Assertions.assertThat(subject.first()).isEqualByComparingTo(Player.FIRST);
        Assertions.assertThat(subject.second()).isEqualByComparingTo(Player.SECOND);
        Assertions.assertThat(subject.current()).isEqualByComparingTo(Player.FIRST);
    }

    @Test
    void shouldSwapPlayers() {
        final var subject = new PlayerProvider<>(Player.FIRST, Player.SECOND);
        subject.swap();

        Assertions.assertThat(subject.current()).isEqualByComparingTo(Player.SECOND);
    }
}
