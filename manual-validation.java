import jakarta.persistence.*;
import jakarta.validation.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for JPA
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Private constructor
    private Order(String customerEmail, BigDecimal totalAmount, OrderStatus status) {
        this.customerEmail = customerEmail;
        this.totalAmount   = totalAmount;
        this.status        = status;
    }

    public static Order create(String customerEmail, BigDecimal totalAmount, OrderStatus status) {
        var order = new Order(
            customerEmail.strip(),
            totalAmount,
            status != null ? status : OrderStatus.NEW
        );

        validate(order);

        return order;
    }

    private static void validate(Order order) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Cannot create Order - validation failed", violations);
        }
    }
}
