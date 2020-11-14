package pl.sda.demo.external.recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.external.product.ProductEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaRecipeRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;

    @Test
    void testShouldFindRecipesByIdsInCollection() {
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
//        when
        Set<RecipeEntity> result = jpaRecipeRepository.findAllRecipesByIdInCollection(ids);
//        then
        assertEquals(ids.size(), result.size());
        assertTrue(result.contains(recipeEntity1));
        assertTrue(result.contains(recipeEntity2));
        assertFalse(result.contains(recipeEntity3));
    }

    @Test
    void testShouldGetRecipeByName() {
        //given
        Set<ProductEntity> products1 = new HashSet<>();
        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        products1.add(productEntity1);
        products1.add(productEntity2);

        RecipeEntity recipeEntity1 = RecipeEntity.builder()
                .name("Kanapka")
                .description("Pyszna kanapka")
                .products(products1)
                .build();

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(recipeEntity1);
        testEntityManager.flush();
        //when
        Optional<RecipeEntity> result = jpaRecipeRepository.getRecipeByName("Kanapka");
        //then
        assertTrue(result.isPresent());
        assertEquals("Kanapka", result.get().getName());
        assertEquals("Pyszna kanapka", result.get().getDescription());
        assertTrue(result.get().getProducts().toString().contains(productEntity1.toString()));
        assertTrue(result.get().getProducts().toString().contains(productEntity2.toString()));
    }

    @Test
    void testShouldFindAllRecipesIdFromCollection() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("kurczak").build();
        ProductEntity productEntity4 = ProductEntity.builder().name("boczek").build();

        Set<ProductEntity> products1 = new HashSet<>();
        Set<ProductEntity> products2 = new HashSet<>();
        Set<ProductEntity> products3 = new HashSet<>();

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

        Set<RecipeEntity> given = new HashSet<>();
        given.add(recipeEntity1);
        given.add(recipeEntity3);
        //when
        List<Integer> result = jpaRecipeRepository.findAllRecipesIdFromCollection(given);
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(3));
        assertFalse(result.contains(2));
    }

    @Test
    void testShouldFindRecipeById() {
        //given
        Set<ProductEntity> products1 = new HashSet<>();
        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        products1.add(productEntity1);
        products1.add(productEntity2);

        RecipeEntity recipeEntity1 = RecipeEntity.builder()
                .name("Inna Kanapka")
                .description("Dobra kanapka")
                .products(products1)
                .build();

        RecipeEntity recipeEntity2 = RecipeEntity.builder()
                .name("Kanapka")
                .description("Pyszna kanapka")
                .products(products1)
                .build();

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(recipeEntity1);
        testEntityManager.persist(recipeEntity2);
        testEntityManager.flush();
        //when
        Optional<RecipeEntity> result = jpaRecipeRepository.findRecipeById(2);
        //then
        assertTrue(result.isPresent());
        assertEquals("Kanapka", result.get().getName());
        assertEquals("Pyszna kanapka", result.get().getDescription());
        assertTrue(result.get().getProducts().toString().contains(productEntity1.toString()));
        assertTrue(result.get().getProducts().toString().contains(productEntity2.toString()));
    }

    @Test
    void testShouldFindAllRecipesByProducts() {

        Set<ProductEntity> products1 = new HashSet<>();
        Set<ProductEntity> products2 = new HashSet<>();
        Set<ProductEntity> products3 = new HashSet<>();
        Set<ProductEntity> products4 = new HashSet<>();
        Set<ProductEntity> products5 = new HashSet<>();
        Set<ProductEntity> products6 = new HashSet<>();

        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("kurczak").build();
        ProductEntity productEntity4 = ProductEntity.builder().name("boczek").build();
        ProductEntity productEntity5 = ProductEntity.builder().name("pomidor").build();
        ProductEntity productEntity6 = ProductEntity.builder().name("ser").build();
        ProductEntity productEntity7 = ProductEntity.builder().name("papryka").build();
        ProductEntity productEntity8 = ProductEntity.builder().name("szczypiorek").build();
        ProductEntity productEntity9 = ProductEntity.builder().name("salata").build();
        ProductEntity productEntity10 = ProductEntity.builder().name("keczup").build();
        ProductEntity productEntity11 = ProductEntity.builder().name("musztarda").build();

        products1.add(productEntity1);
        products1.add(productEntity2);
        products1.add(productEntity4);

        products2.add(productEntity1);
        products2.add(productEntity2);
        products2.add(productEntity6);
        products2.add(productEntity5);


        products3.add(productEntity1);
        products3.add(productEntity2);
        products3.add(productEntity6);
        products3.add(productEntity9);
        products3.add(productEntity4);
        products3.add(productEntity10);
        products3.add(productEntity8);

        products4.add(productEntity1);
        products4.add(productEntity7);

        products5.add(productEntity1);
        products5.add(productEntity2);

        products6.add(productEntity1);
        products6.add(productEntity2);
        products6.add(productEntity4);
        products6.add(productEntity5);
        products6.add(productEntity6);



        RecipeEntity recipeEntity1 = RecipeEntity.builder()
                .name("Kanapka boczkiem")
                .description("Pyszna kanapka")
                .products(products1)
                .build();

        RecipeEntity recipeEntity2 = RecipeEntity.builder()
                .name("Kanapka z serem i pomodorem")
                .description("Dobra kanapka")
                .products(products2)
                .build();
        RecipeEntity recipeEntity3 = RecipeEntity.builder()
                .name("Kanapka na bogato")
                .description("Epicka kanapka")
                .products(products3)
                .build();
        RecipeEntity recipeEntity4 = RecipeEntity.builder()
                .name("Studencka kanapka z paprykiem")
                .description("slaba kanapka")
                .products(products4)
                .build();
        RecipeEntity recipeEntity5 = RecipeEntity.builder()
                .name("Kanapek z maslem")
                .description("masno mi")
                .products(products5)
                .build();
        RecipeEntity recipeEntity6 = RecipeEntity.builder()
                .name("Kanapek z dodatkami")
                .description("lmao kanapka")
                .products(products6)
                .build();

        testEntityManager.persist(productEntity1);
        testEntityManager.persist(productEntity2);
        testEntityManager.persist(productEntity3);
        testEntityManager.persist(productEntity4);
        testEntityManager.persist(productEntity5);
        testEntityManager.persist(productEntity6);
        testEntityManager.persist(productEntity7);
        testEntityManager.persist(productEntity8);
        testEntityManager.persist(productEntity9);
        testEntityManager.persist(productEntity10);
        testEntityManager.persist(productEntity11);

        testEntityManager.persist(recipeEntity1);
        testEntityManager.persist(recipeEntity2);
        testEntityManager.persist(recipeEntity3);
        testEntityManager.persist(recipeEntity4);
        testEntityManager.persist(recipeEntity5);
        testEntityManager.persist(recipeEntity6);
        testEntityManager.flush();

        Set<ProductEntity> allProducts = new HashSet<>();
        allProducts.add(productEntity2);
        allProducts.add(productEntity3);
        allProducts.add(productEntity1);
        allProducts.add(productEntity4);
        allProducts.add(productEntity5);
        allProducts.add(productEntity6);
        allProducts.add(productEntity7);
        allProducts.add(productEntity8);
        allProducts.add(productEntity9);
        allProducts.add(productEntity10);

        Set<ProductEntity> moreProducts = new HashSet<>();
        moreProducts.add(productEntity2);
        moreProducts.add(productEntity3);
        moreProducts.add(productEntity1);
        moreProducts.add(productEntity4);
        moreProducts.add(productEntity5);
        moreProducts.add(productEntity6);
        moreProducts.add(productEntity7);
        moreProducts.add(productEntity8);
        moreProducts.add(productEntity9);
        moreProducts.add(productEntity10);
        moreProducts.add(productEntity11);

        Set<ProductEntity> lessProducts = new HashSet<>();
        lessProducts.add(productEntity2);
        lessProducts.add(productEntity3);
        lessProducts.add(productEntity1);
        lessProducts.add(productEntity4);

        //when
        Set<RecipeEntity> resultAll = jpaRecipeRepository.findAllRecipesByProducts(allProducts);
        Set<RecipeEntity> resultMore = jpaRecipeRepository.findAllRecipesByProducts(moreProducts);
        Set<RecipeEntity> resultLess = jpaRecipeRepository.findAllRecipesByProducts(lessProducts);
        //then
        Assertions.assertEquals(6, resultAll.size());
        assertTrue(resultAll.contains(recipeEntity1));
        assertTrue(resultAll.contains(recipeEntity3));
        assertTrue(resultAll.contains(recipeEntity4));
        assertTrue(resultAll.contains(recipeEntity5));
        assertTrue(resultAll.contains(recipeEntity6));
        assertTrue(resultAll.contains(recipeEntity2));

        Assertions.assertEquals(6, resultMore.size());
        assertTrue(resultMore.contains(recipeEntity1));
        assertTrue(resultMore.contains(recipeEntity3));
        assertTrue(resultMore.contains(recipeEntity4));
        assertTrue(resultMore.contains(recipeEntity5));
        assertTrue(resultMore.contains(recipeEntity6));
        assertTrue(resultMore.contains(recipeEntity2));

        Assertions.assertEquals(2, resultLess.size());
        assertTrue(resultLess.contains(recipeEntity1));
        assertTrue(resultLess.contains(recipeEntity5));
    }
}
