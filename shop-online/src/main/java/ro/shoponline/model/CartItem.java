package ro.shoponline.model;
import jakarta.persistence.*;
@Entity @Table(name="cart_items", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","product_id"}))
public class CartItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne @JoinColumn(name="user_id") private User user;
 @ManyToOne @JoinColumn(name="product_id") private Product product;
 private Integer quantity;
 public Long getId(){return id;} public User getUser(){return user;} public void setUser(User v){user=v;}
 public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
 public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
}
