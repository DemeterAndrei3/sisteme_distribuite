package ro.shoponline.repository;
import ro.shoponline.model.OrderItem; import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {}
