package pl.sda.demo.external.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.domain.user.User;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseUserRepositoryTest {
    private final JpaProductRepository jpaProductRepository = Mockito.mock(JpaProductRepository.class);
    private final JpaUserRepository jpaUserRepository = Mockito.mock(JpaUserRepository.class);
    private final JpaRecipeRepository jpaRecipeRepository = Mockito.mock(JpaRecipeRepository.class);
    private final DatabaseUserRepository databaseUserRepository = new DatabaseUserRepository(jpaUserRepository, jpaProductRepository, jpaRecipeRepository);
    private final ArgumentCaptor<UserEntity> argumentCaptor = ArgumentCaptor.forClass(UserEntity.class);

    @Test
    void testShouldPersistNewUser() {
        //given
        User user = new User(null, "user", "password", new ArrayList<>(), new ArrayList<>(),"user");
        //when
        Mockito.when(jpaRecipeRepository.findAllRecipesByIdInCollection(user.getRecipeId())).thenReturn(new HashSet<>());
        Mockito.when(jpaProductRepository.findAllProductsByIdInList(user.getProductId())).thenReturn(new HashSet<>());

        databaseUserRepository.createUser(user);

        Mockito.verify(jpaRecipeRepository).findAllRecipesByIdInCollection(user.getRecipeId());
        Mockito.verify(jpaProductRepository).findAllProductsByIdInList(user.getProductId());
        Mockito.verify(jpaUserRepository).save(argumentCaptor.capture());

        UserEntity result = argumentCaptor.getValue();
        //then
        assertEquals("user", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.getFavourites().isEmpty());
        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void testShouldUpdateUser() {
        //given
        User user = new User(2, "user", "newPassword", new ArrayList<>(), new ArrayList<>(),"user");
        UserEntity userEntity = new UserEntity(2, "user", "password","user", new HashSet<>(), new HashSet<>());
        //when
        Mockito.when(jpaUserRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));

        databaseUserRepository.updateUser(user);

        Mockito.verify(jpaUserRepository).findById(user.getId());
        Mockito.verify(jpaUserRepository).save(argumentCaptor.capture());

        UserEntity result = argumentCaptor.getValue();
        //then
        assertEquals("user", result.getUsername());
        assertEquals("newPassword", result.getPassword());
        assertEquals(2, result.getId());
        assertTrue(result.getFavourites().isEmpty());
        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void testShouldAddNewProductToFridge() {
        //given
        Product product = new Product(null, "Egg");
        ProductEntity productEntity = new ProductEntity(null, "Egg");
        User user = new User(2, "user", "password", new ArrayList<>(), new ArrayList<>(),"user");
        UserEntity userEntity = new UserEntity(2, "user", "password","user", new HashSet<>(), new HashSet<>());
        //when
        Mockito.when(jpaUserRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));

        databaseUserRepository.addProductToFridge(product, user);

        Mockito.verify(jpaUserRepository).findById(user.getId());
        Mockito.verify(jpaUserRepository).save(argumentCaptor.capture());

        UserEntity result = argumentCaptor.getValue();
        //then
        assertEquals(2, result.getId());
        assertEquals("user", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.getFavourites().isEmpty());
        assertEquals(1, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity));
    }

    @Test
    void testShouldRemoveProductFromFridge() {
        //given
        List<Integer> productsId = new ArrayList<>();
        productsId.add(3);
        productsId.add(3);

        User user = new User(2, "user", "password", new ArrayList<>(), productsId,"user");

        ProductEntity productEntity = new ProductEntity(3, "Egg");
        ProductEntity productEntity2 = new ProductEntity(3, "Butter");

        Set<ProductEntity> products = new HashSet<>();
        products.add(productEntity);
        products.add(productEntity2);

        UserEntity userEntity = new UserEntity(2, "user", "password","user", new HashSet<>(), products);
        //when
        Mockito.when(jpaUserRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
        Mockito.when(jpaProductRepository.findProductById(3)).thenReturn(Optional.of(productEntity));

        databaseUserRepository.removeProductFromFridge(3, user);

        Mockito.verify(jpaUserRepository).findById(user.getId());
        Mockito.verify(jpaProductRepository).findProductById(3);
        Mockito.verify(jpaUserRepository).save(argumentCaptor.capture());

        UserEntity result = argumentCaptor.getValue();
        //then
        assertEquals("user", result.getUsername());
        assertEquals("password", result.getPassword());
        assertEquals(2, result.getId());
        assertTrue(result.getFavourites().isEmpty());
        assertEquals(1, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity2));
    }

    @Test
    void testShouldThrowExceptionForNonexistentProduct(){
        //given
        User user = new User(2, "user", "password", new ArrayList<>(), new ArrayList<>(),"user");
        UserEntity userEntity = new UserEntity(2, "user", "password","user", new HashSet<>(), new HashSet<>());
        //when
        Mockito.when(jpaUserRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
        Mockito.when(jpaProductRepository.findProductById(3)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> databaseUserRepository.removeProductFromFridge(3, user));
        //then
        assertEquals("You cannot remove nonexistent product", ex.getMessage());
        Mockito.verify(jpaUserRepository, Mockito.never()).save(userEntity);
    }

    @Test
    void testShouldAddRecipeToFavourites(){
        //given
        List<Integer> productId = new ArrayList<>();
        productId.add(1);
        productId.add(2);

        ProductEntity productEntity1 = new ProductEntity(1, "Egg");
        ProductEntity productEntity2 = new ProductEntity(2, "Egg");

        Set<ProductEntity> productEntities = new HashSet<>();
        productEntities.add(productEntity1);
        productEntities.add(productEntity2);

        Recipe recipe = new Recipe(1, "testName", "restDescription", productId);
        RecipeEntity recipeEntity = new RecipeEntity(1, "testName", "restDescription", productEntities);

        User user = new User(1, "user", "password", new ArrayList<>(), new ArrayList<>(),"user");

        UserEntity userEntity = new UserEntity(1, "user", "password","user", new HashSet<>(), new HashSet<>());
        //when
        Mockito.when(jpaProductRepository.findAllProductsByIdInList(recipe.getProductId())).thenReturn(productEntities);
        Mockito.when(jpaUserRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));

        databaseUserRepository.addRecipeToFavourites(recipe,user);

        Mockito.verify(jpaProductRepository).findAllProductsByIdInList(recipe.getProductId());
        Mockito.verify(jpaUserRepository).findById(user.getId());
        Mockito.verify(jpaUserRepository).save(argumentCaptor.capture());

        UserEntity result = argumentCaptor.getValue();
        //then
        assertEquals(1, result.getId());
        assertEquals("user", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.getProducts().isEmpty());
        assertEquals(1, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(recipeEntity));
    }

    @Test
    void testShouldRemoveRecipeFromFavourites(){

    }
}
