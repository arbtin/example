import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    private static final String VALID_EMAIL = "john.doe@example.com";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("149.99");

    @Test
    @DisplayName("Should create valid order with minimal arguments")
    void shouldCreateValidOrder() {
        Order order = Order.create(VALID_EMAIL, VALID_AMOUNT, OrderStatus.NEW);

        assertThat(order.getCustomerEmail()).isEqualTo(VALID_EMAIL);
        assertThat(order.getTotalAmount()).isEqualTo(VALID_AMOUNT);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @ParameterizedTest
    @MethodSource("invalidCreationCases")
    @DisplayName("Should reject invalid creation parameters")
    void shouldRejectInvalidInputs(
            String email,
            BigDecimal amount,
            OrderStatus status,
            String expectedErrorMessagePart
    ) {
        assertThatThrownBy(() -> Order.create(email, amount, status))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining(expectedErrorMessagePart);
    }

    private static Stream<Arguments> invalidCreationCases() {
        return Stream.of(
                Arguments.of(null, VALID_AMOUNT, OrderStatus.NEW, "customerEmail: must not be null"),
                Arguments.of("   ", VALID_AMOUNT, OrderStatus.NEW, "customerEmail: must not be blank"),
                Arguments.of(VALID_EMAIL, null, OrderStatus.NEW, "totalAmount: must not be null"),
                Arguments.of(VALID_EMAIL, BigDecimal.ZERO, OrderStatus.NEW, "totalAmount: must be greater than 0"),
                Arguments.of(VALID_EMAIL, new BigDecimal("-10"), OrderStatus.NEW, "totalAmount: must be greater than 0")
        );
    }

    @Test
    @DisplayName("Should trim email automatically")
    void shouldTrimEmail() {
        Order order = Order.create("  test@example.com   ", VALID_AMOUNT, null);

        assertThat(order.getCustomerEmail()).isEqualTo("test@example.com");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW); // default value
    }
}
