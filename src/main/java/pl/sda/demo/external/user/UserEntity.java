package pl.sda.demo.external.user;

import lombok.*;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.RecipeEntity;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@ToString
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String role;

    @ManyToMany
    @JoinTable(name = "user_recipe",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "recipeId"))
    private Set<RecipeEntity> favourites;

    @ManyToMany
    @JoinTable(name = "user_product",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "prouctId"))
    private Set<ProductEntity> products;
    public void updateFromDomain(User user){
        this.password = user.getPassword();
    }
}
