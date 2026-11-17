package ro.shoponline.service;
import ro.shoponline.model.*; import ro.shoponline.repository.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
@Service
public class ShopService {
 private final ProductRepository products; private final UserRepository users; private final CartItemRepository cart;
 private final OrderRepository orders; private final OrderItemRepository orderItems;
 public ShopService(ProductRepository p,UserRepository u,CartItemRepository c,OrderRepository o,OrderItemRepository oi){products=p;users=u;cart=c;orders=o;orderItems=oi;}
 public List<Product> products(String q){return q==null||q.isBlank()?products.findAll():products.findByNameContainingIgnoreCase(q);}
 public Product product(Long id){return products.findById(id).orElseThrow();}
 public User user(String email){return users.findByEmail(email).orElseThrow();}
 public List<CartItem> cart(String email){return cart.findByUserId(user(email).getId());}
 @Transactional public void addToCart(String email,Long productId){
  User u=user(email); Product p=product(productId);
  if(p.getStock()<1) throw new IllegalStateException("Produsul nu este in stoc.");
  CartItem item=cart.findByUserIdAndProductId(u.getId(),productId).orElseGet(()->{CartItem x=new CartItem();x.setUser(u);x.setProduct(p);x.setQuantity(0);return x;});
  if(item.getQuantity()+1>p.getStock()) throw new IllegalStateException("Stoc insuficient.");
  item.setQuantity(item.getQuantity()+1);cart.save(item);
 }
 @Transactional public void removeFromCart(String email,Long id){cart.deleteById(id);}
 @Transactional public Order checkout(String email){
  User u=user(email); List<CartItem> items=cart.findByUserId(u.getId());
  if(items.isEmpty()) throw new IllegalStateException("Cosul este gol.");
  BigDecimal total=BigDecimal.ZERO;
  for(CartItem i:items){if(i.getQuantity()>i.getProduct().getStock())throw new IllegalStateException("Stoc insuficient pentru "+i.getProduct().getName());total=total.add(i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));}
  Order o=new Order();o.setUser(u);o.setCreatedAt(LocalDateTime.now());o.setStatus("PLASATA");o.setTotal(total);orders.save(o);
  for(CartItem i:items){Product p=i.getProduct();p.setStock(p.getStock()-i.getQuantity());products.save(p);OrderItem oi=new OrderItem();oi.setOrder(o);oi.setProduct(p);oi.setQuantity(i.getQuantity());oi.setUnitPrice(p.getPrice());orderItems.save(oi);}
  cart.deleteByUserId(u.getId());return o;
 }
 public List<Order> myOrders(String email){return orders.findByUserIdOrderByCreatedAtDesc(user(email).getId());}
}
