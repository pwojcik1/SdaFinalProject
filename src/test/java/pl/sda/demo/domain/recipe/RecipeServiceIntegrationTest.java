package pl.sda.demo.domain.recipe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.product.ProductService;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RecipeServiceIntegrationTest {

    @Autowired
    private JpaProductRepository jpaProductRepository;

    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ProductService productService;

    @Test
    void testShouldAddRecipeToDb() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        Recipe recipe = new Recipe(null, "name", "description", ids);
        //when
        recipeService.addRecipeToDb(recipe);
        //then
        RecipeEntity result = jpaRecipeRepository.getOne(5);
        assertEquals(5, result.getId());
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
    }

    @Test
    void testShouldUpdateRecipe() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(2);
        Recipe recipe = new Recipe(1, "newName", "newDescription", ids);
        //when
        recipeService.updateRecipeInDb(recipe);
        //then
        RecipeEntity result = jpaRecipeRepository.getOne(1);
        assertEquals(1, result.getId());
        assertEquals("newDescription", result.getDescription());
        assertEquals("newName", result.getName());
        assertEquals(1, result.getProducts().size());
        assertTrue(result.getProducts().contains(jpaProductRepository.getOne(2)));
        assertFalse(result.getProducts().contains(jpaProductRepository.getOne(1)));
        assertFalse(result.getProducts().contains(jpaProductRepository.getOne(4)));
        assertFalse(result.getProducts().contains(jpaProductRepository.getOne(6)));
    }

    @Test
    void testShouldDeleteRecipeFromDb() {
        //given
        //when
        recipeService.deleteRecipeFromDb(1);
        //then
        Optional<RecipeEntity> result = jpaRecipeRepository.findById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testShouldFindRecipeByName() {
        //given
        //when
        Recipe result = recipeService.findRecipeByName("Recipe3");
        //then
        assertEquals(3, result.getId());
        assertEquals("description for recipe3", result.getDescription());
        assertEquals("Recipe3", result.getName());
        assertEquals(2, result.getProductId().size());
        assertTrue(result.getProductId().contains(2));
        assertTrue(result.getProductId().contains(4));
    }

    @Test
    void testShouldFindRecipesByProducts() {
        //given
        List<Product> given = new ArrayList<>();
        given.add(productService.findProductById(1));
        given.add(productService.findProductById(2));
        given.add(productService.findProductById(4));
        given.add(productService.findProductById(6));
        //when
        Set<Recipe> result = recipeService.findRecipeByProducts(given);
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(recipeService.findRecipeById(1)));
        assertTrue(result.contains(recipeService.findRecipeById(3)));
        assertFalse(result.contains(recipeService.findRecipeById(2)));
        assertFalse(result.contains(recipeService.findRecipeById(4)));
    }

    @Test
    void testShouldReturnAllRecipes() {
        //given
        //when
        List<Recipe> result = recipeService.findAllRecipes();
        //then
        assertEquals(4, result.size());
        assertTrue(result.contains(recipeService.findRecipeById(1)));
        assertTrue(result.contains(recipeService.findRecipeById(2)));
        assertTrue(result.contains(recipeService.findRecipeById(3)));
        assertTrue(result.contains(recipeService.findRecipeById(4)));
    }

    @Test
    void testShouldReturnOneRecipeById() {
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(4);
        ids.add(6);
        //when
        Recipe result = recipeService.findRecipeById(1);
        //then
        assertEquals(1, result.getId());
        assertEquals("Recipe1", result.getName());
        assertEquals("description for recipe1", result.getDescription());
        assertEquals(ids, result.getProductId());
    }

    @Test
    void testShouldThrowExceptionIfIdIsIncorrect() {
        //given
        //when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> recipeService.findRecipeById(100));
        //then
        assertEquals("Recipe with given id doesnt exist", ex.getMessage());
    }
}