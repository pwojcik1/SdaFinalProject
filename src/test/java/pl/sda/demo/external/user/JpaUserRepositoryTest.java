package pl.sda.demo.external.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaUserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void testShouldFindProductsByIdsInList() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("egg").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity4 = ProductEntity.builder().name("kurczak").build();

        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(productEntity3);
        testEntityManager.persist(productEntity4);

        //when
        Set<ProductEntity> result = jpaUserRepository.findAllProductsByIdInList(ids);
        //then
        assertEquals(ids.size(), result.size());
        assertTrue(result.contains(productEntity1));
        assertTrue(result.contains(productEntity2));
        assertTrue(result.contains(productEntity3));
        assertFalse(result.contains(productEntity4));
    }

    @Test
    void testShouldFindRecipesByIdsInList() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        Set<ProductEntity> products1 = new HashSet<>();
        Set<ProductEntity> products2 = new HashSet<>();
        Set<ProductEntity> products3 = new HashSet<>();
        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("kurczak").build();
        ProductEntity productEntity4 = ProductEntity.builder().name("boczek").build();

        products1.add(productEntity1);
        products1.add(productEntity2);
        products1.add(productEntity3);
        products2.add(productEntity1);
        products2.add(productEntity4);
        products3.add(productEntity1);
        products3.add(productEntity2);

        RecipeEntity recipeEntity1 = RecipeEntity.builder()
                .name("Kanapka")
                .description("Pyszna kanapka")
                .products(products1)
                .build();

        RecipeEntity recipeEntity2 = RecipeEntity.builder()
                .name("Inna Kanapka")
                .description("Dobra kanapka")
                .products(products2)
                .build();
        RecipeEntity recipeEntity3 = RecipeEntity.builder()
                .name("prawie Kanapka")
                .description("slaba kanapka")
                .products(products3)
                .build();

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(productEntity3);
        testEntityManager.persist(productEntity4);

        testEntityManager.persist(recipeEntity1);
        testEntityManager.persist(recipeEntity2);
        testEntityManager.persist(recipeEntity3);
        testEntityManager.flush();
        //when
        Set<RecipeEntity> result = jpaUserRepository.findAllRecipesByIdInList(ids);
        //then
        assertEquals(ids.size() ,result.size());
        assertTrue(result.contains(recipeEntity1));
        assertTrue(result.contains(recipeEntity2));
        assertFalse(result.contains(recipeEntity3));

    }
}