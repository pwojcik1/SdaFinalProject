package pl.sda.demo.domain.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
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
    ProductEntity productEntity1;
    ProductEntity productEntity2;
    ProductEntity productEntity3;
    RecipeEntity recipeEntity;

    @BeforeEach
    void setup() {
        productEntity1 = new ProductEntity(1, "product1");
        productEntity2 = new ProductEntity(2, "product2");
        productEntity3 = new ProductEntity(3, "product3");

        jpaProductRepository.save(productEntity1);
        jpaProductRepository.save(productEntity2);
        jpaProductRepository.save(productEntity3);

        Set<ProductEntity> products = new HashSet<>();
        products.add(productEntity1);
        products.add(productEntity2);
        products.add(productEntity3);

        recipeEntity = new RecipeEntity(1, "testName", "testDescription", products);
        jpaRecipeRepository.save(recipeEntity);
    }

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
        RecipeEntity result = jpaRecipeRepository.getOne(2);
        assertEquals(2, result.getId());
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
    }

    @Test
    void testShouldUpdateRecipe(){
        //given
        List<Integer> ids = new ArrayList<>();
        ids.add(2);
        Recipe recipe = new Recipe(1,"newName", "newDescription", ids);
        //when
        recipeService.updateRecipeInDb(recipe);
        //then
        RecipeEntity result = jpaRecipeRepository.getOne(1);
        assertEquals(1, result.getId());
        assertEquals("newDescription", result.getDescription());
        assertEquals("newName", result.getName());
        assertEquals(1, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity2));
        assertFalse(result.getProducts().contains(productEntity1));
        assertFalse(result.getProducts().contains(productEntity3));
    }

    @Test
    void testShouldDeleteRecipeFromDb(){
        //given

        //when
        recipeService.deleteRecipeFromDb(1);
        //then
        Optional<RecipeEntity> result = jpaRecipeRepository.findRecipeById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testShouldFindRecipeByName(){
        //given

        //when
        Recipe result = recipeService.findByRecipeName("testName");
        //then
        assertEquals(1, result.getId());
        assertEquals("testDescription", result.getDescription());
        assertEquals("testName", result.getName());
        assertEquals(3, result.getProductId().size());
        assertTrue(result.getProductId().contains(1));
        assertTrue(result.getProductId().contains(2));
        assertTrue(result.getProductId().contains(3));
    }

    @Test
    void testShouldFindRecipesByProducts(){
        //given
        Set<ProductEntity> products1 = new HashSet<>();
        products1.add(productEntity1);

        recipeEntity = new RecipeEntity(2, "testName2", "testDescription2", products1);
        jpaRecipeRepository.save(recipeEntity);

        Set<ProductEntity> products2 = new HashSet<>();
        products2.add(productEntity1);
        products2.add(productEntity2);

        recipeEntity = new RecipeEntity(3, "testName3", "testDescription3", products2);
        jpaRecipeRepository.save(recipeEntity);

        Product product1 = new Product(1,"product1");
        Product product2 = new Product(2,"product2");

        List<Product> given = new ArrayList<>();
        given.add(product1);
        given.add(product2);

        Recipe recipe1 = recipeService.findByRecipeName("testName");
        Recipe recipe2 = recipeService.findByRecipeName("testName2");
        Recipe recipe3 = recipeService.findByRecipeName("testName3");

        //when
        Set<Recipe> result = recipeService.findByProducts(given);
        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(recipe2));
        assertTrue(result.contains(recipe3));
        assertFalse(result.contains(recipe1));
    }
}