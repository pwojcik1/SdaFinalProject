package pl.sda.demo.external.recipe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaRecipeRepositoryIntegrationTest {

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;
    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Test
    void testShouldFindRecipesByIdsInCollection() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(4);
        //when
        Set<RecipeEntity> result = jpaRecipeRepository.findAllRecipesByIdInCollection(ids);
        //then
        assertEquals(ids.size(), result.size());
        assertTrue(result.contains(jpaRecipeRepository.getOne(1)));
        assertTrue(result.contains(jpaRecipeRepository.getOne(2)));
        assertTrue(result.contains(jpaRecipeRepository.getOne(4)));
    }

    @Test
    void testShouldGetRecipeByName() {
        //given
        //when
        Optional<RecipeEntity> recipe = jpaRecipeRepository.findRecipeByName("Recipe3");
        //then
        assertTrue(recipe.isPresent());
        RecipeEntity result = recipe.get();
        assertEquals("Recipe3", result.getName());
        assertEquals("description for recipe3", result.getDescription());
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(4)));
    }

    @Test
    void testShouldFindAllRecipesIdFromCollection() {
        //given
        Set<RecipeEntity> given = new HashSet<>();
        given.add(jpaRecipeRepository.getOne(2));
        given.add(jpaRecipeRepository.getOne(4));
        //when
        List<Integer> result = jpaRecipeRepository.findAllRecipesIdFromCollection(given);
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(2));
        assertTrue(result.contains(4));

    }

    @Test
    void testShouldFindAllRecipesByProducts() {

        Set<ProductEntity> allProducts = new HashSet<>();
        allProducts.add(jpaProductRepository.getOne(1));
        allProducts.add(jpaProductRepository.getOne(2));
        allProducts.add(jpaProductRepository.getOne(3));
        allProducts.add(jpaProductRepository.getOne(4));
        allProducts.add(jpaProductRepository.getOne(5));
        allProducts.add(jpaProductRepository.getOne(6));
        allProducts.add(jpaProductRepository.getOne(7));
        allProducts.add(jpaProductRepository.getOne(8));
        allProducts.add(jpaProductRepository.getOne(9));

        Set<ProductEntity> lessProducts = new HashSet<>();
        lessProducts.add(jpaProductRepository.getOne(1));
        lessProducts.add(jpaProductRepository.getOne(2));
        lessProducts.add(jpaProductRepository.getOne(4));
        lessProducts.add(jpaProductRepository.getOne(6));

        //when
        Set<RecipeEntity> resultAll = jpaRecipeRepository.findAllRecipesByProducts(allProducts);
        Set<RecipeEntity> resultLess = jpaRecipeRepository.findAllRecipesByProducts(lessProducts);
        //then
        assertEquals(4, resultAll.size());
        assertTrue(resultAll.contains(jpaRecipeRepository.getOne(1)));
        assertTrue(resultAll.contains(jpaRecipeRepository.getOne(2)));
        assertTrue(resultAll.contains(jpaRecipeRepository.getOne(3)));
        assertTrue(resultAll.contains(jpaRecipeRepository.getOne(4)));

        assertEquals(2, resultLess.size());
        assertTrue(resultLess.contains(jpaRecipeRepository.getOne(1)));
        assertTrue(resultLess.contains(jpaRecipeRepository.getOne(3)));
    }

    @Test
    public void testShouldFindAllUserFavourites() {
        //given
        //when
        List<RecipeEntity> result = jpaRecipeRepository.findAllUserFavourites("user1");
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(jpaRecipeRepository.getOne(1)));
        assertTrue(result.contains(jpaRecipeRepository.getOne(2)));
    }
}
