package ro.shoponline.repository;
import ro.shoponline.model.CartItem; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional;
public interface CartItemRepository extends JpaRepository<CartItem,Long> { List<CartItem> findByUserId(Long id); Optional<CartItem> findByUserIdAndProductId(Long userId,Long productId); void deleteByUserId(Long id); }
