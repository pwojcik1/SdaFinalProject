package external;

import domain.Recipe;
import domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    @Table(name = "products")
    public class ProductEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)

        private Integer id;
        private String name;

        @OneToMany(mappedBy = "user")
        private Set<UserEntity> users;
        @OneToMany (mappedBy = "user")
        private Set<Recipe>recipes;

}
