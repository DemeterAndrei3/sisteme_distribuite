package ro.shoponline.web;
import ro.shoponline.service.ShopService; import ro.shoponline.repository.*;
import org.springframework.security.core.Authentication; import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller
public class ShopController {
 private final ShopService shop; private final ProductRepository products; private final CategoryRepository categories;
 private final OrderRepository orders;
 public ShopController(ShopService s,ProductRepository p,CategoryRepository c,OrderRepository o){shop=s;products=p;categories=c;orders=o;}
 @GetMapping("/") String home(@RequestParam(required=false) String q,Model m){m.addAttribute("products",shop.products(q));m.addAttribute("q",q);return "index";}
 @GetMapping("/products/{id}") String detail(@PathVariable Long id,Model m){m.addAttribute("product",shop.product(id));return "detail";}
 @GetMapping("/cart") String cart(Authentication a,Model m){var items=shop.cart(a.getName());m.addAttribute("items",items);m.addAttribute("total",items.stream().map(i->i.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity()))).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));return "cart";}
 @PostMapping("/cart/add/{id}") String add(@PathVariable Long id,Authentication a){shop.addToCart(a.getName(),id);return "redirect:/cart";}
 @PostMapping("/cart/remove/{id}") String remove(@PathVariable Long id,Authentication a){shop.removeFromCart(a.getName(),id);return "redirect:/cart";}
 @PostMapping("/checkout") String checkout(Authentication a){shop.checkout(a.getName());return "redirect:/orders";}
 @GetMapping("/orders") String myOrders(Authentication a,Model m){m.addAttribute("orders",shop.myOrders(a.getName()));return "orders";}
 @GetMapping("/admin") String admin(Model m){m.addAttribute("products",products.findAll());m.addAttribute("categories",categories.findAll());m.addAttribute("orders",orders.findAll());return "admin";}
 @PostMapping("/admin/products") String create(@RequestParam String name,@RequestParam String description,@RequestParam java.math.BigDecimal price,@RequestParam Integer stock,@RequestParam(required=false) String imageUrl){var p=new ro.shoponline.model.Product();p.setName(name);p.setDescription(description);p.setPrice(price);p.setStock(stock);p.setImageUrl(imageUrl);products.save(p);return "redirect:/admin";}
 @PostMapping("/admin/products/delete/{id}") String delete(@PathVariable Long id){products.deleteById(id);return "redirect:/admin";}
}
