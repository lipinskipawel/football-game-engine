package io.github.lipinskipawel.board.engine;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("API -- Point")
class PointTest {

    @Nested
    @DisplayName("kickBallTo")
    class KickBall {

        @Test
        @DisplayName("to E")
        void toE() {
            final var point = new Point(50);

            final var actual = point.kickBallTo(51);

            Assertions.assertThat(actual).isEqualByComparingTo(Direction.E);
        }

        @Test
        @DisplayName("to SW")
        void toSW() {
            final var point = new Point(57);

            final var actual = point.kickBallTo(65);

            Assertions.assertThat(actual).isEqualByComparingTo(Direction.SW);
        }

        @Test
        @DisplayName("to NE")
        void toNE() {
            final var point = new Point(57);

            final var actual = point.kickBallTo(49);

            Assertions.assertThat(actual).isEqualByComparingTo(Direction.NE);
        }

        @Test
        @DisplayName("catch runtime exception")
        void catchException() {
            final var point = new Point(57);

            final var runtime = Assertions.catchThrowable(() -> point.kickBallTo(35));

            Assertions.assertThat(runtime)
                .isInstanceOf(RuntimeException.class);
        }
    }
}
