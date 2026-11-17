package ro.shoponline.repository;
import ro.shoponline.model.Product; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product,Long> { List<Product> findByNameContainingIgnoreCase(String name); }
