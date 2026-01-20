import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {

    @Mock
    private Validator validator;

    private OrderFactory factory;

    @BeforeEach
    void setUp() {
        factory = new OrderFactory(validator);
    }

    @Test
    @DisplayName("Should create order when validation passes")
    void shouldCreateOrderWhenValid() {
        // given
        when(validator.validate(any(Order.class))).thenReturn(Collections.emptySet());

        // when
        Order order = factory.create(
                "anna.smith@company.com",
                new BigDecimal("299.50"),
                OrderStatus.PENDING
        );

        // then
        assertThat(order).isNotNull();
        assertThat(order.getCustomerEmail()).isEqualTo("anna.smith@company.com");
        assertThat(order.getTotalAmount()).isEqualTo(new BigDecimal("299.50"));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        verify(validator).validate(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void shouldThrowWhenValidationFails() {
        // given
        ConstraintViolation<Order> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("totalAmount: must be greater than 0");
        when(validator.validate(any(Order.class)))
                .thenReturn(Set.of(violation));

        // when + then
        assertThatThrownBy(() -> factory.create(
                "test@example.com",
                BigDecimal.ZERO,
                OrderStatus.NEW
        ))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("totalAmount: must be greater than 0");

        verify(validator).validate(any(Order.class));
    }

    @Test
    @DisplayName("Should pass through original validation messages")
    void shouldPreserveAllValidationMessages() {
        // given
        when(validator.validate(any(Order.class))).thenReturn(Set.of(
                mockViolation("customerEmail: must not be blank"),
                mockViolation("totalAmount: must not be null")
        ));

        // when + then
        assertThatThrownBy(() -> factory.create("  ", null, null))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContainingAll(
                        "customerEmail: must not be blank",
                        "totalAmount: must not be null"
                );
    }

    private ConstraintViolation<Order> mockViolation(String message) {
        ConstraintViolation<Order> v = mock(ConstraintViolation.class);
        when(v.getMessage()).thenReturn(message);
        return v;
    }
}
