package pl.sda.demo.external;

import lombok.*;


import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @ManyToMany
    @JoinTable (name = "recipe_product"
            , joinColumns = @JoinColumn (name = "recipeId")
            , inverseJoinColumns = @JoinColumn(name = "productId"))
    private Set<ProductEntity> products;

}
