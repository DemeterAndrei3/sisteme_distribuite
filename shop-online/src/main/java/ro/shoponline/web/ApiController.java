package ro.shoponline.web;
import ro.shoponline.model.Product; import ro.shoponline.repository.ProductRepository;
import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/v1/products")
public class ApiController {
 private final ProductRepository products; public ApiController(ProductRepository p){products=p;}
 @GetMapping public List<Product> all(){return products.findAll();}
 @GetMapping("/{id}") public Product one(@PathVariable Long id){return products.findById(id).orElseThrow();}
}
