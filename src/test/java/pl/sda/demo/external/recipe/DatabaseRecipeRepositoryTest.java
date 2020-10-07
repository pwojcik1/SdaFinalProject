package pl.sda.demo.external.recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.mockito.Mockito;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class DatabaseRecipeRepositoryTest {

    private final JpaProductRepository jpaProductRepository = Mockito.mock(JpaProductRepository.class);
    private final JpaRecipeRepository jpaRecipeRepository = Mockito.mock(JpaRecipeRepository.class);
    private final DatabaseRecipeRepository databaseRecipeRepository = new DatabaseRecipeRepository(jpaRecipeRepository, jpaProductRepository);
    private final ArgumentCaptor<RecipeEntity> recipeEntityArgumentCaptor = ArgumentCaptor.forClass(RecipeEntity.class);

    @Test
    void testShouldAPersistNewRecipe() {
        //given
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);

        Recipe recipe = Recipe.builder()
                .name("Kanapka")
                .description("description")
                .productId(ints)
                .build();

        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity1);
        productEntities.add(productEntity2);

        Mockito.when(jpaProductRepository.findAllProductsByIdInList(ints)).thenReturn(productEntities);
        //when
        databaseRecipeRepository.addRecipeToDb(recipe);
        //then
        Mockito.verify(jpaRecipeRepository).save(recipeEntityArgumentCaptor.capture());

        RecipeEntity recipeEntity = recipeEntityArgumentCaptor.getValue();
        assertEquals("Kanapka", recipeEntity.getName());
        assertEquals("description", recipeEntity.getDescription());
        assertEquals(2, recipeEntity.getProducts().size());
        assertTrue(recipeEntity.getProducts().contains(productEntity1));
        assertTrue(recipeEntity.getProducts().contains(productEntity2));
    }

    @Test
    void testShouldUpdateRecipe() {
        //given
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        Recipe recipe = Recipe.builder()
                .id(1)
                .name("Kanapka")
                .description("description")
                .productId(ints)
                .build();


        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();
        ProductEntity productEntity3 = ProductEntity.builder().name("boczek").build();

        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity1);
        productEntities.add(productEntity2);

        Set<ProductEntity> returnProducts = new HashSet<>();
        returnProducts.add(productEntity1);
        returnProducts.add(productEntity2);
        returnProducts.add(productEntity3);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .id(1)
                .name("Kanapka")
                .description("description")
                .products(productEntities)
                .build();
        //when
        Mockito.when(jpaRecipeRepository.findRecipeById(1)).thenReturn(Optional.of(recipeEntity));
        Mockito.when(jpaProductRepository.findAllProductsByIdInList(recipe.getProductId())).thenReturn(returnProducts);

        databaseRecipeRepository.updateRecipeInDb(recipe);
        //then
        Mockito.verify(jpaRecipeRepository).findRecipeById(1);
        Mockito.verify(jpaProductRepository).findAllProductsByIdInList(recipe.getProductId());
        Mockito.verify(jpaRecipeRepository).save(recipeEntityArgumentCaptor.capture());

        RecipeEntity result = recipeEntityArgumentCaptor.getValue();
        assertEquals("Kanapka", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals(3, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
        assertTrue(result.getProducts().contains(productEntity3));
    }

    @Test
    void testShouldReturnRecipeFoundByName() {
        //given
        ProductEntity productEntity1 = ProductEntity.builder().name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().name("maslo").build();

        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity1);
        productEntities.add(productEntity2);

        List<Integer> productIds = new ArrayList<>();
        productIds.add(1);
        productIds.add(2);

        RecipeEntity recipeEntity = RecipeEntity.builder()
                .id(1)
                .name("Kanapka")
                .description("description")
                .products(productEntities)
                .build();
        //when
        Mockito.when(jpaRecipeRepository.getRecipeByName("Kanapka")).thenReturn(Optional.of(recipeEntity));
        Mockito.when(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity.getProducts())).thenReturn(productIds);

        Optional<Recipe> result = databaseRecipeRepository.findByRecipeName("Kanapka");
        //then
        Mockito.verify(jpaRecipeRepository).getRecipeByName("Kanapka");
        Mockito.verify(jpaProductRepository).findAllProductsIdFromCollection(recipeEntity.getProducts());
        assertTrue(result.isPresent());
        Assertions.assertEquals("Kanapka", result.get().getName());
        Assertions.assertEquals(1, result.get().getId());
        Assertions.assertEquals("description", result.get().getDescription());
        Assertions.assertEquals(2, result.get().getProductId().size());
        Assertions.assertTrue(result.get().getProductId().contains(1));
        Assertions.assertTrue(result.get().getProductId().contains(2));
    }

    @Test
    void testShouldReturnRecipesFoundByProducts() {
        //given
        Product product1 = Product.builder().id(1).name("chleb").build();
        Product product2 = Product.builder().id(2).name("maslo").build();
        Product product3 = Product.builder().id(3).name("mleko").build();

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);


        ProductEntity productEntity1 = ProductEntity.builder().id(1).name("chleb").build();
        ProductEntity productEntity2 = ProductEntity.builder().id(2).name("maslo").build();
        ProductEntity productEntity3 = ProductEntity.builder().id(3).name("mleko").build();

        Set<ProductEntity> productsInFridge = new HashSet<>();
        productsInFridge.add(productEntity2);
        productsInFridge.add(productEntity1);
        productsInFridge.add(productEntity3);

        Set<ProductEntity> firstRecipeEntityProducts = new HashSet<>();
        firstRecipeEntityProducts.add(productEntity1);
        firstRecipeEntityProducts.add(productEntity2);


        Set<ProductEntity> secondRecipeEntityProducts = new HashSet<>();
        secondRecipeEntityProducts.add(productEntity1);
        secondRecipeEntityProducts.add(productEntity2);
        secondRecipeEntityProducts.add(productEntity3);

        RecipeEntity recipeEntity1 = RecipeEntity.builder()
                .id(1)
                .name("Kanapka")
                .description("Pyszna kanapka")
                .products(firstRecipeEntityProducts)
                .build();

        RecipeEntity recipeEntity2 = RecipeEntity.builder()
                .id(2)
                .name("Inna Kanapka")
                .description("Dobra kanapka")
                .products(secondRecipeEntityProducts)
                .build();

        Set<RecipeEntity> recipeEntities = new HashSet<>();
        recipeEntities.add(recipeEntity1);
        recipeEntities.add(recipeEntity2);

        List<Integer> firstRecipeProducts = new ArrayList<>();
        firstRecipeProducts.add(1);
        firstRecipeProducts.add(2);

        List<Integer> secondRecipeProducts = new ArrayList<>();
        secondRecipeProducts.add(1);
        secondRecipeProducts.add(2);
        secondRecipeProducts.add(3);

        Recipe recipe1 = Recipe.builder()
                .id(1)
                .name("Kanapka")
                .description("Pyszna kanapka")
                .productId(firstRecipeProducts)
                .build();

        Recipe recipe2 = Recipe.builder()
                .id(2)
                .name("Inna Kanapka")
                .description("Dobra kanapka")
                .productId(secondRecipeProducts)
                .build();

        //when
        Mockito.when(jpaRecipeRepository.findAllRecipesByProducts(productsInFridge)).thenReturn(recipeEntities);
        Mockito.when(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity1.getProducts())).thenReturn(firstRecipeProducts);
        Mockito.when(jpaProductRepository.findAllProductsIdFromCollection(recipeEntity2.getProducts())).thenReturn(secondRecipeProducts);

        Set<Recipe> result = databaseRecipeRepository.findByProducts(products);
        //then
        Mockito.verify(jpaRecipeRepository).findAllRecipesByProducts(productsInFridge);
        Mockito.verify(jpaProductRepository).findAllProductsIdFromCollection(recipeEntity1.getProducts());
        Mockito.verify(jpaProductRepository).findAllProductsIdFromCollection(recipeEntity2.getProducts());

        assertEquals(2, result.size());
        assertTrue(result.contains(recipe1));
        assertTrue(result.contains(recipe2));
    }
}
