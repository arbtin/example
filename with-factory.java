@Component
@RequiredArgsConstructor
public class OrderFactory {

    private final Validator validator;

    public Order create(String customerEmail, BigDecimal totalAmount, OrderStatus status) {
        var order = new Order(customerEmail.strip(), totalAmount, status);

        var violations = validator.validate(order);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return order;
    }
}

// In service layer
@Service
@RequiredArgsConstructor
class OrderService {
    private final OrderFactory orderFactory;
    private final OrderRepository repository;

    public Order placeOrder(...) {
        Order order = orderFactory.create(email, amount, status);
        return repository.save(order);
    }
}
