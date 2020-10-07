package pl.sda.demo.external.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JpaUserRepositoryIntegrationTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testShouldGetUserByName() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("egg").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("chleb").build();

        Set<ProductEntity> products1 = new HashSet<>();
        products1.add(productEntity1);
        products1.add(productEntity2);

        RecipeEntity recipeEntity1 = RecipeEntity.builder()
                .name("Kanapka")
                .description("Pyszna kanapka")
                .products(products1)
                .build();
        Set<RecipeEntity> recipes1 = new HashSet<>();
        recipes1.add(recipeEntity1);

        UserEntity userEntity = UserEntity.builder().username("admin").password("123").products(products1).favourites(recipes1).build();

        entityManager.persist(productEntity1);
        entityManager.persist(productEntity2);
        entityManager.persist(recipeEntity1);
        entityManager.persist(userEntity);
        entityManager.flush();
        //when
        Optional<UserEntity> result = jpaUserRepository.getUserByName("admin");
        //then
        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
        assertEquals("123", result.get().getPassword());
        assertEquals(1, result.get().getId());
        assertEquals(1, result.get().getFavourites().size());
        assertEquals(2, result.get().getProducts().size());
        assertTrue(result.get().getFavourites().toString().contains(recipeEntity1.toString()));
        assertTrue(result.get().getProducts().toString().contains(productEntity1.toString()));
        assertTrue(result.get().getProducts().toString().contains(productEntity2.toString()));
    }
}

