package pl.sda.demo.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.demo.domain.product.Product;
import pl.sda.demo.domain.recipe.Recipe;
import pl.sda.demo.external.product.JpaProductRepository;
import pl.sda.demo.external.product.ProductEntity;
import pl.sda.demo.external.recipe.JpaRecipeRepository;
import pl.sda.demo.external.recipe.RecipeEntity;
import pl.sda.demo.external.user.DatabaseUserRepository;
import pl.sda.demo.external.user.JpaUserRepository;
import pl.sda.demo.external.user.UserEntity;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JpaProductRepository jpaProductRepository;
    @Autowired
    private JpaRecipeRepository jpaRecipeRepository;
    @Autowired
    private DatabaseUserRepository databaseUserRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ProductEntity productEntity1;
    private ProductEntity productEntity2;
    private ProductEntity productEntity3;
    private Set<ProductEntity> userProducts;
    private Set<RecipeEntity> favourites;
    private UserEntity userEntity;
    private RecipeEntity recipeEntity1;
    private RecipeEntity recipeEntity2;
    private Set<ProductEntity> recipeProducts1;
    private Set<ProductEntity> recipeProducts2;

    @BeforeEach
    void setup() {
        productEntity1 = new ProductEntity(1, "product1");
        productEntity2 = new ProductEntity(2, "product2");
        productEntity3 = new ProductEntity(3, "product3");

        jpaProductRepository.save(productEntity1);
        jpaProductRepository.save(productEntity2);
        jpaProductRepository.save(productEntity3);

        recipeProducts1 = new HashSet<>();
        recipeProducts1.add(productEntity1);
        recipeProducts1.add(productEntity3);

        recipeProducts2 = new HashSet<>();
        recipeProducts2.add(productEntity2);

        recipeEntity1 = new RecipeEntity(1, "recipeName1", "recipeDescription1", recipeProducts1);
        recipeEntity2 = new RecipeEntity(2, "recipeName2", "recipeDescription2", recipeProducts2);
        jpaRecipeRepository.save(recipeEntity1);
        jpaRecipeRepository.save(recipeEntity2);

        userProducts = new HashSet<>();
        userProducts.add(productEntity1);
        userProducts.add(productEntity2);

        favourites = new HashSet<>();
        favourites.add(recipeEntity2);

        userEntity = new UserEntity(1, "testUsername", "testPassword", "user", favourites, userProducts);
        jpaUserRepository.save(userEntity);
    }

    @Test
    void testShouldCreateUser() {
        //given
        User user = new User(1, "username", "password", null, null, "user");
        //when
        userService.createUser(user);
        //then
        UserEntity result = jpaUserRepository.getOne(2);
        assertEquals(2, result.getId());
        assertEquals("username", result.getUsername());
        assertTrue(passwordEncoder.matches("password", result.getPassword()));
        assertTrue(result.getFavourites().isEmpty());
        assertTrue(result.getProducts().isEmpty());
    }

    @Test
    void updateUser() {
        //given
        User user = new User(1, "newUsername", "newPassword", null, null, "user");
        //when
        userService.updateUser(user);
        //then
        UserEntity result = jpaUserRepository.getOne(1);
        assertEquals(1, result.getId());
        assertEquals("testUsername", result.getUsername());
        assertTrue(passwordEncoder.matches("newPassword", result.getPassword()));
        assertEquals(1, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(recipeEntity2));
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
    }

    @Test
    void deleteUser() {
        //given

        //when
        userService.deleteUser(1);
        //then
        Optional<UserEntity> result = jpaUserRepository.findById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void addProductToFridge() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("testUsername");
        Product newProduct = new Product(3, "testProduct3");
        //when
        assertTrue(user.isPresent());
        userService.addProductToFridge(newProduct, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(1);

        assertEquals(1, result.getId());
        assertEquals("testUsername", result.getUsername());
        assertEquals("testPassword", result.getPassword());
        assertEquals(1, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(recipeEntity2));
        assertEquals(3, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
        assertTrue(result.getProducts().contains(productEntity3));

    }

    @Test
    void removeProductFromFridge() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("testUsername");
        //when
        assertTrue(user.isPresent());
        userService.removeProductFromFridge(1, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(1);

        assertEquals(1, result.getId());
        assertEquals("testUsername", result.getUsername());
        assertEquals("testPassword", result.getPassword());
        assertEquals(1, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(recipeEntity2));
        assertEquals(1, result.getProducts().size());
        assertFalse(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
    }

    @Test
    void addRecipeToFavourites() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("testUsername");
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(3);
        Recipe recipe = new Recipe(1, "recipeName1", "recipeDescription1", ids);
        //when
        assertTrue(user.isPresent());
        userService.addRecipeToFavourites(recipe, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(1);

        assertEquals(1, result.getId());
        assertEquals("testUsername", result.getUsername());
        assertEquals("testPassword", result.getPassword());
        assertEquals(2, result.getFavourites().size());
        assertTrue(result.getFavourites().contains(recipeEntity2));
        assertTrue(result.getFavourites().contains(recipeEntity1));
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
    }

    @Test
    void deleteRecipeFromFavourites() {
        //given
        Optional<User> user = databaseUserRepository.findByUsername("testUsername");
        //when
        assertTrue(user.isPresent());
        userService.deleteRecipeFromFavourites(2, user.get());
        //then
        UserEntity result = jpaUserRepository.getOne(1);

        assertEquals(1, result.getId());
        assertEquals("testUsername", result.getUsername());
        assertEquals("testPassword", result.getPassword());
        assertTrue(result.getFavourites().isEmpty());
        assertEquals(2, result.getProducts().size());
        assertTrue(result.getProducts().contains(productEntity1));
        assertTrue(result.getProducts().contains(productEntity2));
    }
}