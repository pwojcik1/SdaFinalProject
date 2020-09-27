package external;

import domain.Product;
import domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

@Id
@GeneratedValue (strategy = GenerationType.IDENTITY)

    private Integer id;
    private String login;
    private String password;
    @OneToMany (mappedBy = "recipe")
    private Set<Recipe> favorite;
    @OneToMany(mappedBy = "product")
    private Set<Product> fridge;


}
