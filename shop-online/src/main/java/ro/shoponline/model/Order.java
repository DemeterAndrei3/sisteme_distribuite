package ro.shoponline.model;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="orders")
public class Order {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne @JoinColumn(name="user_id") private User user;
 @Column(name="created_at") private LocalDateTime createdAt;
 private String status; private BigDecimal total;
 public Long getId(){return id;} public User getUser(){return user;} public void setUser(User v){user=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){total=v;}
}
