package ro.shoponline.model;
import jakarta.persistence.*; import java.math.BigDecimal;
@Entity @Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(columnDefinition="TEXT") private String description;
 @Column(nullable=false) private BigDecimal price;
 @Column(nullable=false) private Integer stock;
 @Column(name="image_url") private String imageUrl;
 @ManyToOne @JoinColumn(name="category_id") private Category category;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
 public Integer getStock(){return stock;} public void setStock(Integer v){stock=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public Category getCategory(){return category;} public void setCategory(Category v){category=v;}
}
