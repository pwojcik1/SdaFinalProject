package pl.sda.demo.external.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.external.product.ProductEntity;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "recipes")
public class RecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    @ManyToMany
    @JoinTable(name = "recipe_product",
            joinColumns = @JoinColumn(name = "recipeId"),
            inverseJoinColumns = @JoinColumn(name = "productId"))
    private Set<ProductEntity> products;

    public void updateFromDomain(Recipe recipe, Set<ProductEntity> products) {
        this.name = recipe.getName();
        this.description = recipe.getDescription();
        this.products = products;
    }
}
