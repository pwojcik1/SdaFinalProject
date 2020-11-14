package pl.sda.demo.external.product;

import lombok.*;
import pl.sda.demo.domain.product.Product;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@ToString
@Setter
@EqualsAndHashCode
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public void updateFromDomain(Product product) {
        this.name = product.getName();
    }
}
