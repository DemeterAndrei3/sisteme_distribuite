package ro.shoponline.model;
import jakarta.persistence.*; import java.math.BigDecimal;
@Entity @Table(name="order_items")
public class OrderItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne @JoinColumn(name="order_id") private Order order;
 @ManyToOne @JoinColumn(name="product_id") private Product product;
 private Integer quantity;
 @Column(name="unit_price") private BigDecimal unitPrice;
 public Long getId(){return id;} public Order getOrder(){return order;} public void setOrder(Order v){order=v;}
 public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
 public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
 public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
}
